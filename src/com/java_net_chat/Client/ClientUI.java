package com.java_net_chat.Client;

import com.java_net_chat.CmdRsp;

public interface ClientUI {
    void setUsersList(String[] usersList);
    void addMessage(String msg);
    void statusCallback(CmdRsp s);
}
