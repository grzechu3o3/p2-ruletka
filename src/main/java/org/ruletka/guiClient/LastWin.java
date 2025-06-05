package org.ruletka.guiClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class LastWin extends JPanel {
    public static final int MAX_SIZE=7;
    LinkedList<Integer> wins = new LinkedList<Integer>();
    JLabel lastWin;

    public LastWin() {
        lastWin = new JLabel("Ostatnie wygrane: ");
        add(lastWin);
    }

    public void addWin(int num) {
        Timer delay = new Timer(9000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(wins.size()==MAX_SIZE) {
                    wins.remove(0);
                }
                wins.add(num);
                colorLabel(wins);
            }
        });
        delay.setRepeats(false);
        delay.start();
    }

    public void colorLabel(LinkedList<Integer> wins) {
        StringBuilder sb = new StringBuilder("<html>Ostatnie wygrane: ");
        for(Integer num : wins) {
            Color c = getColor(num);
            String color = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            sb.append("<span style='color:").append(color).append(";'>").append(num).append("</span> ");

        }
        sb.append("</html>");
        lastWin.setText(sb.toString());
    }

    private Color getColor(int num) {
        if (num == 0) return Color.GREEN.darker();
        int[] reds = {1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36};
        for (int r : reds) {
            if (r == num) return Color.RED;
        }
        return Color.BLACK;
    }
}
