package com.example.proe.notification.connect;

import android.util.Log;
import android.widget.Toast;

import com.example.proe.LoginActivity;
import com.example.proe.Model.BodyPushToken;
import com.example.proe.Model.ModelOrderDetail2;
import com.example.proe.Model.ModelPushToken;
import com.example.proe.Model.ModelSellItem;
import com.example.proe.Model.ModelTokenUser;
import com.example.proe.Model.Result;
import com.example.proe.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CallSendNotification {
  private static String TAG = CallSendNotification.class.getSimpleName();
  private static ApiService mApiService;

  public static MutableLiveData<Result> sendNotification(BodyPushToken bodyPushToken) {
    final MutableLiveData<Result> tokenMutableLiveData = new MutableLiveData<>();
    final Result result = new Result();
    Retrofit mHttpsRetrofit = HttpsRetrofit.getInstance();
    mApiService = mHttpsRetrofit.create(ApiService.class);

    mApiService.sendNotification(Constant.KEY_PUSH, bodyPushToken).enqueue(new Callback<ModelPushToken>() {
      @Override
      public void onResponse(Call<ModelPushToken> call, Response<ModelPushToken> response) {
        if (response.isSuccessful()) {
          result.setStatus(true);
          result.setMessage("Success");
          tokenMutableLiveData.setValue(result);
        }
        Log.i(TAG, "Success : " + response);
      }

      @Override
      public void onFailure(Call<ModelPushToken> call, Throwable t) {
        Log.i(TAG, "Error : " + t.getMessage());
        result.setStatus(false);
        result.setMessage(t.getMessage());
        tokenMutableLiveData.setValue(result);
      }
    });

    return tokenMutableLiveData;
  }

  public static MutableLiveData<ArrayList<String>> getTokenFirebaseAll(String userType) {
    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tokenUser");
    final MutableLiveData<ArrayList<String>> token = new MutableLiveData<>();
    final ArrayList<String> tokensList = new ArrayList<>();

    databaseReference.orderByChild("accountType").equalTo(userType).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          for (DataSnapshot ds : dataSnapshot.getChildren()) {
            ModelTokenUser modelTokenUser = ds.getValue(ModelTokenUser.class);
            String token = modelTokenUser.getToken();
            tokensList.add(token);
          }
        }
        token.setValue(tokensList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        token.setValue(tokensList);
      }
    });
    return token;
  }

  public static MutableLiveData<String> getTokenFirebase(String uid) {
    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tokenUser");
    final MutableLiveData<String> token = new MutableLiveData<>();
    databaseReference.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          token.setValue(Utils.recArrayList(dataSnapshot).get(0).get("token").toString());
        } else {
          token.setValue("");
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        token.setValue("");
      }
    });
    return token;
  }
}
