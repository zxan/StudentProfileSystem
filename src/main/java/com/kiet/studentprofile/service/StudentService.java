package com.kiet.studentprofile.service;

import com.kiet.studentprofile.exception.DuplicateStudentException;
import com.kiet.studentprofile.exception.StudentNotFoundException;
import com.kiet.studentprofile.model.Student;
import com.kiet.studentprofile.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    // ---------- Q3.1 Add (Q2.8: no duplicate IDs, scores 0-100) ----------
    public Student addStudent(Student s) {
        if (s.getStudentId() == null || s.getStudentId().isBlank()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        validateScore("Programming", s.getProgrammingScore());
        validateScore("Mathematics", s.getMathematicsScore());

        if (repo.existsById(s.getStudentId())) {
            throw new DuplicateStudentException("Student ID already exists: " + s.getStudentId());
        }
        if (repo.existsByEmailIgnoreCase(s.getEmail())) {
            throw new DuplicateStudentException("Email already registered: " + s.getEmail());
        }
        return repo.save(s);
    }

    // ---------- Q3.2 Retrieve all ----------
    public List<Student> getAll() {
        return repo.findAll();
    }

    // ---------- Q3.3 / Q2.4 Search ----------
    public Student getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("No student with ID: " + id));
    }

    public Student getByEmail(String email) {
        return repo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new StudentNotFoundException("No student with email: " + email));
    }

    public List<Student> getByCourse(String course) {
        return repo.findByCourseNameIgnoreCase(course);
    }

    // ---------- Q3.4 Update scores and skills (partial: null = leave unchanged) ----------
    @Transactional
    public Student updateStudent(String id, Double programmingScore,
                                 Double mathematicsScore, Set<String> skills) {
        Student s = getById(id);

        if (programmingScore != null) {
            validateScore("Programming", programmingScore);
            s.setProgrammingScore(programmingScore);
        }
        if (mathematicsScore != null) {
            validateScore("Mathematics", mathematicsScore);
            s.setMathematicsScore(mathematicsScore);
        }
        if (skills != null) {
            s.getTechnicalSkills().clear();
            s.getTechnicalSkills().addAll(skills);
        }
        return repo.save(s);
    }

    // ---------- Q3.5 Delete ----------
    public void deleteStudent(String id) {
        Student s = getById(id);
        repo.delete(s);
    }

    // ---------- Q2.5 / Q3.6 Merit list: average descending ----------
    public List<Student> getMeritList() {
        return repo.findAll().stream()
                .sorted(Comparator.comparingDouble(Student::getAverageScore).reversed()
                        .thenComparing(Student::getName))
                .toList();
    }

    // ---------- Q2.6 Students with the same skill set ----------
    // Key = sorted, lower-cased skills joined into one string. Only groups of 2+ are kept.
    public Map<String, List<Student>> getStudentsWithSameSkills() {
        Map<String, List<Student>> grouped = repo.findAll().stream()
                .filter(st -> !st.getTechnicalSkills().isEmpty())
                .collect(Collectors.groupingBy(
                        st -> st.getTechnicalSkills().stream()
                                .map(sk -> sk.trim().toLowerCase())
                                .collect(Collectors.toCollection(TreeSet::new))
                                .stream()
                                .collect(Collectors.joining(", ")),
                        LinkedHashMap::new,
                        Collectors.toList()));

        return grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    // ---------- Q2.7 Weak in programming (<50) but maths >= 75 ----------
    public List<Student> getWeakProgrammingStrongMaths() {
        return repo.findByProgrammingScoreLessThanAndMathematicsScoreGreaterThanEqual(50.0, 75.0);
    }

    // ---------- Q2.8 Score validation ----------
    private void validateScore(String subject, Double score) {
        if (score == null) {
            throw new IllegalArgumentException(subject + " score is required");
        }
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException(subject + " score must be between 0 and 100");
        }
    }
}