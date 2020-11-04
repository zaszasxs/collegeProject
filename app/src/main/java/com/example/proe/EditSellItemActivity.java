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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditSellItemActivity extends AppCompatActivity {

    private String SellitemID;
    private TextView txcategory;
    private EditText etdescription,etprice,ettitle;
    private Button btnupdate;
    private ImageView imagesell;
    private ImageButton btnback;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sell_item);

        SellitemID = getIntent().getStringExtra("SellitemID");

        txcategory = findViewById(R.id.txcategory);
        imagesell = findViewById(R.id.imagesell);
        ettitle = findViewById(R.id.ettitle);
        etdescription = findViewById(R.id.etdescription);
        etprice = findViewById(R.id.etprice);
        btnupdate = findViewById(R.id.btnupdate);
        btnback = findViewById(R.id.btnback);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadSellitem();

        txcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category();
            }
        });

        btnupdate.setOnClickListener((new View.OnClickListener() {
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

    private String Itemtitle,Itemdescription,Itemprice,Itemcategory;

    private void loadSellitem() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(firebaseAuth.getUid()).child("SellItem").child(SellitemID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String SellitemID =""+dataSnapshot.child("SellitemID").getValue();
                        String Itemtitle =""+dataSnapshot.child("Itemtitle").getValue();
                        String imagesell =""+dataSnapshot.child("imagesell").getValue();
                        String Itemcategory =""+dataSnapshot.child("Itemcategory").getValue();
                        String Itemdescription =""+dataSnapshot.child("Itemdescription").getValue();
                        String Itemprice =""+dataSnapshot.child("Itemprice").getValue();
                        String timestamp =""+dataSnapshot.child("timestamp").getValue();
                        String Uid = ""+dataSnapshot.child("Uid").getValue();

                        ettitle.setText(Itemtitle);
                        etdescription.setText(Itemdescription);
                        etprice.setText(Itemprice);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    private void category() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SellItem Category")
                .setItems(Constants.itemcategory, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category = Constants.itemcategory[which];

                        if (category == "Plastic"){
                            imagesell.setImageDrawable(getDrawable(R.drawable.ic_water));
                            ettitle.setText("Plastic");
                        }
                        else if (category == "Metal"){
                            imagesell.setImageDrawable(getDrawable(R.drawable.ic_soda));
                            ettitle.setText("Metal");
                        }
                        else if (category == "Glass"){
                            imagesell.setImageDrawable(getDrawable(R.drawable.ic_beer_bottle));
                            ettitle.setText("Glass");
                        }
                        else if (category == "Other"){
                            imagesell.setImageDrawable(getDrawable(R.drawable.ic_etc));
                            ettitle.setText("");
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

       updatrsellitem();
    }

    private void updatrsellitem() {
        progressDialog.setMessage("Updating ...");
        progressDialog.show();


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Itemtitle",""+Itemtitle);
        hashMap.put("Itemcategory",""+Itemcategory);
        hashMap.put("Itemdescription",""+Itemdescription);
        hashMap.put("Itemprice",""+Itemprice);
        hashMap.put("Uid",""+firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(firebaseAuth.getUid()).child("SellItem").child(SellitemID)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(EditSellItemActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditSellItemActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
