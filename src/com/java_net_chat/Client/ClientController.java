package com.java_net_chat.Client;

import com.java_net_chat.User;

import java.util.List;

public interface ClientController {
    void disconnect();
    void sendTextMessage(String text);
    void logIn(String nickName, String pass);
    void changeNickName(String nickName);
    User getUser();
    List<String> getUsersList();
}
