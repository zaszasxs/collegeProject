package com.example.proe.Model;

import java.io.Serializable;

public class ModelInfoBuyer implements Serializable {

    String InfomationID,Infotitle,Infodescription,timestamp,Uid,InfoBy;

    public ModelInfoBuyer() {
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

    public String getInfoBy() {
        return InfoBy;
    }

    public void setInfoBy(String infoBy) {
        InfoBy = infoBy;
    }
}
