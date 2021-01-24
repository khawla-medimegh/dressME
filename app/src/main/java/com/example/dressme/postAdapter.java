package com.example.dressme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class postAdapter extends FirestorePagingAdapter<PostModel, postAdapter.PostsViewHolder> {

    private OnListItemClick onListItemClick;

    public postAdapter(@NonNull FirestorePagingOptions<PostModel> options, OnListItemClick onListItemClick) {
        super(options);
        this.onListItemClick= onListItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull PostModel model) {
        holder.list_desc.setText(model.getDescription());
        holder.setimages(model.getPostimage());
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
        return new PostsViewHolder(view);
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView list_desc;
        private ImageView list_images;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_desc = itemView.findViewById(R.id.list_desc);
            list_images = itemView.findViewById(R.id.list_images);

            itemView.setOnClickListener(this);
        }
        public void setimages(String url){
            list_images = itemView.findViewById(R.id.list_images);
            Glide.with(itemView.getContext()).load(url).into(list_images);
        }

        @Override
        public void onClick(View v) {
            onListItemClick.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
        }

    }

    public interface OnListItemClick{
        void onItemClick(DocumentSnapshot snapshot, int adapterPosition);
    }
}
