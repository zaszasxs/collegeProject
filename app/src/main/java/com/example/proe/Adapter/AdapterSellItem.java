package com.example.proe.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proe.EditSellItemActivity;
import com.example.proe.Filter.FilterSellItem;
import com.example.proe.Model.ModelSellItem;
import com.example.proe.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterSellItem extends RecyclerView.Adapter<AdapterSellItem.HolderSellItem> implements Filterable {

    private Context context;
    public ArrayList<ModelSellItem> sellItemslist, filterlist;
    private FilterSellItem filter;

    public AdapterSellItem(Context context,ArrayList<ModelSellItem> sellItemslist){
        this.context = context;
        this.sellItemslist = sellItemslist;
        this.filterlist = sellItemslist;

    }
    @NonNull
    @Override
    public HolderSellItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_sellitem,parent,false);
        return new HolderSellItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSellItem holder, int position) {
        final ModelSellItem modelSellItem = sellItemslist.get(position);

        String id = modelSellItem.getSellitemID();
        String uid = modelSellItem.getUid();
        String title = modelSellItem.getItemtitle();
        String icon = modelSellItem.getImagesell();
        String category = modelSellItem.getItemcategory();
        String description = modelSellItem.getItemdescription();
        String price = modelSellItem.getItemprice();
        String timestamp = modelSellItem.getTimestamp();


        holder.txtitle.setText(title);
        holder.txcategory.setText(category);
        holder.txprice.setText("฿"+ price +"/ Kg");


        switch (category) {
            case "พลาสติก":
                holder.imagesell.setImageDrawable(context.getDrawable(R.drawable.ic_water));
                break;
            case "โลหะ":
                holder.imagesell.setImageDrawable(context.getDrawable(R.drawable.ic_soda));
                break;
            case "แก้ว":
                holder.imagesell.setImageDrawable(context.getDrawable(R.drawable.ic_beer_bottle));
                break;
            case "อื่นๆ":
                holder.imagesell.setImageDrawable(context.getDrawable(R.drawable.ic_etc));
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailBottomSheet(modelSellItem);
            }
        });
    }

    private void detailBottomSheet(ModelSellItem modelSellItem) {
        final BottomSheetDialog bottomSheetDialog =  new BottomSheetDialog(context);

        View view = LayoutInflater.from(context).inflate(R.layout.sellitem_detail,null);
        bottomSheetDialog.setContentView(view);

        ImageButton backbtn = view.findViewById(R.id.backbtn);
        final ImageButton deletebtn = view.findViewById(R.id.deletebtn);
        ImageButton editbtn = view.findViewById(R.id.editbtn);
        ImageView  imagesell = view.findViewById(R.id.imagesell);
        TextView txcategory = view.findViewById(R.id.txcategory);
        TextView txtitle = view.findViewById(R.id.txtitle);
        TextView txdescription = view.findViewById(R.id.txdescription);
        TextView txquanlity = view.findViewById(R.id.txquanlity);
        TextView txprice = view.findViewById(R.id.txprice);

        final String id = modelSellItem.getSellitemID();
        String uid = modelSellItem.getUid();
        final String title = modelSellItem.getItemtitle();
        String icon = modelSellItem.getImagesell();
        String category = modelSellItem.getItemcategory();
        String description = modelSellItem.getItemdescription();
        String price = modelSellItem.getItemprice();
        String timestamp = modelSellItem.getTimestamp();

        txtitle.setText(title);
        txcategory.setText(category);
        txprice.setText("฿"+ price +"/ Kg");

        bottomSheetDialog.show();

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditSellItemActivity.class);
                intent.putExtra("SellitemID",id);
                context.startActivity(intent);
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure to delete this item "+title + "??")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletesellitem(id);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void deletesellitem(String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(firebaseAuth.getUid()).child("SellItem").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Sell Item delete...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public int getItemCount() {
        return sellItemslist.size();

    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterSellItem(this, filterlist);
        }
        return filter;
    }

    class HolderSellItem extends RecyclerView.ViewHolder{

        private ImageView imagesell;
        private TextView txcategory,txtitle,txprice;

        public HolderSellItem(@NonNull View itemView) {
            super(itemView);

             imagesell = itemView.findViewById(R.id.imagesell);
             txtitle = itemView.findViewById(R.id.txtitle);
             txcategory = itemView.findViewById(R.id.txcategory);
             txprice = itemView.findViewById(R.id.txprice);
             
        }

    }
}

