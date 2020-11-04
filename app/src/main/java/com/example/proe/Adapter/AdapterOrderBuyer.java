package com.example.proe.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proe.Filter.FilterOrderBuyer;
import com.example.proe.Model.ModelOrderBuyer;
import com.example.proe.OrderDetailBuyerActivity;
import com.example.proe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderBuyer extends RecyclerView.Adapter<AdapterOrderBuyer.HolderOrderBuyer> implements Filterable {

    private Context context;
    public ArrayList<ModelOrderBuyer> orderBuyerArrayList, filterList;
    private FilterOrderBuyer filter;

    public AdapterOrderBuyer(Context context, ArrayList<ModelOrderBuyer> orderBuyerArrayList) {
        this.context = context;
        this.orderBuyerArrayList = orderBuyerArrayList;
        this.filterList = orderBuyerArrayList;
    }

    @NonNull
    @Override
    public HolderOrderBuyer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_buyer,parent,false);
        return new HolderOrderBuyer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderBuyer holder, int position) {
        ModelOrderBuyer modelOrderBuyer = orderBuyerArrayList.get(position);

        final String OrderID = modelOrderBuyer.getOrderID();
        String OrderTime = modelOrderBuyer.getOrderTime();
        String OrderStatus = modelOrderBuyer.getOrderStatus();
        String OrderCost = modelOrderBuyer.getOrderCost();
        final String OrderBy = modelOrderBuyer.getOrderBy();
        String OrderTo = modelOrderBuyer.getOrderTo();

        loadUserInfo(modelOrderBuyer, holder);

        holder.txamount.setText("Total : ฿" +OrderCost);
        holder.txstatus.setText(OrderStatus);
        holder.txorder.setText("OrderID: "+OrderID);

        if (OrderStatus.equals("In Progress")){
            holder.txstatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else if (OrderStatus.equals("Completed")){
            holder.txstatus.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if (OrderStatus.equals("Cancelled")){
            holder.txstatus.setTextColor(context.getResources().getColor(R.color.red));
        }

        Calendar calendar =Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(OrderTime));
        String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();

        holder.txdate.setText(formatedDate);

        holder.itemView.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailBuyerActivity.class);
                intent.putExtra("OrderID",OrderID);
                intent.putExtra("OrderBy",OrderBy);
                context.startActivity(intent);
            }
        });
    }

    private void loadUserInfo(ModelOrderBuyer modelOrderBuyer, final HolderOrderBuyer holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(modelOrderBuyer.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String email = ""+dataSnapshot.child("Email").getValue();
                        holder.EmailIv.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderBuyerArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){

            filter = new FilterOrderBuyer(this,filterList);
        }

        return filter;
    }

    class HolderOrderBuyer extends RecyclerView.ViewHolder{

        private TextView txorder,txdate,EmailIv,txamount,txstatus;


        public HolderOrderBuyer(@NonNull View itemView) {
            super(itemView);

            txorder = itemView.findViewById(R.id.txorder);
            txamount = itemView.findViewById(R.id.txamount);
            txdate = itemView.findViewById(R.id.txdate);
            txstatus = itemView.findViewById(R.id.txstatus);
            EmailIv = itemView.findViewById(R.id.EmailIv);
        }
    }
}
