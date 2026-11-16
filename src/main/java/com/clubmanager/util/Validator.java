package com.clubmanager.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// so this class is the "bouncer" of the app: everything the user types goes through it BEFORE it's allowed anywhere near the database
// every method here either returns the clean, converted value OR throws an IllegalArgumentException with a human friendly message, which MembersPanel catches and shows in a popup
public class Validator {

    // so this is a "pattern" (a regex) describing what an email looks like: some characters, an @, some characters, a dot, then at least 2 letters e.g. juma@test.com
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    // so this pattern allows only letters (any language, that's the \p{L}), spaces, dots, apostrophes and hyphens, so "Mary-Jane O'Neil" passes but "J0hn" doesn't
    private static final String NAME_PATTERN = "^[\\p{L} .'-]+$";

    // so this pattern matches the shape of DECIMAL(4,2) in MySQL: one or two digits, optionally followed by a dot and one or two digits e.g. 2 or 2.2 or 12.75
    private static final String GROUP_PATTERN = "^\\d{1,2}(\\.\\d{1,2})?$";

    // so this private constructor stops anyone doing "new Validator()", all the methods are static so you just call Validator.something() directly
    private Validator() {
    }

    // so this checks a text field isn't empty (or just spaces) and isn't longer than the database column allows, then returns the trimmed text
    public static String requireText(String value, String fieldName, int maxLength) {
        String trimmed = (value == null) ? "" : value.trim();   // null or spaces only both count as empty

        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        if (trimmed.length() > maxLength) {
            // MySQL would reject this anyway (VARCHAR is limited), so we catch it early with a clearer message
            throw new IllegalArgumentException(fieldName + " is too long (maximum " + maxLength + " characters).");
        }
        return trimmed;
    }

    // so this checks the name is not empty, fits in VARCHAR(60) and only has sensible characters
    public static String validateName(String value) {
        String name = requireText(value, "Full name", 60);
        if (!name.matches(NAME_PATTERN)) {
            throw new IllegalArgumentException("Full name can only contain letters, spaces, dots, apostrophes and hyphens.");
        }
        return name;
    }

    // so this checks the email is not empty, fits in VARCHAR(100) and looks like a real email
    public static String validateEmail(String value) {
        String email = requireText(value, "Email", 100);
        if (!email.matches(EMAIL_PATTERN)) {
            throw new IllegalArgumentException("Email is not valid. Example: name@example.com");
        }
        return email;
    }

    // so this turns the year text into a whole number (the data type check) and makes sure it's between 1 and 6
    public static int parseYear(String value) {
        String text = requireText(value, "Year of study", 10);
        int year;
        try {
            year = Integer.parseInt(text);   // throws NumberFormatException for things like "two" or "2.5"
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Year of study must be a whole number, e.g. 2.");
        }
        if (year < 1 || year > 6) {
            throw new IllegalArgumentException("Year of study must be between 1 and 6.");
        }
        return year;
    }

    // so this checks the text matches the DECIMAL(4,2) shape, then converts it to a number
    public static double parseGroup(String value) {
        String text = requireText(value, "Candidate group", 10);
        if (!text.matches(GROUP_PATTERN)) {
            throw new IllegalArgumentException("Candidate group must be a number like 2 or 2.2 (at most 2 digits before and 2 after the decimal point).");
        }
        return Double.parseDouble(text);   // safe now, the pattern already proved it's a valid number
    }

    // so this turns the date text into a real LocalDate and makes sure it isn't in the future (nobody can join tomorrow)
    public static LocalDate parseDate(String value) {
        String text = requireText(value, "Date joined", 10);
        LocalDate date;
        try {
            date = LocalDate.parse(text);   // only accepts real dates in YYYY-MM-DD, so 2026-02-30 fails too
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Date joined must be a real date in the format YYYY-MM-DD, e.g. 2026-11-17.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date joined cannot be in the future.");
        }
        return date;
    }
}