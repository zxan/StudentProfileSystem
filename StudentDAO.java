package com.kiet.studentjdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class StudentDAO {

    // ---------- Schema ----------

    public void createTables() throws SQLException {
        String students = "CREATE TABLE IF NOT EXISTS students ("
                + "student_id VARCHAR(20) PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "email VARCHAR(120) NOT NULL UNIQUE, "
                + "course_name VARCHAR(100) NOT NULL, "
                + "programming_score DOUBLE NOT NULL "
                + "CHECK (programming_score BETWEEN 0 AND 100), "
                + "mathematics_score DOUBLE NOT NULL "
                + "CHECK (mathematics_score BETWEEN 0 AND 100))";

        String skills = "CREATE TABLE IF NOT EXISTS student_skills ("
                + "student_id VARCHAR(20) NOT NULL, "
                + "skill VARCHAR(50) NOT NULL, "
                + "PRIMARY KEY (student_id, skill), "
                + "FOREIGN KEY (student_id) REFERENCES students(student_id) "
                + "ON DELETE CASCADE)";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(students);
            st.executeUpdate(skills);
        }
    }

    public void deleteAll() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate("DELETE FROM student_skills");
            st.executeUpdate("DELETE FROM students");
        }
    }

    // ---------- Insert (Q2.8: no duplicate IDs, scores already validated) ----------

    public void addStudent(Student s) throws SQLException {
        if (existsBy("student_id", s.getStudentId())) {
            throw new IllegalStateException("Duplicate student ID: " + s.getStudentId());
        }
        if (existsBy("email", s.getEmail())) {
            throw new IllegalStateException("Email already registered: " + s.getEmail());
        }

        String insertStudent = "INSERT INTO students "
                + "(student_id, name, email, course_name, programming_score, mathematics_score) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String insertSkill = "INSERT INTO student_skills (student_id, skill) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);   // student + skills are saved together or not at all
            try (PreparedStatement ps = con.prepareStatement(insertStudent);
                 PreparedStatement psSkill = con.prepareStatement(insertSkill)) {

                ps.setString(1, s.getStudentId());
                ps.setString(2, s.getName());
                ps.setString(3, s.getEmail());
                ps.setString(4, s.getCourseName());
                ps.setDouble(5, s.getProgrammingScore());
                ps.setDouble(6, s.getMathematicsScore());
                ps.executeUpdate();

                Set<String> seen = new HashSet<>();   // skip duplicates such as Java / java
                for (String skill : s.getTechnicalSkills()) {
                    String clean = skill.trim();
                    if (!clean.isEmpty() && seen.add(clean.toLowerCase())) {
                        psSkill.setString(1, s.getStudentId());
                        psSkill.setString(2, clean);
                        psSkill.addBatch();
                    }
                }
                psSkill.executeBatch();
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    private boolean existsBy(String column, String value) throws SQLException {
        // column is always a constant chosen inside this class, never user input
        String sql = "SELECT 1 FROM students WHERE LOWER(" + column + ") = LOWER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ---------- Queries ----------

    public List<Student> findAll() throws SQLException {
        return fetch("");
    }

    // Q2.4 - search by ID, email or course
    public Optional<Student> findById(String id) throws SQLException {
        return fetch("WHERE student_id = ?", id).stream().findFirst();
    }

    public Optional<Student> findByEmail(String email) throws SQLException {
        return fetch("WHERE LOWER(email) = LOWER(?)", email).stream().findFirst();
    }

    public List<Student> findByCourse(String course) throws SQLException {
        return fetch("WHERE LOWER(course_name) = LOWER(?)", course);
    }

    // Q2.7 - weak in programming (below 50) but mathematics at least 75
    public List<Student> findWeakProgrammingStrongMaths() throws SQLException {
        return fetch("WHERE programming_score < ? AND mathematics_score >= ?", 50.0, 75.0);
    }

    // ---------- Shared loader ----------

    private List<Student> fetch(String whereClause, Object... params) throws SQLException {
        String sql = "SELECT student_id, name, email, course_name, "
                + "programming_score, mathematics_score FROM students "
                + whereClause + " ORDER BY student_id";

        Map<String, Student> byId = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student(
                            rs.getString("student_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("course_name"),
                            rs.getDouble("programming_score"),
                            rs.getDouble("mathematics_score"),
                            new ArrayList<>());
                    byId.put(s.getStudentId(), s);
                }
            }
            attachSkills(con, byId);
        }
        return new ArrayList<>(byId.values());
    }

    private void attachSkills(Connection con, Map<String, Student> byId) throws SQLException {
        if (byId.isEmpty()) {
            return;
        }
        String sql = "SELECT student_id, skill FROM student_skills ORDER BY skill";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Student s = byId.get(rs.getString("student_id"));
                if (s != null) {
                    s.getTechnicalSkills().add(rs.getString("skill"));
                }
            }
        }
    }
}
