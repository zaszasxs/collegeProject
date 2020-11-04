package com.example.proe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.proe.Adapter.AdapterOrderDetail;
import com.example.proe.Model.ModelOrderDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    String OrderTo,OrderID;

    private ImageButton btnback,btnreview;
    private TextView txorder,txdate,txstatus,shopnameIv,txtotalitem,txtotalprice,addressIv;

    private RecyclerView itemRv;
    private ArrayList<ModelOrderDetail> orderDetailArrayList ;
    private RecyclerView.Adapter adapterOrderDetail;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        btnback = findViewById(R.id.btnback);
        txorder = findViewById(R.id.txorder);
        txdate = findViewById(R.id.txdate);
        txstatus = findViewById(R.id.txstatus);
        shopnameIv = findViewById(R.id.shopnameIv);
        txtotalprice = findViewById(R.id.txtotalprice);
        txtotalitem = findViewById(R.id.txtotalitem);
        addressIv = findViewById(R.id.addressIv);
        itemRv = findViewById(R.id.itemRv);
        btnreview =findViewById(R.id.btnreview);

        Intent intent = getIntent();
        OrderTo = intent.getStringExtra("OrderTo");
        OrderID = intent.getStringExtra("OrderID");

        firebaseAuth = FirebaseAuth.getInstance();

        LoadBuyerInfo();
        LoadOrderDetail();
        LoadOrderSellitem();

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(OrderDetailActivity.this,WriteReviewActivity.class);
                intent1.putExtra("BuyerUid",OrderTo);
                startActivity(intent1);
            }
        });


    }

    private void LoadOrderSellitem() {

        orderDetailArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(OrderTo).child("Order").child(OrderID).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            ModelOrderDetail modelOrderDetail = ds.getValue(ModelOrderDetail.class);
                            orderDetailArrayList.add(modelOrderDetail);
                        }

                        adapterOrderDetail = new AdapterOrderDetail(OrderDetailActivity.this, orderDetailArrayList);
                        itemRv.setAdapter(adapterOrderDetail);

                        txtotalitem.setText(""+dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void LoadOrderDetail() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(OrderTo).child("Order").child(OrderID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String OrderBy = ""+dataSnapshot.child("OrderBy").getValue();
                        String OrderCost = ""+dataSnapshot.child("OrderCost").getValue();
                        String OrderID = ""+dataSnapshot.child("OrderID").getValue();
                        String OrderStatus = ""+dataSnapshot.child("OrderStatus").getValue();
                        String OrderTime = ""+dataSnapshot.child("OrderTime").getValue();
                        String OrderTo = ""+dataSnapshot.child("OrderTo").getValue();
                        String Latitude = ""+dataSnapshot.child("Latitude").getValue();
                        String Longitude = ""+dataSnapshot.child("Longitude").getValue();

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(OrderTime));
                        String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();

                        if (OrderStatus.equals("In Progress")){
                            txstatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                        else if (OrderStatus.equals("Completed")){
                            txstatus.setTextColor(getResources().getColor(R.color.green));
                        }
                        else if (OrderStatus.equals("Cancelled")) {
                            txstatus.setTextColor(getResources().getColor(R.color.red));
                        }

                        txorder.setText(OrderID);
                        txstatus.setText(OrderStatus);
                        txtotalprice.setText("฿"+OrderCost);
                        txdate.setText(formatedDate);

                        findAddress(Latitude,   Longitude);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void findAddress(String latitude, String longitude) {

        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat,lon,1);

            String address = addresses.get(0).getAddressLine(0);
            addressIv.setText(address);
        }
        catch (Exception e){

        }
    }

    private void LoadBuyerInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(OrderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String ShopName = ""+dataSnapshot.child("ShopName").getValue();
                        shopnameIv.setText(ShopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
