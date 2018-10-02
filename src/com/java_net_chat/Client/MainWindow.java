package com.java_net_chat.Client;

import com.java_net_chat.CmdRsp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame implements ClientUI {
    private JTextArea textAreaChat;
    private JTextArea textAreaUsersList;
    private JTextField textFieldSend;
    private JButton buttonSend;
    private ClientController clientController;


    public MainWindow(ClientController clientController) {
        this.clientController = clientController;

        setTitle("Main window");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(300, 300, 400, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        textAreaChat = new JTextArea();
        textAreaChat.setEditable(false);
        add(textAreaChat, BorderLayout.CENTER);

        textAreaUsersList = new JTextArea();
        textAreaUsersList.setEditable(false);
        textAreaUsersList.setBackground(Color.LIGHT_GRAY);
        add(textAreaUsersList, BorderLayout.LINE_END);
        textAreaUsersList.append("Users:\n\r");

        // Панель с текстовым полем для отправки и кнопкой
        JPanel panelSend = new JPanel();
        panelSend.setLayout(new BorderLayout());

        textFieldSend = new JTextField();
        panelSend.add(textFieldSend, BorderLayout.CENTER);

        buttonSend = new JButton();
        buttonSend.setText("Отправить");
        panelSend.add(buttonSend, BorderLayout.LINE_END);

        add(panelSend, BorderLayout.PAGE_END);
        setVisible(true);

        // Меню
        JMenuBar mainMenuBar = new JMenuBar();
        mainMenuBar.add(createFileMenu());
        mainMenuBar.add(createConfigMenu());
        setJMenuBar(mainMenuBar);

        // Обработчики
        textFieldSend.addActionListener(e -> sendText());
        buttonSend.addActionListener(e -> sendText());
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Файл");
        return fileMenu;
    }

    private JMenu createConfigMenu() {
        JMenu configMenu = new JMenu("Настройки");
        JMenuItem userData = new JMenuItem("Данные пользователя");
        configMenu.add(userData);

        userData.addActionListener(e -> new UserDataWindow(clientController));
        return configMenu;
    }

    private void sendText() {
        String msg = textFieldSend.getText().trim();
        textFieldSend.setText("");

        if(msg.length() > 0) {
            clientController.sendTextMessage(msg);
        }
    }

    @Override
    public void setUsersList(String[] usersList) {
        textAreaUsersList.setText("Users:\n\r");
        for(String user: usersList) {
            textAreaUsersList.append(user + "\n\r");
        }
    }

    @Override
    public void addMessage(String msg) {
        textAreaChat.append(msg + "\n\r");
    }

    @Override
    public void statusCallback(CmdRsp s) {}
}
