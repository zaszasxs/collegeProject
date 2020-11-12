package com.example.proe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proe.BuyerDetailActivity;
import com.example.proe.Model.ModelCartitem;
import com.example.proe.R;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartitem extends RecyclerView.Adapter<AdapterCartitem.HolderCartitem>{

    private Context context;
    public ArrayList <ModelCartitem> cartitems;

    public AdapterCartitem(Context context, ArrayList<ModelCartitem> cartitems) {
        this.context = context;
        this.cartitems = cartitems;
    }

    @NonNull
    @Override
    public HolderCartitem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cartitem,parent,false);
        return new HolderCartitem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartitem holder, final int position) {
        ModelCartitem modelCartitem = cartitems.get(position);

        final String id = modelCartitem.getId();
        String getSid = modelCartitem.getSid();
        String title = modelCartitem.getName();
        final String cost = modelCartitem.getCost();
        String price = modelCartitem.getPrice();
        String num = modelCartitem.getNum();

        holder.txtitle.setText(""+title);
        holder.txprice.setText("฿"+cost);
        holder.txpriceeach.setText("฿"+ price);
        holder.txquanlity.setText("["+num+"]");

        holder.txremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyDB easyDB = EasyDB.init(context,"ITEM_DB")
                        .setTableName("ITEM_TABLE")
                        .addColumn(new Column("Item_ID",new String[]{"text","unique"}))
                        .addColumn(new Column("Item_SellID",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Finalprice",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Quanlity",new String[]{"text","not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1,id);
                Toast.makeText(context, "Remove from cart...", Toast.LENGTH_SHORT).show();

                cartitems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                double tx = Double.parseDouble((((BuyerDetailActivity)context).txtotalprice.getText().toString().trim().replace("฿","")));
                double totalPrice =  tx - Double.parseDouble(cost.replace("฿",""));

                ((BuyerDetailActivity)context).totalprice = 0.00;
                ((BuyerDetailActivity)context).txtotalprice.setText("฿" +String.format("%.2f",totalPrice));

                ((BuyerDetailActivity)context).cartCount();

            }
        });
    }

    @Override
    public int getItemCount() {
        return cartitems.size();
    }

    class HolderCartitem extends RecyclerView.ViewHolder {

        private TextView txtitle,txprice,txpriceeach,txquanlity,txremove;

        public HolderCartitem(@NonNull View itemView) {
            super(itemView);

            txtitle = itemView.findViewById(R.id.txtitle);
            txprice = itemView.findViewById(R.id.txprice);
            txpriceeach = itemView.findViewById(R.id.txpriceeach);
            txquanlity = itemView.findViewById(R.id.txquanlity);
            txremove = itemView.findViewById(R.id.txremove);

        }
    }
}
