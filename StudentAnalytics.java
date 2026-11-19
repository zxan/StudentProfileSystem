package com.kiet.studentjdbc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Collection-based business logic. Works on lists of students already
 * loaded from the database.
 */
public final class StudentAnalytics {

    private StudentAnalytics() {
    }

    // Q2.5 - merit list: average score descending, name as tie-breaker
    public static List<Student> meritList(List<Student> students) {
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparingDouble(Student::getAverageScore).reversed()
                .thenComparing(Student::getName));
        return sorted;
    }

    // Q2.6 - students whose set of technical skills is identical.
    // Key = sorted, lower-cased skill set; only groups with 2+ students are kept.
    public static Map<Set<String>, List<Student>> groupBySameSkills(List<Student> students) {
        Map<Set<String>, List<Student>> groups = new LinkedHashMap<>();
        for (Student s : students) {
            Set<String> key = new TreeSet<>();
            for (String skill : s.getTechnicalSkills()) {
                key.add(skill.trim().toLowerCase());
            }
            if (key.isEmpty()) {
                continue;
            }
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        groups.values().removeIf(group -> group.size() < 2);
        return groups;
    }
}
