package com.example.dressme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import static com.example.dressme.ProfilTailor.Extra_postID;

public class Postthis_tailor extends AppCompatActivity {
    private static final String TAG="Post";
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userID;
    FirebaseFirestore fStore;
    DocumentReference documentReference1;
    TextView delete ;
    EditText description;
    ImageView postImage, close;
    Button updateBtn;
    String postId, imgUrl;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postthis_tailor);
        Intent intent =getIntent();
        postId= intent.getStringExtra(Extra_postID);

        delete = findViewById(R.id.delete);
        description = findViewById(R.id.descriptionPosttailor);
        postImage = findViewById(R.id.imagePosttailor);
        updateBtn = findViewById(R.id.updatechanges);
        close = findViewById(R.id.close_posttailor);
        dialog = new Dialog(this);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
        documentReference1 = fStore.collection("tailors").document(userID).collection("posts").document(postId);
        documentReference1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    description.setText(documentSnapshot.getString("description"));
                    imgUrl = documentSnapshot.getString("postimage");
                    Glide.with(Postthis_tailor.this).load(imgUrl).into(postImage);
                } else {
                    Log.d("postthistailor", "Document do not exist");
                }
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog( Postthis_tailor.this);
                progressDialog.setMessage("Updating");
                progressDialog.show();
                if ( description.getText().toString().isEmpty()) {
                    Toast.makeText(Postthis_tailor.this, "Description is empty. ", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> edited = new HashMap<>();
                edited.put("description", description.getText().toString());
                documentReference1.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Postthis_tailor.this, "Post updated.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure"+ e.toString());
                    }
                });
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteCurrentPost();
            }
        });
    }
    private void DeleteCurrentPost() {

        dialog.setContentView(R.layout.confirm_delete);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close = dialog.findViewById(R.id.closept);
        Button y = dialog.findViewById(R.id.yest);
        Button n = dialog.findViewById(R.id.not);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Postthis_tailor.this, "You canceled", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentReference1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Postthis_tailor.this, "Post deleted", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.dismiss();
                onBackPressed();
            }
        });
        n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Postthis_tailor.this,"You clicked No",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}