package com.example.jce.Chat;

public class Messages {

    String message,senderID,FileName,FileUri,ImgUri;
    long timestamp;
    String currenttime,date;

    public Messages() {
    }

    public Messages(String message, String senderID, String fileName, String fileUri, String imgUri, long timestamp, String currenttime, String date) {
        this.message = message;
        this.senderID = senderID;
        FileName = fileName;
        FileUri = fileUri;
        ImgUri = imgUri;
        this.timestamp = timestamp;
        this.currenttime = currenttime;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileUri() {
        return FileUri;
    }

    public void setFileUri(String fileUri) {
        FileUri = fileUri;
    }

    public String getImgUri() {
        return ImgUri;
    }

    public void setImgUri(String imgUri) {
        ImgUri = imgUri;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
