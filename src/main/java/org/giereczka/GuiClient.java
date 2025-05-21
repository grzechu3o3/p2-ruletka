package org.giereczka;

import javax.swing.*;
import java.awt.*;

public class GuiClient {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Ruletka");
        JPanel login = new JPanel();
        JLabel nick = new JLabel("Nick: ");
        JTextField nickname = new JTextField();
        nickname.setPreferredSize(new Dimension(100, 30));
        JButton loginButton = new JButton("Login");

        login.add(nick);
        login.add(nickname);
        login.add(loginButton);

        loginButton.addActionListener(e -> {
            new RuletkaClient("127.0.0.1", 666);
        });

        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.CENTER;
        frame.add(login);
        frame.setVisible(true);
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
