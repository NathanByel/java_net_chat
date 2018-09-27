package com.java_net_chat.Messages;

import java.util.UUID;

public class UsersListMessage extends Message {
    public UsersListMessage(String[] users) {
        super(MessageType.USERS_LIST_MESSAGE, users);
    }

    public UsersListMessage() {
        this(null);
    }

    public String[] getUsers() {
        return (String[]) super.data;
    }

    @Override
    public String serialize() {
        if(getUsers() != null) {
            String[] users = (String[]) super.data;
            StringBuilder text = new StringBuilder();
            for(String user: users) {
                text.append(user);
                text.append(" ");
            }
            return super.uuid + ":" + type + ":" + text.toString();
        }
        return null;
    }

    @Override
    public boolean deserialize(String data) {
        String[] fields = data.split(":", 3);
        if (fields.length != 3) return false;
        if ( MessageType.valueOf(fields[1]) != MessageType.USERS_LIST_MESSAGE ) return false;

        super.uuid = UUID.fromString(fields[0]);
        super.data = fields[2].split(" ");
        return true;
    }
}
