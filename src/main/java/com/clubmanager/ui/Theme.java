package com.clubmanager.ui;

// importing all dependencies from the respective libraries
import javax.swing.*;
import java.awt.*;


public class Theme {

    // this sets the colors in hex codes so the app looks presentable, static enables it to be for the entire project cause it's class-relevant not object relevant
    public static final Color SIDEBAR       = new Color(0x1E3A5F);  // dark navy
    public static final Color SIDEBAR_HOVER = new Color(0x2C4F7C);  // lighter navy
    public static final Color BACKGROUND    = new Color(0xF4F6F9);  // very light gray
    public static final Color CARD          = Color.WHITE;
    public static final Color ACCENT        = new Color(0x2563EB);  // blue
    public static final Color DANGER        = new Color(0xDC2626);  // red
    public static final Color TEXT          = new Color(0x1F2937);  // near-black
    public static final Color MUTED         = new Color(0x6B7280);  // gray text
    public static final Color BORDER        = new Color(0xE5E7EB);  // light gray lines

    // Sets the font and it's sizes
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font BODY  = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD  = new Font("Segoe UI", Font.BOLD, 14);

    // so for this method, every thime the code runs this function is called to apply the fonts and the sizes, as well as rounding the corners of the buttons
    public static void install() {
        UIManager.put("defaultFont", BODY);       // every component uses our font
        UIManager.put("Button.arc", 10);          // rounded corners on buttons
        UIManager.put("Component.arc", 10);       // rounded corners on dropdowns etc.
        UIManager.put("TextComponent.arc", 10);   // rounded corners on text boxes
    }

    // This method receives text, places it into a Jlabel, styles it and returns it

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE);
        label.setForeground(TEXT);
        return label;
    }

    // Creates a small grey label that sits just above every input field
    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BOLD);
        label.setForeground(MUTED);
        return label;
    }

    // so this particular method, it calls another method for styling the buttons, then passes paramters into it, so for example, primary button is add, this method calls the method for styling add and passes white as its color and Accent ad its intensity, its kinda like a constructor
    public static JButton primaryButton(String text) {
        return styledButton(text, ACCENT, Color.WHITE);
    }

    public static JButton dangerButton(String text) {
        return styledButton(text, DANGER, Color.WHITE);
    }

    public static JButton secondaryButton(String text) {
        return styledButton(text, BORDER, TEXT);
    }

    //So this creates the card, basically a white box with inner paddings and it basically styles them
    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    // So now this is the method that styles the buttons, this is the method called by the other three above, it styles each button using the parameters defined in the three methods above
    private static JButton styledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(BOLD);
        button.setFocusPainted(false);
        button.setMargin(new Insets(10, 20, 10, 20));   // inner padding
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}


// So this file is for keeping the app's theme, colors and fonts consistent all through