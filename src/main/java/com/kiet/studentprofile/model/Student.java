package com.kiet.studentprofile.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @NotBlank(message = "Student ID is required")
    @Column(name = "student_id", length = 20)
    private String studentId;

    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank(message = "Course name is required")
    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @NotNull(message = "Programming score is required")
    @DecimalMin(value = "0.0", message = "Programming score must be at least 0")
    @DecimalMax(value = "100.0", message = "Programming score cannot exceed 100")
    @Column(name = "programming_score", nullable = false)
    private Double programmingScore;

    @NotNull(message = "Mathematics score is required")
    @DecimalMin(value = "0.0", message = "Mathematics score must be at least 0")
    @DecimalMax(value = "100.0", message = "Mathematics score cannot exceed 100")
    @Column(name = "mathematics_score", nullable = false)
    private Double mathematicsScore;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "student_skills",
            joinColumns = @JoinColumn(name = "student_id")
    )
    @Column(name = "skill", length = 50)
    private Set<String> technicalSkills = new HashSet<>();

    // ---------- Constructors ----------

    public Student() {
    }

    public Student(String studentId, String name, String email, String courseName,
                   Double programmingScore, Double mathematicsScore,
                   Set<String> technicalSkills) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.courseName = courseName;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.technicalSkills = (technicalSkills != null) ? technicalSkills : new HashSet<>();
    }

    // ---------- Getters and Setters ----------

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Double getProgrammingScore() {
        return programmingScore;
    }

    public void setProgrammingScore(Double programmingScore) {
        this.programmingScore = programmingScore;
    }

    public Double getMathematicsScore() {
        return mathematicsScore;
    }

    public void setMathematicsScore(Double mathematicsScore) {
        this.mathematicsScore = mathematicsScore;
    }

    public Set<String> getTechnicalSkills() {
        return technicalSkills;
    }

    public void setTechnicalSkills(Set<String> technicalSkills) {
        this.technicalSkills = (technicalSkills != null) ? technicalSkills : new HashSet<>();
    }

    // ---------- Derived business logic (Q2.1 - Q2.3) ----------

    public double getAverageScore() {
        return (programmingScore + mathematicsScore) / 2.0;
    }

    public String getPerformanceCategory() {
        double avg = getAverageScore();
        if (avg >= 85.0) {
            return "Excellent";
        } else if (avg >= 70.0) {
            return "Good";
        } else if (avg >= 50.0) {
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
        } else {
            return "Both Equal";
        }
    }

    // ---------- Object methods ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student other = (Student) o;
        return Objects.equals(studentId, other.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return "Student{id=" + studentId
                + ", name=" + name
                + ", course=" + courseName
                + ", avg=" + String.format("%.2f", getAverageScore())
                + ", category=" + getPerformanceCategory() + "}";
    }
}