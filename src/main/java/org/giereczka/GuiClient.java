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

        result = new JTextArea();
        result.setEditable(false);


        add(inputs, BorderLayout.SOUTH);
        add(new JScrollPane(result), BorderLayout.NORTH);




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

    private void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiClient().setVisible(true));
    }
}
