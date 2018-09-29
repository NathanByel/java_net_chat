package com.java_net_chat.Messages;

import java.util.UUID;

public class ChangeNicknameMessage extends Message {
    public ChangeNicknameMessage(String nickName) {
        super(MessageType.CHANGE_NICKNAME_MESSAGE, nickName);
    }

    public ChangeNicknameMessage() {
        this(null);
    }

    public String getNickName() {
        if(super.data instanceof String) {
            return (String) super.data;
        }
        return null;
    }

    @Override
    public String serialize() {
        if(getNickName() != null) {
            return super.uuid + ":" + type + ":" + getNickName();
        }
        return null;
    }

    @Override
    public boolean deserialize(String data) {
        String[] fields = data.split(":", 3);
        if (fields.length != 3) return false;
        if ( MessageType.valueOf(fields[1]) != MessageType.CHANGE_NICKNAME_MESSAGE) return false;

        super.uuid = UUID.fromString(fields[0]);
        super.data = fields[2];
        return true;
    }
}
