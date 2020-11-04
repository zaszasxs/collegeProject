package com.example.proe.Model;

import java.util.List;

public class ModelPushToken {
  /**
   * multicast_id : 4328645197068623533
   * success : 1
   * failure : 0
   * canonical_ids : 0
   * results : [{"message_id":"0:1604402145008838%308200a9f9fd7ecd"}]
   */

  private long multicast_id;
  private int success;
  private int failure;
  private int canonical_ids;
  private List<ResultsBean> results;

  public long getMulticast_id() {
    return multicast_id;
  }

  public void setMulticast_id(long multicast_id) {
    this.multicast_id = multicast_id;
  }

  public int getSuccess() {
    return success;
  }

  public void setSuccess(int success) {
    this.success = success;
  }

  public int getFailure() {
    return failure;
  }

  public void setFailure(int failure) {
    this.failure = failure;
  }

  public int getCanonical_ids() {
    return canonical_ids;
  }

  public void setCanonical_ids(int canonical_ids) {
    this.canonical_ids = canonical_ids;
  }

  public List<ResultsBean> getResults() {
    return results;
  }

  public void setResults(List<ResultsBean> results) {
    this.results = results;
  }

  public static class ResultsBean {
    /**
     * message_id : 0:1604402145008838%308200a9f9fd7ecd
     */

    private String message_id;

    public String getMessage_id() {
      return message_id;
    }

    public void setMessage_id(String message_id) {
      this.message_id = message_id;
    }
  }
}
