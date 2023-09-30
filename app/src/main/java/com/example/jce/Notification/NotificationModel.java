package com.example.jce.Notification;

public class NotificationModel {

    String FileName,FileUri,ImgUri,Message,Sender,TimeStamp,sentDate,sentTime;

    public NotificationModel() {
    }

    public NotificationModel(String fileName, String fileUri, String imgUri, String message, String sender, String timeStamp, String sentDate, String sentTime) {
        FileName = fileName;
        FileUri = fileUri;
        ImgUri = imgUri;
        Message = message;
        Sender = sender;
        TimeStamp = timeStamp;
        this.sentDate = sentDate;
        this.sentTime = sentTime;
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

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }
}
