package com.java_net_chat.Messages;

import java.util.UUID;

public class InfoMessage extends Message {
    public InfoMessage(String text) {
        super(MessageType.INFO_MESSAGE, text);
    }

    public InfoMessage() {
        this(null);
    }

    public String getText() {
        return (String) super.data;
    }

    @Override
    public String serialize() {
        if( getText() != null ) {
            return super.uuid + ":" + type + ":" + getText();
        }
        return null;
    }

    @Override
    public boolean deserialize(String data) {
        String[] fields = data.split(":", 3);
        if (fields.length != 3) return false;
        if ( MessageType.valueOf(fields[1]) != MessageType.INFO_MESSAGE ) return false;

        super.uuid = UUID.fromString(fields[0]);
        super.data = fields[2];
        return true;
    }
}
