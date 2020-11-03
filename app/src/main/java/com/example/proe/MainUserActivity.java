package com.example.proe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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

import com.example.proe.Adapter.AdapterBuyer;
import com.example.proe.Adapter.AdapterOrderUser;
import com.example.proe.Model.ModelBuyerUI;
import com.example.proe.Model.ModelOrderUser;
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

public class MainUserActivity extends AppCompatActivity {

    private TextView NameIv,EmailIv,tabpmg,tabbuyer,tabinfo,cityIv;
    private EditText search_bar;
    private ImageButton btnlogout,filterbuyerbtn;
    private ImageView profileIv;
    private RelativeLayout relativepmg,relativebuyer,relativeinfo;
    private RecyclerView buyerRv,orderRv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelBuyerUI> modelBuyerUIS;
    private AdapterBuyer adapterBuyer;

    private ArrayList<ModelOrderUser> OrderUsersliat;
    private AdapterOrderUser adapterOrderUser;

    private String buyerUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        NameIv = findViewById(R.id.NameIv);
        EmailIv = findViewById(R.id.EmailIv);
        tabpmg = findViewById(R.id.tabpmg);
        tabbuyer = findViewById(R.id.tabbuyer);
        tabinfo = findViewById(R.id.tabinfo);
        relativepmg = findViewById(R.id.relativepmg);
        relativeinfo = findViewById(R.id.relativeinfo);
        relativebuyer = findViewById(R.id.relativebuyer);
        buyerRv = findViewById(R.id.buyerRv);
        orderRv = findViewById(R.id.orderRv);

        profileIv = findViewById(R.id.profileIv);
        search_bar =findViewById(R.id.search_bar);



        btnlogout = findViewById(R.id.btnlogout);
        filterbuyerbtn = findViewById(R.id.filterbuyerbtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        showPMGUI();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {

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

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUserActivity.this,EditUserActivity.class));
            }
        });

        tabpmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPMGUI();
            }
        });

        tabbuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBuyerUI();
            }
        });

        tabinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoUI();
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
                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainUserActivity.this,LoginActivity.class));
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
                            String Email = ""+dataSnapshot1.child("Email").getValue();
                            String profileImage = ""+dataSnapshot1.child("profileImage").getValue();
                            String City = ""+dataSnapshot1.child("City").getValue();

                            NameIv.setText(Name);
                            EmailIv.setText(Email);


                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_account_pirple_24dp).into(profileIv);
                            } catch (Exception e) {
                                profileIv.setImageResource(R.drawable.ic_account_pirple_24dp);
                            }

                            //load only buyer in these user city
                            loadBuyer(City);
                            loadOrder();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadOrder() {
        OrderUsersliat = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                OrderUsersliat.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Order");
                    reference.orderByChild("OrderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            ModelOrderUser modelOrderUser =ds.getValue(ModelOrderUser.class);

                                            OrderUsersliat.add(modelOrderUser);
                                        }

                                        adapterOrderUser = new AdapterOrderUser(MainUserActivity.this,OrderUsersliat);
                                        orderRv.setAdapter(adapterOrderUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadBuyer(final String city) {
        modelBuyerUIS = new ArrayList<>();


        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("User");
        databaseReference.orderByChild("AccountType").equalTo("Buyer")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        modelBuyerUIS.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            ModelBuyerUI modelBuyerUI = dataSnapshot1.getValue(ModelBuyerUI.class);

                            String ShopCity = ""+dataSnapshot1.child("City").getValue();

                            //show only user city shop
                            if (ShopCity.equals(city)){
                                modelBuyerUIS.add(modelBuyerUI);
                            }
                        }
                        //setup adapter
                        adapterBuyer = new AdapterBuyer(MainUserActivity.this,modelBuyerUIS);
                        //set adapter to recycleview

                        buyerRv.setAdapter(adapterBuyer);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showPMGUI() {
        relativebuyer.setVisibility(View.GONE);
        relativeinfo.setVisibility(View.GONE);
        relativepmg.setVisibility(View.VISIBLE);

        tabpmg.setTextColor(getResources().getColor(R.color.black));
        tabpmg.setBackgroundResource(R.drawable.sharp_recmenu1);

        tabbuyer.setTextColor(getResources().getColor(R.color.white));
        tabbuyer.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabinfo.setTextColor(getResources().getColor(R.color.white));
        tabinfo.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showBuyerUI() {
        relativebuyer.setVisibility(View.VISIBLE);
        relativeinfo.setVisibility(View.GONE);
        relativepmg.setVisibility(View.GONE);

        tabpmg.setTextColor(getResources().getColor(R.color.white));
        tabpmg.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabbuyer.setTextColor(getResources().getColor(R.color.black));
        tabbuyer.setBackgroundResource(R.drawable.sharp_recmenu1);

        tabinfo.setTextColor(getResources().getColor(R.color.white));
        tabinfo.setBackgroundColor(getResources().getColor(android.R.color.transparent));


    }

    private void showInfoUI() {
        relativebuyer.setVisibility(View.GONE);
        relativeinfo.setVisibility(View.VISIBLE);
        relativepmg.setVisibility(View.GONE);

        tabpmg.setTextColor(getResources().getColor(R.color.white));
        tabpmg.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabbuyer.setTextColor(getResources().getColor(R.color.white));
        tabbuyer.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabinfo.setTextColor(getResources().getColor(R.color.black));
        tabinfo.setBackgroundResource(R.drawable.sharp_recmenu1);
    }

}
