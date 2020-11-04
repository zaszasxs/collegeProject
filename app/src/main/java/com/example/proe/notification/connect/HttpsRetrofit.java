package com.example.proe.notification.connect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpsRetrofit {
    private static Retrofit mRetrofit;
    private static HttpLoggingInterceptor mLogging;
    private static OkHttpClient.Builder httpClient;
    private static int timeConnect = 30;
    private static String mBaseUrl = "https://fcm.googleapis.com/fcm/";

    public static Retrofit getInstance(){
        if (mRetrofit == null){
            mLogging = new HttpLoggingInterceptor();
            mLogging.setLevel(HttpLoggingInterceptor.Level.BODY);

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            httpClient = new OkHttpClient.Builder()
                    .readTimeout(timeConnect, TimeUnit.SECONDS)
                    .connectTimeout(timeConnect, TimeUnit.SECONDS)
                    .addInterceptor(mLogging);


            mRetrofit = new Retrofit.Builder()
                    .baseUrl(mBaseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();
        }

        return mRetrofit;
    }

}
