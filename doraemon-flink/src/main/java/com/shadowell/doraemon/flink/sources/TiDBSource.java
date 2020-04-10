package com.paic.app.sources;

import com.paic.app.config.TiDBConfig;
import java.sql.*;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/16
 */
public class TiDBSource implements JDBCSource {

    private Connection connection = null;
    private Statement state = null;
    private TiDBConfig tiDBConfig;

    public TiDBSource(TiDBConfig tiDBConfig) {
    	this.tiDBConfig = tiDBConfig;
	}

    @Override
    public Connection connect(){
        try {
            Class.forName(tiDBConfig.getDriverName());
            connection = DriverManager.getConnection(tiDBConfig.getUrl(),
				tiDBConfig.getUsername(), tiDBConfig.getPassword());

        }catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public ResultSet execute(String sql) {
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            return result;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {
        try {
            if (state != null) {
                state.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
		}
    }
}
