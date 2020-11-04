package com.example.proe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.proe.Adapter.AdapterReview;
import com.example.proe.Model.ModelReview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShopReviewActivity extends AppCompatActivity {

    private String BuyerUid;

    private ImageButton backbtn;
    private TextView shopnameIv,txrating;
    private ImageView probuyerIv;
    private RatingBar ratingbar;
    private RecyclerView reviewRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelReview> reviewArrayList;
    private AdapterReview adapterReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_review);

        BuyerUid = getIntent().getStringExtra("BuyerUid");

        backbtn = findViewById(R.id.backbtn);
        probuyerIv = findViewById(R.id.probuyerIv);
        shopnameIv = findViewById(R.id.shopnameIv);
        ratingbar = findViewById(R.id.ratingbar);
        txrating = findViewById(R.id.txrating);
        reviewRv = findViewById(R.id.reviewRv);

        firebaseAuth = FirebaseAuth.getInstance();
        loadBuyerDetail();
        loadReview();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private float ratingSum = 0;
    private void loadReview() {

        reviewArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(BuyerUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reviewArrayList.clear();
                        ratingSum = 0;
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                            ratingSum = ratingSum+rating;

                            ModelReview modelReview = ds.getValue(ModelReview.class);
                            reviewArrayList.add(modelReview);
                        }

                        adapterReview = new AdapterReview(ShopReviewActivity.this,reviewArrayList);
                        reviewRv.setAdapter(adapterReview);

                        long numberOfReview = dataSnapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReview;

                        txrating.setText(String.format("%.2f",avgRating) +"[" +numberOfReview+ "]");
                        ratingbar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadBuyerDetail() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(BuyerUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String ShopName = ""+dataSnapshot.child("ShopName").getValue();
                        String profileImage = ""+dataSnapshot.child("profileImage").getValue();

                        shopnameIv.setText(ShopName);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_account_gray_24dp).into(probuyerIv);
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
}
