package com.example.proe.Model;

public class Ratings {
    String uid, review, timestamp;
    int ratings;

    public Ratings(String uid, String review, String timestamp, int ratings) {
        this.uid = uid;
        this.review = review;
        this.timestamp = timestamp;
        this.ratings = ratings;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }

    @Override
    public String toString() {
        return "Ratings{" +
                "uid='" + uid + '\'' +
                ", review='" + review + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", ratings=" + ratings +
                '}';
    }
}
