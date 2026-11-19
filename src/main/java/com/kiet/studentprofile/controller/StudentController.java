package com.kiet.studentprofile.controller;

import com.kiet.studentprofile.dto.StudentUpdateRequest;
import com.kiet.studentprofile.model.Student;
import com.kiet.studentprofile.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // Q3.1 Add a student
    @PostMapping
    public ResponseEntity<Student> addStudent(@Valid @RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addStudent(student));
    }

    // Q3.2 Retrieve all students
    @GetMapping
    public List<Student> getAllStudents() {
        return service.getAll();
    }

    // Q3.3 Search by ID
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable String id) {
        return service.getById(id);
    }

    // Q3.4 Update scores and skills
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable String id,
                                 @Valid @RequestBody StudentUpdateRequest req) {
        return service.updateStudent(id, req.getProgrammingScore(),
                req.getMathematicsScore(), req.getTechnicalSkills());
    }

    // Q3.5 Delete
    @DeleteMapping("/{id}")
    public Map<String, String> deleteStudent(@PathVariable String id) {
        service.deleteStudent(id);
        return Map.of("message", "Student " + id + " deleted");
    }

    // Q3.6 Merit list
    @GetMapping("/merit-list")
    public List<Student> getMeritList() {
        return service.getMeritList();
    }

    // ---- Q2 extras, exposed so you can demo them in Postman / viva ----

    // Q2.4 Search by email or course: /search?email=...  or  /search?course=...
    @GetMapping("/search")
    public List<Student> search(@RequestParam(required = false) String email,
                                @RequestParam(required = false) String course) {
        if (email != null) {
            return List.of(service.getByEmail(email));
        }
        if (course != null) {
            return service.getByCourse(course);
        }
        throw new IllegalArgumentException("Provide either 'email' or 'course' as a query parameter");
    }

    // Q2.6 Same technical skills
    @GetMapping("/same-skills")
    public Map<String, List<Student>> sameSkills() {
        return service.getStudentsWithSameSkills();
    }

    // Q2.7 Weak in programming, strong in maths
    @GetMapping("/needs-improvement")
    public List<Student> needsImprovement() {
        return service.getWeakProgrammingStrongMaths();
    }
}