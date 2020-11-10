package com.example.proe.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Mechanical {
    private Double glass, metal, plastic,ulglass, ulmetal, ulplastic;
    private String uid, error;
    private Mechanical() {
    }

    public double getGlass() {
        return glass;
    }

    public void setGlass(Double glass) {
        this.glass = glass;
    }

    public double getMetal() {
        return metal;
    }

    public void setMetal(Double metal) {
        this.metal = metal;
    }

    public Double getUlglass() {
        return ulglass;
    }

    public void setUlglass(Double ulglass) {
        this.ulglass = ulglass;
    }

    public Double getUlmetal() {
        return ulmetal;
    }

    public void setUlmetal(Double ulmetal) {
        this.ulmetal = ulmetal;
    }

    public Double getUlplastic() {
        return ulplastic;
    }

    public void setUlplastic(Double ulplastic) {
        this.ulplastic = ulplastic;
    }

    public double getPlastic() {
        return plastic;
    }

    public void setPlastic(Double plastic) {
        this.plastic = plastic;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("glass", glass);
        result.put("metal", metal);
        result.put("plastic", plastic);
        result.put("uid", uid);
        result.put("error", error);
        result.put("ulplastic", ulplastic);
        result.put("ulglass", ulglass);
        result.put("ulmetal", ulmetal);
        return result;
    }

    @Override
    public String toString() {
        return "Mechanical{" +
                "glass=" + glass +
                ", metal=" + metal +
                ", plastic=" + plastic +
                ", ulglass=" + ulglass +
                ", ulmetal=" + ulmetal +
                ", ulplastic=" + ulplastic +
                ", uid='" + uid + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
