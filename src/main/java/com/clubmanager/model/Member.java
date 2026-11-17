package com.clubmanager.model;

import java.time.LocalDate;

/**
 This file is like SQL for java, it connects the database to teh front end by creating a schema corresponding to the one in the database
 */
public class Member {

    // Private attributes are the attributes everyone has, only code inside member class can touch these fields, so we use getter methods in other classes 
    private int id;                  
    private String name;
    private int year;               
    private String course;
    private String email;
    private LocalDate dateJoined;
    private double candidateGroup;
    // This is for members that are ALREADY enrolled in the club
    // We use what's called a Data Access Object to read a row, since everyone already has an ID 
    public Member(int id, String name, int year, String course,
                  String email, LocalDate dateJoined, double candidateGroup) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.course = course;
        this.email = email;
        this.dateJoined = dateJoined;
        this.candidateGroup = candidateGroup;
    }

    // This is for members who are enrolling for the first time into the clun
    // they don't have an ID yet, so the UI is what uses this
    public Member(String name, int year, String course,
                  String email, LocalDate dateJoined, double candidateGroup) {
        this(0, name, year, course, email, dateJoined, candidateGroup);
    }

    // Getter methods, which allow other methods to read private fields in class Member
     public int getId() { return id; }
    public String getName() { return name; }
    public int getYear() { return year; }
    public String getCourse() { return course; }
    public String getEmail() { return email; }
    public LocalDate getDateJoined() { return dateJoined; }
    public double getCandidateGroup() { return candidateGroup; }

    // Setter methods, which allow other methods to change values in the private fields
     public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setYear(int year) { this.year = year; }
    public void setCourse(String course) { this.course = course; }
    public void setEmail(String email) { this.email = email; }
    public void setDateJoined(LocalDate dateJoined) { this.dateJoined = dateJoined; }
    public void setCandidateGroup(double candidateGroup) { this.candidateGroup = candidateGroup; }
}


// so the fields in class are private so that other methods don't have unlimited access to manipulate them, like when a user enters a club, they won't be able to change the restrictions in the class like putting thei email as empty or something, so that's the purpose of using privat fields in the member class