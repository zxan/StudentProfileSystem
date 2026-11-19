package com.kiet.studentjdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Student {

    private String studentId;
    private String name;
    private String email;
    private String courseName;
    private double programmingScore;
    private double mathematicsScore;
    private List<String> technicalSkills;

    // ---------- Constructors ----------

    public Student() {
        this.technicalSkills = new ArrayList<>();
    }

    public Student(String studentId, String name, String email, String courseName,
                   double programmingScore, double mathematicsScore,
                   List<String> technicalSkills) {
        setStudentId(studentId);
        setName(name);
        setEmail(email);
        setCourseName(courseName);
        setProgrammingScore(programmingScore);
        setMathematicsScore(mathematicsScore);
        setTechnicalSkills(technicalSkills);
    }

    // ---------- Getters and setters (setters validate the data) ----------

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = requireText(studentId, "Student ID");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireText(name, "Name");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String value = requireText(email, "Email");
        if (!value.contains("@") || value.startsWith("@") || value.endsWith("@")) {
            throw new IllegalArgumentException("Email format is invalid: " + value);
        }
        this.email = value;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = requireText(courseName, "Course name");
    }

    public double getProgrammingScore() {
        return programmingScore;
    }

    public void setProgrammingScore(double programmingScore) {
        this.programmingScore = requireScore("Programming", programmingScore);
    }

    public double getMathematicsScore() {
        return mathematicsScore;
    }

    public void setMathematicsScore(double mathematicsScore) {
        this.mathematicsScore = requireScore("Mathematics", mathematicsScore);
    }

    public List<String> getTechnicalSkills() {
        return technicalSkills;
    }

    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = (technicalSkills == null)
                ? new ArrayList<>()
                : new ArrayList<>(technicalSkills);
    }

    // ---------- Business methods (Q2.1 - Q2.3) ----------

    public double getAverageScore() {
        return (programmingScore + mathematicsScore) / 2.0;
    }

    public String getPerformanceCategory() {
        double avg = getAverageScore();
        if (avg >= 85) {
            return "Excellent";
        } else if (avg >= 70) {
            return "Good";
        } else if (avg >= 50) {
            return "Satisfactory";
        } else {
            return "Needs Improvement";
        }
    }

    public String getStrongerSubject() {
        if (programmingScore > mathematicsScore) {
            return "Programming";
        } else if (mathematicsScore > programmingScore) {
            return "Mathematics";
        }
        return "Both Equal";
    }

    // ---------- Validation helpers (Q2.8) ----------

    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }

    private static double requireScore(String subject, double score) {
        if (Double.isNaN(score) || score < 0 || score > 100) {
            throw new IllegalArgumentException(
                    subject + " score must be between 0 and 100 (got " + score + ")");
        }
        return score;
    }

    // ---------- Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return Objects.equals(studentId, ((Student) o).studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return String.format("Student{id=%s, name=%s, course=%s, avg=%.2f, category=%s, skills=%s}",
                studentId, name, courseName, getAverageScore(),
                getPerformanceCategory(), technicalSkills);
    }
}
