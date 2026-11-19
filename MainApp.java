package com.kiet.studentjdbc;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainApp {

    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();

        try {
            dao.createTables();
            dao.deleteAll();   // start clean so the demo can be re-run

            // ----- insert sample records -----
            List<Student> samples = Arrays.asList(
                    new Student("S101", "Aarav Sharma", "aarav@kiet.edu", "B.Tech CSE",
                            92, 88, Arrays.asList("Java", "Spring", "SQL")),
                    new Student("S102", "Riya Verma", "riya@kiet.edu", "B.Tech CSE",
                            45, 82, Arrays.asList("Python", "ML")),
                    new Student("S103", "Karan Singh", "karan@kiet.edu", "B.Tech IT",
                            78, 72, Arrays.asList("Java", "Spring", "SQL")),
                    new Student("S104", "Neha Gupta", "neha@kiet.edu", "B.Tech CSE",
                            40, 35, Arrays.asList("HTML", "CSS")),
                    new Student("S105", "Imran Khan", "imran@kiet.edu", "B.Tech IT",
                            65, 65, Arrays.asList("python", "ml")));

            heading("Inserting students");
            for (Student s : samples) {
                dao.addStudent(s);
                System.out.println("Saved: " + s.getStudentId() + " " + s.getName());
            }

            // ----- Q2.8 validation -----
            heading("Q2.8 Validation");
            try {
                dao.addStudent(new Student("S101", "Another Student", "another@kiet.edu",
                        "B.Tech CSE", 50, 50, Arrays.asList("C")));
            } catch (IllegalStateException e) {
                System.out.println("Rejected: " + e.getMessage());
            }
            try {
                new Student("S106", "Bad Score", "bad@kiet.edu", "B.Tech CSE",
                        120, 50, Arrays.asList("C"));
            } catch (IllegalArgumentException e) {
                System.out.println("Rejected: " + e.getMessage());
            }

            // ----- Q2.1 - Q2.3 -----
            heading("Q2.1 - Q2.3 Average, category, stronger subject");
            System.out.printf("%-6s %-14s %8s  %-18s %s%n",
                    "ID", "Name", "Average", "Category", "Stronger subject");
            for (Student s : dao.findAll()) {
                System.out.printf("%-6s %-14s %8.2f  %-18s %s%n",
                        s.getStudentId(), s.getName(), s.getAverageScore(),
                        s.getPerformanceCategory(), s.getStrongerSubject());
            }

            // ----- Q2.4 search -----
            heading("Q2.4 Search");
            System.out.println("By ID S102           : "
                    + dao.findById("S102").map(Student::getName).orElse("not found"));
            System.out.println("By email karan@...   : "
                    + dao.findByEmail("karan@kiet.edu").map(Student::getName).orElse("not found"));
            System.out.println("By course B.Tech CSE :");
            for (Student s : dao.findByCourse("B.Tech CSE")) {
                System.out.println("   " + s.getStudentId() + " " + s.getName());
            }

            // ----- Q2.5 merit list -----
            heading("Q2.5 Merit list");
            int rank = 1;
            for (Student s : StudentAnalytics.meritList(dao.findAll())) {
                System.out.printf("%d. %-14s %6.2f%n", rank++, s.getName(), s.getAverageScore());
            }

            // ----- Q2.6 same skills -----
            heading("Q2.6 Students with the same technical skills");
            Map<Set<String>, List<Student>> groups =
                    StudentAnalytics.groupBySameSkills(dao.findAll());
            for (Map.Entry<Set<String>, List<Student>> e : groups.entrySet()) {
                StringBuilder names = new StringBuilder();
                for (Student s : e.getValue()) {
                    if (names.length() > 0) {
                        names.append(", ");
                    }
                    names.append(s.getName());
                }
                System.out.println(e.getKey() + " -> " + names);
            }

            // ----- Q2.7 -----
            heading("Q2.7 Programming below 50 but mathematics 75 or more");
            for (Student s : dao.findWeakProgrammingStrongMaths()) {
                System.out.printf("%s %s (programming %.0f, mathematics %.0f)%n",
                        s.getStudentId(), s.getName(),
                        s.getProgrammingScore(), s.getMathematicsScore());
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void heading(String text) {
        System.out.println();
        System.out.println("=== " + text + " ===");
    }
}
