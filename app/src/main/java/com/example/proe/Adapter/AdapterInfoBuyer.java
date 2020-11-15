package com.example.proe.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proe.InFormationDetailActivity;
import com.example.proe.Model.ModelInfoBuyer;
import com.example.proe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterInfoBuyer extends RecyclerView.Adapter<AdapterInfoBuyer.HolderInfoBuyer>{

    private Context context;
    public ArrayList<ModelInfoBuyer> infoBuyerArrayList;

    public AdapterInfoBuyer(Context context, ArrayList<ModelInfoBuyer> infoBuyerArrayList) {
        this.context = context;
        this.infoBuyerArrayList = infoBuyerArrayList;
    }

    @NonNull
    @Override
    public HolderInfoBuyer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_info_buyer,parent,false);
        return new AdapterInfoBuyer.HolderInfoBuyer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderInfoBuyer holder, final int position) {

        ModelInfoBuyer modelInfoBuyer = infoBuyerArrayList.get(position);

        String InfomationID = modelInfoBuyer.getInfomationID();
        String Infotitle = modelInfoBuyer.getInfotitle();
        String Infodescription = modelInfoBuyer.getInfodescription();
        String Uid = modelInfoBuyer.getUid();
        String InfoTime = modelInfoBuyer.getTimestamp();
        String InfoBy = modelInfoBuyer.getInfoBy();

        LoadBuyerInfo(modelInfoBuyer,holder);

        holder.txinfo.setText("ID : "+ InfomationID);
        holder.txtitle.setText("หัวข้อ : "+Infotitle);

        Calendar calendar =Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(InfoTime));
        final String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();
        holder.txdate.setText(formatedDate);

        holder.itemView.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detail = new Intent(context, InFormationDetailActivity.class);
                detail.putExtra("dataObj",infoBuyerArrayList.get(position));
                detail.putExtra("dataCurrent",formatedDate);
                context.startActivity(detail);
            }
        });
    }

    private void LoadBuyerInfo(ModelInfoBuyer modelInfoBuyer, HolderInfoBuyer holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(modelInfoBuyer.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String postOwner = "" + dataSnapshot.child("ShopName").getValue();
                            holder.txinfoby.setText("ผู้รับซื้อ : "+postOwner);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return infoBuyerArrayList.size();
    }

    class HolderInfoBuyer extends RecyclerView.ViewHolder {

        private TextView txinfo,txdate,txtitle,txinfoby;

        public HolderInfoBuyer(@NonNull View itemView) {
            super(itemView);

            txinfo = itemView.findViewById(R.id.tvInformationId);
            txdate = itemView.findViewById(R.id.tvInformationDate);
            txtitle = itemView.findViewById(R.id.tvInformationTitle);
            txinfoby = itemView.findViewById(R.id.tvInformationBy);
        }
    }
}
