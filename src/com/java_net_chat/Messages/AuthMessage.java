package com.java_net_chat.Messages;

import com.java_net_chat.User;

import java.util.UUID;

public class AuthMessage extends Message {
    public AuthMessage(User user) {
        super(MessageType.AUTH_MESSAGE, user);
    }

    public AuthMessage() {
        this(null);
    }

    public User getUser() {
        if(super.data instanceof User) {
            return (User) super.data;
        }
        return null;
    }

    @Override
    public String serialize() {
        if(getUser() != null) {
            return super.uuid + ":" + type + ":" + getUser().getNickName() + ":" + getUser().getPass();
        }
        return null;
    }

    @Override
    public boolean deserialize(String data) {
        String[] fields = data.split(":", 4);
        if (fields.length != 4) return false;
        if ( MessageType.valueOf(fields[1]) != MessageType.AUTH_MESSAGE ) return false;

        super.uuid = UUID.fromString(fields[0]);
        super.data = new User(fields[2], fields[3]);
        return true;
    }
}
