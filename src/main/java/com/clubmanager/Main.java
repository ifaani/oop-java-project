package com.clubmanager;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
import com.clubmanager.ui.MainFrame;
import com.clubmanager.ui.Theme;

public class Main {
    public static void main(String[] args) {
        // Imports the java FlatLight skin 
        FlatLightLaf.setup();   // switch on the modern look BEFORE making any window
        Theme.install(); // calls the function that sets everything
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true)); // creates a new frame window with the specifications in mainframe.java
        
    }
}

// says which folder this class belongs to