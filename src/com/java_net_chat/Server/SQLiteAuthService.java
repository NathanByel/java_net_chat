package com.java_net_chat.Server;

import com.java_net_chat.Log;
import com.java_net_chat.User;
import com.java_net_chat.UserInfo;
import org.sqlite.JDBC;

import java.io.IOException;
import java.sql.*;

/*
    1. Добавить в сетевой чат авторизацию через базу данных SQLite.
    2.*Добавить в сетевой чат возможность смены ника.
*/

public class SQLiteAuthService implements AuthService {
    private static final String TAG = "SQL Auth";
    //private static final String DB_PATH = "../../chat_db.db";
    private static final String DB_PATH = "E:/REPOSITORY/GitHUB/java_net_chat/src/chat_db.db";

    private Connection connection = null;

    @Override
    public void start() {
        try {
//            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(JDBC.PREFIX + DB_PATH);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserInfo getUserInfo(User user) {
        return new UserInfo(user.getNickName(), user.getPass()) ;
    }

    @Override
    public boolean checkUser(User user) {
        try {
            connection = DriverManager.getConnection(JDBC.PREFIX + DB_PATH);

            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT Password FROM users WHERE Nickname = '" + user.getNickName() + "'");
            if(resultSet.next()) {
                String pass = resultSet.getString("Password");
                if( (pass != null) && pass.equals(user.getPass())) {
                    Log.e(TAG, "Auth OK");
                    return true;
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "SQL error - " + e.getMessage());
            return false;
            //e.printStackTrace();
        }
        Log.e(TAG, "Auth error");
        return false;
    }

    @Override
    public void close() throws IOException {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
