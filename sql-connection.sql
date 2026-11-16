
CREATE DATABASE clubDatabase;
USE clubDatabase;

CREATE TABLE members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    year INT,
    course VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    date_joined DATE,
    candidate_group DECIMAL(4,2)
);

CREATE TABLE events (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    event_date DATE,
    venue VARCHAR(100),
    description VARCHAR(500)
);

CREATE TABLE attendance (
    member_id INT NOT NULL,
    event_id INT NOT NULL,
    attended BOOL DEFAULT FALSE,
    PRIMARY KEY (member_id, event_id),
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
);

CREATE TABLE payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    amount DECIMAL(8,2) NOT NULL,
    paid_date DATE,
    payment_method VARCHAR(50),
    purpose VARCHAR(100),
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

CREATE TABLE officials (
    official_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    role VARCHAR(50) NOT NULL,
    term_start DATE,
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

INSERT INTO members (name, year, course, email, date_joined, candidate_group) VALUES
    ('Juma Kauma', 2, 'CNS', 'juma@test.com', '2026-10-11', 2.2),
    ('Diamond Kisuya', 1, 'LAW', 'diamond@test.com', '2026-09-15', 1.1),
    ('James Kagika', 3, 'BBIT', 'james@test.com', '2026-10-06', 3.2);

INSERT INTO events (title, event_date, venue, description) VALUES
    ('RadicalTrips', '2026-12-12', 'The mall', 'A fun way to travel the world'),
    ('Nyama zuri', '2026-12-20', 'Sarit', 'Learn how to make the most delicious meals'),
    ('Aquaworld', '2026-12-20', 'Maasai market', 'A wonderful swimming experience');

INSERT INTO attendance (member_id, event_id) VALUES (1,1), (2,1), (3,2), (2,3);

INSERT INTO officials (member_id, role, term_start) VALUES (1, 'Chairperson', '2026-10-11');

INSERT INTO payment (member_id, amount, paid_date, payment_method, purpose) VALUES
    (1, 500.00, '2026-10-12', 'M-Pesa', 'Membership fee'),
    (2, 500.00, '2026-09-16', 'Cash', 'Membership fee');
