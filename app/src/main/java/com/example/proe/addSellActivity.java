package com.example.proe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class addSellActivity extends AppCompatActivity {

    private TextView txcategory;
    private EditText etdescription,etprice,ettitle;
    private Button btnadd;
    private ImageView imagesell;
    private ImageButton btnback;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sell);

        txcategory = findViewById(R.id.txcategory);
        imagesell = findViewById(R.id.imagesell);
        ettitle = findViewById(R.id.ettitle);
        etdescription = findViewById(R.id.etdescription);
        etprice = findViewById(R.id.etprice);
        btnadd = findViewById(R.id.btnadd);
        btnback = findViewById(R.id.btnback);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        txcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category();
            }
        });

        btnadd.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputsellitem();
            }
        }));

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }

    private String Itemtitle,Itemdescription,Itemquanlity,Itemprice,Itemcategory;


    private void category() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("เลือกหมวดหมู่ประเภท")
                .setItems(Constants.itemcategory, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category = Constants.itemcategory[which];

                        if (category == "พลาสติก"){
                            imagesell.setImageDrawable(getDrawable(R.drawable.ic_water));

                        }
                        else if (category == "โลหะ"){
                            imagesell.setImageDrawable(getDrawable(R.drawable.ic_soda));

                        }
                        else if (category == "แก้ว"){
                            imagesell.setImageDrawable(getDrawable(R.drawable.ic_beer_bottle));

                        }
                        else if (category == "อื่นๆ"){
                            imagesell.setImageDrawable(getDrawable(R.drawable.ic_etc));

                        }

                        txcategory.setText(category);

                    }
                })
                .show();
    }

    private void inputsellitem() {
        Itemtitle = ettitle.getText().toString().trim();
        Itemcategory = txcategory.getText().toString().trim();
        Itemdescription = etdescription.getText().toString().trim();
        Itemprice = etprice.getText().toString().trim();

        if (TextUtils.isEmpty(Itemtitle)){
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return;    //don't proceed further
        }

        if (TextUtils.isEmpty(Itemcategory)){
            Toast.makeText(this, "category is required", Toast.LENGTH_SHORT).show();
            return;    //don't proceed further
        }
        if (TextUtils.isEmpty(Itemprice)){
            Toast.makeText(this, "Price is required", Toast.LENGTH_SHORT).show();
            return;    //don't proceed further
        }

        addsellitem();
    }

    private void addsellitem() {
        progressDialog.setMessage("adding sellitem");
        progressDialog.show();
        String timestamp = ""+System.currentTimeMillis();



        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("SellitemID",""+timestamp);
        hashMap.put("Itemtitle",""+Itemtitle);
        hashMap.put("Itemcategory",""+Itemcategory);
        hashMap.put("Itemdescription",""+Itemdescription);
        hashMap.put("Itemprice",""+Itemprice);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("Uid",""+firebaseAuth.getUid());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(firebaseAuth.getUid()).child("SellItem").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(addSellActivity.this, "Sell Item Added", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(addSellActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



    }
}
