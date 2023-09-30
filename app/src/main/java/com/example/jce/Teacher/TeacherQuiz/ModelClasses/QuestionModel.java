package com.example.jce.Teacher.TeacherQuiz.ModelClasses;

public class QuestionModel {

    private String question, OptionA,OptionB,OptionC,optionD,CorrectAnswer;

    private String key;

    private int setNum;


    public QuestionModel() {
    }

    public QuestionModel(String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer, String key, int setNum) {
        this.question = question;
        OptionA = optionA;
        OptionB = optionB;
        OptionC = optionC;
        this.optionD = optionD;
        CorrectAnswer = correctAnswer;
        this.key = key;
        this.setNum = setNum;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return OptionA;
    }

    public void setOptionA(String optionA) {
        OptionA = optionA;
    }

    public String getOptionB() {
        return OptionB;
    }

    public void setOptionB(String optionB) {
        OptionB = optionB;
    }

    public String getOptionC() {
        return OptionC;
    }

    public void setOptionC(String optionC) {
        OptionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectAnswer() {
        return CorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        CorrectAnswer = correctAnswer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getSetNum() {
        return setNum;
    }

    public void setSetNum(int setNum) {
        this.setNum = setNum;
    }
}
