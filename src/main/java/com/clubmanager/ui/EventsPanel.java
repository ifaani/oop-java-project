package com.clubmanager.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// so this is the Events page, for now it's a PLACEHOLDER: a styled table with sample rows typed straight into the code, it doesn't talk to the database yet
public class EventsPanel extends JPanel {

    public EventsPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // so the top area holds the title on the left and a small gray note on the right that tells people this is sample data
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);   // see-through, so the page background shows
        north.add(Theme.title("Events"), BorderLayout.WEST);

        JLabel note = new JLabel("Sample data - not connected to the database yet");
        note.setFont(Theme.BODY);
        note.setForeground(Theme.MUTED);
        north.add(note, BorderLayout.EAST);
        add(north, BorderLayout.NORTH);

        // so these column names mirror the events table in MySQL (event_id, title, event_date, venue, description)
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Title", "Date", "Venue", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   // read only, nobody can type into the cells
            }
        };

        // so these are the sample rows, the same events as in the club_db.sql script
        model.addRow(new Object[]{1, "RadicalTrips", "2026-12-12", "The mall", "A fun way to travel the world"});
        model.addRow(new Object[]{2, "Nyama zuri", "2026-12-20", "Sarit", "Learn how to make the most delicious meals"});
        model.addRow(new Object[]{3, "Aquaworld", "2026-12-20", "Maasai market", "A wonderful swimming experience"});

        JTable table = new JTable(model);
        Theme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(60);   // narrow ID column, same as Members

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        add(scroll, BorderLayout.CENTER);
    }
}