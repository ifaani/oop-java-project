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
        // this enables the north area to be a small panel instead of just the title. The heading sits on the left and a Refresh button sits on the right (EAST)
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);   // see-through, so the page background shows
        north.add(titleLabel, BorderLayout.WEST);

        // this code creates thhe refresh button to enable read operation of CRUD, it calls loadmembers function fetched the data from the database and displays it
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

        //this is a listener function, basically it waits for a user to do something which in this case, when a user clicks, it loads the details of the particular member in the form for them to update or delete
        table.getSelectionModel().addListSelectionListener(e -> {
            // here, while the mouse is still moving the selection is "adjusting", we wait until it settles so this doesn't run twice for one click
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelectedRow();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        add(scroll, BorderLayout.CENTER);

        // FORM styling
        JPanel east = new JPanel(new BorderLayout());
        east.setOpaque(false);   // makes it transparent, so the page background shows
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
    // this one actually builds and styles the form for signing up a new member and sets its dimensions
    private JPanel buildForm() {
        JPanel form = Theme.card();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(300, 540));   

        JLabel heading = new JLabel("Member details");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(Theme.TEXT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(heading);
        form.add(Box.createVerticalStrut(16));

        // reads the input fields to receive data passed into the db
        addField(form, "Full name", nameField);
        addField(form, "Year of study", yearField);
        addField(form, "Course", courseField);
        addField(form, "Email", emailField);
        addField(form, "Date joined (YYYY-MM-DD)", dateField);
        addField(form, "Candidate group", groupField);

        // Buttons side by side
        JButton addButton = Theme.primaryButton("Add");
        // this uses Swing's Jbutton to create buttons for deleting and updating records, with event listeners factored in 
        JButton updateButton = Theme.successButton("Update");
        JButton deleteButton = Theme.dangerButton("Delete");
        JButton clearButton = Theme.secondaryButton("Clear");
        addButton.addActionListener(e -> addMember());
        updateButton.addActionListener(e -> updateMember());   // when clicked, run updateMember() further down
        deleteButton.addActionListener(e -> deleteMember());   // when clicked, run deleteMember() further down
        clearButton.addActionListener(e -> clearForm());

        // a section for the buttons to be styled side by side
        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
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
        field.setPreferredSize(new Dimension(200, 32));         
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        form.add(label);
        form.add(Box.createVerticalStrut(4));
        form.add(field);
        form.add(Box.createVerticalStrut(6));                  
    }

   // so this function asks the databse for all the members in teh db and displays them in the table styled above
    private void loadMembers() {
        try {
            tableModel.setRowCount(0);   // wipe the old rows
            for (Member m : dao.getAll()) {
                tableModel.addRow(new Object[]{
                        m.getId(), m.getName(), m.getYear(), m.getCourse(),
                        m.getEmail(), m.getDateJoined(), m.getCandidateGroup()
                });
            }
            // so once the table is filled, getRowCount function counts the rows and show the total in the heading
            titleLabel.setText("Members (" + tableModel.getRowCount() + ")");
        } catch (SQLException ex) {
            // error handling such that, if the password is wrong or msql is off or something, a popup is diplayed describing the exact error 
            showDatabaseError(ex, "loading the members");
        }
    }

    //function for adding a member through the form, so it adds a member through the form then passes that member into the database
    private void addMember() {
        try {
            // this reads the form data input by the user, and if any input is incorrect, it displays a popup explaining the error so that a member is never added with bad data
            dao.add(buildMemberFromForm(0));   // 0 = a new member, MySQL will give it a real id
            loadMembers();
            clearForm();
        } catch (IllegalArgumentException ex) {
            // if a user leaves a field empty or enters incorrect format, it displays a popup with the error while leaving everything else in the form filled to prevent re-typing

            // this is specifically for user input
            showValidationError(ex.getMessage());
        } catch (SQLException ex) {
            // and this is specifically for database errors
            showDatabaseError(ex, "saving the new member");
        }
    }

    // so this is the UPDATE operation, it works like add operation, but carries whatever changes to the selected member id, so it changes the specifc row without adding a new member
    private void updateMember() {
        int id = selectedMemberId();
        if (id == -1) return;   // this handles errors when nothing is selected for updation, so it just stops

        try {
            dao.update(buildMemberFromForm(id));
            loadMembers();
            clearForm();
        } catch (IllegalArgumentException ex) {
            showValidationError(ex.getMessage());   
        } catch (SQLException ex) {
            showDatabaseError(ex, "updating the member");
        }
    }

    // so this is the DELETE operation, it asks "are you sure?" first and only deletes if the user clicks Yes
    private void deleteMember() {
        int id = selectedMemberId();
        if (id == -1) return;   // nothing selected, stop

        // this function reads the name of the selected person to be deleted, so it displays a popup with the member's name who is to be deleted
        String name = String.valueOf(tableModel.getValueAt(table.getSelectedRow(), 1));
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete " + name + "? This cannot be undone.",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        
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

    // so this reads the six text boxes when a user is inputting data, CHECKS each one against the Validator, converts them to the right types if wrong and builds a Member with the given id
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

    // so this finds which row is selected and returns that member's id, if no row is selected it shows a message and returns -1 as a signal for "nothing selected"
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

    // so for this function, when one clicks a row, it loads their data into the form fields respectively to enable editing
    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row == -1) return;   // handles anything about clearing the form, so it just stops

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

    // error handling such that, when a sql error is encountered, it is parsed into a user friendly popup, the "action" part explains what the user was trying to do
    private void showDatabaseError(SQLException ex, String action) {
        ex.printStackTrace();   

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
            // when mysql is switched off, this error message is displayed
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