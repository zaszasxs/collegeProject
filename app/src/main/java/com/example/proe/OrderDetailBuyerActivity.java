package com.example.proe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proe.Adapter.AdapterOrderDetail;
import com.example.proe.Adapter.AdapterOrderDetail2;
import com.example.proe.Model.ModelOrderDetail;
import com.example.proe.Model.ModelOrderDetail2;
import com.example.proe.Model.Result;
import com.example.proe.notification.connect.CallSendNotification;
import com.example.proe.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderDetailBuyerActivity extends AppCompatActivity {

  String OrderID, OrderBy, OrderTo;
  String sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude;

  private ImageButton btnback, editbtn, mapbtn;
  private TextView txorder, txdate, txstatus, EmailIv, phoneIv, txtotalitem, txtotalprice, addressIv;
  private RecyclerView itemRv;

  private FirebaseAuth firebaseAuth;

  private ArrayList<ModelOrderDetail> orderDetailArrayList;
  private AdapterOrderDetail adapterOrderDetail;


  private ArrayList<ModelOrderDetail2> sellItemslist;
  private AdapterOrderDetail2 adapterSellItem;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_order_detail_buyer);

    OrderID = getIntent().getStringExtra("OrderID");
    OrderBy = getIntent().getStringExtra("OrderBy");
    OrderTo = getIntent().getStringExtra("OrderTo");

    btnback = findViewById(R.id.btnback);
    editbtn = findViewById(R.id.editbtn);
    mapbtn = findViewById(R.id.mapbtn);

    txorder = findViewById(R.id.txorder);
    txdate = findViewById(R.id.txdate);
    txstatus = findViewById(R.id.txstatus);
    txtotalitem = findViewById(R.id.txtotalitem);
    txtotalprice = findViewById(R.id.txtotalprice);
    EmailIv = findViewById(R.id.EmailIv);
    phoneIv = findViewById(R.id.phoneIv);
    addressIv = findViewById(R.id.addressIv);

    itemRv = findViewById(R.id.itemRv);

    firebaseAuth = FirebaseAuth.getInstance();
    loadMyInfo();
    loadBuyerInfo();
    loadOrderDetail();
    loadOrderSellitem();

    btnback.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    mapbtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openMap();
      }
    });

    editbtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        editOrderStatud();
      }
    });

  }

  private void editOrderStatud() {
    final String[] option = {"กำลังดำเนินการ", "เสร็จสิ้น", "ยกเลิก"};

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("สถานะรายการสินค้าขณะนี้")
        .setItems(option, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int i) {
            String seletOption = option[i];
            editOrderStatudDialog(seletOption);
          }
        }).show();
  }

  private void editOrderStatudDialog(final String seletOption) {
    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("OrderStatus", "" + seletOption);

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
    reference.child(firebaseAuth.getUid()).child("Order").child(OrderID)
        .updateChildren(hashMap)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {

            String message = "ตอนนี้รายการสินค้าของคุณ" + seletOption;

            Toast.makeText(OrderDetailBuyerActivity.this, message, Toast.LENGTH_SHORT).show();

            prepareNotificationMessage(OrderID, message);
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Toast.makeText(OrderDetailBuyerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
  }

  private void openMap() {
    String address = "https://maps.google.com/maps?saddr=" + sourceLatitude + "," + sourceLongitude + "&daddr=" + destinationLatitude + "," + destinationLongitude;
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
    startActivity(intent);
  }

  private void loadMyInfo() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
    reference.child(firebaseAuth.getUid())
        .addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            sourceLatitude = "" + dataSnapshot.child("Latitude").getValue();
            sourceLongitude = "" + dataSnapshot.child("Longitude").getValue();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });
  }

  private void loadBuyerInfo() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
    reference.child(OrderBy)
        .addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            destinationLatitude = "" + dataSnapshot.child("Latitude").getValue();
            destinationLongitude = "" + dataSnapshot.child("Longitude").getValue();
            String Email = "" + dataSnapshot.child("Email").getValue();
            String Phone = "" + dataSnapshot.child("Phone").getValue();

            EmailIv.setText(Email);
            phoneIv.setText(Phone);
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });
  }

  private void loadOrderDetail() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
    reference.child(firebaseAuth.getUid()).child("Order").child(OrderID)
        .addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String OrderBy = "" + dataSnapshot.child("OrderBy").getValue();
            String OrderCost = "" + dataSnapshot.child("OrderCost").getValue();
            String OrderID = "" + dataSnapshot.child("OrderID").getValue();
            String OrderStatus = "" + dataSnapshot.child("OrderStatus").getValue();
            String OrderTime = "" + dataSnapshot.child("OrderTime").getValue();
            String OrderTo = "" + dataSnapshot.child("OrderTo").getValue();
            String Latitude = "" + dataSnapshot.child("Latitude").getValue();
            String Longitude = "" + dataSnapshot.child("Longitude").getValue();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(OrderTime));
            String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();

            if (OrderStatus.equals("กำลังดำเนินการ")) {
              txstatus.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if (OrderStatus.equals("เสร็จสิ้น")) {
              txstatus.setTextColor(getResources().getColor(R.color.green));
            } else if (OrderStatus.equals("ยกเลิก")) {
              txstatus.setTextColor(getResources().getColor(R.color.red));
            }

            txorder.setText(OrderID);
            txstatus.setText(OrderStatus);
            txtotalprice.setText("฿" + OrderCost);
            txdate.setText(formatedDate);

            findAddress(Latitude, Longitude);
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
      addresses = geocoder.getFromLocation(lat, lon, 1);

      String address = addresses.get(0).getAddressLine(0);
      addressIv.setText(address);
    } catch (Exception e) {
      Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void loadOrderSellitem() {

    orderDetailArrayList = new ArrayList<>();
    initListOrder();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(OrderTo).child("Order").child(OrderID).child("Items");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        sellItemslist.clear();
        if (dataSnapshot.exists()) {
          for (DataSnapshot ds : dataSnapshot.getChildren()) {

            ModelOrderDetail2 question = ds.getValue(ModelOrderDetail2.class);
            sellItemslist.add(question);

            adapterSellItem.notifyDataSetChanged();

            txtotalitem.setText("" + sellItemslist.size());
          }
        }
        adapterSellItem.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void initListOrder() {
    sellItemslist = new ArrayList<>();
    adapterSellItem = new AdapterOrderDetail2(OrderDetailBuyerActivity.this, sellItemslist);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    itemRv.setLayoutManager(layoutManager);
    itemRv.setAdapter(adapterSellItem);
  }


  private void prepareNotificationMessage(String OrderID, String message) {
    //when user place, send notification to seller

    //preparex data for notification
    String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
    String NOTIFICATION_TITLE = "Your Order" + OrderID;
    String NOTIFICATION_MESSAGE = "" + message;
    String NOTIFICATION_TYPE = "OrderStatusChanged";

    JSONObject notificationJo = new JSONObject();
    JSONObject notificationBodyJo = new JSONObject();

    try {
      notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
      notificationBodyJo.put("Uid", OrderBy);
      notificationBodyJo.put("BuyerUid", firebaseAuth.getUid());
      notificationBodyJo.put("OrderID", OrderID);
      notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
      notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);
      //where to send
      notificationJo.put("to", NOTIFICATION_TOPIC);
      notificationJo.put("data", notificationBodyJo);
    } catch (Exception e) {
      Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }


    simpleGetTokenFromUID(OrderBy, OrderID,message);

  }

  private void sendFCMNotification(JSONObject notificationJo) {

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        //notification sent

      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        //notification failed

      }
    }) {
      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "key = " + Constants.FCM_KEY);

        return headers;
      }
    };

    Volley.newRequestQueue(this).add(jsonObjectRequest);
  }

  private void simpleSendNotification(String recipientToken, final String OrderID, String message) {
    ArrayList<String> tokens = new ArrayList<>();
    tokens.add(recipientToken);
    // ในกรณีที่ต้องการส่ง 2 เครื่อง เช่น ต้องการส่งเครื่อง A และเครื่องตัวเอง เปิดคอมเม้นด้านล่าง
    //tokens.add(SharedPreferences.getToken(this));

    String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
    String NOTIFICATION_TITLE = "รายการสินค้า : " + OrderID;
    String NOTIFICATION_MESSAGE = "" + message ;
    String NOTIFICATION_TYPE = "OrderStatusChanged";

    CallSendNotification.sendNotification(Utils.createObject(NOTIFICATION_TITLE, NOTIFICATION_MESSAGE, tokens)).observe(this, new Observer<Result>() {
      @Override
      public void onChanged(Result modelPushToken) {

      }
    });
  }

  private void simpleGetTokenFromUID(String uid, final String OrderID, String message) {
    CallSendNotification.getTokenFirebase(uid).observe(this, new Observer<String>() {
      @Override
      public void onChanged(String token) {
        simpleSendNotification(token, OrderID,message);
      }
    });

  }
}
