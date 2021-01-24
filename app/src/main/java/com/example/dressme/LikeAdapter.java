package com.example.dressme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikeAdapter extends FirestoreRecyclerAdapter<LikeModel, LikeAdapter.LikesViewHolder> {

    private LikeAdapter.OnItemClickListener listener;

    public LikeAdapter(@NonNull FirestoreRecyclerOptions<LikeModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull LikesViewHolder holder, int position, @NonNull LikeModel model) {
        holder.list_desc.setText(model.getName());
        holder.setimages(model.getImageUrl());
    }

    @NonNull
    @Override
    public LikesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like, parent, false);
        return new LikeAdapter.LikesViewHolder(view);
    }

    public class LikesViewHolder extends RecyclerView.ViewHolder {
        private TextView list_desc;
        private CircleImageView list_images;

        public LikesViewHolder(@NonNull View itemView) {
            super(itemView);
            list_desc = itemView.findViewById(R.id.text_ViewLikes);
            list_images = itemView.findViewById(R.id.imageLikes);

            itemView.setOnClickListener(new View.OnClickListener() {
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
            list_images = itemView.findViewById(R.id.imageLikes);
            Glide.with(itemView.getContext()).load(url).into(list_images);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot snapshot, int position);
    }
    public void setOnItemClickListener(LikeAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}