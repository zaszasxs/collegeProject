package com.example.proe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proe.Model.ModelPushToken;
import com.example.proe.Model.Result;
import com.example.proe.notification.SharedPreferences;
import com.example.proe.notification.connect.CallSendNotification;
import com.example.proe.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

public class LoginActivity extends AppCompatActivity {
  private static String TAG = LoginActivity.class.getSimpleName();
  private EditText etemail, etpassword;
  private Button btnlogin;
  private TextView txforgot, txnoaccount;

  private FirebaseAuth firebaseAuth;
  private ProgressDialog progressDialog;
  private String token = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    etemail = findViewById(R.id.etemail);
    etpassword = findViewById(R.id.etpassword);
    txforgot = findViewById(R.id.txforgot);
    txnoaccount = findViewById(R.id.txnoaccount);
    btnlogin = findViewById(R.id.btnlogin);

    firebaseAuth = FirebaseAuth.getInstance();
    progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Please wait");
    progressDialog.setCanceledOnTouchOutside(false);

    txnoaccount.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(LoginActivity.this, RegisterUserActivity.class));
      }
    });

    txforgot.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
      }
    });

    btnlogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        LoginUser();
      }
    });

    FirebaseMessaging.getInstance().getToken()
        .addOnCompleteListener(new OnCompleteListener<String>() {
          @Override
          public void onComplete(@NonNull Task<String> task) {
            if (!task.isSuccessful()) {
              Log.w(TAG, "Fetching FCM registration token failed", task.getException());
              return;
            }
            // Get new FCM registration token
            String newToken = task.getResult();
            token = newToken;
            Log.i("LoginActivityToken", "token == " + newToken);

          }
        });
  }

  private String Email, Password;

  private void LoginUser() {
    Email = etemail.getText().toString().trim();
    Password = etpassword.getText().toString().trim();

    if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
      Toast.makeText(this, "Invaild email pattern", Toast.LENGTH_SHORT).show();
      return;
    }

    if (TextUtils.isEmpty(Password)) {
      Toast.makeText(this, "enter password", Toast.LENGTH_SHORT).show();
      return;
    }

    progressDialog.setMessage("Logging in");
    progressDialog.show();

    firebaseAuth.signInWithEmailAndPassword(Email, Password)
        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
          @Override
          public void onSuccess(AuthResult authResult) {
            makeOnline();
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
  }

  private void makeOnline() {
    progressDialog.setMessage("Checking User...");

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("online", "true");

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
    databaseReference.child(firebaseAuth.getUid()).updateChildren(hashMap)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {
            checkType();
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
  }

  private void checkType() {
    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
    databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
        .addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
              updateToken(Utils.recArrayList(dataSnapshot), dataSnapshot);
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });
  }

  private void updateToken(final ArrayList<HashMap<String, Object>> userData, final DataSnapshot dataSnapshot) {
    String checkUidEntry = "";

    for (int i = 0; i < userData.size(); i++) {
      if (Objects.equals(userData.get(0).get("uid"), firebaseAuth.getUid())) {
        checkUidEntry = userData.get(0).get("uid").toString();
        break;
      }
    }

    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tokenUser");
    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tokenUser");

    databaseReference.orderByChild("uid").equalTo(checkUidEntry).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
        String email = userData.get(0).get("Email").toString();
        String uid = userData.get(0).get("uid").toString();

        if (dataSnapshot.exists()) {
          Map<String, Object> childUpdates = new HashMap<>();
          childUpdates.put(firebaseAuth.getUid(), Utils.createModelTokenUser(email, token, uid));

          reference.updateChildren(childUpdates)
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                  loginSuccess(userData.get(0));
                }
              })
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
              });
        } else {
          reference.child(firebaseAuth.getUid()).setValue(Utils.createModelTokenUser(email, token, uid))
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                  loginSuccess(userData.get(0));
                }
              })
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
              });
        }
        progressDialog.dismiss();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void loginSuccess(HashMap<String, Object> dataSnapshot) {
    String AccountType = "" + dataSnapshot.get("AccountType");
    SharedPreferences.saveToken(this, token);
    SharedPreferences.saveUserType(this,AccountType);

    if (AccountType.equals("Buyer")) {
      progressDialog.dismiss();
      startActivity(new Intent(LoginActivity.this, MainBuyerActivity.class));
      finish();
    } else {
      progressDialog.dismiss();
      startActivity(new Intent(LoginActivity.this, MainUserActivity.class));
      finish();
    }
  }


  /////////////////// simple notification /////////////////////
  /////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////

  private void simpleSendNotification(String recipientToken) {
    ArrayList<String> tokens = new ArrayList<>();
    tokens.add(recipientToken);
    // ในกรณีที่ต้องการส่ง 2 เครื่อง เช่น ต้องการส่งเครื่อง A และเครื่องตัวเอง เปิดคอมเม้นด้านล่าง
    //tokens.add(SharedPreferences.getToken(this));
    String titleNotification = "SimpleTitle";
    String bodyNotification = "Body";

    CallSendNotification.sendNotification(Utils.createObject(titleNotification, bodyNotification, tokens)).observe(this, new Observer<Result>() {
      @Override
      public void onChanged(Result modelPushToken) {
        if (modelPushToken.getStatus()) {
          Toast.makeText(LoginActivity.this, "send success", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(LoginActivity.this, "send error", Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private void simpleGetTokenFromUID(String uid) {
    CallSendNotification.getTokenFirebase(uid).observe(this, new Observer<String>() {
      @Override
      public void onChanged(String token) {
        Toast.makeText(getApplicationContext(), "" + token, Toast.LENGTH_SHORT).show();
      }
    });
    Toast.makeText(this, ""+CallSendNotification.getTokenFirebase(uid).getValue(), Toast.LENGTH_SHORT).show();
  }

}
