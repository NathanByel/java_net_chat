package com.java_net_chat.Messages;

import com.java_net_chat.CmdRsp;

import java.util.UUID;

public class ResponseMessage extends Message {
    public ResponseMessage(CmdRsp rsp) {
        super(MessageType.RESPONSE_MESSAGE, rsp);
    }

    public ResponseMessage() {
        this(null);
    }

    public CmdRsp getRsp() {
        return (CmdRsp) super.data;
    }

    @Override
    public String serialize() {
        if(getRsp() != null) {
            return super.uuid + ":" + type + ":" + super.data;
        }
        return null;
    }

    @Override
    public boolean deserialize(String data) {
        String[] fields = data.split(":", 3);
        if (fields.length != 3) return false;
        if ( MessageType.valueOf(fields[1]) != MessageType.RESPONSE_MESSAGE ) return false;

        super.uuid = UUID.fromString(fields[0]);
        super.data = CmdRsp.valueOf(fields[2]);
        return true;
    }
}
