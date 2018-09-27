package com.java_net_chat;

public class User {
    private final String nickName;
    private final String pass;

    public User(String nickName, String pass) {
        this.nickName = nickName;
        this.pass = pass;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPass() {
        return pass;
    }

    /*
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    */
}
