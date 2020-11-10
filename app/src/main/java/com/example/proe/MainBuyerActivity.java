package com.example.proe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proe.Adapter.AdapterInfoBuyer;
import com.example.proe.Adapter.AdapterOrderBuyer;
import com.example.proe.Adapter.AdapterSellItem;
import com.example.proe.Model.ModelInfoBuyer;
import com.example.proe.Model.ModelOrderBuyer;
import com.example.proe.Model.ModelSellItem;
import com.example.proe.Model.Result;
import com.example.proe.notification.SharedPreferences;
import com.example.proe.notification.connect.CallSendNotification;
import com.example.proe.utils.Utils;
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
import java.util.Observable;

public class MainBuyerActivity extends AppCompatActivity {

    private TextView NameIv,ShopnameIv,EmailIv,tabboard,tabinfo,taborder,txfilter,txfilterorder;
    private ImageButton btnlogout,addbtn,filtersellbtn,filterorderbtn,btnreview,postbtn;
    private EditText etsearch;
    private ImageView profileIv;
    private RelativeLayout relativeboard,relativeorder,relativeinfo;
    private RecyclerView sellitemRv,orderRv,infoRv;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelSellItem> sellItemslist;
    private AdapterSellItem adapterSellItem;

    private ArrayList<ModelOrderBuyer> orderBuyerArrayList;
    private AdapterOrderBuyer adapterOrderBuyer;

    private ArrayList<ModelInfoBuyer> infoBuyerArrayList = new ArrayList<>();
    private AdapterInfoBuyer adapterInfoBuyer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_buyer);

        NameIv = findViewById(R.id.nameIv);
        ShopnameIv = findViewById(R.id.shopnameIv);
        EmailIv = findViewById(R.id.emailIv);
        tabboard = findViewById(R.id.tabboard);
        tabinfo = findViewById(R.id.tabinfo);
        taborder = findViewById(R.id.taborder);
        txfilter = findViewById(R.id.txfilter);
        txfilterorder = findViewById(R.id.txfilterorder);

        etsearch = findViewById(R.id.etsearch);

        relativeboard = findViewById(R.id.relativeboard);
        relativeinfo = findViewById(R.id.relativeorder);
        relativeorder = findViewById(R.id.relativeorder);

        profileIv = findViewById(R.id.profileIv);

        btnlogout = findViewById(R.id.btnlogout);
        addbtn = findViewById(R.id.addbtn);
        filtersellbtn = findViewById(R.id.filtersellbtn);
        filterorderbtn = findViewById(R.id.filterorderbtn);
        btnreview = findViewById(R.id.btnreview);
        postbtn = findViewById(R.id.postbtn);
        sellitemRv = findViewById(R.id.sellitemRv);
        orderRv = findViewById(R.id.orderRv);
        infoRv = findViewById(R.id.infoRv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        clearNotificationAll();
        checkUser();
        loadSellitem();
        loadOrderBuyer();

        showDashboardUI();

        loadInfoBuyer();

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

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainBuyerActivity.this,addInfomationActivity.class));
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

        taborder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderUI();
            }
        });

        tabinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInformationUI();
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

        filterorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] option ={"ALL","In Progress","Completed","Cancelled"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainBuyerActivity.this);
                builder.setTitle("Filter Order: ")
                        .setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which ==0){
                                    txfilterorder.setText("Showing All Order");
                                    adapterOrderBuyer.getFilter().filter("");
                                }
                                else{

                                    String optionClick = option[which];
                                    txfilterorder.setText("Showing " +optionClick+ " Order");
                                    adapterOrderBuyer.getFilter().filter(optionClick);

                                }
                            }
                        })
                        .show();
            }
        });

        btnreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainBuyerActivity.this,ShopReviewActivity.class);
                intent.putExtra("BuyerUid", ""+ firebaseAuth.getUid());
                startActivity(intent);
            }
        });

    }

    private void loadInfoBuyer() {
        adapterInfoBuyer = new AdapterInfoBuyer(MainBuyerActivity.this,infoBuyerArrayList);
        infoRv.setAdapter(adapterInfoBuyer);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(firebaseAuth.getUid()).child("Information")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        infoBuyerArrayList.clear();

                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            ModelInfoBuyer modelInfoBuyer = ds.getValue(ModelInfoBuyer.class);
                            infoBuyerArrayList.add(modelInfoBuyer);
                        }
                        adapterInfoBuyer.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void loadOrderBuyer() {
        orderBuyerArrayList = new ArrayList<>();
        adapterOrderBuyer = new AdapterOrderBuyer(MainBuyerActivity.this,orderBuyerArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainBuyerActivity.this);
        orderRv.setLayoutManager(layoutManager);
        orderRv.setAdapter(adapterOrderBuyer);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(firebaseAuth.getUid()).child("Order")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderBuyerArrayList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            ModelOrderBuyer modelOrderBuyer = ds.getValue(ModelOrderBuyer.class);
                            orderBuyerArrayList.add(modelOrderBuyer);
                        }

                        adapterOrderBuyer.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        try {
                            SharedPreferences.clearData(getApplicationContext());
                            progressDialog.setMessage("Logging in");
                            progressDialog.show();
                            firebaseAuth.signOut();
                            checkUser();
                        }catch (Exception e){

                        }
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
        clearNotificationAll();
        if(user==null){
            startActivity(new Intent(MainBuyerActivity.this,LoginActivity.class));
            finish();
        }
        else{
            LoadMyinfo();
        }
    }

    private void clearNotificationAll(){
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
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



    private void showDashboardUI() {
        relativeboard.setVisibility(View.VISIBLE);
        relativeinfo.setVisibility(View.GONE);
        relativeorder.setVisibility(View.GONE);

        tabboard.setTextColor(getResources().getColor(R.color.black));
        tabboard.setBackgroundResource(R.drawable.sharp_recmenu1);

        tabinfo.setTextColor(getResources().getColor(R.color.white));
        tabinfo.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        taborder.setTextColor(getResources().getColor(R.color.white));
        taborder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrderUI() {
        isViewInFomation(false);

        taborder.setTextColor(getResources().getColor(R.color.black));
        taborder.setBackgroundResource(R.drawable.sharp_recmenu1);

        tabboard.setTextColor(getResources().getColor(R.color.white));
        tabboard.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabinfo.setTextColor(getResources().getColor(R.color.white));
        tabinfo.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showInformationUI() {
        isViewInFomation(true);

        tabinfo.setTextColor(getResources().getColor(R.color.black));
        tabinfo.setBackgroundResource(R.drawable.sharp_recmenu1);

        tabboard.setTextColor(getResources().getColor(R.color.white));
        tabboard.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        taborder.setTextColor(getResources().getColor(R.color.white));
        taborder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void isViewInFomation(boolean info) {
        relativeboard.setVisibility(View.GONE);
        relativeinfo.setVisibility(View.GONE);
        relativeorder.setVisibility(View.VISIBLE);
        if (info) {
            findViewById(R.id.infoRv).setVisibility(View.VISIBLE);
            findViewById(R.id.orderRv).setVisibility(View.GONE);
            findViewById(R.id.filterorderbtn).setVisibility(View.GONE);
            findViewById(R.id.txfilterorder).setVisibility(View.GONE);
        }else {
            findViewById(R.id.infoRv).setVisibility(View.GONE);
            findViewById(R.id.orderRv).setVisibility(View.VISIBLE);
            findViewById(R.id.filterorderbtn).setVisibility(View.VISIBLE);
            findViewById(R.id.txfilterorder).setVisibility(View.VISIBLE);
        }
    }

}

