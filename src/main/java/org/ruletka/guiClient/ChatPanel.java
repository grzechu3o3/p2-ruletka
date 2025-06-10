package org.ruletka.guiClient;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;


public class ChatPanel extends JPanel {
    private final JTextArea chatArea;
    private final JTextField chatInput;
    private final JButton sendButton;
    private final PrintWriter output;

    public ChatPanel(PrintWriter out) {
        super(new BorderLayout());
        this.output = out;

        setBorder(BorderFactory.createTitledBorder("Chat"));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFocusable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 16));

        chatInput = new JTextField(20);
        sendButton = new JButton("Wyślij");

        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(chatInput, BorderLayout.NORTH);
        add(sendButton, BorderLayout.SOUTH);

        chatInput.addActionListener(e -> {
            sendMsg(chatInput.getText());
        });
        sendButton.addActionListener(e->{
            sendMsg(chatInput.getText());
        });
    }
    public void sendMsg(String msg){
        output.println("c|"+msg);
        chatInput.setText("");
    }
    public void append(String nick, String msg)
    {
        chatArea.append(nick.toUpperCase() + ": " + msg + "\n");
    }
}
