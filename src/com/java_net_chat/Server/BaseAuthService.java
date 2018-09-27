package com.java_net_chat.Server;

import com.java_net_chat.User;
import com.java_net_chat.UserInfo;

import java.io.IOException;

public class BaseAuthService implements AuthService {
    @Override
    public void start() {

    }

    @Override
    public UserInfo getUserInfo(User user) {
        return new UserInfo(user.getNickName(), user.getPass()) ;
    }

    @Override
    public boolean checkUser(User user) {
        return true;
    }

    @Override
    public void close() throws IOException {

    }
}
