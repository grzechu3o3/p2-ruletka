package org.ruletka.guiClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

public class RouletteStrip extends JPanel {
    private List<Integer> numbers = Arrays.asList(
            0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27,
            13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1,
            20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26
    );
    private final JLabel label = new JLabel("Koło", SwingConstants.CENTER);

    private Timer timer;
    private int index = 0;
    private long startTime;
    private int winningNumber;

    public RouletteStrip() {
        setLayout(new BorderLayout());
        label.setOpaque(true);
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        add(label, BorderLayout.CENTER);
    }

    public void spinRoulette(int winningNumber) {
        this.winningNumber = winningNumber;
        index = 0;
        startTime = System.currentTimeMillis();

        if(timer!=null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long timeElapsed = System.currentTimeMillis() - startTime;

                label.setText(String.valueOf(numbers.get(index)));
                index = (index + 1) % numbers.size();

                if(timeElapsed >= 7000 && timeElapsed <= 9000) {
                    int delay = (int) ((timeElapsed - 7000) / 8+50);
                    timer.setDelay(Math.min(delay, 500));
                }
                if(timeElapsed >= 9000) {
                    timer.stop();

                    int finalIndex = numbers.indexOf(winningNumber);
                    if(finalIndex != -1) {
                        label.setText(String.valueOf(numbers.get(finalIndex)));
                    } else label.setText("?");
                }
            }
        });
        timer.start();
    }

}
