package com.example.dressme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static com.example.dressme.profilthis.Extra_tID;

import static com.example.dressme.profilthis.Extra_pID;




public class postthis extends AppCompatActivity {
    public static final String Extra_pUrl = "PostUrl";
    public static final String Extra_tailID = "tailleur";
    public static final String Extra_username = "TailleurName";
    FirebaseFirestore fStore;
    TextView description;
    ImageView postImage, close;
    Button commandBtn;
    String imgUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postthis);
        Intent intent =getIntent();
        description = findViewById(R.id.descriptionPost);
        postImage = findViewById(R.id.imagePost);
        commandBtn = findViewById(R.id.commandPost);
        close = findViewById(R.id.close_post);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final String tailName= intent.getStringExtra(Extra_username);
        final String tailorID= intent.getStringExtra(Extra_tID);
        String postId = intent.getStringExtra(Extra_pID);
        fStore = FirebaseFirestore.getInstance();

        DocumentReference  documentReference1 = fStore.collection("tailors").document(tailorID).collection("posts").document(postId);
        documentReference1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    description.setText(documentSnapshot.getString("description"));
                    imgUrl = documentSnapshot.getString("postimage");
                    Glide.with(postthis.this).load(imgUrl).into(postImage);
                } else {
                    Log.d("postthis", "Document do not exist");
                }
            }
        });


        commandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(postthis.this, CommandPost.class);
                intent.putExtra(Extra_pUrl, imgUrl);
                intent.putExtra(Extra_tailID, tailorID);
                intent.putExtra(Extra_username, tailName);
                startActivity(intent);
            }
        });
    }

}