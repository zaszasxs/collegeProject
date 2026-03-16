package com.example.proe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.proe.Adapter.AdapterSellItem;
import com.example.proe.Model.ModelSellItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductionPartlistActivity extends AppCompatActivity {

    private ImageButton btnback, filterbtn;
    private EditText etsearch;
    private TextView txfilter;
    private RecyclerView partlistRv;

    private ArrayList<ModelSellItem> allPartList;
    private ArrayList<ModelSellItem> displayList;
    private AdapterSellItem adapterSellItem;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_partlist);

        btnback = findViewById(R.id.btnback);
        filterbtn = findViewById(R.id.filterbtn);
        etsearch = findViewById(R.id.etsearch);
        txfilter = findViewById(R.id.txfilter);
        partlistRv = findViewById(R.id.partlistRv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        allPartList = new ArrayList<>();
        displayList = new ArrayList<>();
        adapterSellItem = new AdapterSellItem(this, displayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        partlistRv.setLayoutManager(layoutManager);
        partlistRv.setAdapter(adapterSellItem);

        loadAllPartItems();

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterSellItem.getFilter().filter(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        filterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductionPartlistActivity.this);
                builder.setTitle("เลือกหมวดหมู่ประเภท")
                        .setItems(Constants.itemcategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String select = Constants.itemcategory1[which];
                                if (select.equals("ทั้งหมด")) {
                                    txfilter.setText("แสดงทั้งหมด");
                                    applyFilter(null);
                                } else {
                                    txfilter.setText("แสดง: " + select);
                                    applyFilter(select);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    private void loadAllPartItems() {
        progressDialog.setMessage("กำลังโหลดข้อมูล...");
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allPartList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : userSnapshot.child("SellItem").getChildren()) {
                        ModelSellItem modelSellItem = itemSnapshot.getValue(ModelSellItem.class);
                        if (modelSellItem != null) {
                            allPartList.add(modelSellItem);
                        }
                    }
                }
                applyFilter(null);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void applyFilter(String category) {
        displayList.clear();
        for (ModelSellItem item : allPartList) {
            if (category == null || category.equals(item.getItemcategory())) {
                displayList.add(item);
            }
        }
        adapterSellItem = new AdapterSellItem(this, displayList);
        partlistRv.setAdapter(adapterSellItem);
        etsearch.setText("");
    }
}

