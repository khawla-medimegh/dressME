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

public class CommandAdapter4 extends FirestoreRecyclerAdapter<CommandModel, CommandAdapter4.CommandsViewHolder> {

    public CommandAdapter4.OnItemClickListener listener;

    public CommandAdapter4(@NonNull FirestoreRecyclerOptions<CommandModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommandsViewHolder holder, int position, @NonNull CommandModel model) {
        holder.conf_desc.setText(model.getDespComd());

        holder.conf_nomt.setText(model.getNclient());
        holder.conf_prix.setText(model.getPrice());


        switch (model.getEtat()){
            case "waiting for client to confirm" :
                holder.prog_etat.setText("  waiting for client  \n  to confirm the price");
                holder.conf_btn.setVisibility(View.GONE);
                break;
            case "rejected" :
                holder.prog_etat.setText("Rejected !");
                holder.conf_btn.setText("delete");
                holder.conf_btn.setVisibility(View.VISIBLE);
                break;
            case "encours":
                holder.prog_etat.setText(" Loading ...");
                holder.conf_btn.setText("Ready");
                break;
            case "pret":
                holder.prog_etat.setText(" Ready, waiting for \n client to confirm");
                holder.conf_btn.setVisibility(View.GONE);
                break;
            case "pretConfirme":
                holder.prog_etat.setText(" Achieved");
                holder.conf_btn.setText("DONE");
                holder.conf_btn.setVisibility(View.VISIBLE);
                break;
        }

        holder.setimages(model.getComdUrl());
    }

    @NonNull
    @Override
    public CommandsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progressactivite, parent, false);
        return new CommandAdapter4.CommandsViewHolder(view);
    }

    public class CommandsViewHolder extends RecyclerView.ViewHolder {
        public ImageView confImg ;
        public TextView conf_desc ;
        public TextView conf_nomt;
        public TextView conf_prix;
        public TextView prog_etat;
        public TextView conf_btn;

        public CommandsViewHolder(@NonNull View itemView) {
            super(itemView);
            confImg = itemView.findViewById(R.id.prog_imgt);
            conf_desc = itemView.findViewById(R.id.prog_dest);
            conf_nomt = itemView.findViewById(R.id.prog_nomtt);
            conf_prix = itemView.findViewById(R.id.prog_prixt);
            conf_btn = itemView.findViewById(R.id.ready_btn);
            prog_etat= itemView.findViewById(R.id.prog_etatt);
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
            confImg = itemView.findViewById(R.id.prog_imgt);
            Glide.with(itemView.getContext()).load(url).into(confImg);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    public void setOnItemClickListener(CommandAdapter4.OnItemClickListener listener){
        this.listener = listener;
    }
}
