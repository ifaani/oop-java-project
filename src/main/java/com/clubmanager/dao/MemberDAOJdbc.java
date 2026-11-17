package com.clubmanager.dao;
// This line is useful for package management, it kinda divides everything into packages, so this line defines this file as the dao package

import com.clubmanager.db.DatabaseConnection;
// imports the class for connecting to our specific database
import com.clubmanager.model.Member;
// enables use of defined models in other files like models and connection to the db

import java.sql.Connection;
// so this apparently imports a connection between java and the database, idek we needed to import that but yeah, we sorta do I guess
import java.sql.Date;
// Imports date data type from java sql library
import java.sql.PreparedStatement;
// enables us to write and execute SQL queries in java
import java.sql.ResultSet;
// enables java to work with the results provided after a SELECT SQL statement from the database
import java.sql.SQLException;
// we need to mention the error type, so this allows that
import java.util.ArrayList;
import java.util.List;
// enables using java's list function, which kind of works like arrays, so when we have two different club memmbers, we don't create two different variables, we just create one variable holding all the values



public class MemberDAOJdbc implements MemberDAO {

    // Code for CREATE operation
    @Override
    public void add(Member m) throws SQLException {
        String sql = "INSERT INTO members (name, year, course, email, date_joined, candidate_group) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
                   // so this tells mysql that we're trying to add a value to a member table, the specific values are defined below

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                // this block attempts to establish a connection to the db first, then parses everything into a normal sql statement

            ps.setString(1, m.getName());
            ps.setInt(2, m.getYear());
            ps.setString(3, m.getCourse());
            ps.setString(4, m.getEmail());
            ps.setDate(5, Date.valueOf(m.getDateJoined()));
            ps.setDouble(6, m.getCandidateGroup());
            // so here, it takes the calue input by the user, (the get parts) and sets them according to the data type in the table

            ps.executeUpdate();   // runs the INSERT
        }
    }

    // Code for READ operation
    @Override
    public List<Member> getAll() throws SQLException {
        String sql = "SELECT * FROM members ORDER BY member_id";
        List<Member> members = new ArrayList<>();
        // so this tells mysql that we're trying to read everything in a particular table, and creates an array to be displayed in the java GUI

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {   // a loop to move to the next row depending on how many people are there, and stops when there aren't any more
                Date joined = rs.getDate("date_joined");
                members.add(new Member(
                        rs.getInt("member_id"),
                        rs.getString("name"),
                        rs.getInt("year"),
                        rs.getString("course"),
                        rs.getString("email"),
                        joined == null ? null : joined.toLocalDate(),
                        rs.getDouble("candidate_group")));
            } // so this gets all the values from the database and displays them as an array
        }
        return members;
    }

    // Code for UPDATE operation
    @Override
    public void update(Member m) throws SQLException {
        String sql = "UPDATE members SET name = ?, year = ?, course = ?, email = ?, "
                   + "date_joined = ?, candidate_group = ? WHERE member_id = ?"; // So this tells MYSQL that we're tryna update a certain row in the table

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getName());
            ps.setInt(2, m.getYear());
            ps.setString(3, m.getCourse());
            ps.setString(4, m.getEmail());
            ps.setDate(5, Date.valueOf(m.getDateJoined()));
            ps.setDouble(6, m.getCandidateGroup());
            ps.setInt(7, m.getId());   // so all this listens to what exactly one wants to update, and the get id is whose data you want to update

            ps.executeUpdate();
        }
    }

    // Code for DELETE operation
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM members WHERE member_id = ?";
        // So this tells mysql we wanna delete a record cause maybe a club memmber resigned or something

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } // so for this we just need the id of the member, then it deletes everything about them
    }
}

//so this file is the Data Access Object file, it stands as the middleman between the UI and the database. It is spoken to by the UI during the CRUD operations and it in turn speaks to the database telling it what to do according to what the UI has selected

// ALL the operations follow a standard format, with minor changes deoending on what happens on the database, but the code pattern is pretty regular and followable