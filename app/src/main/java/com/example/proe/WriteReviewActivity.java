package com.example.proe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class WriteReviewActivity extends AppCompatActivity {

    private ImageButton btnback;
    private ImageView probuyerIv;
    private TextView shopnameIv;
    private RatingBar ratingbar;
    private EditText etreview;
    private FloatingActionButton btnsubmit;

    private String BuyerUid;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        BuyerUid = getIntent().getStringExtra("BuyerUid");

        btnback = findViewById(R.id.btnback);
        probuyerIv = findViewById(R.id.probuyerIv);
        shopnameIv = findViewById(R.id.shopnameIv);
        ratingbar = findViewById(R.id.ratingbar);
        etreview = findViewById(R.id.etreview);
        btnsubmit = findViewById(R.id.btnsubmit);

        firebaseAuth = FirebaseAuth.getInstance();

        loadMyReview();
        loadBuyerInfo();

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputData();
            }
        });

    }

    private void loadBuyerInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(BuyerUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ShopName = ""+dataSnapshot.child("ShopName").getValue();
                String BuyerImage = ""+dataSnapshot.child("profileImage").getValue();

                shopnameIv.setText(ShopName);
                try {
                    Picasso.get().load(BuyerImage).placeholder(R.drawable.ic_account_gray_24dp).into(probuyerIv);
                }
                catch (Exception e){
                    probuyerIv.setImageResource(R.drawable.ic_account_gray_24dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMyReview() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(BuyerUid).child("Ratings").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            String uid = ""+dataSnapshot.child("uid").getValue();
                            String ratings = ""+dataSnapshot.child("ratings").getValue();
                            String review = ""+dataSnapshot.child("review").getValue();
                            String timestamp = ""+dataSnapshot.child("timestamp").getValue();

                            float myRating = Float.parseFloat(ratings);
                            ratingbar.setRating(myRating);
                            etreview.setText(review);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void InputData() {

        String rating = ""+ratingbar.getRating();
        String review = etreview.getText().toString().trim();

        String timestamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("ratings",""+rating);
        hashMap.put("review",""+review);
        hashMap.put("timestamp",""+timestamp);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(BuyerUid).child("Ratings").child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(WriteReviewActivity.this, "Review published successful...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(WriteReviewActivity.this,ShopReviewActivity.class);
                        intent.putExtra("BuyerUid",BuyerUid);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WriteReviewActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
