package org.giereczka;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class GuiClient extends JFrame {
    static String ip = "localhost";
    static int port = 666;
    private Socket s;
    PrintWriter out;
    BufferedReader in;
    private JTextField betField;
    private JTextArea result;
    private JTextField numField;
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

        JPanel info = new JPanel(new GridLayout(1,2));
        info.add(timer = new JLabel("Czas do końca rundy: -- s"));
        info.add(win = new JLabel("Wygrane: 0"));

        add(inputs, BorderLayout.SOUTH);
        add(new JScrollPane(result), BorderLayout.CENTER);
        add(info, BorderLayout.EAST);




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
        out.println("b|"+num+"|"+bet);
        try {
            String response = in.readLine();
            result.append(response);
            betField.setText("");
            numField.setText("");
        } catch (IOException e) {
            error("Błąd komunikacji z serwerem!");
        }
    }
    private void listen() {
        try {
            String l;
            while((l = in.readLine()) != null) {
                final String msg = l;
                SwingUtilities.invokeLater(()->processMsg(msg));
            }
        } catch (IOException e) {
            error("Brak połączenia z serwerem!");
        }
    }

    private void processMsg(String msg) {
        result.append(msg+"\n");
        if(msg.startsWith("[WIN]")) {
            win.setText("Wygrana");
        }
        if(msg.startsWith("[LOSE]")) {
            win.setText("Przegrałeś!");
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiClient().setVisible(true));
    }
}
