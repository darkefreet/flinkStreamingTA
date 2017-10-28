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

import DataProcess.WindowStreamProcess;
import DataSource.SatoriSource;
import Model.Instances.Instance;
import DataProcess.StreamParser;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.*;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer09;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;
import org.apache.hadoop.util.Time;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;


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

	private static XMLConfiguration config;
	private static DataStream<String> source;
	private static TwitterSource twitterSource;
	private static SatoriSource satoriSource;

	private static void sinkFunction(DataStream<String> sinkSource, SubnodeConfiguration subconf){
		switch(subconf.getString("type")){
			case "print":{
				sinkSource.print();
				break;
			}
			case "kafka":{
				String ipPort = subconf.configurationAt("type").getString("[@ip]")+":"+subconf.configurationAt("type").getString("[@port]");
				FlinkKafkaProducer09<String> myProducer = new FlinkKafkaProducer09<String>(
						ipPort,            											// broker list
						subconf.configurationAt("type").getString("[@topic]"),      // target topic
						new SimpleStringSchema());   // serialization schema
				// the following is necessary for at-least-once delivery guarantee
				myProducer.setLogFailuresOnly(true);   // "false" by default
				myProducer.setFlushOnCheckpoint(true);  // "false" by default
				sinkSource.addSink(myProducer);
				break;
			}
			case "text":{
				sinkSource.writeAsText(subconf.configurationAt("type").getString("[@path]")).setParallelism(1);
				break;
			}
			case "csv":{
				sinkSource.writeAsCsv(subconf.configurationAt("type").getString("[@path]")).setParallelism(1);
				break;
			}
			case "socket":{

				break;
			}
			default:{
				//do nothing
			}
		}
	}

	public static void main(String[] args) throws Exception {

		// set up the execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment
				.getExecutionEnvironment();

		String fileConfig = args[0];
		config = new XMLConfiguration(fileConfig);

		//LISTING SOURCES AND UNION SOURCES INTO A SINGLE DATASTREAM SOURCE
		List<HierarchicalConfiguration> hconfig = config.configurationsAt("sources.source");
		for(HierarchicalConfiguration con : hconfig){
			switch(con.getString("")){
				case "socket":{
					if(source==null)
						source = env.socketTextStream(con.getString("[@ip]"),con.getInt("[@port]"),"\n", 0);
					else
						source = source.union(env.socketTextStream(con.getString("[@ip]"),con.getInt("[@port]"),"\n", 0));
					break;
				}
				case "twitter":{
					if(twitterSource==null)
						twitterSource = new TwitterSource(con.getString("[@properties]"));
					if(source==null)
						source = env.addSource(twitterSource);
					else
						source = source.union(env.addSource(twitterSource));
					break;
				}
				case "satori":{
					if(satoriSource ==null) {
						Properties prop = new Properties();
						FileInputStream input = new FileInputStream(con.getString("[@properties]"));
						prop.load(input);
						satoriSource = new SatoriSource(prop,con.getString("[@channel]"));
					}
					if(source==null)
						source = env.addSource(satoriSource);
					else
						source = source.union(env.addSource(satoriSource));
					break;
				}
				case "kafka":{
					Properties properties = new Properties();
					properties.setProperty("bootstrap.servers", con.getString("[@ip]")+":"+con.getString("[@port]"));
					DataStream<String> stream = env.addSource(new FlinkKafkaConsumer09<String>(con.getString("[@topic]"), new SimpleStringSchema(), properties));
					if(source==null)
						source = stream;
					else
						source = source.union(stream);
					break;
				}
				default:{
					//do nothing
				}
			}
		}

		//Stream parsing and Stream transformation
		DataStream<Instance> streamOutput =
			source.flatMap(new StreamParser(config)).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Instance>() {
				@Override
				public long extractAscendingTimestamp(Instance element) {
					return Time.now();
				}
			});

		if(config.configurationAt("dataTransformation.dataSink").getBoolean("[@status]")){
			DataStream<String> sinkSource = streamOutput.flatMap(new FlatMapFunction<Instance, String>() {
				@Override
				public void flatMap(Instance instance, Collector<String> collector){
					collector.collect(instance.toString());
				}
			});
			sinkFunction(sinkSource,config.configurationAt("dataTransformation.dataSink"));
		}

		KeySelector keySelect = new KeySelector<Instance, String>() {
			public String getKey(Instance inst) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
				switch (config.getString("window.keyBy")) {
					case "static":
						return config.configurationAt("window.keyBy").getString("[@value]");
					case "id":
						return inst.getId();
					default:
						return "1";
				}
			}

		};

		if(config.configurationAt("window").getBoolean("[@status]")){
			DataStream<String> windowedStream;
			Long windowTime = 0L;
			Long overlapTime = 0L;
			windowTime = windowTime + (config.getInt("window.size.hours")*3600) + (config.getInt("window.size.minutes")*60) + (config.getInt("window.size.seconds"));
			overlapTime = overlapTime + (config.getInt("window.overlap.hours")*3600) + (config.getInt("window.overlap.minutes")*60) + (config.getInt("window.overlap.seconds"));
			switch(config.getString("window.type")){
				case "tumbling": {
					switch (config.getString("window.time")) {
						case "event": {
							windowedStream = streamOutput.keyBy(keySelect)
									.window(TumblingEventTimeWindows.of(org.apache.flink.streaming.api.windowing.time.Time.seconds(windowTime)))
									.apply(new WindowStreamProcess(config));
							break;
						}
						default: {
							windowedStream = streamOutput.keyBy(keySelect)
									.window(TumblingProcessingTimeWindows.of(org.apache.flink.streaming.api.windowing.time.Time.seconds(windowTime)))
									.apply(new WindowStreamProcess(config));
							break;
						}
					}
					break;
				}
				case "sliding":{
					switch (config.getString("window.time")) {
						case "event": {
							windowedStream = streamOutput.keyBy(keySelect)
									.window(SlidingEventTimeWindows.of(org.apache.flink.streaming.api.windowing.time.Time.seconds(windowTime), org.apache.flink.streaming.api.windowing.time.Time.seconds(overlapTime)))
									.apply(new WindowStreamProcess(config));
							break;
						}
						default: {
							windowedStream = streamOutput.keyBy(keySelect)
									.window(SlidingProcessingTimeWindows.of(org.apache.flink.streaming.api.windowing.time.Time.seconds(windowTime), org.apache.flink.streaming.api.windowing.time.Time.seconds(overlapTime)))
									.apply(new WindowStreamProcess(config));
							break;
						}
					}
					break;
				}
				default:{
					switch (config.getString("window.time")) {
						case "event": {
							windowedStream = streamOutput.keyBy(keySelect)
									.window(EventTimeSessionWindows.withGap(org.apache.flink.streaming.api.windowing.time.Time.seconds(windowTime)))
									.apply(new WindowStreamProcess(config));
							break;
						}
						default: {
							windowedStream = streamOutput.keyBy(keySelect)
									.window(ProcessingTimeSessionWindows.withGap(org.apache.flink.streaming.api.windowing.time.Time.seconds(windowTime)))
									.apply(new WindowStreamProcess(config));
							break;
						}
					}
					break;
				}
			}
			if(config.configurationAt("window.dataSink").getBoolean("[@status]"))
				sinkFunction(windowedStream,config.configurationAt("window.dataSink"));
		}
		env.execute();
	}

}


