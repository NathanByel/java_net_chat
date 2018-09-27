package com.java_net_chat;

public enum CmdRsp {
    CMD_ALIVE,
    CMD_END ,
    CMD_AUTH ,
    CMD_TO_USER,
    CMD_GET_USERS,

    RSP_OK ,
    RSP_ERR,
    RSP_WRONG_CMD,
    RSP_WRONG_PARAM,
    RSP_NEED_AUTH,
    RSP_AUTH_TIMEOUT,
    RSP_OK_AUTH,
    RSP_WRONG_AUTH,
    RSP_NICK_BUSY,
    RSP_USER_NOT_FOUND,
    RSP_USERS_LIST
}
