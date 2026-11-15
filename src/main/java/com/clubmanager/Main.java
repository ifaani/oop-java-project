// says which folder this class belongs to
package com.clubmanager;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    public static void main(String[] args) {
        // Imports the java FlatLight skin 
        FlatLightLaf.setup();   // switch on the modern look BEFORE making any window

        // all windows should be built on Swing's thread
        SwingUtilities.invokeLater(() -> {
            // dimensions for the java window 
            JFrame frame = new JFrame("Club Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);   // centers the window on screen
            frame.add(new JLabel("It works!", SwingConstants.CENTER));
            frame.setVisible(true);
        });
    }
}
