package com.java_net_chat.Messages;

import java.util.UUID;

public class PrivateMessage extends Message {
    private String to;
    private String from;

    public PrivateMessage(String from, String to, String text) {
        super(MessageType.PRIVATE_MESSAGE, text);
        this.from = from;
        this.to = to;
    }

    public PrivateMessage() {
        this(null, null, null);
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getText() {
        return (String) super.data;
    }

    @Override
    public String serialize() {
        if( (from != null) && (to != null) && (getText() != null) ) {
            return super.uuid + ":" + type + ":" + from + ":" + to + ":" + getText();
        }
        return null;
    }

    @Override
    public boolean deserialize(String data) {
        String[] fields = data.split(":", 5);
        if (fields.length != 5) return false;
        if ( MessageType.valueOf(fields[1]) != MessageType.PRIVATE_MESSAGE ) return false;

        super.uuid = UUID.fromString(fields[0]);
        from = fields[2];
        to = fields[3];
        super.data = fields[4];
        return true;
    }
}
