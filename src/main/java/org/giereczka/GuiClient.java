package org.giereczka;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class GuiClient extends JFrame {
    static String ip = "localhost";
    static int port = 666;
    PrintWriter out;
    BufferedReader in;
    private JTextField betField, numField;

    private final JButton play;
    private JTextArea result;
    private JLabel timer, win;
    private ChatPanel chatPanel;
    ConnectionHandler conn;
    Soundplayer spin = new Soundplayer("/spin.wav");

    public GuiClient() {
        // <editor-fold desc="Elementy gui">
        setTitle("Klient Ruletki");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1280, 720);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        betField = new JTextField(6);
        numField = new JTextField(6);
        play = new JButton("Zagraj");
        play.addActionListener(e-> {
            updateBet(false);
            bet();
        });

        JPanel inputs = new JPanel(new FlowLayout(FlowLayout.LEFT));

        inputs.add(new JLabel("Bet(w $):"));
        inputs.add(betField);
        inputs.add(new JLabel("Liczba: "));
        inputs.add(numField);
        inputs.add(play);


        result = new JTextArea(4,40);
        result.setEditable(false);
        result.setFocusable(false);

        JPanel info = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        info.add(timer = new JLabel("Czas do końca rundy: -- s"));
        info.add(win = new JLabel("Wygrane: 0"));


        result.setFont(new Font("Arial", Font.PLAIN, 12));


        add(inputs, BorderLayout.SOUTH);
        add(new JScrollPane(result), BorderLayout.CENTER);
        result.setBorder(BorderFactory.createTitledBorder("Result"));
        add(info, BorderLayout.EAST);
        // </editor-fold>

        try {
            conn = new ConnectionHandler(ip, port);
            out = conn.getOut();
            in = conn.getIn();

            String welcome = conn.read();
            result.append(welcome + "\n");

            String nick = JOptionPane.showInputDialog(this, "Podaj nick:", "", JOptionPane.PLAIN_MESSAGE);
            if(isValidNick(nick)) {
                out.println("n|" + nick);
                result.append(conn.read() + "\n");
                result.append(conn.read() + "\n");
            } else {
                error("Niepoprawny nick!");
                System.exit(1);
            }
            new Thread(this::listen).start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        chatPanel = new ChatPanel(out);
        add(chatPanel, BorderLayout.WEST);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (conn!=null && !conn.isClosed()) conn.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    System.exit(0);
                }
            }
        });
    }

    private boolean isValidNick(String nick) {
        return nick != null && !nick.isEmpty() && !nick.contains("|") && !nick.contains(":");
    }

    private void listen() {
        try {
            win.setText("Wygrane: 0");
            String l;
            while((l = conn.read()) != null) {
                final String msg = l;
                SwingUtilities.invokeLater(()->processMsg(msg));
            }
        } catch (IOException e) {
            error("Brak połączenia z serwerem!");
        }
    }

    private static final List<String> ignored_prefixList = Arrays.asList(
            "[TIMER]", "[RESULT]", "[CHAT", "c", "[INFO]", "[NEW_ROUND]"
    );

    private void processMsg(String msg) {
        if(msg.startsWith("[TIMER]")) {
           String time = msg.replaceAll("[^0-9]", "");
           try {
               int remainingTime = Integer.parseInt(time);
               timer.setText("Czas do końca rundy: " + remainingTime + "s");
               if (remainingTime == 9) {
                    spin.play();
               }
           } catch (NumberFormatException ignore) {}
        } else if(msg.startsWith("[CHAT]")) {
           String content = msg.substring("[CHAT]".length());
           String parts[] = content.split(":", 2);
           chatPanel.append(parts[0], parts[1]);
           return;
        } else if(msg.startsWith("[NEW_ROUND]")) {
            updateBet(true);
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

    private void updateBet(boolean state) {
        SwingUtilities.invokeLater(()->{
            play.setEnabled(state);
            play.revalidate();
            play.repaint();
        });
    }

    public void bet() {
        String bet = betField.getText().trim();
        String num = numField.getText().trim();
        if(bet.isEmpty() || num.isEmpty()) {
            updateBet(true);
            error("Nieprawidłowy zakład!");
            return;
        }
        try {
            if (Integer.parseInt(bet) < 0 || Integer.parseInt(num) > 36) {
                updateBet(true);
                error("Można postawić tylko na liczbę od 0 do 36!");
                return;
            }
            conn.send("b|"+num+"|"+bet);
            betField.setText("");
            numField.setText("");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            error("Niepoprawne dane!");
        }

    }
    public void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiClient().setVisible(true));
    }
}
