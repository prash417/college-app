package com.example.jce.Marks;

public class MarksModel {

    String SBranch,SUsn,SemNo,SubCode,sMarks,subName,IANo,sOutOfMarks;


    public MarksModel() {
    }

    public MarksModel(String SBranch, String SUsn, String semNo, String subCode, String sMarks, String subName, String IANo, String sOutOfMarks) {
        this.SBranch = SBranch;
        this.SUsn = SUsn;
        SemNo = semNo;
        SubCode = subCode;
        this.sMarks = sMarks;
        this.subName = subName;
        this.IANo = IANo;
        this.sOutOfMarks = sOutOfMarks;
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

    public String getSubCode() {
        return SubCode;
    }

    public void setSubCode(String subCode) {
        SubCode = subCode;
    }

    public String getsMarks() {
        return sMarks;
    }

    public void setsMarks(String sMarks) {
        this.sMarks = sMarks;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getIANo() {
        return IANo;
    }

    public void setIANo(String IANo) {
        this.IANo = IANo;
    }

    public String getsOutOfMarks() {
        return sOutOfMarks;
    }

    public void setsOutOfMarks(String sOutOfMarks) {
        this.sOutOfMarks = sOutOfMarks;
    }
}
