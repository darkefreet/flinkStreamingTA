package flinkstreaming;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Model.AnalysisResult;
import Model.Instance;
import Streamprocess.StreamParser;
import Streamprocess.WindowStreamProcess;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.*;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;
import org.apache.flink.streaming.util.serialization.SerializationSchema;

import java.lang.reflect.InvocationTargetException;


/**
 * Skeleton for a Flink Streaming Job.
 *
 * For a full example of a Flink Streaming Job, see the SocketTextStreamWordCount.java
 * file in the same package/directory or have a look at the website.
 *
 * You can also generate a .jar file that you can submit on your Flink
 * cluster.
 * Just type
 * 		mvn clean package
 * in the projects root directory.
 * You will find the jar in
 * 		target/1-1.0-SNAPSHOT.jar
 * From the CLI you can then run
 * 		./bin/flink run -c flinkstreaming.StreamingJob target/1-1.0-SNAPSHOT.jar
 *
 * For more information on the CLI see:
 *
 * http://flink.apache.org/docs/latest/apis/cli.html
 */
public class StreamingJob {

	private static DataStream<String> source;
	private static XMLConfiguration config;

	public static void main(String[] args) throws Exception {

		// set up the execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment
				.getExecutionEnvironment();

		//read from configuration

		config = new XMLConfiguration("config.xml");

		switch(config.getString("source.name")){
			case "twitter":{
				//set Twitter properties to authenticate
				TwitterSource twitterSource = new TwitterSource(config.getString("source.properties-file"));
				// get input data
				source = env.addSource(twitterSource);
			}
			break;
			case "socket":{
				source = env.socketTextStream(config.getString("source.ip"),config.getInt("source.port"),"\n", 0);
			}
			break;
			default: {
				System.out.println("No source given");
				source = null;
			}
		}

		//test
		DataStream<Instance> streamOutput =
				source.flatMap(new StreamParser()).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Instance>() {
					@Override
					public long extractAscendingTimestamp(Instance element) {
						return element.getTime();
					}
				});

		KeySelector keySelect = new KeySelector<Instance, String>() {
			public String getKey(Instance inst) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
				switch(config.getString("keyBy.type")){
					case "static":
						return config.getString("keyBy.value");
					case "object":
						return String.valueOf(inst.getClass().getDeclaredMethod(config.getString("keyBy.function")).invoke(inst));
					default:
						return "1";
				}
			}
		};

		DataStream<AnalysisResult> windowedStream;
		Long windowTime = 0L;
		Long overlapTime = 0L;
		windowTime = windowTime + (config.getInt("window.size.hours")*3600) + (config.getInt("window.size.minutes")*60) + (config.getInt("window.size.seconds"));
		overlapTime = overlapTime + (config.getInt("window.overlap.hours")*3600) + (config.getInt("window.overlap.minutes")*60) + (config.getInt("window.overlap.seconds"));
		switch(config.getString("window.type")){
			case "tumbling": {
				switch (config.getString("window.time")) {
					case "event": {
						windowedStream = streamOutput.keyBy(keySelect)
								.window(TumblingEventTimeWindows.of(Time.seconds(windowTime)))
								.apply(new WindowStreamProcess());
						break;
					}
					default: {
						windowedStream = streamOutput.keyBy(keySelect)
								.window(TumblingProcessingTimeWindows.of(Time.seconds(windowTime)))
								.apply(new WindowStreamProcess());
						break;
					}
				}
				break;
			}
			case "sliding":{
				switch (config.getString("window.time")) {
					case "event": {
						windowedStream = streamOutput.keyBy(keySelect)
								.window(SlidingEventTimeWindows.of(Time.seconds(windowTime), Time.seconds(overlapTime)))
								.apply(new WindowStreamProcess());
						break;
					}
					default: {
						windowedStream = streamOutput.keyBy(keySelect)
								.window(SlidingProcessingTimeWindows.of(Time.seconds(windowTime), Time.seconds(overlapTime)))
								.apply(new WindowStreamProcess());
						break;
					}
				}
				break;
			}
			default:{
				switch (config.getString("window.time")) {
					case "event": {
						windowedStream = streamOutput.keyBy(keySelect)
								.window(EventTimeSessionWindows.withGap(Time.seconds(windowTime)))
								.apply(new WindowStreamProcess());
						break;
					}
					default: {
						windowedStream = streamOutput.keyBy(keySelect)
								.window(ProcessingTimeSessionWindows.withGap(Time.seconds(windowTime)))
								.apply(new WindowStreamProcess());
						break;
					}
				}
				break;
			}
		}

		switch(config.getString("windowSink.type")){
			case "text": {
				windowedStream.writeAsText(config.getString("windowSink.path")).setParallelism(1);
				break;
			}
			case "csv": {
				windowedStream.writeAsCsv(config.getString("windowSink.path")).setParallelism(1);
				break;
			}
			case "socket":{
				windowedStream.writeToSocket(config.getString("windowSink.ip"), config.getInt("windowSink.port"), new SerializationSchema<AnalysisResult>() {
					@Override
					public byte[] serialize(AnalysisResult analysisResult) {
						return analysisResult.toString().getBytes();
					}
				});
				break;
			}

			default:{
				windowedStream.print();
				break;
			}
		}

		// execute program
		env.execute("Java word count from SocketTextStream Example");
	}

}


