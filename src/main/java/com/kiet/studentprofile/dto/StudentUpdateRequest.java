package com.kiet.studentprofile.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.util.Set;

public class StudentUpdateRequest {

    @DecimalMin(value = "0.0", message = "Programming score must be at least 0")
    @DecimalMax(value = "100.0", message = "Programming score cannot exceed 100")
    private Double programmingScore;

    @DecimalMin(value = "0.0", message = "Mathematics score must be at least 0")
    @DecimalMax(value = "100.0", message = "Mathematics score cannot exceed 100")
    private Double mathematicsScore;

    private Set<String> technicalSkills;

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
        this.technicalSkills = technicalSkills;
    }
}