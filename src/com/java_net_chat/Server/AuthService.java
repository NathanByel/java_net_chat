package com.java_net_chat.Server;

import com.java_net_chat.User;
import com.java_net_chat.UserInfo;

import java.io.Closeable;

public interface AuthService extends Closeable {
    void start();
    boolean checkUser(User user);
    boolean changeNickName(String oldNickName, String newNickName);
    UserInfo getUserInfo(User user);
}
