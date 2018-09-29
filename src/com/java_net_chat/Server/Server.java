package com.java_net_chat.Server;

import com.java_net_chat.Log;
import com.java_net_chat.Messages.*;
import com.java_net_chat.User;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.java_net_chat.Log.ANSI_BLUE;
import static com.java_net_chat.Log.ANSI_GREEN;
import static com.java_net_chat.Log.ANSI_RED;

public class Server {
    private static final String TAG = "SERVER";
    private static final int PORT = 8189;
    //private static final int clientTimeout = 10000;
    private List<ClientHandler> clients = new ArrayList<>();
    private AuthService authService;
    // *****************************************************************************************************************
    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        try ( ServerSocket serverSocket = new ServerSocket(PORT);
              AuthService authService = new SQLiteAuthService() ) {

            this.authService = authService;
            authService.start();
            Log.i(TAG, "Сервер запущен.");

            while(true) {
                Socket socket = serverSocket.accept();
                Log.i(TAG, "Сокет " + socket.hashCode() + " подключен. Ожидание авторизации.");
                ClientHandler client = new ClientHandler(this, socket);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка сервера! " + e.toString());
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);

        sendBroadcastMessage(clientHandler, new BroadcastMessage(clientHandler.getUser().getNickName(),
                                                            clientHandler.getUser().getNickName() + " зашел в чат!") );
        sendUserList(null);
        Log.i(TAG, "Клиент " + clientHandler.getUser().getNickName() + " залогинился. Всего - " + clients.size());
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        sendBroadcastMessage(clientHandler, new BroadcastMessage(clientHandler.getUser().getNickName(),
                                                            clientHandler.getUser().getNickName() + " вышел из чата!") );
        sendUserList(null);
        Log.i(TAG, "Клиент " + clientHandler.hashCode() + " отключен. Всего - " + clients.size());
    }

    public boolean isNickNameBusy(User user) {
        for (ClientHandler client : clients) {
            if (client.getUser().getNickName().equals( user.getNickName() )) {
                return true;
            }
        }
        return false;
    }

    public boolean sendBroadcastMessage(ClientHandler fromClient, Message msg) {
        String nickName = fromClient.getUser().getNickName();
        Log.i(TAG, "Msg send " + ANSI_BLUE + nickName + ANSI_GREEN + " -> ALL");

        for(ClientHandler toClient: clients) {
            if(toClient != fromClient) {
                toClient.sendMessage(msg);
            }
        }
        return true;
    }

    public boolean sendPrivateMessage(ClientHandler clientHandler, PrivateMessage msg) {
        //String nickName = clientHandler.getUser().getNickName();
        Log.i(TAG, "Msg send " + ANSI_BLUE + msg.getFrom() + ANSI_GREEN + " -> "  + ANSI_BLUE + msg.getTo());
        for(ClientHandler client: clients) {
            if( client.getUser().getNickName().equals(msg.getTo()) ) {
                client.sendMessage(msg);
                return true;
            }
        }
        Log.e(TAG, "User " + ANSI_BLUE + msg.getTo() + ANSI_RED + " not found");
        return false;
    }

    public void sendUserList(ClientHandler clientHandler) {
        List<String> users = new ArrayList<>();
        for(ClientHandler client: clients) {
            users.add(client.getUser().getNickName());
        }

        if(clientHandler != null) {
            clientHandler.sendMessage(new UsersListMessage( users.toArray(new String[0])) );
        } else {
            for(ClientHandler client: clients) {
                client.sendMessage( new UsersListMessage( users.toArray(new String[0])) );
            }
        }
    }

    public AuthService getAuthService() {
        return authService;
    }
}
