package com.example.dressme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class CommandAdapter2 extends FirestoreRecyclerAdapter<CommandModel, CommandAdapter2.CommandsViewHolder> {

    public CommandAdapter2.OnItemClickListener listener;

    public CommandAdapter2(@NonNull FirestoreRecyclerOptions<CommandModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommandsViewHolder holder, int position, @NonNull CommandModel model) {
        holder.conf_desc.setText(model.getDespComd());

        holder.conf_nomt.setText(model.getNtailor());

        switch (model.getEtat()){
            case "nonConfirme" :
                holder.prog_etat.setText("Not Confirmed !");
                break;
            case "encours":
                holder.prog_etat.setText(" Loading ...");
                break;
            case "pret":
                holder.prog_etat.setText(" Ready!");
                holder.conf_btn.setVisibility(View.VISIBLE);
                break;
        }
        holder.conf_prix.setText(model.getPrice());
        holder.setimages(model.getComdUrl());
    }

    @NonNull
    @Override
    public CommandsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
        return new CommandAdapter2.CommandsViewHolder(view);
    }

    public class CommandsViewHolder extends RecyclerView.ViewHolder {
        public ImageView confImg ;
        public TextView conf_desc ;
        public TextView conf_nomt;
        public TextView prog_etat;
        public TextView conf_prix;
        public TextView conf_btn;

        public CommandsViewHolder(@NonNull View itemView) {
            super(itemView);
            confImg = itemView.findViewById(R.id.prog_img);
            conf_desc = itemView.findViewById(R.id.prog_des);
            conf_nomt = itemView.findViewById(R.id.prog_nomt);
            prog_etat = itemView.findViewById(R.id.prog_etat);
            conf_prix = itemView.findViewById(R.id.prog_prix);
            conf_btn = itemView.findViewById(R.id.showdetails);
            conf_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
        public void setimages(String url){
            confImg = itemView.findViewById(R.id.prog_img);
            Glide.with(itemView.getContext()).load(url).into(confImg);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    public void setOnItemClickListener(CommandAdapter2.OnItemClickListener listener){
        this.listener = listener;
    }
}
