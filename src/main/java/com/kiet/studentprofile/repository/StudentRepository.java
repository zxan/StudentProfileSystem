package com.kiet.studentprofile.repository;

import com.kiet.studentprofile.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {

    Optional<Student> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Student> findByCourseNameIgnoreCase(String courseName);

    // Q2.7 - Spring builds the SQL from the method name
    List<Student> findByProgrammingScoreLessThanAndMathematicsScoreGreaterThanEqual(
            Double programmingBelow, Double mathematicsAtLeast);
}