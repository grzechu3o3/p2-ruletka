package org.ruletka.guiClient;

import javax.swing.*;
import java.util.List;

public class MessageProcessor {
    private JLabel timerLabel, winLabel;
    private JTextArea resultArea;
    private ChatPanel chatPanel;
    private JButton playButton;
    private Soundplayer spinSound;
    private List<String> ignoredPrefixes;
    private Runnable enableBet;

    public MessageProcessor(JLabel timer, JLabel win, JTextArea res, ChatPanel chat, JButton play, Soundplayer spin, List<String> ignored, Runnable enableBet) {
        this.timerLabel = timer;
        this.winLabel = win;
        this.resultArea = res;
        this.chatPanel = chat;
        this.playButton = play;
        this.spinSound = spin;
        this.ignoredPrefixes = ignored;
        this.enableBet = enableBet;
    }

    public void process(String msg) {
        if(msg.startsWith("[TIMER]")) {
            handleTimer(msg);
        } else if(msg.startsWith("[CHAT]")) {
            handleChat(msg);
        } else if(msg.startsWith("[WIN]")) winLabel.setText("Wygrana!");
        else if(msg.startsWith("[LOSE]")) winLabel.setText("Przegrałeś :(");
        else if(msg.startsWith("[NEW_ROUND]")) {
            handleNewRound();
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
