package com.example.proe.notification.connect;

import com.example.proe.Model.BodyPushToken;
import com.example.proe.Model.ModelPushToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

  @Headers({
      "Accept: application/json",
      "Content-Type: application/json"})
  @POST("send")
  Call<ModelPushToken> sendNotification(@Header("Authorization") String accessToken, @Body BodyPushToken dataPost);


}
