package com.example.proe.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proe.Model.ModelInfoBuyer;
import com.example.proe.R;

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
    public void onBindViewHolder(@NonNull HolderInfoBuyer holder, int position) {

        ModelInfoBuyer modelInfoBuyer = infoBuyerArrayList.get(position);

        String InfomationID = modelInfoBuyer.getInfomationID();
        String Infotitle = modelInfoBuyer.getInfotitle();
        String Infodescription = modelInfoBuyer.getInfodescription();
        String Uid = modelInfoBuyer.getUid();
        String InfoTime = modelInfoBuyer.getInfoTime();

        holder.txinfo.setText("Information ID : "+ InfomationID);
        holder.txtitle.setText("Information title : "+Infotitle);

        Calendar calendar =Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(InfoTime));
        String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();
        holder.txdate.setText(formatedDate);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class HolderInfoBuyer extends RecyclerView.ViewHolder {

        private TextView txinfo,txdate,txtitle;

        public HolderInfoBuyer(@NonNull View itemView) {
            super(itemView);

            txinfo = itemView.findViewById(R.id.txinfo);
            txdate = itemView.findViewById(R.id.txdate);
            txtitle = itemView.findViewById(R.id.txtitle);
        }
    }
}
