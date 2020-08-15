package com.example.proe.Model;

public class ModelBuyerUI {
    private String uid,Email,Name,ShopName,Phone,CompleteAddress,Country,State,City,Latitude,Longitude,timestamp,AccountType,online,ShopOpen,profileImage;

    public ModelBuyerUI(){

    }

    public ModelBuyerUI (String uid, String email,String shopName, String name, String phone, String completeAddress, String country, String state, String city, String latitude, String longitude, String timestamp, String accountType, String online, String shopOpen, String profileImage){
        this.uid = uid;
        this.Email = email;
        this.Name = name;
        this.Phone = phone;
        this.CompleteAddress = completeAddress;
        this.Country = country;
        this.State = state;
        this.City = city;
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.timestamp = timestamp;
        this.AccountType = accountType;
        this.online = online;
        this.ShopOpen = shopOpen;
        this.profileImage = profileImage;
        this.ShopName = shopName;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getCompleteAddress() {
        return CompleteAddress;
    }

    public void setCompleteAddress(String completeAddress) {
        CompleteAddress = completeAddress;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAccountType() {
        return AccountType;
    }

    public void setAccountType(String accountType) {
        AccountType = accountType;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getShopOpen() {
        return ShopOpen;
    }

    public void setShopOpen(String shopOpen) {
        ShopOpen = shopOpen;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }
}
