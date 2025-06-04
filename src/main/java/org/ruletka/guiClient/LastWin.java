package org.ruletka.guiClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class LastWin extends JPanel {
    public static final int MAX_SIZE=7;
    List<Integer> wins = new ArrayList<Integer>();
    private DefaultTableModel model;
    private JTable table;


    public LastWin() {


        model = new DefaultTableModel(new Object[]{"Wygrane liczby"},0);
        table = new JTable(model);

        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }

    public void addWin(int num) {
        if(wins.size()==MAX_SIZE) {
            wins.remove(0);
        }
        wins.add(num);
        updateTable();
    }

    private void updateTable() {
        model.setRowCount(0); // wyczyść tabelę
        for (Integer win : wins) {
            model.addRow(new Object[]{win});
        }
    }
}
