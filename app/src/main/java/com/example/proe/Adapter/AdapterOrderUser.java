package com.example.proe.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proe.Model.ModelOrderUser;
import com.example.proe.OrderDetailActivity;
import com.example.proe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser> {

    private Context context;
    public ArrayList<ModelOrderUser> modelOrderUsers;

    public AdapterOrderUser(Context context,ArrayList<ModelOrderUser> modelOrderUsers){
        this.context = context;
        this.modelOrderUsers = modelOrderUsers;
    }

    @NonNull
    @Override
    public HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_user,parent,false);
        return new HolderOrderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderUser holder, int position) {
        final ModelOrderUser modelOrderUser = modelOrderUsers.get(position);
        final String OrderID = modelOrderUser.getOrderID();
        String OrderTime = modelOrderUser.getOrderTime();
        String OrderStatus = modelOrderUser.getOrderStatus();
        String OrderCost = modelOrderUser.getOrderCost();
        String OrderBy = modelOrderUser.getOrderBy();
        final String OrderTo = modelOrderUser.getOrderTo();

        LoadBuyerInfo(modelOrderUser,holder);

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

        holder.txdata.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("OrderTo",OrderTo);
                intent.putExtra("OrderID",OrderID);
                context.startActivity(intent);
            }
        });
    }

    private void LoadBuyerInfo(ModelOrderUser modelOrderUser, final HolderOrderUser holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(modelOrderUser.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String shopName = ""+dataSnapshot.child("ShopName").getValue();
                        holder.shopnameIv.setText(shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelOrderUsers.size();
    }

    class HolderOrderUser extends RecyclerView.ViewHolder{

        private TextView txorder,txdata,txstatus,txamount,shopnameIv;

        public HolderOrderUser(@NonNull View itemView) {
            super(itemView);

            txorder = itemView.findViewById(R.id.txorder);
            txdata = itemView.findViewById(R.id.txdate);
            txstatus = itemView.findViewById(R.id.txstatus);
            txamount = itemView.findViewById(R.id.txamount);
            shopnameIv =  itemView.findViewById(R.id.shopnameIv);
        }
    }
}
