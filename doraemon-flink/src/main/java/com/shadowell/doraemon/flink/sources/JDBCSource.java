package com.shadowell.doraemon.flink.sources;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/16
 */
public interface JDBCSource {

    Connection connect();

    ResultSet execute(String sql);

    void close();

}
