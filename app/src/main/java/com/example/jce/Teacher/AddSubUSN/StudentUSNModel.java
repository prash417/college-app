package com.example.jce.Teacher.AddSubUSN;

public class StudentUSNModel {

    String SBranch,SUsn,SemNo,SName;

    public StudentUSNModel() {
    }

    public StudentUSNModel(String SBranch, String SUsn, String semNo, String SName) {
        this.SBranch = SBranch;
        this.SUsn = SUsn;
        SemNo = semNo;
        this.SName = SName;
    }

    public String getSBranch() {
        return SBranch;
    }

    public void setSBranch(String SBranch) {
        this.SBranch = SBranch;
    }

    public String getSUsn() {
        return SUsn;
    }

    public void setSUsn(String SUsn) {
        this.SUsn = SUsn;
    }

    public String getSemNo() {
        return SemNo;
    }

    public void setSemNo(String semNo) {
        SemNo = semNo;
    }

    public String getSName() {
        return SName;
    }

    public void setSName(String SName) {
        this.SName = SName;
    }
}
