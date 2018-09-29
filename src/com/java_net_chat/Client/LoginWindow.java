package com.java_net_chat.Client;

import com.java_net_chat.CmdRsp;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class LoginWindow extends JFrame implements ClientUI {
    private static final String INPUT_NICK_TEXT         = "Введите никнейм...";
    private static final String INPUT_PASS_TEXT         = "Введите пароль...";
    private static final String INPUT_PASS_CONFIRM_TEXT = "Подтверждение пароля...";
    private JTextField nickNameField;
    private JPasswordField passwordField;
    private JPasswordField passwordConfirmField;
    private JButton buttonLogIn;
    private ClientController clientController;

    public LoginWindow(ClientController clientController) {
        this.clientController = clientController;

        setTitle("Вход");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(300, 300, 300, 200);
        setLocationRelativeTo(null);
        setLayout( new BoxLayout(getContentPane(), BoxLayout.Y_AXIS) );

        // Поле ник
        nickNameField = new JTextField();
        nickNameField.setText(INPUT_NICK_TEXT);
        add(nickNameField);

        // Поле пароль
        passwordField = new JPasswordField();
        passwordField.setEchoChar('\u0000'); // Делаем текст видимым
        passwordField.setText(INPUT_PASS_TEXT);
        add(passwordField);

        // Поле подтверждение пароля
        passwordConfirmField = new JPasswordField();
        passwordConfirmField.setEchoChar('\u0000'); // Делаем текст видимым
        passwordConfirmField.setText(INPUT_PASS_CONFIRM_TEXT);
        passwordConfirmField.setVisible(false);
        add(passwordConfirmField);

        // Селектор
        JPanel rButtonsPanel = new JPanel();
        rButtonsPanel.setLayout(new BoxLayout(rButtonsPanel, BoxLayout.X_AXIS));

        ButtonGroup rButtonGroup = new ButtonGroup();
        JRadioButton logInRButton = new JRadioButton("Вход");
        JRadioButton joinRButton = new JRadioButton("Регистрация");
        rButtonGroup.add(logInRButton);
        rButtonGroup.add(joinRButton);
        logInRButton.setSelected(true);
        rButtonsPanel.add(logInRButton);
        rButtonsPanel.add(joinRButton);
        add(rButtonsPanel);

        // Кнопка
        buttonLogIn = new JButton();
        buttonLogIn.setAlignmentX(CENTER_ALIGNMENT);
        buttonLogIn.setText("Войти");
        //buttonLogIn.setFocusPainted(false);
        //buttonLogIn.requestFocusInWindow();
        add(buttonLogIn);

        setVisible(true);

        // Обработчики
        nickNameField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if( nickNameField.getText().equals(INPUT_NICK_TEXT) ) {
                    nickNameField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(nickNameField.getText().isEmpty()) {
                    nickNameField.setText(INPUT_NICK_TEXT);
                }
            }
        });

        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if( new String( passwordField.getPassword() ).equals(INPUT_PASS_TEXT) ) {
                    passwordField.setText("");
                    passwordField.setEchoChar('*');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(passwordField.getPassword().length == 0) {
                    passwordField.setEchoChar('\u0000'); // Делаем текст видимым
                    passwordField.setText(INPUT_PASS_TEXT);
                }
            }
        });

        passwordConfirmField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if( new String( passwordConfirmField.getPassword() ).equals(INPUT_PASS_CONFIRM_TEXT) ) {
                    passwordConfirmField.setText("");
                    passwordConfirmField.setEchoChar('*');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(passwordConfirmField.getPassword().length == 0) {
                    passwordConfirmField.setEchoChar('\u0000'); // Делаем текст видимым
                    passwordConfirmField.setText(INPUT_PASS_CONFIRM_TEXT);
                }
            }
        });

        logInRButton.addActionListener(e -> {
            passwordConfirmField.setVisible(false);
            this.setTitle("Вход");
            this.revalidate();
        });

        joinRButton.addActionListener(e -> {
            passwordConfirmField.setVisible(true);
            this.setTitle("Регистрация");
            this.revalidate();
        });

        buttonLogIn.addActionListener(e -> {
            String nickName = nickNameField.getText();
            String pass = new String( passwordField.getPassword());
            String passConfirm = new String( passwordConfirmField.getPassword());

            if(logInRButton.isSelected()) {
                if (nickName.equals(INPUT_NICK_TEXT) || pass.equals(INPUT_PASS_TEXT)) {
                    JOptionPane.showMessageDialog(null, "Заполните оба поля!");
                    return;
                }
            } else {
                if (nickName.equals(INPUT_NICK_TEXT) || pass.equals(INPUT_PASS_TEXT)) {
                    JOptionPane.showMessageDialog(null, "Заполните оба поля!");
                    return;
                }

                if(!pass.equals(passConfirm)) {
                    JOptionPane.showMessageDialog(null, "Пароль и подтверждение пароля не совпадают!");
                    return;
                }
            }

            nickNameField.setEnabled(false);
            passwordField.setEnabled(false);
            passwordConfirmField.setEnabled(false);
            buttonLogIn.setEnabled(false);
            clientController.logIn(nickName, pass);
        });
    }

    @Override
    public void statusCallback(CmdRsp s) {
        if(s.equals(CmdRsp.RSP_OK_AUTH)) {
            //JOptionPane.showMessageDialog(null,"Вы успешно вошли!");
            this.dispose();
            return;
        } else if(s.equals(CmdRsp.RSP_WRONG_AUTH)) {
            JOptionPane.showMessageDialog(null,"Ошибка авторизации! Не верный логин или пароль!");
        } else if(s.equals(CmdRsp.RSP_NICK_BUSY)) {
            JOptionPane.showMessageDialog(null,"Ошибка авторизации! Пользователь уже в сети!");
        } else if(s.equals(CmdRsp.RSP_AUTH_TIMEOUT)) {
            JOptionPane.showMessageDialog(null,"Таймаут авторизации!");
        } else {
            JOptionPane.showMessageDialog(null,"Ошибка подключения!");
        }

        nickNameField.setEnabled(true);
        passwordField.setEnabled(true);
        passwordConfirmField.setEnabled(true);
        buttonLogIn.setEnabled(true);
    }

    @Override
    public void setUsersList(String[] usersList) {}

    @Override
    public void addMessage(String msg) {}
}
