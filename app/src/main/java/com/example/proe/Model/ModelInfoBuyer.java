package com.example.proe.Model;

public class ModelInfoBuyer {

    String InfomationID,Infotitle,Infodescription,timestamp,Uid,InfoTime;

    public ModelInfoBuyer() {
    }

    public ModelInfoBuyer(String infomationID, String infotitle, String infodescription, String timestamp, String uid,String InfoTime) {
        this.InfomationID = infomationID;
        this.Infotitle = infotitle;
        this.Infodescription = infodescription;
        this.timestamp = timestamp;
        this.Uid = uid;
        this.InfoTime = InfoTime;
    }

    public String getInfomationID() {
        return InfomationID;
    }

    public void setInfomationID(String infomationID) {
        InfomationID = infomationID;
    }

    public String getInfotitle() {
        return Infotitle;
    }

    public void setInfotitle(String infotitle) {
        Infotitle = infotitle;
    }

    public String getInfodescription() {
        return Infodescription;
    }

    public void setInfodescription(String infodescription) {
        Infodescription = infodescription;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getInfoTime() {
        return InfoTime;
    }

    public void setInfoTime(String infoTime) {
        InfoTime = infoTime;
    }
}
