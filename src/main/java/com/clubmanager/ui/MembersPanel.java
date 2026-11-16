package com.clubmanager.ui;

import com.clubmanager.dao.MemberDAO;
import com.clubmanager.model.Member;
import com.clubmanager.util.Validator;   // so this brings in our new Validator class, the one that checks everything the user types before it goes to the database

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

    // so this is the big "Members" heading, we keep it in a variable (instead of adding it straight to the page) so loadMembers() can change its text to show how many members were read, e.g. "Members (3)"
    private final JLabel titleLabel = Theme.title("Members");

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
        // so the north area is now a small panel instead of just the title: the heading sits on the left (WEST) and a Refresh button sits on the right (EAST)
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);   // see-through, so the page background shows
        north.add(titleLabel, BorderLayout.WEST);

        // so this is the Refresh button, clicking it runs loadMembers() again which asks MySQL for the members all over again, that's the READ operation you can trigger on demand
        JButton refreshButton = Theme.secondaryButton("Refresh");
        refreshButton.addActionListener(e -> loadMembers());
        north.add(refreshButton, BorderLayout.EAST);
        add(north, BorderLayout.NORTH);

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

        // so this listens for the user clicking a row in the table, when they do, that member's details are copied into the form so they can be edited (Update) or removed (Delete)
        table.getSelectionModel().addListSelectionListener(e -> {
            // so while the mouse is still moving the selection is "adjusting", we wait until it settles so this doesn't run twice for one click
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelectedRow();
            }
        });

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
        form.setPreferredSize(new Dimension(300, 540));   // so this went from 560 to 540 (and the field heights/gaps below got a bit smaller) so the extra Update and Delete buttons still fit in the window

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
        // so these two are the new buttons, Update saves the changes made to the selected member and Delete removes the selected member (Delete is red so people think twice)
        JButton updateButton = Theme.primaryButton("Update");
        JButton deleteButton = Theme.dangerButton("Delete");
        JButton clearButton = Theme.secondaryButton("Clear");
        addButton.addActionListener(e -> addMember());
        updateButton.addActionListener(e -> updateMember());   // when clicked, run updateMember() further down
        deleteButton.addActionListener(e -> deleteMember());   // when clicked, run deleteMember() further down
        clearButton.addActionListener(e -> clearForm());

        // a section for the buttons to be styled side by side
        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));   // 2 rows, 2 columns, 8px gaps (it was 1 row, 2 columns, 8px gap before we added Update and Delete, four buttons need two rows to fit)
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));   // taller than before (44) because there are now two rows of buttons
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
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
        field.setPreferredSize(new Dimension(200, 32));            // so the field height went from 36 to 32 to save vertical space
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        form.add(label);
        form.add(Box.createVerticalStrut(4));
        form.add(field);
        form.add(Box.createVerticalStrut(6));                      // and the gap under each field went from 10 to 6, same reason
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
            // so once the table is filled we count the rows and show the number in the heading e.g. "Members (3)", that's quick visible proof the READ worked
            titleLabel.setText("Members (" + tableModel.getRowCount() + ")");
        } catch (SQLException ex) {
            // so if MySQL is off, the password is wrong etc we land here instead of the app crashing, and the user gets a friendly popup
            showDatabaseError(ex, "loading the members");
        }
    }

    //function for adding a member through the form, so it adds a member through the form then passes that member into the database
    private void addMember() {
        try {
            // so the form is read AND checked inside buildMemberFromForm(), if anything is wrong it throws and we jump straight to the catch below, so dao.add() never runs with bad data
            dao.add(buildMemberFromForm(0));   // 0 = a new member, MySQL will give it a real id
            loadMembers();
            clearForm();
        } catch (IllegalArgumentException ex) {
            // so this catches the Validator's complaints (empty field, wrong type etc), we keep the form as it is so the user can fix the mistake without retyping everything
            showValidationError(ex.getMessage());
        } catch (SQLException ex) {
            // so this catches database problems, like a duplicate email or MySQL being off
            showDatabaseError(ex, "saving the new member");
        }
    }

    // so this is the UPDATE operation, it works like addMember() but the Member it builds carries the id of the selected row, and that id ends up in the SQL "WHERE member_id = ?" so only that one row changes
    private void updateMember() {
        int id = selectedMemberId();
        if (id == -1) return;   // nothing was selected, selectedMemberId() already told the user, so we just stop

        try {
            dao.update(buildMemberFromForm(id));
            loadMembers();
            clearForm();
        } catch (IllegalArgumentException ex) {
            showValidationError(ex.getMessage());   // bad input, same as in addMember()
        } catch (SQLException ex) {
            showDatabaseError(ex, "updating the member");
        }
    }

    // so this is the DELETE operation, it asks "are you sure?" first and only deletes if the user clicks Yes
    private void deleteMember() {
        int id = selectedMemberId();
        if (id == -1) return;   // nothing selected, stop

        // so we read the name from the selected row (column 1) so the popup says "Delete Juma Kauma?" instead of something generic
        String name = String.valueOf(tableModel.getValueAt(table.getSelectedRow(), 1));
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete " + name + "? This cannot be undone.",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        // so showConfirmDialog returns which button was clicked, we only go ahead if it was Yes, clicking No or the X does nothing
        if (choice == JOptionPane.YES_OPTION) {
            try {
                dao.delete(id);
                loadMembers();
                clearForm();
            } catch (SQLException ex) {
                showDatabaseError(ex, "deleting the member");
            }
        }
    }

    // so this reads the six text boxes, CHECKS each one with the Validator, converts them to the right types and builds a Member with the given id
    // it's used by both Add and Update so the checking is written in ONE place only
    // the arguments are checked from top to bottom so the user is told about the first wrong field first
    private Member buildMemberFromForm(int id) {
        return new Member(
                id,
                Validator.validateName(nameField.getText()),
                Validator.parseYear(yearField.getText()),
                Validator.requireText(courseField.getText(), "Course", 100),
                Validator.validateEmail(emailField.getText()),
                Validator.parseDate(dateField.getText()),
                Validator.parseGroup(groupField.getText()));
    }

    // so this finds which row is selected and returns that member's id (it's stored in column 0), if no row is selected it shows a message and returns -1 as a signal for "nothing selected"
    private int selectedMemberId() {
        int row = table.getSelectedRow();   // getSelectedRow() gives -1 when nothing is selected
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a member in the table first.",
                    "No member selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return -1;
        }
        return (int) tableModel.getValueAt(row, 0);   // column 0 holds the ID
    }

    // so this copies the clicked row's values into the form fields, it's what runs when you click a row
    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row == -1) return;   // the selection was cleared (e.g. by clearForm), so leave the form alone

        nameField.setText(text(tableModel.getValueAt(row, 1)));
        yearField.setText(text(tableModel.getValueAt(row, 2)));
        courseField.setText(text(tableModel.getValueAt(row, 3)));
        emailField.setText(text(tableModel.getValueAt(row, 4)));
        dateField.setText(text(tableModel.getValueAt(row, 5)));
        groupField.setText(text(tableModel.getValueAt(row, 6)));
    }

    // so this turns a table value into text and gives "" instead of the word "null" when a value is missing
    private String text(Object value) {
        return value == null ? "" : value.toString();
    }

    // so this shows a popup for bad input, it's a warning (yellow triangle) because the user just needs to fix what they typed
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid input", JOptionPane.WARNING_MESSAGE);
    }

    // so this turns the scary technical SQLException into a message a normal person understands, and shows it in a red error popup
    // "action" is just a few words about what we were doing, e.g. "deleting the member", used in the fallback message
    private void showDatabaseError(SQLException ex, String action) {
        ex.printStackTrace();   // the full technical details still go to the console for us developers to debug, the user just doesn't see them

        String message;
        if (ex.getErrorCode() == 1062) {
            // 1062 is MySQL's own number for "duplicate entry", it happens because email is UNIQUE in the table
            message = "A member with that email already exists. Each member needs a different email.";
        } else if (ex.getErrorCode() == 1045) {
            // 1045 = access denied, the username or password in DatabaseConnection.java is wrong
            message = "MySQL refused the username or password. Check DatabaseConnection.java.";
        } else if (ex.getErrorCode() == 1049) {
            // 1049 = unknown database, the club_db script was never run
            message = "The database club_db was not found. Run the club_db.sql script first.";
        } else if (ex.getSQLState() != null && ex.getSQLState().startsWith("08")) {
            // SQLState codes starting with 08 mean "connection problem", this is what we get when MySQL is switched off
            message = "Cannot reach the database. Please check that MySQL is running and try again.";
        } else {
            // anything we didn't plan for still gets a clear popup instead of a crash
            message = "Something went wrong while " + action + ".\nDetails: " + ex.getMessage();
        }
        JOptionPane.showMessageDialog(this, message, "Database error", JOptionPane.ERROR_MESSAGE);
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