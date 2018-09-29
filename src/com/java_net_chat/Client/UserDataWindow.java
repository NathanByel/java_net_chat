package com.java_net_chat.Client;

import com.java_net_chat.CmdRsp;

import javax.swing.*;
import java.awt.*;

public class UserDataWindow extends JDialog implements ClientUI {
    private JTextField textFieldNickName;

    private JButton buttonSave;
    private ClientController clientController;

    public UserDataWindow(ClientController clientController) {
        this.clientController = clientController;

        setTitle("User Data window");
        setBounds(300, 300, 400, 200);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Никнейм
        Panel nickNamePanel = new Panel();
        nickNamePanel.setLayout(new BoxLayout(nickNamePanel, BoxLayout.X_AXIS));
        //nickNamePanel.setBorder(new EmptyBorder(10, 5, 10, 0));
        nickNamePanel.setMaximumSize( new Dimension(this.getWidth(), 20));

        JLabel captionNickName = new JLabel("Никнейм:");
        captionNickName.setAlignmentX(LEFT_ALIGNMENT);
        nickNamePanel.add(captionNickName);

        textFieldNickName = new JTextField();
        textFieldNickName.setAlignmentX(RIGHT_ALIGNMENT);
        nickNamePanel.add(textFieldNickName);
        add(nickNamePanel);

        // Кнопка
        buttonSave = new JButton();
        buttonSave.setText("Сохранить");
        buttonSave.setAlignmentX(CENTER_ALIGNMENT);
        add(buttonSave);

        // Обработчики
        buttonSave.addActionListener(e -> {
            String nickName = textFieldNickName.getText();
            if((nickName == null) || nickName.isEmpty()) {
                JOptionPane.showMessageDialog(null,"Никнейм не может быть пустым!");
                return;
            }

            if(nickName.contains(" ")) {
                JOptionPane.showMessageDialog(null,"Никнейм не должен содержать пробелы!");
                return;
            }

            clientController.changeNickName(nickName);
        });

        setModal(true);
        setVisible(true);
    }

    @Override
    public void setUsersList(String[] usersList) {}

    @Override
    public void addMessage(String msg) {}

    @Override
    public void statusCallback(CmdRsp s) {}
}
