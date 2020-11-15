package com.example.proe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proe.Model.ModelInfoBuyer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class InFormationDetailActivity extends AppCompatActivity {

  private TextView tvInformationId, tvInfomationDate, tvInfomationTitle, tvInformationDetail, tvInfomationBy;
  private ModelInfoBuyer dataObj;
  private String dataCurrent;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_in_formation_detail);


    findViewBy();
    initOnClick();
    initSetView();
  }

  private void initOnClick() {
    findViewById(R.id.btnback).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
  }

  private void findViewBy() {
    tvInformationId = findViewById(R.id.tvInformationId);
    tvInfomationDate = findViewById(R.id.tvInfomationDate);
    tvInfomationTitle = findViewById(R.id.tvInfomationTitle);
    tvInformationDetail = findViewById(R.id.tvInformationDetail);
    tvInfomationBy = findViewById(R.id.tvInfomationBy);
  }

  private void initSetView() {
    dataObj = (ModelInfoBuyer) getIntent().getSerializableExtra("dataObj");
    dataCurrent = getIntent().getStringExtra("dataCurrent");

    tvInformationId.setText(dataObj.getInfomationID());
    tvInfomationDate.setText(dataCurrent);
    tvInfomationTitle.setText(dataObj.getInfotitle());
    tvInformationDetail.setText(dataObj.getInfodescription());

    isCheckStateAccountType();
  }

  private void isCheckStateAccountType() {
    initProgressDialog();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");

    reference.child(dataObj.getUid()).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          String postOwner = "" + dataSnapshot.child("ShopName").getValue();
          tvInfomationBy.setText(postOwner);
        }
        progressDialog.dismiss();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Toast.makeText(InFormationDetailActivity.this, "error " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
      }
    });
  }

  private void initProgressDialog() {
    progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Please wait...");
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();
  }
}