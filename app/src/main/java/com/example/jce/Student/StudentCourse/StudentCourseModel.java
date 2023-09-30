package com.example.jce.Student.StudentCourse;

public class StudentCourseModel {

    String label,uri;

    public StudentCourseModel() {
    }

    public StudentCourseModel(String label, String uri) {
        this.label = label;
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
