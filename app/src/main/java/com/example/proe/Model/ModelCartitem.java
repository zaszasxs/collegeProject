package com.example.proe.Model;

public class ModelCartitem {
    private String id,Sid,name,price,cost,num;

    public ModelCartitem() {
    }

    public ModelCartitem(String id, String sid, String name, String price, String cost, String num) {
        this.id = id;
        this.Sid = sid;
        this.name = name;
        this.price = price;
        this.cost = cost;
        this.num = num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSid() {
        return Sid;
    }

    public void setSid(String sid) {
        Sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
