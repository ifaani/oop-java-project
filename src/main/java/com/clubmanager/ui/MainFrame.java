package com.clubmanager.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import com.clubmanager.dao.MemberDAOJdbc;
import com.clubmanager.dao.MemberDAO;


public class MainFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);   // the stack of pages
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private String activePage = "";

    // defines the dimensions of the window
    public MainFrame() {
        setTitle("Club Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        MemberDAO memberDao = new MemberDAOJdbc();   // real MySQL waiter
        content.setBackground(Theme.BACKGROUND);
        content.add(new MembersPanel(memberDao), "members");

        // This is the card that communicates with the dao to enable CRUD operations to run on the database 
        content.setBackground(Theme.BACKGROUND); 
        content.add(new EventsPanel(), "events");
        content.add(new PaymentsPanel(), "payments");
        add(content, BorderLayout.CENTER);

        //page that runs when the app starts
        showPage("members");   // the page shown when the app starts
    }

    // this builds a sidebar for interacting with the UI and sets its dimensions
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));   // stack things top to bottom
        sidebar.setBackground(Theme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 12, 24, 12));

        // Specifies the label and font to be used in the sidebar
        JLabel brand = new JLabel("Club Manager");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand.setForeground(Color.WHITE);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 8, 24, 0));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brand);

        // navigation buttons for moving from one page(card) to another
        addNavButton(sidebar, "Members", "members");
        addNavButton(sidebar, "Events", "events");
        addNavButton(sidebar, "Payments", "payments");

        return sidebar;
    }

    // so this styles the navigation buttons to all look the same, kinda like the class for the buttons, and the code buttons above are the objects
    private void addNavButton(JPanel sidebar, String text, String pageName) {
        JButton button = new JButton(text);
        button.setFont(Theme.BOLD);
        button.setForeground(Color.WHITE);
        button.setBackground(Theme.SIDEBAR);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMargin(new Insets(12, 16, 12, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        // The "routing": clicking the button shows that page
        button.addActionListener(e -> showPage(pageName));

        // Hover effect, when hovering the mouse, it changes color of a button, equivalent of mouseover in javascript
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!pageName.equals(activePage)) button.setBackground(Theme.SIDEBAR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!pageName.equals(activePage)) button.setBackground(Theme.SIDEBAR);
            }
        });

        // So this one creates the buttons in key value pairs, so its like "Members: " "button for members page", and they're placed side by side
        navButtons.put(pageName, button);
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(4));   // small gap between buttons
    }

    // Routing logic code
    private void showPage(String pageName) {
        cards.show(content, pageName);   // this function switches to that specified cardcard
        activePage = pageName;
        // Highlight the active button blue when the mouse hovers, reset the others
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean active = entry.getKey().equals(pageName);
            entry.getValue().setBackground(active ? Theme.ACCENT : Theme.SIDEBAR);
        }
    }
}

// So this file creates the window for the GUI and how it'll look like, menu on the left and cards on the right