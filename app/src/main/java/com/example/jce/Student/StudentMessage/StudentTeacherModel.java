package com.example.jce.Student.StudentMessage;

public class StudentTeacherModel {
    String username,uri,userId;

    public StudentTeacherModel() {
    }

    public StudentTeacherModel(String username, String uri, String userId) {
        this.username = username;
        this.uri = uri;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
