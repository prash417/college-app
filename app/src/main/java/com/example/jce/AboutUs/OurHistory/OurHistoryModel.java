package com.example.jce.AboutUs.OurHistory;

public class OurHistoryModel {

    String label,info;

    public OurHistoryModel() {
    }

    public OurHistoryModel(String label, String info) {
        this.label = label;
        this.info = info;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
