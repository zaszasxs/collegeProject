package com.example.proe;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proe.Adapter.AdapterCartitem;
import com.example.proe.Adapter.AdapterSellItem;
import com.example.proe.Adapter.AdapterSellitemUser;
import com.example.proe.Model.ModelCartitem;
import com.example.proe.Model.ModelSellItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class BuyerDetailActivity extends AppCompatActivity {

    private ImageView probuyerIv;
    private TextView ShopnameIv, PhoneIv, EmailIv, opencloseIv, addressIv,txfilter,txcartcount;
    private ImageButton callbtn,backbtn,mapbtn,filtersellbtn,cartbtn;
    private EditText etsearch;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String BuyerUid;

    private RecyclerView sellitemuserRv;
    private ArrayList<ModelSellItem> sellItemslist;
    private AdapterSellitemUser adapterSellItem;

    private ArrayList<ModelCartitem> cartitems;
    private AdapterCartitem adapterCartitem;


    private String mylatitude, mylongitude , myPhone;
    private String shopName, shopPhone, shopEmail, shopAddress, shoplatitude, shoplongitude;

    private EasyDB easyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_detail);

        probuyerIv = findViewById(R.id.probuyerIv);

        ShopnameIv = findViewById(R.id.shopnameIv);
        PhoneIv = findViewById(R.id.phoneIv);
        EmailIv = findViewById(R.id.emailIv);
        opencloseIv = findViewById(R.id.opencloseIv);
        addressIv = findViewById(R.id.addressIv);
        txfilter = findViewById(R.id.txfilter);
        txcartcount = findViewById(R.id.txcartcount);

        etsearch = findViewById(R.id.etsearch);

        sellitemuserRv = findViewById(R.id.sellitemuserRv);

        backbtn = findViewById(R.id.backbtn);
        callbtn = findViewById(R.id.callbtn);
        mapbtn = findViewById(R.id.mapbtn);
        cartbtn = findViewById(R.id.cartbtn);
        filtersellbtn = findViewById(R.id.filtersellbtn);

        BuyerUid = getIntent().getStringExtra("BuyerUid");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();

        LoadMyInfo();
        LoadBuyerInfo();
        LoadBuyerSellitem();


        easyDB = EasyDB.init(this,"ITEM_DB")
                .setTableName("ITEM_TABLE")
                .addColumn(new Column("Item_ID",new String[]{"text","unique"}))
                .addColumn(new Column("Item_SellID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Finalprice",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quanlity",new String[]{"text","not null"}))
                .doneTableColumn();
        easyDB.deleteAllDataFromTable();


        deletecartData();
        cartCount();

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

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaPhone();
            }
        });

        mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        filtersellbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BuyerDetailActivity.this);
                builder.setTitle("Choose Category").setItems(Constants.itemcategory1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String select = Constants.itemcategory1[which];
                        txfilter.setText(select);
                        if (select.equals("All")){
                            LoadBuyerSellitem();
                        }
                        else{
                            adapterSellItem.getFilter().filter(select);
                        }
                    }
                })
                        .show();
            }
        });

        cartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCart();
            }
        });



    }

    private void deletecartData() {

        easyDB.deleteAllDataFromTable();
    }

    public void cartCount(){
        int count = easyDB.getAllData().getCount();
        if (count <= 0){
            txcartcount.setVisibility(View.GONE);
        }
        else{
            txcartcount.setVisibility(View.VISIBLE);
            txcartcount.setText(""+count);
        }
    }

    public double totalprice = 0.00;

    public TextView txtotalprice;

    private void showDialogCart() {

        cartitems = new ArrayList<>();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);
        TextView shopnameIv = view.findViewById(R.id.shopnameIv);
        RecyclerView cartitemRv = view.findViewById(R.id.cartitemRv);
        TextView txtotal = view.findViewById(R.id.txtotal);
        txtotalprice = view.findViewById(R.id.txtotalprice);
        Button checkoutbtn = view.findViewById(R.id.checkoutbtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        shopnameIv.setText(shopName);
        EasyDB easyDB = EasyDB.init(this,"ITEM_DB")
                .setTableName("ITEM_TABLE")
                .addColumn(new Column("Item_ID",new String[]{"text","unique"}))
                .addColumn(new Column("Item_SellID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Finalprice",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quanlity",new String[]{"text","not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String Sid = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);

            totalprice = totalprice + Double.parseDouble(cost);

            ModelCartitem modelCartitem =  new ModelCartitem(
                    ""+id,
                    ""+Sid,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity
            );

            cartitems.add(modelCartitem);

        }

        adapterCartitem = new AdapterCartitem(this,cartitems);

        cartitemRv.setAdapter(adapterCartitem);

        txtotalprice.setText("฿" +String.format("%.2f",totalprice));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                totalprice = 0.00;

            }
        });

        checkoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mylatitude.equals("")|| mylatitude.equals("null")||mylongitude.equals("")||mylongitude.equals("null")){
                    Toast.makeText(BuyerDetailActivity.this, "Please enter your place before placing order", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (myPhone.equals("")|| myPhone.equals("null")) {
                    Toast.makeText(BuyerDetailActivity.this, "Please enter your phone number before placing order", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cartitems.size() == 0){
                    Toast.makeText(BuyerDetailActivity.this, "No item in cart", Toast.LENGTH_SHORT).show();
                    return;
                }

                submitOrder();
            }
        });
    }

    private void submitOrder() {
        progressDialog.setMessage("Placing Order...");
        progressDialog.show();

        final String timestamp = ""+System.currentTimeMillis();

        String cost = txtotalprice.getText().toString().trim().replace("฿","");

        final HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("OrderID",""+timestamp);
        hashMap.put("OrderTime",""+timestamp);
        hashMap.put("OrderStatus","In Progress");
        hashMap.put("OrderCost",""+cost);
        hashMap.put("OrderBy",""+firebaseAuth.getUid());
        hashMap.put("OrderTo",""+BuyerUid);
        hashMap.put("Latitude",""+mylatitude);
        hashMap.put("Longitude",""+mylongitude);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(BuyerUid).child("Order");
        reference.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for (int i=0 ; i< cartitems.size() ; i++){
                            String Sid = cartitems.get(i).getSid();
                            String id = cartitems.get(i).getId();
                            String cost = cartitems.get(i).getCost();
                            String name = cartitems.get(i).getName();
                            String price = cartitems.get(i).getPrice();
                            String quantity = cartitems.get(i).getQuantity();

                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("Sid",Sid);
                            hashMap1.put("name",name);
                            hashMap1.put("cost",cost);
                            hashMap1.put("price",price);
                            hashMap1.put("quantity",quantity);

                            reference.child(timestamp).child("Items").child(Sid).setValue(hashMap);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(BuyerDetailActivity.this, "Order Placed Successfully...", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(BuyerDetailActivity.this, OrderDetail.class);
                        intent.putExtra("OrderTo",BuyerUid);
                        intent.putExtra("OrderID",timestamp);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(BuyerDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void openMap() {
        String address = "https://maps.google.com/maps?saddr=" + mylatitude + "," + mylongitude + "&daddr=" + shoplatitude + "," + shoplongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void diaPhone() {
        Intent intent1 = new Intent(Intent.ACTION_DIAL);
        intent1.setData(Uri.parse("tel:"+Uri.encode(shopPhone)));
        startActivity(intent1);

    }


    private void LoadBuyerInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(BuyerUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = ""+dataSnapshot.child("name").getValue();
                shopName = ""+dataSnapshot.child("ShopName").getValue();
                shopAddress = ""+dataSnapshot.child("CompleteAddress").getValue();
                shopEmail = ""+dataSnapshot.child("Email").getValue();
                shopPhone = ""+dataSnapshot.child("Phone").getValue();
                shoplatitude = ""+dataSnapshot.child("Latitude").getValue();
                shoplongitude = ""+dataSnapshot.child("Longitude").getValue();
                String profileImage = ""+dataSnapshot.child("profileImage").getValue();
                String shopOpen = ""+dataSnapshot.child("ShopOpen").getValue();


                ShopnameIv.setText(shopName);
                addressIv.setText(shopAddress);
                EmailIv.setText(shopEmail);
                PhoneIv.setText(shopPhone);

                if (shopOpen.equals("true")){
                    opencloseIv.setText("Open");
                }
                else{
                    opencloseIv.setText("Close");
                }
                try {
                    Picasso.get().load(profileImage).into(probuyerIv);
                }
                catch (Exception e ){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LoadMyInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            String Name = ""+dataSnapshot1.child("Name").getValue();
                            String AccountType = ""+dataSnapshot1.child("AccountType").getValue();
                            String Email = ""+dataSnapshot1.child("Email").getValue();
                            myPhone = ""+dataSnapshot1.child("Phone").getValue();
                            String profileImage = ""+dataSnapshot1.child("profileImage").getValue();
                            String City = ""+dataSnapshot1.child("City").getValue();
                            mylatitude  = ""+dataSnapshot1.child("Latitude").getValue();
                            mylongitude  = ""+dataSnapshot1.child("Longitude").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void LoadBuyerSellitem() {

        sellItemslist =new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(BuyerUid).child("SellItem")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        sellItemslist.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            ModelSellItem modelSellItem = ds.getValue(ModelSellItem.class);
                            sellItemslist.add(modelSellItem);
                        }

                        adapterSellItem = new AdapterSellitemUser(BuyerDetailActivity.this,sellItemslist);

                        sellitemuserRv.setAdapter(adapterSellItem);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
