package com.example.jce.Teacher.TeacherMessage;

public class TeacherStudentsModel {

    String username,usn,branch,uri,userId;


    public TeacherStudentsModel() {
    }


    public TeacherStudentsModel(String username, String usn, String branch, String uri,String userId) {
        this.username = username;
        this.usn = usn;
        this.branch = branch;
        this.uri = uri;
        this.userId = userId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
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
