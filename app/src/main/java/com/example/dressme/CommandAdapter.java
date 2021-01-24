package com.example.dressme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;
import java.util.concurrent.Executor;

import static java.util.Objects.*;

public class CommandAdapter extends FirestoreRecyclerAdapter<CommandModel, CommandAdapter.CommandsViewHolder> {

    public CommandAdapter.OnItemClickListener listener;

    public CommandAdapter (@NonNull FirestoreRecyclerOptions<CommandModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommandsViewHolder holder, int position, @NonNull CommandModel model) {
        holder.conf_desc.setText(model.getDespComd());

        holder.conf_nomt.setText(model.getNtailor());
        holder.conf_prix.setText(model.getPrice());
        holder.setimages(model.getComdUrl());
    }

    @NonNull
    @Override
    public CommandsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_confirm, parent, false);
        return new CommandAdapter.CommandsViewHolder(view);
    }

    public class CommandsViewHolder extends RecyclerView.ViewHolder {
        public ImageView confImg ;
        public TextView conf_desc ;
        public TextView conf_nomt;
        public TextView conf_prix;
        public Button conf_btn;

        public CommandsViewHolder(@NonNull View itemView) {
            super(itemView);
            confImg = itemView.findViewById(R.id.conf_img);
            conf_desc = itemView.findViewById(R.id.con_des);
            conf_nomt = itemView.findViewById(R.id.conf_nomt);
            conf_prix = itemView.findViewById(R.id.conf_prix);
            conf_btn = itemView.findViewById(R.id.conf_btn);
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
            confImg = itemView.findViewById(R.id.conf_img);
            Glide.with(itemView.getContext()).load(url).into(confImg);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    public void setOnItemClickListener(CommandAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}

