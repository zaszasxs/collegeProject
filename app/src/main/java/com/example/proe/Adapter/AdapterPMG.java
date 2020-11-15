package com.example.proe.Adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterPMG {

    class HolderPMG extends RecyclerView.ViewHolder {

        private TextView txmechanic,txplastic,txmetal,txglass;
        private ImageView imerror;
        private ImageButton editbtn,deletebtn;

        public HolderPMG(@NonNull View itemView) {
            super(itemView);


        }
    }
}
