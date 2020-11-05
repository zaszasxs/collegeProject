package com.example.proe.Model;

public class ModelOrderDetail2 {
    String Uid;
    String timestamp;
    String SellitemID;
    String Itemcategory;
    String Itemprice;
    String Itemtitle;
    String Itemdescription;

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSellitemID() {
        return SellitemID;
    }

    public void setSellitemID(String sellitemID) {
        SellitemID = sellitemID;
    }

    public String getItemcategory() {
        return Itemcategory;
    }

    public void setItemcategory(String itemcategory) {
        Itemcategory = itemcategory;
    }

    public String getItemprice() {
        return Itemprice;
    }

    public void setItemprice(String itemprice) {
        Itemprice = itemprice;
    }

    public String getItemtitle() {
        return Itemtitle;
    }

    public void setItemtitle(String itemtitle) {
        Itemtitle = itemtitle;
    }

    public String getItemdescription() {
        return Itemdescription;
    }

    public void setItemdescription(String itemdescription) {
        Itemdescription = itemdescription;
    }
}
