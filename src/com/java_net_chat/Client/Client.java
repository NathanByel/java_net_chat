package com.java_net_chat.Client;

import com.java_net_chat.Channel.NetDataChannel;
import com.java_net_chat.CmdRsp;
import com.java_net_chat.Log;
import com.java_net_chat.Messages.*;
import com.java_net_chat.User;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Client implements ClientController {
    private static final String TAG = "CLIENT";
    private final int CONNECT_TRY = 3;
    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;
    private boolean connected = false;
    private boolean subscribed = false;
    private int connectTry = 0;

    private NetDataChannel net;
    private User user;

    private ClientUI clientUI;
    private ClientUI logInUI;

    private Thread receiverThread = null;
    private List<String> usersList = new ArrayList<>();

    private History history;

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        logInUI = new LoginWindow(this);
    }

    private void connectThreadStart() {
        new Thread(() -> {
            while(connectTry < CONNECT_TRY) {
                if (connected) {

                    if(subscribed) {
                        connectTry = 0;
                    }
                    //netSend("<ping>");
                    //if(!subscribed) {
                    //    netSend(CmdRsp.CMD_AUTH + " " + user.getNickName() + " " + user.getPass());
                   // }
                } else {
                    connectToServer();
                    connectTry++;
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            logInUI.statusCallback(CmdRsp.RSP_ERR);
        }).start();
    }

    private void msgReceiverThreadStart() {
        if(receiverThread == null) {
            receiverThread = new Thread(() -> {
                while (true) {
                    if (connected) {
                        Message msg = net.getMessage();
                        if (msg != null) {
                            parseMessage(msg);
                        } else {
                            Log.e(TAG, "getMessage error. Ошибка сети!");
                            net.close();
                            connected = false;
                            subscribed = false;
                        }
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            receiverThread.start();
        }
    }

    private void connectToServer() {
        if(net != null) {
            net.close();
        }

        Log.i(TAG, "Попытка подключения к серверу - " + SERVER_ADDR + ":" + SERVER_PORT);
        try {
            net = new NetDataChannel(SERVER_ADDR, SERVER_PORT);
            Log.i(TAG, "Подключено!");
            net.sendMessage(new AuthMessage(user));
            connected = true;
        } catch (IOException e) {
            Log.e(TAG,"Ошибка сети..." + e.toString());
            subscribed = false;
            connected = false;
        }
    }

    private void parseMessage(Message msg) {
        switch (msg.getType()) {
            case BROADCAST_MESSAGE: {
                    BroadcastMessage m = (BroadcastMessage) msg;
                    String text = m.getFrom() + ": " + m.getText();
                    history.add(text);
                    clientUI.addMessage(text);
                }
                break;

            case PRIVATE_MESSAGE: {
                    PrivateMessage m = (PrivateMessage) msg;
                    String text = "(PM)" + m.getFrom() + ": " + m.getText();
                    history.add(text);
                    clientUI.addMessage(text);
                }
                break;

            case USERS_LIST_MESSAGE: {
                    UsersListMessage m = (UsersListMessage) msg;
                    clientUI.setUsersList(m.getUsers());
                }
                break;

            case INFO_MESSAGE: {
                    InfoMessage m = (InfoMessage) msg;
                    clientUI.addMessage("Info: " + m.getText());
                }
                break;

            case RESPONSE_MESSAGE:
                parseResponse((ResponseMessage) msg);
                break;

            default:
                Log.e(TAG, "Wrong message type");
        }
    }

    private void parseResponse(ResponseMessage msg) {
        switch (msg.getRsp()) {
            case RSP_OK_AUTH:
                subscribed = true;
                logInUI.statusCallback(msg.getRsp());
                if (clientUI == null) {
                    clientUI = new MainWindow(this);
                    history = new History(user.getNickName());
                    List<String> lastHistory = history.getLastRecords(100);
                    if(lastHistory != null) {
                        for (int i = lastHistory.size() - 1; i >= 0; i--) {
                            clientUI.addMessage(lastHistory.get(i));
                        }
                    }
                }
                break;

            case RSP_WRONG_AUTH:
            case RSP_NICK_BUSY:
            case RSP_AUTH_TIMEOUT:
                subscribed = false;
                connectTry = CONNECT_TRY;
                logInUI.statusCallback(msg.getRsp());
                break;

            case RSP_USER_NOT_FOUND:
                clientUI.addMessage("Пользователь не найден.\n\r");
                break;

            case RSP_CHANGE_NICK_ERR:
                JOptionPane.showMessageDialog(null,"Такой пользователь уже существует!");
                break;

            case RSP_CHANGE_NICK_OK:
                JOptionPane.showMessageDialog(null,"Данные сохранены! Войдите заново.");
                System.exit(0);
                break;

            case RSP_OK:
                break;
            //case RSP_USERS_LIST:
                // ПЕРЕДЕЛАТЬ
                /*usersList.clear();
                usersList.addAll(Arrays.asList(cmd.split(" ")));
                usersList.remove(0);// (CmdRsp.RSP_USERS_LIST);

                if (clientUI != null) {
                    clientUI.setUsersList(usersList.toArray(new String[0]));
                }*/
                //break;

            default:
                Log.e(TAG, "Wrong response type - " + msg.getRsp().toString());
        }

    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public List<String> getUsersList() {
        return usersList;
    }

    @Override
    public void logIn(String nickName, String pass) {
        connectTry = 0;
        user = new User(nickName, pass);
        connectThreadStart();
        msgReceiverThreadStart();
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void sendTextMessage(String text) {
        if(text.startsWith("/")) {
            String[] parts = text.split(" ", 2);
            String toUser = parts[0].substring(1);
            if ( (parts.length == 2) /*&& (clientController.getUsersList().contains(toUser))*/ ) {
                text = parts[1];
                sendMessage(new PrivateMessage(user.getNickName(), toUser, text));
                text = "Я(" + user.getNickName() + ")->" + toUser + ": " + text;
                clientUI.addMessage(text);
            } else {
                clientUI.addMessage("Пользователь не найден или не верная команда.\n\r");
                return;
            }
        } else {
            sendMessage(new BroadcastMessage(user.getNickName(), text));
            text = "Я(" + user.getNickName() + "): " + text;
            clientUI.addMessage(text);
        }
        history.add(text);
    }

    private void sendMessage(Message msg) {
        if (connected && subscribed) {
            if( !net.sendMessage(msg) ) {
                Log.e(TAG, "sendMessage Ошибка сети!");
            }
        } else {
            clientUI.addMessage("Нет подключения к серверу!");
            Log.e(TAG, "Нет подключения к серверу!");
        }
    }

    @Override
    public void changeNickName(String nickName) {
        if (connected && subscribed) {
            if( !net.sendMessage( new ChangeNicknameMessage(nickName)) ) {
                Log.e(TAG, "sendMessage Ошибка сети!");
            }
        } else {
            clientUI.addMessage("Нет подключения к серверу!");
            Log.e(TAG, "Нет подключения к серверу!");
        }
    }
}