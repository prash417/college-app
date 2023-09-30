package com.example.jce.Teacher.AddSubUSN;

public class SubjectModel {
    String SubCode,SubName,SBranch;

    public SubjectModel() {
    }

    public SubjectModel(String subCode, String subName, String SBranch) {
        SubCode = subCode;
        SubName = subName;
        this.SBranch = SBranch;
    }

    public String getSubCode() {
        return SubCode;
    }

    public void setSubCode(String subCode) {
        SubCode = subCode;
    }

    public String getSubName() {
        return SubName;
    }

    public void setSubName(String subName) {
        SubName = subName;
    }

    public String getSBranch() {
        return SBranch;
    }

    public void setSBranch(String SBranch) {
        this.SBranch = SBranch;
    }
}
