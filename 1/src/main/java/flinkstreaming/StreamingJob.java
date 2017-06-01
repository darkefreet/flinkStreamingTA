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

import Model.ClusterResult;
import Model.Instance;
import Streamprocess.ClusterText;
import Streamprocess.ClusterWindow;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;


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
			default:
				System.out.println("No source given");
				source = null;
		}

		//test
		DataStream<Instance> streamOutput =
				source.flatMap(new ClusterText()).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Instance>() {
					@Override
					public long extractAscendingTimestamp(Instance element) {
						return element.getTime();
					}
				});

		DataStream<ClusterResult> windowedStream = streamOutput.keyBy(new KeySelector<Instance, String>() {
			public String getKey(Instance inst) {return "1";}
		}).window(TumblingProcessingTimeWindows.of(Time.minutes(5)))
				.apply(new ClusterWindow());

		windowedStream.print();
		// execute program
		env.execute("Java word count from SocketTextStream Example");
	}



}


