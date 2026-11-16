package com.clubmanager.dao;


import com.clubmanager.model.Member;
// This tells Java that the Member class is located in another package
import java.sql.SQLException;

import java.util.List;

public interface MemberDAO {

    void add(Member member) throws SQLException;        // Create

    List<Member> getAll() throws SQLException;          // Read

    void update(Member member) throws SQLException;     // Update 

    void delete(int id) throws SQLException;            // Delete
}

// earlier schema for explaining what will happen when we connect to the database, this was just a boiler plate for reference purposes
