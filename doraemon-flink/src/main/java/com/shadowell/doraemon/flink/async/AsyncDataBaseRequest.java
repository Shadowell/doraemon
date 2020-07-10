//package com.shadowell.doraemon.flink.core;
//
//import com.paic.app.entity.LoginEvent;
//import com.paic.app.entity.RulesEntity;
//import com.paic.app.sources.TiDBSource;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.streaming.api.scala.async.ResultFuture;
//import org.apache.flink.streaming.api.scala.async.RichAsyncFunction;
//
//import java.sql.ResultSet;
//import java.util.Collections;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
///**
// * Author: Jie Feng
// * Date: Created in 2019/12/20
// */
//public class AsyncDataBaseRequest extends RichAsyncFunction<RulesEntity> {
//
//	private transient TiDBSource dbClient;
//	private transient ExecutorService executorService;
//
//	@Override
//	public void open(Configuration config) throws Exception {
//		super.open(config);
//		dbClient.connect();
//		executorService = Executors.newFixedThreadPool(30);
//	}
//
//	@Override void asyncInvoke(String input, ResultFuture<RulesEntity> resultFuture) throws Exception {
//		executorService.submit(() -> {
//
//			ResultSet resultSet = dbClient.execute(input);
//			resultFuture.complete(Collections.singletonList(resultSet));
//		});
//	}
//
//	@Override
//	public void timeout(LoginEvent input, ResultFuture<RulesEntity> resultFuture) {
//
//	}
//
//	@Override
//	public void close() throws Exception {
//		super.close();
//	}
//}
