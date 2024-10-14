package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent; // ANYA IS AMAZING!!!!!
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HopfieldUI extends JFrame {
    private Matrix matrix;
    private JButton[][] buttons;
    private int rows;
    private int cols;
    private JButton saveButton, deleteButton, trainButton, useButton, randomizeButton;
    private mongoDb db;
    private HopfieldNetwork network;

    public HopfieldUI(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        matrix = new Matrix(rows, cols, -1);
        buttons = new JButton[rows][cols];
        db = new mongoDb();
        network = new HopfieldNetwork(rows, cols);

        setTitle("Hopfield Network UI");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(rows, cols));
        initializeButtons(gridPanel);
        add(gridPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        initializeSaveButton();
        controlPanel.add(saveButton);
        initializeDeleteButton();
        controlPanel.add(deleteButton);
        initializeTrainButton();
        controlPanel.add(trainButton);
        initializeUseButton();
        controlPanel.add(useButton);
        initializeRandomizeButton(); // Add the Randomize button here
        controlPanel.add(randomizeButton); // Add the Randomize button to the control panel
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void initializeSaveButton() {
        saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(150, 50));
        saveButton.setBackground(Color.GREEN);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 16));
        saveButton.addActionListener(e -> saveMatrixToDB());
    }

    private void initializeDeleteButton() {
        deleteButton = new JButton("Delete All Matrices");
        deleteButton.setPreferredSize(new Dimension(200, 50));
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteButton.addActionListener(e -> {
            db.deleteAll();
            db.clearWeights();
        });
    }

    private void initializeTrainButton() {
        trainButton = new JButton("Train");
        trainButton.setPreferredSize(new Dimension(150, 50));
        trainButton.setBackground(Color.ORANGE);
        trainButton.setForeground(Color.WHITE);
        trainButton.setFont(new Font("Arial", Font.BOLD, 16));
        trainButton.addActionListener(e -> network.train(matrix));
    }

    private void initializeUseButton() {
        useButton = new JButton("Use");
        useButton.setPreferredSize(new Dimension(150, 50));
        useButton.setBackground(Color.BLUE);
        useButton.setForeground(Color.WHITE);
        useButton.setFont(new Font("Arial", Font.BOLD, 16));
        useButton.addActionListener(e -> use());
    }

    // New method to initialize the Randomize button
    private void initializeRandomizeButton() {
        randomizeButton = new JButton("Randomize");
        randomizeButton.setPreferredSize(new Dimension(150, 50));
        randomizeButton.setBackground(Color.MAGENTA);
        randomizeButton.setForeground(Color.WHITE);
        randomizeButton.setFont(new Font("Arial", Font.BOLD, 16));
        randomizeButton.addActionListener(e -> randomise());
    }

    private void saveMatrixToDB() {
        db.saveMatrixToDB(matrix.getMatrix());
    }

    private void initializeButtons(JPanel gridPanel) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setBackground(Color.WHITE);

                final int row = i;
                final int col = j;
                buttons[i][j].addActionListener(e -> toggleCell(row, col));

                gridPanel.add(buttons[i][j]);
            }
        }
    }

    private void toggleCell(int row, int col) {
        int value = (int) matrix.getMatrix()[row][col];
        if (value == -1) {
            buttons[row][col].setBackground(Color.BLACK);
        } else {
            buttons[row][col].setBackground(Color.WHITE);
        }
        matrix.setValue(row, col, value * (-1));
    }

    private void toggleCell(int i) {
        int row = i / cols;
        int col = i % cols;
        toggleCell(row, col);
    }

    public void use() {
        // The randomise() method call is removed from here
        
        ArrayList<Integer> indexList = new ArrayList<>();
        fill(indexList);
        Collections.shuffle(indexList); // Shuffle the list here
        System.out.println("Shuffled:");
    
        // Create a Swing Timer to handle the updates
        Timer timer = new Timer(10, null);  // Adjust the delay (in milliseconds) as needed
        timer.addActionListener(new ActionListener() {
            int i = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (i < indexList.size()) {
                    try {
                        int index = indexList.get(i);
                        double voteResult = network.vote(index, matrix);
                        if (voteResult < 0 && matrix.get(index) == 1.0) {
                            toggleCell(index);
                        } else if (voteResult > 0 && matrix.get(index) == -1.0) {
                            toggleCell(index);
                        }
                        i++;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    ((Timer) e.getSource()).stop(); // Stop the timer when done
                    System.out.println("Processing done!");
                }
            }
        });
        timer.start();  // Start the timer to begin updates
    }

    public void myWait() {
        try {
            Thread.sleep(1); // Adjust delay for visualization
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void fill(ArrayList<Integer> list) {
        int total = rows * cols;
        for (int i = 0; i < total; ++i) {
            list.add(i);
        }
    }

    public void randomise() {
        Random random = new Random();
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                double randomDouble = random.nextDouble();
                if (randomDouble >= 0.5) {
                    toggleCell(i, j);
                }
            }
        }
    }
}
