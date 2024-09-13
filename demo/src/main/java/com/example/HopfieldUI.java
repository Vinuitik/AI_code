package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent; // ANYA IS AMAZING!!!!!
import java.awt.event.ActionListener;

public class HopfieldUI extends JFrame {
    private Matrix matrix;
    private JButton[][] buttons;
    private int rows;
    private int cols;

    public HopfieldUI(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        matrix = new Matrix(rows, cols);
        buttons = new JButton[rows][cols];

        setTitle("Hopfield Network UI");
        setSize(600,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(rows,cols));
        initializeButtons();

        setVisible(true);

    }

    private void initializeButtons() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setBackground(Color.WHITE);

                final int row = i;
                final int col = j;
                buttons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toggleCell(row, col);
                    }
                });

                add(buttons[i][j]);
            }
        }
    }

    private void toggleCell(int row, int col) {
        int value = (int)matrix.getMatrix()[row][col];
        if(value==0){
            buttons[row][col].setBackground(Color.BLACK);
        }
        else{
            buttons[row][col].setBackground(Color.WHITE);
        }
        matrix.setValue(row, col, (value+1)%2);
    }
}
