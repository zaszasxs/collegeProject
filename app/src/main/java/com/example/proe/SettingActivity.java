package com.example.proe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingActivity extends AppCompatActivity {

    private ImageButton btnback;
    private TextView txnofistatus;
    private SwitchCompat fcmswitch;

    private static final String enableMessage = "Notification are enable";
    private static final String disableMessage = "Notification are disable";

    private FirebaseAuth firebaseAuth;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEdit;

    private boolean isChecked = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btnback = findViewById(R.id.btnback);
        txnofistatus = findViewById(R.id.txnofistatus);
        fcmswitch = findViewById(R.id.fcmswitch);

        firebaseAuth = FirebaseAuth.getInstance();

        sp = getSharedPreferences("SETTINGS_SP",MODE_PRIVATE);
        isChecked = sp.getBoolean("FCM_ENABLED",false);
        fcmswitch.setChecked(isChecked);
        if (isChecked){
            txnofistatus.setText(enableMessage);
        }
        else{
            txnofistatus.setText(disableMessage);
        }

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fcmswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    subscribeToTopic();
                }
                else{
                    unsubscribeToTopic();
                }
            }
        });
    }

    private void subscribeToTopic(){

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        spEdit = sp.edit();
                        spEdit.putBoolean("FCM_ENABLED",true);
                        spEdit.apply();

                        Toast.makeText(SettingActivity.this, ""+enableMessage, Toast.LENGTH_SHORT).show();
                        txnofistatus.setText(enableMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unsubscribeToTopic(){

        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        spEdit = sp.edit();
                        spEdit.putBoolean("FCM_ENABLED",false);
                        spEdit.apply();

                        Toast.makeText(SettingActivity.this, ""+disableMessage, Toast.LENGTH_SHORT).show();
                        txnofistatus.setText(disableMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
