#!/bin/sh

## 参数定义
dt=`date +"%Y%m%d" -d "-1 days"`
outpath=/xxxx_log_${dt}.txt
brokerlist=192.168.1.100:9092,192.168.1.101:9092,192.168.1.102:9092
topic = test_topic

echo $dt $outpath $brokerlist

## 判断文件大小，如果大于0，则加载文件，写入kafka
## 注意 结尾的 | > out.txt要加上，否则会出现很多奇怪的大于号
fileSize=`du -b ${outpath} | awk '{print $1}'`
if [ $fileSize -gt 0 ] then
    cat ${outpath} | ./kafka_2.11-1.0.0/bin/kafka-console-producer.sh --broker-list ${brokerlist} --sync --topic ${topic} | > out.txt
fi
