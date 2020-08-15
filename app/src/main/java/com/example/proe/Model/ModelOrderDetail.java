package com.example.proe.Model;

public class ModelOrderDetail {

    private String Sid,name,cost,price,quantity;

    public ModelOrderDetail() {
    }

    public ModelOrderDetail(String sid, String name, String cost, String price, String quantity) {
        this.Sid = sid;
        this.name = name;
        this.cost = cost;
        this.price = price;
        this.quantity = quantity;
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
