package com.example.proe;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.proe.Model.Result;
import com.example.proe.notification.connect.CallSendNotification;
import com.example.proe.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class addInfomationActivity extends AppCompatActivity {

    private EditText etdescription,ettitle;
    private Button btnadd;
    private ImageButton btnback;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_infomation);

        ettitle = findViewById(R.id.ettitle);
        etdescription = findViewById(R.id.etdescription);
        btnadd = findViewById(R.id.btnadd);
        btnback = findViewById(R.id.btnback);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        btnadd.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputinfomation();
            }
        }));

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private String Infotitle,Infodescription;


    private void inputinfomation() {
        Infotitle = ettitle.getText().toString().trim();
        Infodescription = etdescription.getText().toString().trim();


        if (TextUtils.isEmpty(Infotitle)){
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return;    //don't proceed further
        }

        if (TextUtils.isEmpty(Infodescription)){
            Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show();
            return;    //don't proceed further
        }

        addinfomation();
    }

    private void addinfomation() {
        progressDialog.setMessage("adding Infomation");
        progressDialog.show();
        String timestamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("InfomationID",""+timestamp);
        hashMap.put("Infotitle",""+Infotitle);
        hashMap.put("Infodescription",""+Infodescription);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("Uid",""+firebaseAuth.getUid());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(firebaseAuth.getUid()).child("Information").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getTokensSendNotificationAll("User");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(addInfomationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getTokensSendNotificationAll(String notificationAccountType){
        CallSendNotification.getTokenFirebaseAll(notificationAccountType).observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> tokens) {
                sendInformationNotification(tokens);
            }
        });
    }

    private void sendInformationNotification(ArrayList<String> tokens) {
        String titleNotification = "Information News!!";
        String bodyNotification = Infotitle;

        CallSendNotification.sendNotification(Utils.createObject(titleNotification, bodyNotification, tokens)).observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result modelPushToken) {
                if (modelPushToken.getStatus()) {
                    progressDialog.dismiss();
                    Toast.makeText(addInfomationActivity.this, "Information Added", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(addInfomationActivity.this, "Send notification error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
