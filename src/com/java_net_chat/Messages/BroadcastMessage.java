package com.java_net_chat.Messages;

import java.util.UUID;

public class BroadcastMessage extends Message {
    private String from;
    public BroadcastMessage(String from, String text) {
        super(MessageType.BROADCAST_MESSAGE, text);
        this.from = from;
    }

    public BroadcastMessage() {
        this(null, null);
    }

    public String getText() {
        return (String) super.data;
    }

    public String getFrom() {
        return from;
    }

    @Override
    public String serialize() {
        if( (from != null) && (getText() != null) ) {
            return super.uuid + ":" + type + ":" + from + ":" + getText();
        }
        return null;
    }

    @Override
    public boolean deserialize(String data) {
        String[] fields = data.split(":", 4);
        if (fields.length != 4) return false;
        if ( MessageType.valueOf(fields[1]) != MessageType.BROADCAST_MESSAGE ) return false;

        super.uuid = UUID.fromString(fields[0]);
        from = fields[2];
        super.data = fields[3];
        return true;
    }
}
