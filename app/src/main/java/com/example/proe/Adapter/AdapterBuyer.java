package com.example.proe.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proe.BuyerDetailActivity;
import com.example.proe.Model.ModelBuyerUI;
import com.example.proe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterBuyer extends RecyclerView.Adapter<AdapterBuyer.Holderbuyer>  {

    //view holder

    private Context context;
    public ArrayList<ModelBuyerUI> buyerList;


    public AdapterBuyer(Context context, ArrayList<ModelBuyerUI> buyerList) {
        this.context = context;
        this.buyerList = buyerList;

    }

    @Override
    public Holderbuyer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_sharp,parent,false);
        return new Holderbuyer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holderbuyer holder, int position) {
        ModelBuyerUI modelBuyerUI = buyerList.get(position);
        String accountType = modelBuyerUI.getAccountType();
        String address = modelBuyerUI.getCompleteAddress();
        String city = modelBuyerUI.getCity();
        String state = modelBuyerUI.getState();
        String country = modelBuyerUI.getCountry();
        String email = modelBuyerUI.getEmail();
        String latitude = modelBuyerUI.getLatitude();
        String longitude = modelBuyerUI.getLongitude();
        String online = modelBuyerUI.getOnline();
        String timestamp = modelBuyerUI.getTimestamp();
        String name = modelBuyerUI.getName();
        String phone = modelBuyerUI.getPhone();
        final String uid = modelBuyerUI.getUid();
        String shopOpen = modelBuyerUI.getShopOpen();
        String shopName = modelBuyerUI.getShopName();
        String profileImage = modelBuyerUI.getProfileImage();
        
        LoadReview(modelBuyerUI, holder);

        //set data
        holder.shopnameIv.setText(shopName);
        holder.addressIv.setText(address);
        holder.phoneIv.setText(phone);
        if (online.equals("true")) {
            holder.onlineIv.setVisibility(View.VISIBLE);
        }
        else {
            holder.onlineIv.setVisibility(View.GONE);
        }
        if (shopOpen.equals("true")){
            holder.txshopclose.setVisibility(View.GONE);
        }
        else {
            holder.txshopclose.setVisibility(View.VISIBLE);

        }
        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_account_gray_24dp).into(holder.probuyerIv);
        }
        catch (Exception e){
            holder.probuyerIv.setImageResource(R.drawable.ic_account_gray_24dp);
        }
        //holder click listener,show detail
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BuyerDetailActivity.class);
                intent.putExtra("BuyerUid", uid);
                context.startActivity(intent);

            }
        });

    }

    private float ratingSum = 0;
    private void LoadReview(ModelBuyerUI modelBuyerUI, final Holderbuyer holder) {

        String BuyerUid = modelBuyerUI.getUid();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(BuyerUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ratingSum = 0;
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                            ratingSum = ratingSum+rating;

                        }

                        long numberOfReview = dataSnapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReview;


                        holder.ratingbar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return buyerList.size();   // record no. of record
    }



    class Holderbuyer extends RecyclerView.ViewHolder{

        //ui view for row_sharp
        private ImageView probuyerIv,onlineIv;
        private TextView txshopclose,shopnameIv,addressIv,phoneIv;
        private RatingBar ratingbar;

        public Holderbuyer(@NonNull View itemView) {
            super(itemView);

            probuyerIv = itemView.findViewById(R.id.probuyerIv);
            onlineIv = itemView.findViewById(R.id.onlineIv);

            txshopclose = itemView.findViewById(R.id.txshopclose);
            shopnameIv = itemView.findViewById(R.id.shopnameIv);
            addressIv = itemView.findViewById(R.id.addressIv);
            phoneIv = itemView.findViewById(R.id.phoneIv);

            ratingbar = itemView.findViewById(R.id.ratingbar);
        }
    }
}
