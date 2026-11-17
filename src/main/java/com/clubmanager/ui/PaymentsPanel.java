package com.clubmanager.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// so this is the Payments page, also a PLACEHOLDER: a styled table with sample rows typed straight into the code, no database behind it yet
public class PaymentsPanel extends JPanel {

    public PaymentsPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // so same top area as the Events page: title on the left, sample data note on the right
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(Theme.title("Payments"), BorderLayout.WEST);

        JLabel note = new JLabel("Sample data - not connected to the database yet");
        note.setFont(Theme.BODY);
        note.setForeground(Theme.MUTED);
        north.add(note, BorderLayout.EAST);
        add(north, BorderLayout.NORTH);

        // so these columns mirror the payment table (payment_id, member_id, amount, paid_date, payment_method, purpose), except we show the member's NAME instead of their member_id because a name means more to a person reading the table
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Member", "Amount (KES)", "Date Paid", "Method", "Purpose"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   // read only
            }
        };

        // so these are the sample rows, the first two match the payments in the club_db.sql script
        model.addRow(new Object[]{1, "Juma Kauma", "500.00", "2026-10-12", "M-Pesa", "Membership fee"});
        model.addRow(new Object[]{2, "Diamond Kisuya", "500.00", "2026-09-16", "Cash", "Membership fee"});
        model.addRow(new Object[]{3, "Juma Kauma", "200.00", "2026-11-01", "M-Pesa", "Event contribution"});

        JTable table = new JTable(model);
        Theme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(60);   // narrow ID column, same as Members

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        add(scroll, BorderLayout.CENTER);
    }
}
