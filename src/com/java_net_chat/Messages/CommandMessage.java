package com.java_net_chat.Messages;

public class CommandMessage extends Message {
    public CommandMessage() {
        super(MessageType.COMMAND_MESSAGE, null);
    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public boolean deserialize(String data) {
        return false;
    }
}
