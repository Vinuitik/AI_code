package com.example;

import javax.swing.SwingUtilities;
//import com.example.HopfieldUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HopfieldUI(10, 10));
        //System.out.println("null");
    }
}