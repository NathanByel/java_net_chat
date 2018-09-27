package com.java_net_chat.Channel;

import com.java_net_chat.Messages.Message;

import java.io.Closeable;

public interface DataChannel extends Closeable {
    void open();
    boolean sendMessage(Message msg);
    Message getMessage();
}
