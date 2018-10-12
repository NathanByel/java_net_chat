package com.java_net_chat.Server;

import com.java_net_chat.Channel.NetDataChannel;
import com.java_net_chat.CmdRsp;
import com.java_net_chat.Log;
import com.java_net_chat.Messages.*;
import com.java_net_chat.User;
import com.java_net_chat.UserInfo;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    private static final String TAG = "CLIENT HANDLER";
    private static final int AUTH_TIMEOUT = 120000;
    private UserInfo userInfo;
    private Server server;
    private ClientHandler THIS = this;

    private long clientLastActiveTime = 0;

    private boolean subscribed = false;
    private boolean authorised = false;

    private ExecutorService executorService;
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

        executorService = Executors.newFixedThreadPool(3);
        executorService.execute(timeOutTask);

        try {
            if( executorService.submit(authTask).get() ) {
                executorService.execute(messageReceiverTask);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dropClient();
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
                clientLastActiveTime = System.currentTimeMillis();
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

            case CHANGE_NICKNAME_MESSAGE:
                if( server.getAuthService().changeNickName(this.userInfo.getNickName(), ((ChangeNicknameMessage)msg).getNickName())) {
                    net.sendMessage(new ResponseMessage(CmdRsp.RSP_CHANGE_NICK_OK));
                } else {
                    net.sendMessage(new ResponseMessage(CmdRsp.RSP_CHANGE_NICK_ERR));
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
        net.close();
        if(subscribed) {
            server.unsubscribe(this);
        }
        executorService.shutdownNow();
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

    // Таски
    private Callable<Boolean> authTask = new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            while(true) {
                Message msg = net.getMessage();
                if(msg == null) {
                    dropClient();
                    return false;
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
            server.subscribe(THIS);
            subscribed = true;

            net.sendMessage( new InfoMessage("Добро пожаловать!") );
            net.sendMessage( new InfoMessage("Для отправки личных сообщений используйте формат:") );
            net.sendMessage( new InfoMessage("/имя сообщение") );
            return true;
        }
    };

    private Runnable messageReceiverTask = new Runnable() {
        @Override
        public void run() {
            while(true) {
                Message msg = net.getMessage();
                if(msg != null) {
                    parseFromMessage(msg);
                } else {
                    dropClient();
                    break;
                }
            }
        }
    };

    private Runnable timeOutTask = new Runnable() {
        @Override
        public void run() {
            clientLastActiveTime = System.currentTimeMillis();
            while(true) {
                long time = System.currentTimeMillis();

                if(!authorised) {
                    if( (time - clientLastActiveTime) > AUTH_TIMEOUT) {
                        net.sendMessage( new ResponseMessage(CmdRsp.RSP_AUTH_TIMEOUT));
                        Log.e(TAG, "Клиент " + this.hashCode() + " отключен. Таймаут авторизации.");
                        dropClient();
                        break;
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    };
}
