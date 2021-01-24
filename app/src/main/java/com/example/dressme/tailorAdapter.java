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
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class tailorAdapter extends FirestoreRecyclerAdapter<tailor, tailorAdapter.TailorsViewHolder> {

    private OnItemClickListener listener;

    public tailorAdapter(@NonNull FirestoreRecyclerOptions<tailor> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TailorsViewHolder holder, int position, @NonNull tailor model) {
        holder.list_desc.setText(model.getUsername());
        holder.setimages(model.getImageUrl());
    }

    @NonNull
    @Override
    public TailorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemdesign, parent, false);
        return new TailorsViewHolder(view);
    }

    public class TailorsViewHolder extends RecyclerView.ViewHolder {
        private TextView list_desc;
        private CircleImageView list_images;

        public TailorsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_desc = itemView.findViewById(R.id.text_View);
            list_images = itemView.findViewById(R.id.imagetailleurs);

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
            list_images = itemView.findViewById(R.id.imagetailleurs);
            Glide.with(itemView.getContext()).load(url).into(list_images);
        }

    }


    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot snapshot, int position);
    }
    public void setOnItemClickListener(tailorAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}
