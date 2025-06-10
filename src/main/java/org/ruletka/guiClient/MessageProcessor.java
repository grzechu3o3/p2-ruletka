package org.ruletka.guiClient;

import javax.swing.*;
import java.util.List;
import java.util.TimerTask;

public class MessageProcessor {
    private JLabel timerLabel;
    private JTextArea resultArea;
    private ChatPanel chatPanel;
    private JButton playButton;
    private Soundplayer spinSound;
    private List<String> ignoredPrefixes;
    private Runnable enableBet;
    private RouletteStrip strip;
    LastWin lw;

    public MessageProcessor(JLabel timer, JTextArea res, ChatPanel chat, JButton play, Soundplayer spin, List<String> ignored, Runnable enableBet, RouletteStrip strip, LastWin lastWin) {
        this.timerLabel = timer;
        this.resultArea = res;
        this.chatPanel = chat;
        this.playButton = play;
        this.spinSound = spin;
        this.ignoredPrefixes = ignored;
        this.enableBet = enableBet;
        this.strip = strip;
        this.lw = lastWin;
    }

    public void process(String msg) {
        if(msg.startsWith("[TIMER]")) {
            handleTimer(msg);
        } else if(msg.startsWith("[CHAT]")) {
            handleChat(msg);
        } else if(msg.startsWith("[WIN]")) {
            JOptionPane.showMessageDialog(null, msg, "Wygrana", JOptionPane.INFORMATION_MESSAGE);
        } else if(msg.startsWith("[LOSE]")) {
            JOptionPane.showMessageDialog(null, msg, "Przegrana", JOptionPane.INFORMATION_MESSAGE);
        }
        else if(msg.startsWith("[NEW_ROUND]")) {
            handleNewRound();
        } else if(msg.startsWith("[RESULT]")) {
            int winning_number = Integer.parseInt(msg.substring("[RESULT]".length()).trim());
            strip.spinRoulette(winning_number);
            lw.addWin(winning_number);
        } else if(msg.startsWith("[ERROR]")) {
            enableBet.run();
        } else if(isIgnored(msg)) return;
        else resultArea.append(msg + "\n");

    }

    private void handleTimer(String msg) {
        String time = msg.replaceAll("[^0-9]", "");
        try {
            int remainingTime = Integer.parseInt(time);
            timerLabel.setText("Czas rundy: " + remainingTime + "s");
            if(remainingTime == 9 && spinSound != null) spinSound.play();
        } catch (NumberFormatException ignored) {};
    }

    private void handleChat(String msg) {
        String content = msg.substring("[CHAT]".length());
        String[] parts = content.split(":",2);
        chatPanel.append(parts[0], parts[1]);
    }

    private void handleNewRound() {
        enableBet.run();
    }

    private boolean isIgnored(String msg) {
        for(String prefix : ignoredPrefixes) {
            if (msg.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
