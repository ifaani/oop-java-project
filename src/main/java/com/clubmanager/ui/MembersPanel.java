package com.clubmanager.ui;

import com.clubmanager.dao.MemberDAO;
import com.clubmanager.model.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
// imports already talked about in previous files

public class MembersPanel extends JPanel {

    private final MemberDAO dao;               // the dao that communicates to mysql
    private final DefaultTableModel tableModel;
    private final JTable table;

    // Form fields corresponding to table attributes
    private final JTextField nameField   = new JTextField();
    private final JTextField yearField   = new JTextField();
    private final JTextField courseField = new JTextField();
    private final JTextField emailField  = new JTextField();
    private final JTextField dateField   = new JTextField();
    private final JTextField groupField  = new JTextField();

    
    public MembersPanel(MemberDAO dao) {
        this.dao = dao;
        // so when a user is created, this constructor takes their data in a variable "MemberDAO" and showcases it in the table
        
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        // so this styles the page that displays all the members IN TABLE FORMAT

        // NORTH styling
        add(Theme.title("Members"), BorderLayout.NORTH);

        // TABLE styling
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Year", "Course", "Email", "Date Joined", "Group"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   // stop people typing directly into table cells
            }
        };
        table = new JTable(tableModel);
        styleTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        add(scroll, BorderLayout.CENTER);

        // FORM styling
        JPanel east = new JPanel(new BorderLayout());
        east.setOpaque(false);   // see-through, so the page background shows
        east.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        east.add(buildForm(), BorderLayout.NORTH);
        add(east, BorderLayout.EAST);

        clearForm();
        loadMembers();   // fill the table when the page opens
    }

    // So this code snippet actually builds the table that displays all member data and makes it functional, like scrolling up goes up, down goes down etc etc

    private void styleTable() {
        table.setFont(Theme.BODY);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);   // one row at a time
        table.setSelectionBackground(new Color(0xDBEAFE));             // soft blue highlight
        table.setSelectionForeground(Theme.TEXT);
        table.getTableHeader().setFont(Theme.BOLD);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setMaxWidth(60);           // narrow ID column
        table.getColumnModel().getColumn(2).setMaxWidth(70);           // narrow Year column
    }
    // this one actually builds the form for signing up a new member and sets its dimensions
    private JPanel buildForm() {
        JPanel form = Theme.card();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(300, 560));

        JLabel heading = new JLabel("Member details");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(Theme.TEXT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(heading);
        form.add(Box.createVerticalStrut(16));

        // readis the input fields to receive data passed into the db
        addField(form, "Full name", nameField);
        addField(form, "Year of study", yearField);
        addField(form, "Course", courseField);
        addField(form, "Email", emailField);
        addField(form, "Date joined (YYYY-MM-DD)", dateField);
        addField(form, "Candidate group", groupField);

        // Buttons side by side
        JButton addButton = Theme.primaryButton("Add");
        JButton clearButton = Theme.secondaryButton("Clear");
        addButton.addActionListener(e -> addMember());
        clearButton.addActionListener(e -> clearForm());

        // a section for the buttons to be styled side by side
        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));   // 1 row, 2 columns, 8px gap
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        buttons.add(addButton);
        buttons.add(clearButton);
        form.add(Box.createVerticalStrut(6));
        form.add(buttons);

        return form;
    }

    // so this section adds a label above every field for entry so we know what data we are entering
    private void addField(JPanel form, String labelText, JTextField field) {
        JLabel label = Theme.fieldLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setPreferredSize(new Dimension(200, 36));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        form.add(label);
        form.add(Box.createVerticalStrut(4));
        form.add(field);
        form.add(Box.createVerticalStrut(10));
    }

   // so this function asks the databse for all the members in teh db and siplays them in the table styled above
    private void loadMembers() {
        try {
            tableModel.setRowCount(0);   // wipe the old rows
            for (Member m : dao.getAll()) {
                tableModel.addRow(new Object[]{
                        m.getId(), m.getName(), m.getYear(), m.getCourse(),
                        m.getEmail(), m.getDateJoined(), m.getCandidateGroup()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();   // TODO: Amore's error handling goes here
        }
    }

    //function for adding a member through the form, so it adds a member through the form then passes that member into the database
    private void addMember() {
        try {
            Member member = new Member(
                    nameField.getText().trim(),
                    Integer.parseInt(yearField.getText().trim()),
                    courseField.getText().trim(),
                    emailField.getText().trim(),
                    LocalDate.parse(dateField.getText().trim()),
                    Double.parseDouble(groupField.getText().trim()));

            dao.add(member);
            loadMembers();
            clearForm();
        } catch (SQLException ex) {
            ex.printStackTrace();   // TODO: Amore's error handling goes here
        }
    }

    // this function clears all the data input by a user into the form and reloads afresh
    private void clearForm() {
        nameField.setText("");
        yearField.setText("");
        courseField.setText("");
        emailField.setText("");
        dateField.setText(LocalDate.now().toString());   // default to today
        groupField.setText("");
        table.clearSelection();
        nameField.requestFocus();
    }
}

// So this file is what makes read operation possible, it holds data for all the members and has a form for signing in new members, it only talks to the dao though, not mysql directly