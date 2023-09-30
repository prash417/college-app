package com.example.jce.Notes;

public class NotesModel {

    String filename,fileurl,by;


    public NotesModel() {
    }

    public NotesModel(String filename, String fileurl, String by) {
        this.filename = filename;
        this.fileurl = fileurl;
        this.by = by;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }
}
