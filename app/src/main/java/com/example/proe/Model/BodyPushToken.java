package com.example.proe.Model;

import java.util.List;

public class BodyPushToken {

  private String priority;
  private boolean silent;
  private DataBean data;
  private List<String> registration_ids;

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public boolean isSilent() {
    return silent;
  }

  public void setSilent(boolean silent) {
    this.silent = silent;
  }

  public DataBean getData() {
    return data;
  }

  public void setData(DataBean data) {
    this.data = data;
  }

  public List<String> getRegistration_ids() {
    return registration_ids;
  }

  public void setRegistration_ids(List<String> registration_ids) {
    this.registration_ids = registration_ids;
  }

  public static class DataBean {
    private String title;
    private String body;


    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getBody() {
      return body;
    }

    public void setBody(String body) {
      this.body = body;
    }
  }
}
