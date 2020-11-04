package com.example.proe.Model;

public class ModelPMG {

    private String error,plastic,metal,glass,ulplastic,ulmetal,ulglass,uid;

    public ModelPMG() {
    }

    public ModelPMG(String error, String plastic, String metal, String glass, String ulplastic, String ulmetal, String ulglass, String uid) {
        this.error = error;
        this.plastic = plastic;
        this.metal = metal;
        this.glass = glass;
        this.ulplastic = ulplastic;
        this.ulmetal = ulmetal;
        this.ulglass = ulglass;
        this.uid = uid;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPlastic() {
        return plastic;
    }

    public void setPlastic(String plastic) {
        this.plastic = plastic;
    }

    public String getMetal() {
        return metal;
    }

    public void setMetal(String metal) {
        this.metal = metal;
    }

    public String getGlass() {
        return glass;
    }

    public void setGlass(String glass) {
        this.glass = glass;
    }

    public String getUlplastic() {
        return ulplastic;
    }

    public void setUlplastic(String ulplastic) {
        this.ulplastic = ulplastic;
    }

    public String getUlmetal() {
        return ulmetal;
    }

    public void setUlmetal(String ulmetal) {
        this.ulmetal = ulmetal;
    }

    public String getUlglass() {
        return ulglass;
    }

    public void setUlglass(String ulglass) {
        this.ulglass = ulglass;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
