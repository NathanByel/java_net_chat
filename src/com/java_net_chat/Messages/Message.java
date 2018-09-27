package com.java_net_chat.Messages;

import java.util.UUID;

public abstract class Message {
    protected UUID uuid;
    protected MessageType type;
    protected Object data;

    public Message(MessageType type, Object data) {
        this.uuid = UUID.randomUUID();
        this.type = type;
        this.data = data;
    }

    public UUID getUuid() {
        return uuid;
    }

    public MessageType getType() {
        return type;
    }

    public abstract String serialize();
    public abstract boolean deserialize(String data);
}
