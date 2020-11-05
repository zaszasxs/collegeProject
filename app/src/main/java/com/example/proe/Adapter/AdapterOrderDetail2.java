package com.example.proe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proe.Model.ModelOrderDetail2;
import com.example.proe.R;

import java.util.ArrayList;

public class AdapterOrderDetail2 extends RecyclerView.Adapter<AdapterOrderDetail2.HolderOrderDetail> {

    private Context context;
    public ArrayList<ModelOrderDetail2> orderDetailArrayList;

    public AdapterOrderDetail2(Context context, ArrayList<ModelOrderDetail2> orderDetailArrayList) {
        this.context = context;
        this.orderDetailArrayList = orderDetailArrayList;
    }


    @NonNull
    @Override
    public HolderOrderDetail onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_detail,parent,false);
        return new HolderOrderDetail(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderDetail holder, int position) {
        ModelOrderDetail2 modelOrderDetail = orderDetailArrayList.get(position);


        String title = modelOrderDetail.getItemtitle();
        String price = modelOrderDetail.getItemprice().toString();


        holder.txtitle.setText(""+title);
        holder.txprice.setText("฿"+price);
        //holder.txpriceeach.setText("฿"+price);
        //holder.txnum.setText("["+num+"]");
    }

    @Override
    public int getItemCount() {
        return orderDetailArrayList.size();
    }

    class HolderOrderDetail extends RecyclerView.ViewHolder{


        private TextView txtitle,txpriceeach,txnum,txprice;


        public HolderOrderDetail(@NonNull View itemView) {
            super(itemView);

            txtitle = itemView.findViewById(R.id.txtitle);
            txpriceeach = itemView.findViewById(R.id.txpriceeach);
            txprice = itemView.findViewById(R.id.txprice);
            txnum = itemView.findViewById(R.id.txnum);
        }
    }
}
