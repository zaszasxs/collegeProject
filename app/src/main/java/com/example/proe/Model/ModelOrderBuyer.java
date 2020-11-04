package com.example.proe.Model;

public class ModelOrderBuyer {

    String OrderID,OrderTime,OrderStatus,OrderCost,OrderBy,OrderTo,Latitude,Longitude;

    public ModelOrderBuyer() {
    }

    public ModelOrderBuyer(String orderID, String orderTime, String orderStatus, String orderCost, String orderBy, String orderTo, String latitude, String longitude) {
        this.OrderID = orderID;
        this.OrderTime = orderTime;
        this.OrderStatus = orderStatus;
        this.OrderCost = orderCost;
        this.OrderBy = orderBy;
        this.OrderTo = orderTo;
        this.Latitude = latitude;
        this.Longitude = longitude;

    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getOrderCost() {
        return OrderCost;
    }

    public void setOrderCost(String orderCost) {
        OrderCost = orderCost;
    }

    public String getOrderBy() {
        return OrderBy;
    }

    public void setOrderBy(String orderBy) {
        OrderBy = orderBy;
    }

    public String getOrderTo() {
        return OrderTo;
    }

    public void setOrderTo(String orderTo) {
        OrderTo = orderTo;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
