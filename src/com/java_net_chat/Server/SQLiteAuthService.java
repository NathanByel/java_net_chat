package com.java_net_chat.Server;

import com.java_net_chat.User;
import com.java_net_chat.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.JDBC;

import java.io.IOException;
import java.sql.*;

/*
    1. Добавить в сетевой чат авторизацию через базу данных SQLite.
    2.*Добавить в сетевой чат возможность смены ника.
*/

public class SQLiteAuthService implements AuthService {
    private static final Logger log = LoggerFactory.getLogger("SQL Auth");

    private static final String DB_PATH = "chat_db.db";
    //private static final String DB_PATH = "E:/REPOSITORY/GitHUB/java_net_chat/src/chat_db.db";

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
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT Password FROM users WHERE NickName = '" + user.getNickName() + "'");
            if(resultSet.next()) {
                String pass = resultSet.getString("Password");
                if( (pass != null) && pass.equals(user.getPass())) {
                    log.info(user.getNickName() + " - Auth OK");
                    return true;
                }
            }
        } catch (SQLException e) {
            log.error("SQL error - " + e.getMessage());
            return false;
            //e.printStackTrace();
        }
        log.error(user.getNickName() + " - Auth error");
        return false;
    }

    @Override
    public boolean changeNickName(String oldNickName, String newNickName) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT Nickname FROM users WHERE NickName = '" + oldNickName + "'");
            if(resultSet.next()) {
                log.info("User found");

                if (stmt.executeUpdate("UPDATE users SET NickName = '" + newNickName + "' WHERE NickName = '" + oldNickName + "'") > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            log.error("SQL error - " + e.getMessage());
            return false;
            //e.printStackTrace();
        }
        log.error("User not found");
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
