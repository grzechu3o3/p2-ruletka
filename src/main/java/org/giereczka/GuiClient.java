package org.giereczka;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class GuiClient extends JFrame {
    static String ip = "localhost";
    static int port = 666;
    private Socket s;
    PrintWriter out;
    BufferedReader in;
    private JTextField betField, numField, chatInput;
    private JButton sendButton;
    private JTextArea result, chatArea;
    private JLabel timer, win;


    public GuiClient() {
        setTitle("Klient Ruletki");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLayout(new BorderLayout());

        betField = new JTextField(6);
        numField = new JTextField(6);
        JButton play = new JButton("Zagraj");
        play.addActionListener(e-> bet());

        JPanel inputs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputs.add(new JLabel("Bet(w $):"));
        inputs.add(betField);
        inputs.add(new JLabel("Liczba: "));
        inputs.add(numField);
        inputs.add(play);

        result = new JTextArea(4,40);
        result.setEditable(false);

        JPanel info = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        info.add(timer = new JLabel("Czas do końca rundy: -- s"));
        info.add(win = new JLabel("Wygrane: 0"));

        JPanel chat = new JPanel(new BorderLayout());
        chat.setBorder(BorderFactory.createTitledBorder("Chat"));
        chatArea = new JTextArea(5,20);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chat.add(chatArea, BorderLayout.CENTER);

        chatInput = new JTextField(20);
        chat.add(chatInput, BorderLayout.NORTH);

        sendButton = new JButton("Wyślij");
        chat.add(sendButton, BorderLayout.SOUTH);
        sendButton.addActionListener(e->{
            out.println("c|"+chatInput.getText());
        });

        add(inputs, BorderLayout.SOUTH);
        add(new JScrollPane(result), BorderLayout.CENTER);
        add(info, BorderLayout.EAST);
        add(chat, BorderLayout.WEST);


        try {
            s = new Socket(ip, port);
            out = new PrintWriter(s.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            String sMsg = in.readLine();
            result.append(sMsg + "\n");

            String nick = JOptionPane.showInputDialog(this, "Podaj nick:", "", JOptionPane.PLAIN_MESSAGE);
            if (nick != null && !nick.isEmpty()) {
                out.println("n|"+nick);
                String res = in.readLine();
                result.append(res+"\n");
                res = in.readLine();
                result.append(res+"\n");
            } else {
                error("Błędny nick!");
                System.exit(1);
            }
            new Thread(this::listen).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void bet() {
        String bet = betField.getText().trim();
        String num = numField.getText().trim();
        if(bet.isEmpty() || num.isEmpty()) {
            error("Nieprawidłowy zakład!");
            return;
        }
        try {
            if (Integer.parseInt(bet) < 0 || Integer.parseInt(num) > 36) {
                error("Można postawić tylko na liczbę od 0 do 36!");
                return;
            }
            out.println("b|"+num+"|"+bet);
            betField.setText("");
            numField.setText("");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            error("Niepoprawne dane!");
        }

    }
    private void listen() {
        try {
            win.setText("Wygrane: 0");
            String l;
            while((l = in.readLine()) != null) {
                final String msg = l;
                SwingUtilities.invokeLater(()->processMsg(msg));
            }
        } catch (IOException e) {
            error("Brak połączenia z serwerem!");
        }
    }

    private static final List<String> ignored_prefixList = Arrays.asList(
            "[TIMER]", "[RESULT]", "[CHAT", "c"
    );


    private void processMsg(String msg) {
        if(msg.startsWith("[TIMER]")) {
           String time = msg.replaceAll("[^0-9]", "");
           try {
               int remainingTime = Integer.parseInt(time);
               timer.setText("Czas do końca rundy: " + remainingTime + "s");
           } catch (NumberFormatException ignore) {}
        } else if(msg.startsWith("[CHAT]")) {
            String message = msg.replaceAll("\\[CHAT\\]*", "");
            chatArea.append(message+"\n");
        }
        for(String prefix : ignored_prefixList) {
            if(msg.startsWith(prefix)) {
                return;
            }
        }
        result.append(msg+"\n");
        if(msg.startsWith("[WIN]")) {
            win.setText("Wygrana!");
        }
        if(msg.startsWith("[LOSE]")) {
            win.setText("Przegrałeś :(");
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiClient().setVisible(true));
    }
}
