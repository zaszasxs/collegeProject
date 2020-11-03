package com.example.proe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proe.Adapter.AdapterSellItem;
import com.example.proe.Model.ModelSellItem;
import com.example.proe.notification.SharedPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainBuyerActivity extends AppCompatActivity {

    private TextView NameIv,ShopnameIv,EmailIv,tabboard,tabinfo,txfilter;
    private ImageButton btnlogout,addbtn,filtersellbtn;
    private EditText etsearch;
    private ImageView profileIv;
    private RelativeLayout relativeboard,relativeinfo;
    private RecyclerView sellitemRv;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelSellItem> sellItemslist;
    private AdapterSellItem adapterSellItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_buyer);

        NameIv = findViewById(R.id.nameIv);
        ShopnameIv = findViewById(R.id.shopnameIv);
        EmailIv = findViewById(R.id.emailIv);
        tabboard = findViewById(R.id.tabboard);
        tabinfo = findViewById(R.id.tabinfo);
        txfilter = findViewById(R.id.txfilter);

        etsearch = findViewById(R.id.etsearch);


        relativeboard = findViewById(R.id.relativeboard);
        relativeinfo = findViewById(R.id.relativeinfo);

        profileIv = findViewById(R.id.profileIv);

        btnlogout = findViewById(R.id.btnlogout);
        addbtn = findViewById(R.id.addbtn);
        filtersellbtn = findViewById(R.id.filtersellbtn);

        sellitemRv = findViewById(R.id.sellitemRv);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadSellitem();

        showDashboardUI();

        etsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterSellItem.getFilter().filter(s);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeOffline();
            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainBuyerActivity.this,addSellActivity.class));
            }
        });


        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainBuyerActivity.this,EditBuyerActivity.class));
            }
        });

        tabboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDashboardUI();
            }
        });

        tabinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoUI();
            }
        });

        filtersellbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainBuyerActivity.this);
                builder.setTitle("Choose Category").setItems(Constants.itemcategory1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String select = Constants.itemcategory1[which];
                        txfilter.setText(select);
                        if (select.equals("All")){
                            loadSellitem();
                        }
                        else{
                            loadFilterItem(select);
                        }
                    }
                })
                .show();
            }
        });

    }

    private void loadFilterItem(final String select) {
        sellItemslist = new ArrayList<>();


        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(firebaseAuth.getUid()).child("SellItem")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        sellItemslist.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                            String Itemcategory = ""+dataSnapshot1.child("Itemcategory").getValue();

                            if (select.equals(Itemcategory)){
                                ModelSellItem modelSellItem = dataSnapshot1.getValue(ModelSellItem.class);
                                sellItemslist.add(modelSellItem);
                            }


                        }
                        //setup adapter
                        adapterSellItem = new AdapterSellItem(MainBuyerActivity.this,sellItemslist);
                        //set adapter to recycleview
                        sellitemRv.setAdapter(adapterSellItem);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadSellitem() {
        sellItemslist = new ArrayList<>();

        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(firebaseAuth.getUid()).child("SellItem")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        sellItemslist.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            ModelSellItem modelSellItem = dataSnapshot1.getValue(ModelSellItem.class);

                            sellItemslist.add(modelSellItem);

                        }
                        //setup adapter
                        adapterSellItem = new AdapterSellItem(MainBuyerActivity.this,sellItemslist);
                        //set adapter to recycleview
                        sellitemRv.setAdapter(adapterSellItem);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void MakeOffline() {
        progressDialog.setMessage("Logging Out...");

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SharedPreferences.clearData(getApplicationContext());
                        progressDialog.setMessage("Logging in");
                        progressDialog.show();
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainBuyerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainBuyerActivity.this,LoginActivity.class));
            finish();
        }
        else{
            LoadMyinfo();
        }
    }

    private void LoadMyinfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            String Name = ""+dataSnapshot1.child("Name").getValue();
                            String AccountType = ""+dataSnapshot1.child("AccountType").getValue();
                            String ShopName = ""+dataSnapshot1.child("ShopName").getValue();
                            String Email = ""+dataSnapshot1.child("Email").getValue();
                            String profileImage = ""+dataSnapshot1.child("profileImage").getValue();


                            NameIv.setText(Name);
                            EmailIv.setText(Email);
                            ShopnameIv.setText(ShopName);



                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_account_buyer_24dp).into(profileIv);
                            }
                            catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_account_buyer_24dp);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showInfoUI() {
        relativeboard.setVisibility(View.GONE);
        relativeinfo.setVisibility(View.VISIBLE);

        tabboard.setTextColor(getResources().getColor(R.color.white));
        tabboard.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabinfo.setTextColor(getResources().getColor(R.color.black));
        tabinfo.setBackgroundResource(R.drawable.sharp_recmenu1);
    }

    private void showDashboardUI() {
        relativeboard.setVisibility(View.VISIBLE);
        relativeinfo.setVisibility(View.GONE);

        tabinfo.setTextColor(getResources().getColor(R.color.white));
        tabinfo.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabboard.setTextColor(getResources().getColor(R.color.black));
        tabboard.setBackgroundResource(R.drawable.sharp_recmenu1);
    }

}

