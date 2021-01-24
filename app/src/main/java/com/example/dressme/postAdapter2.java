package com.example.dressme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class postAdapter2 extends FirestoreRecyclerAdapter<PostModel, postAdapter2.PostsViewHolder> {

    private OnItemClickListener listener;

    public postAdapter2 (@NonNull FirestoreRecyclerOptions<PostModel> options) {
        super(options);
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

    public class PostsViewHolder extends RecyclerView.ViewHolder {
        private TextView list_desc;
        private ImageView list_images;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_desc = itemView.findViewById(R.id.list_desc);
            list_images = itemView.findViewById(R.id.list_images);

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
            list_images = itemView.findViewById(R.id.list_images);
            Glide.with(itemView.getContext()).load(url).into(list_images);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
