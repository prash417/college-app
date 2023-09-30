package com.example.jce.Student.StudentQuiz.StudentQuizModels;

public class StudentCategoryModel {
    String CategoryName,CategoryImage,Key;
    int setNum;

    public StudentCategoryModel() {
    }

    public StudentCategoryModel(String categoryName, String categoryImage, String key, int setNum) {
        CategoryName = categoryName;
        CategoryImage = categoryImage;
        Key = key;
        this.setNum = setNum;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCategoryImage() {
        return CategoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        CategoryImage = categoryImage;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public int getSetNum() {
        return setNum;
    }

    public void setSetNum(int setNum) {
        this.setNum = setNum;
    }
}
