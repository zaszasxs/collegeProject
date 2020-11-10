package com.example.proe.notification;

import android.content.Context;

public class SharedPreferences {
    private final static String KEY_TOKEN = "TOKEN";
    private final static String KEY_USER_TYPE = "USER_TYPE";
    private final static String KEY_DEVICE_CONNECT = "DEVICE_CONNECT";

    public static void saveDeviceConnect(Context context, String token) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DEVICE_CONNECT, token);
        editor.apply();
    }

    public static String getDeviceConnect(Context context) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DEVICE_CONNECT, "");
    }

    public static void saveToken(Context context, String token) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    public static void saveUserType(Context context, String token) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_TYPE, token);
        editor.apply();
    }

    public static String getUserType(Context context) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("USER", context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_TYPE, "");
    }

    public static void clearData(Context context){
        saveUserType(context,"");
        saveToken(context,"");
        saveDeviceConnect(context,"");
    }
}
