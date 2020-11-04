package com.example.proe.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proe.Model.ModelReview;
import com.example.proe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.HolderReview>{

    private Context context;
    public ArrayList<ModelReview> modelReviewArrayList;

    public AdapterReview(Context context, ArrayList<ModelReview> modelReviewArrayList) {
        this.context = context;
        this.modelReviewArrayList = modelReviewArrayList;
    }

    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_review,parent,false);
        return new HolderReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReview holder, int position) {
        ModelReview modelReview = modelReviewArrayList.get(position);

        String uid = modelReview.getUid();
        String ratings = modelReview.getRatings();
        String review = modelReview.getReview();
        String timestamp = modelReview.getTimestamp();

        loadUserDetail(modelReview,holder);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.ratingbar.setRating(Float.parseFloat(ratings));
        holder.txreview.setText(review);
        holder.txdate.setText(dateFormat);


    }

    private void loadUserDetail(ModelReview modelReview, final HolderReview holder) {
        String uid = modelReview.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String Name = ""+dataSnapshot.child("Name").getValue();
                        String profileImage = ""+dataSnapshot.child("profileImage").getValue();

                        holder.NameIv.setText(Name);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_account_gray_24dp).into(holder.probuyerIv);
                        }
                        catch (Exception e){
                            holder.probuyerIv.setImageResource(R.drawable.ic_account_gray_24dp);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelReviewArrayList.size();
    }

    class HolderReview extends RecyclerView.ViewHolder{


        private ImageView probuyerIv;
        private TextView NameIv,txdate,txreview;
        private RatingBar ratingbar;
        public HolderReview(@NonNull View itemView) {
            super(itemView);

            probuyerIv = itemView.findViewById(R.id.probuyerIv);
            NameIv = itemView.findViewById(R.id.NameIv);
            txdate = itemView.findViewById(R.id.txdate);
            txreview = itemView.findViewById(R.id.txreview);
            ratingbar = itemView.findViewById(R.id.ratingbar);
        }
    }
}
