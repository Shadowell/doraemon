//package com.paic.app;
//
//import com.paic.app.entity.LoginEvent;
//import com.paic.app.entity.LoginWarning;
//import org.apache.flink.cep.CEP;
//import org.apache.flink.cep.PatternStream;
//import org.apache.flink.cep.pattern.Pattern;
//import org.apache.flink.cep.pattern.conditions.IterativeCondition;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.windowing.time.Time;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//public class FlinkLoginFail {
//
//    public static void main(String[] args) throws Exception {
//
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//        DataStream<LoginEvent> loginEventStream = env.fromCollection(Arrays.asList(
//                new LoginEvent("1","192.168.0.1","fail"),
//                new LoginEvent("1","192.168.0.2","fail"),
//                new LoginEvent("1","192.168.0.3","fail"),
//                new LoginEvent("2","192.168.10,10","success"),
//                new LoginEvent("2", "192.168.10.21", "success"),
//                new LoginEvent("2", "192.168.10.22", "success")
//
//        ));
//
//        Pattern<LoginEvent, LoginEvent> loginSuccessPattern = Pattern.<LoginEvent>
//                begin("begin")
//                .where(new IterativeCondition<LoginEvent>() {
//                    @Override
//                    public boolean filter(LoginEvent loginEvent, Context context) throws Exception {
//                        return loginEvent.getType().equals("success");
//                    }
//                })
//                .times(3)
//                .next("next")
//                .where(new IterativeCondition<LoginEvent>() {
//                    @Override
//                    public boolean filter(LoginEvent loginEvent, Context context) throws Exception {
//                        return loginEvent.getType().equals("success");
//                    }
//                })
//                .within(Time.seconds(3));
//
//        Pattern<LoginEvent, LoginEvent> loginFailPattern = Pattern.<LoginEvent>
//                begin("begin")
//                .where(new IterativeCondition<LoginEvent>() {
//                    @Override
//                    public boolean filter(LoginEvent loginEvent, Context context) throws Exception {
//                        return loginEvent.getType().equals("fail");
//                    }
//                })
//                .next("next")
//                .where(new IterativeCondition<LoginEvent>() {
//                    @Override
//                    public boolean filter(LoginEvent loginEvent, Context context) throws Exception {
//                        return loginEvent.getType().equals("fail");
//                    }
//                })
//                .within(Time.seconds(3));
//
//        PatternStream<LoginEvent> patternStream = CEP.pattern(
//                loginEventStream.keyBy(LoginEvent::getUserId),
//                loginFailPattern);
//
//        DataStream<LoginWarning> loginFailDataStream = patternStream.select((Map<String, List<LoginEvent>> pattern) -> {
//            List<LoginEvent> first = pattern.get("begin");
//            List<LoginEvent> second = pattern.get("next");
//
//            return new LoginWarning(second.get(0).getUserId(),second.get(0).getIp(), second.get(0).getType());
//        });
//
//        PatternStream<LoginEvent> patternSuccessStream = CEP.pattern(
//                loginEventStream.keyBy(LoginEvent::getUserId),
//                loginSuccessPattern);
//
//        DataStream<LoginWarning> loginSuccessDataStream = patternSuccessStream.select((Map<String, List<LoginEvent>> pattern1) ->{
//            List<LoginEvent> first1 = pattern1.get("begin");
//            List<LoginEvent> second1 = pattern1.get("next");
//            return new LoginWarning(first1.get(0).getUserId(), first1.get(0).getIp(), first1.get(0).getType());
//        });
//
//        loginSuccessDataStream.print();
//        //loginFailDataStream.print();
//
//
//        env.execute();
//    }
//
//}
