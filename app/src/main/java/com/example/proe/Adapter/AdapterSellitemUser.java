package com.example.proe.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proe.BuyerDetailActivity;
import com.example.proe.Filter.FilterSellItem;
import com.example.proe.Filter.FilterSellItemUser;
import com.example.proe.Model.ModelSellItem;
import com.example.proe.R;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterSellitemUser extends RecyclerView.Adapter<AdapterSellitemUser.HolderSellitemUser> implements Filterable {

    private Context context;
    public ArrayList<ModelSellItem> sellItemslist,filterlist;
    private FilterSellItemUser filter;

    public AdapterSellitemUser(Context context,ArrayList<ModelSellItem> sellItemslist){
        this.context = context;
        this.sellItemslist = sellItemslist;
        this.filterlist = sellItemslist;
    }

    @NonNull
    @Override
    public HolderSellitemUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_sellitem_user,parent,false);

        return new HolderSellitemUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSellitemUser holder, int position) {
        final ModelSellItem modelSellItem = sellItemslist.get(position);

        String id = modelSellItem.getSellitemID();
        String uid = modelSellItem.getUid();
        String title = modelSellItem.getItemtitle();
        String icon = modelSellItem.getImagesell();
        String category = modelSellItem.getItemcategory();
        String description = modelSellItem.getItemdescription();
        String quanlity = modelSellItem.getItemquanlity();
        String price = modelSellItem.getItemprice();
        String timestamp = modelSellItem.getTimestamp();

        holder.txtitle.setText(title);
        holder.txcategory.setText(category);
        holder.txprice.setText("฿"+price);
        holder.txquanlity.setText(quanlity);
        holder.txdescription.setText(description);

        holder.txcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSellitemDialog(modelSellItem);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private double cost = 0;
    private double finalprice = 0;
    public int quanlity = 0;
    private void showSellitemDialog(ModelSellItem modelSellItem) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_buyer,null);

        ImageView imagesell = view.findViewById(R.id.imagesell);
        TextView txcategory = view.findViewById(R.id.txcategory);
        final TextView txtitle = view.findViewById(R.id.txtitle);
        TextView txdescription = view.findViewById(R.id.txdescription);
        final TextView txquanlity = view.findViewById(R.id.txquanlity);
        final TextView txprice = view.findViewById(R.id.txprice);
        final TextView txnum = view.findViewById(R.id.txnum);
        final TextView txfinalprice = view.findViewById(R.id.txfinalprice);
        ImageButton desbtn = view.findViewById(R.id.desbtn);
        ImageButton incbtn = view.findViewById(R.id.incbtn);
        Button addbtn = view.findViewById(R.id.addbtn);

        String title = modelSellItem.getItemtitle();
        final String id = modelSellItem.getSellitemID();
        String icon = modelSellItem.getImagesell();
        String category = modelSellItem.getItemcategory();
        String description = modelSellItem.getItemdescription();
        String quantity = modelSellItem.getItemquanlity();
        String price = modelSellItem.getItemprice();

        cost = Double.parseDouble(price.replaceAll("฿",""));
        finalprice = Double.parseDouble(price.replaceAll("฿",""));
        quanlity = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        txtitle.setText(""+title);
        txcategory.setText(""+category);
        txdescription.setText(""+description);
        txquanlity.setText(""+quantity);
        txprice.setText("฿"+modelSellItem.getItemprice());
        txfinalprice.setText("฿"+finalprice);

        final AlertDialog dialog = builder.create();
        dialog.show();

        incbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalprice = finalprice + cost;
                quanlity++;

                txfinalprice.setText("฿"+finalprice);
                txnum.setText(""+quanlity);
            }
        });

        desbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (quanlity >1){
                   finalprice = finalprice - cost;
                   quanlity--;

                   txfinalprice.setText("฿"+finalprice);
                   txnum.setText(""+quanlity);
               }

            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = txtitle.getText().toString().trim();
                String price = txprice.getText().toString().trim().replace("฿","");
                String finalprice = txfinalprice.getText().toString().trim().replace("฿","");
                String quanlity = txquanlity.getText().toString().trim();

                //add to db(sqlite)
                addtoCart(id,title,price,finalprice,quanlity);

                dialog.dismiss();
            }
        });

    }

    private int itemID = 1;
    private void addtoCart(String id, String title, String price, String finalprice, String quanlity) {
        itemID++;

        EasyDB easyDB = EasyDB.init(context,"ITEM_DB")
                .setTableName("ITEM_TABLE")
                .addColumn(new Column("Item_ID",new String[]{"text","unique"}))
                .addColumn(new Column("Item_SellID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Finalprice",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quanlity",new String[]{"text","not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_ID",itemID)
                .addData("Item_SellID",id)
                .addData("Item_Name",title)
                .addData("Item_Price",price)
                .addData("Item_Finalprice",finalprice)
                .addData("Item_Quanlity",quanlity)
                .doneDataAdding();

        Toast.makeText(context, "Added to cart...", Toast.LENGTH_SHORT).show();

        ((BuyerDetailActivity)context).cartCount();

    }

    @Override
    public int getItemCount() {
        return sellItemslist.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterSellItemUser(this, filterlist);
        }
        return filter;
    }

    class HolderSellitemUser extends RecyclerView.ViewHolder {

        private ImageView imagesell;
        private TextView txcategory,txtitle,txquanlity,txprice,txcart,txdescription;
        public HolderSellitemUser(@NonNull View itemView) {
            super(itemView);

            imagesell = itemView.findViewById(R.id.imagesell);
            txtitle = itemView.findViewById(R.id.txtitle);
            txcategory = itemView.findViewById(R.id.txcategory);
            txquanlity = itemView.findViewById(R.id.txquanlity);
            txprice = itemView.findViewById(R.id.txprice);
            txcart = itemView.findViewById(R.id.txcart);
            txdescription = itemView.findViewById(R.id.txdescription);

        }
    }
}
