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

public class customizeAdapter extends FirestoreRecyclerAdapter<customizeModel, customizeAdapter.CustomViewHolder> {

    private OnItemClickListener listener;

    public customizeAdapter(@NonNull FirestoreRecyclerOptions<customizeModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomViewHolder holder, int position, @NonNull customizeModel model) {
        holder.nomtl.setText(model.getNtailor());
        String imgurl=model.getImgUrl();
        String url1 = model.getUrl1();
        String url2 = model.getUrl2();
        String url3 = model.getUrl3();
        holder.setimages(imgurl,url1,url2,url3);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom, parent, false);
        return new CustomViewHolder(view);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView proft;
        private TextView nomtl;
        private ImageView imgp1, imgp2, imgp3;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            nomtl =  itemView.findViewById(R.id.custom_nom);
            proft =  itemView.findViewById(R.id.custom_img);
            imgp1 =  itemView.findViewById(R.id.custom_imgp1);
            imgp2 =  itemView.findViewById(R.id.custom_imgp2);
            imgp3 =  itemView.findViewById(R.id.custom_imgp3);

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
        public void setimages(String imgurl, String url1, String url2, String url3 ){
            proft =  itemView.findViewById(R.id.custom_img);
            imgp1 =  itemView.findViewById(R.id.custom_imgp1);
            imgp2 =  itemView.findViewById(R.id.custom_imgp2);
            imgp3 =  itemView.findViewById(R.id.custom_imgp3);
            Glide.with(itemView.getContext()).load(imgurl).into(proft);
            Glide.with(itemView.getContext()).load(url1).into(imgp1);
            Glide.with(itemView.getContext()).load(url2).into(imgp2);
            Glide.with(itemView.getContext()).load(url3).into(imgp3);
        }

    }


    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot snapshot, int position);
    }
    public void setOnItemClickListener(customizeAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}
