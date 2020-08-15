package com.example.proe.Model;

public class ModelSellItem {

    private String SellitemID,Itemtitle,Itemcategory,imagesell,Itemdescription,Itemquanlity,
            Itemprice,timestamp,Uid;

    public ModelSellItem() {
    }

    public ModelSellItem(String sellitemID, String itemtitle, String itemcategory, String imagesell, String itemdescription, String itemquanlity, String itemprice, String timestamp, String uid) {
        this.SellitemID = sellitemID;
        this.Itemtitle = itemtitle;
        this.Itemcategory = itemcategory;
        this.imagesell = imagesell;
        this.Itemdescription = itemdescription;
        this.Itemquanlity = itemquanlity;
        this. Itemprice = itemprice;
        this.timestamp = timestamp;
        this. Uid = uid;
    }

    public String getSellitemID() {
        return SellitemID;
    }

    public void setSellitemID(String sellitemID) {
        SellitemID = sellitemID;
    }

    public String getItemtitle() {
        return Itemtitle;
    }

    public void setItemtitle(String itemtitle) {
        Itemtitle = itemtitle;
    }

    public String getItemcategory() {

        return Itemcategory;
    }

    public void setItemcategory(String itemcategory) {
        Itemcategory = itemcategory;
    }

    public String getImagesell() {


        return imagesell;
    }

    public void setImagesell(String imagesell) {
        this.imagesell = imagesell;
    }

    public String getItemdescription() {
        return Itemdescription;
    }

    public void setItemdescription(String itemdescription) {
        Itemdescription = itemdescription;
    }

    public String getItemquanlity() {
        return Itemquanlity;
    }

    public void setItemquanlity(String itemquanlity) {
        Itemquanlity = itemquanlity;
    }

    public String getItemprice() {
        return Itemprice;
    }

    public void setItemprice(String itemprice) {
        Itemprice = itemprice;
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
}
