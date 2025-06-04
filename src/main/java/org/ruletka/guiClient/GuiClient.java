package org.ruletka.guiClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class GuiClient extends JFrame {
    static String ip = "localhost";
    static int port = 666;
    PrintWriter out;
    BufferedReader in;
    private JTextField betField, numField;
    JRadioButton red, black;
    private final JButton play;
    private JTextArea result;
    private JLabel timer, win;
    private ChatPanel chatPanel;
    ConnectionHandler conn;
    Soundplayer spin = new Soundplayer("/spin.wav");
    private MessageProcessor mp;
    private ButtonGroup bg;

    public GuiClient() {
        // <editor-fold desc="Elementy gui">
        setTitle("Klient Ruletki");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1280, 720);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        betField = new JTextField(6);
        numField = new JTextField(6);
        red = new JRadioButton("Red");
        black = new JRadioButton("Black");
        play = new JButton("Zagraj");
        play.addActionListener(e-> {
            updateBet(false);
            bet();
        });

        JPanel inputs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputs.add(new JLabel("Zakład w $:"));
        inputs.add(betField);
        inputs.add(new JLabel("Liczba: "));
        inputs.add(numField);
        inputs.add(new JLabel("Kolory:"));
        bg = new ButtonGroup();
        bg.add(red);
        bg.add(black);
        inputs.add(red);
        inputs.add(black);
        inputs.add(play);

        result = new JTextArea(4,40);
        result.setEditable(false);
        result.setFocusable(false);

        JPanel info = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        info.add(timer = new JLabel("Czas do końca rundy: -- s"));
        info.add(win = new JLabel("Wygrane: 0"));

        RouletteStrip strip = new RouletteStrip();
        result.setFont(new Font("Arial", Font.PLAIN, 12));
        JPanel results = new JPanel();
        results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
        results.add(strip);

        add(inputs, BorderLayout.SOUTH);
        results.add(new JScrollPane(result));
        result.setBorder(BorderFactory.createTitledBorder("Result"));

        add(results, BorderLayout.CENTER);
        add(info, BorderLayout.EAST);
        // </editor-fold>

        try {
            conn = new ConnectionHandler(ip, port);
            out = conn.getOut();
            in = conn.getIn();

            String welcome = conn.read();
            result.append(welcome + "\n");

            String nick = JOptionPane.showInputDialog(this, "Podaj nick:", "", JOptionPane.PLAIN_MESSAGE);
            if(Validator.isValidNick(nick)) {
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

        mp = new MessageProcessor(timer, win, result, chatPanel, play, spin, ignored_prefixList, () -> updateBet(true), strip);

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

    private void listen() {
        try {
            win.setText("Wygrane: 0");
            String l;
            while((l = conn.read()) != null) {
                final String msg = l;
                SwingUtilities.invokeLater(()->mp.process(msg));
            }
        } catch (IOException e) {
            error("Brak połączenia z serwerem!");
        }
    }

    private static final List<String> ignored_prefixList = Arrays.asList("[TIMER]");

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
        boolean isRed = red.isSelected();
        boolean isBlack = black.isSelected();

        if(!Validator.isPositive(bet)) {
            updateBet(true);
            error("Nieprawidłowa kwota zakładu!");
            return;
        }

        if(!num.isEmpty() && (isRed || isBlack)) {
            updateBet(true);
            error("Nie można obstawiać liczby i koloru jednocześnie!");
            return;
        }

        if(num.isEmpty() && !isRed && !isBlack) {
            updateBet(true);
            error("Nie wprowadzono liczby lub nie wybrano koloru!");
            return;
        }

        if(!num.isEmpty()) {
            if(!Validator.isValidBet(num)) {
                updateBet(true);
                error("Nieprawidłowa liczba! Zakres 0-36");
                return;
            }
            conn.send("b|" + num + "|" + bet);
        } else {
            String color = isRed ? "red" : "black";
            conn.send("b|" + color + "|" + bet);
        }

        betField.setText("");
        numField.setText("");
        bg.clearSelection();
    }

    public void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiClient().setVisible(true));
    }
}
