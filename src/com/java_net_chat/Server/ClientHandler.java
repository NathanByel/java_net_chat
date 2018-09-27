package com.java_net_chat.Server;

import com.java_net_chat.Channel.NetDataChannel;
import com.java_net_chat.CmdRsp;
import com.java_net_chat.Log;
import com.java_net_chat.Messages.*;
import com.java_net_chat.User;
import com.java_net_chat.UserInfo;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private static final String TAG = "CLIENT HANDLER";
    private static final int AUTH_TIMEOUT = 120000;
    private UserInfo userInfo;
    private Server server;

    private long clientLastAliveTime = 0; // Не используется пока
    private long authStartTime = 0;

    private boolean run = true;
    private boolean subscribed = false;
    private boolean authorised = false;

    private NetDataChannel net;

    // *****************************************************************************************************************
    public ClientHandler(Server server, Socket socket) {
        this.server = server;

        try {
            net = new NetDataChannel(socket);
        } catch (IOException e) {
            e.printStackTrace();

            return;
        }

        authStartTime = System.currentTimeMillis();
        new Thread(() -> {
            while(true) {
                Message msg = net.getMessage();
                if(msg == null) {
                    dropClient();
                    return;
                }

                if(msg.getType() == MessageType.AUTH_MESSAGE) {

                    CmdRsp cmdRsp = checkAuth( ((AuthMessage)msg).getUser() );
                    if (cmdRsp == CmdRsp.RSP_OK) {
                        authorised = true;
                        break;
                    } else {
                        Log.e(TAG, "Клиент " + this.hashCode() + " " + cmdRsp + " Необходима регистрация.");
                        net.sendMessage( new ResponseMessage(cmdRsp) );
                        //net.sendMessage( new ResponseMessage(CmdRsp.RSP_NEED_AUTH) );
                        //dropClient();
                        //return;
                    }
                }
            }

            net.sendMessage( new ResponseMessage(CmdRsp.RSP_OK_AUTH) );
            server.subscribe(this);
            subscribed = true;

            /*net.sendMessage( new InfoMessage("Добро пожаловать!\r\n" +
                    "Для отправки личных сообщений используйте формат:\r\n" +
                    "/имя сообщение\r\n") );*/
            net.sendMessage( new InfoMessage("Добро пожаловать!") );
            net.sendMessage( new InfoMessage("Для отправки личных сообщений используйте формат:") );
            net.sendMessage( new InfoMessage("/имя сообщение") );

            while(run) {
                Message msg = net.getMessage();
                if(msg != null) {
                    parseFromMessage(msg);
                } else {
                    dropClient();
                }
            }
        }).start();

        new Thread(() -> {
            while(run) {
                long time = System.currentTimeMillis();

                if(!authorised) {
                    if( (time - authStartTime) > AUTH_TIMEOUT) {
                        net.sendMessage( new ResponseMessage(CmdRsp.RSP_AUTH_TIMEOUT));
                        Log.e(TAG, "Клиент " + this.hashCode() + " отключен. Таймаут авторизации.");
                        dropClient();
                        break;
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private CmdRsp checkAuth(User user) {
        if(     (user == null)
                || user.getNickName().isEmpty()
                || user.getPass().isEmpty() ) return CmdRsp.RSP_WRONG_PARAM;

        if( server.isNickNameBusy(user) ) return CmdRsp.RSP_NICK_BUSY;
        if ( !server.getAuthService().checkUser(user) ) return CmdRsp.RSP_WRONG_AUTH;
        userInfo = server.getAuthService().getUserInfo(user);
        return CmdRsp.RSP_OK;
    }

    /*
    * Обработка сообщений от клиента
    */
    private void parseFromMessage(Message msg) {
        switch (msg.getType()) {
            case ALIVE_MESSAGE:
                clientLastAliveTime = System.currentTimeMillis();
                net.sendMessage(new ResponseMessage(CmdRsp.RSP_OK));
                break;

            case BROADCAST_MESSAGE:
                if(server.sendBroadcastMessage(this, msg)) {
                    net.sendMessage(new ResponseMessage(CmdRsp.RSP_OK));
                } else {
                    net.sendMessage(new ResponseMessage(CmdRsp.RSP_ERR));
                }
                break;

            case PRIVATE_MESSAGE:
                if(server.sendPrivateMessage(this, (PrivateMessage) msg)) {
                    net.sendMessage(new ResponseMessage(CmdRsp.RSP_OK));
                } else {
                    net.sendMessage(new ResponseMessage(CmdRsp.RSP_USER_NOT_FOUND));
                }
                break;

            //case INFO_MESSAGE:
            //    break;

            //case END_CMD:
                //Log.i(TAG, "Клиент " + user.getNickName() + " конец сессии.");
                //dropClient();
            //    break;
            default:
                net.sendMessage(new ResponseMessage(CmdRsp.RSP_WRONG_CMD));
        }
    }

    private void dropClient() {
        Log.i(TAG, "Drop client");
        if(subscribed) {
            server.unsubscribe(this);
        }
        net.close();
        run = false;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public User getUser() {
        return userInfo;
    }

    public void sendMessage(Message msg) {
        net.sendMessage(msg);
    }
}
