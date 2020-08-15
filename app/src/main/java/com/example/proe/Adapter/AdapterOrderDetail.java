package com.example.proe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proe.Model.ModelCartitem;
import com.example.proe.Model.ModelOrderDetail;
import com.example.proe.R;

import java.util.ArrayList;

public class AdapterOrderDetail extends RecyclerView.Adapter<AdapterOrderDetail.HolderOrderDetail> {

    private Context context;
    private ArrayList<ModelOrderDetail> orderDetails;

    public AdapterOrderDetail(Context context, ArrayList<ModelOrderDetail> orderDetails) {
        this.context = context;
        this.orderDetails = orderDetails;
    }

    @NonNull
    @Override
    public AdapterOrderDetail.HolderOrderDetail onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_orderdetail,parent,false);
        return new HolderOrderDetail(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrderDetail.HolderOrderDetail holder, int position) {
        ModelOrderDetail modelOrderDetail = orderDetails.get(position);
        String getSid = modelOrderDetail.getSid();
        String title = modelOrderDetail.getName();
        String cost = modelOrderDetail.getCost();
        String price = modelOrderDetail.getPrice();
        String quantity = modelOrderDetail.getQuantity();

        holder.txtitle.setText(""+title);
        holder.txprice.setText("฿"+cost);
        holder.txpriceeach.setText("฿"+price);
        holder.txnum.setText("/"+quantity+"");
    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    class HolderOrderDetail extends RecyclerView.ViewHolder {

        private TextView txtitle,txprice,txpriceeach,txnum;

        public HolderOrderDetail(@NonNull View itemView) {
            super(itemView);

            txtitle = itemView.findViewById(R.id.txtitle);
            txtitle = itemView.findViewById(R.id.txprice);
            txtitle = itemView.findViewById(R.id.txpriceeach);
            txtitle = itemView.findViewById(R.id.txnum);
        }
    }
}


