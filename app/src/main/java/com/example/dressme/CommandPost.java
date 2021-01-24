package com.example.dressme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.Map;

import static com.example.dressme.postthis.Extra_pUrl;
import static com.example.dressme.postthis.Extra_tailID;

public class CommandPost extends AppCompatActivity {
    EditText descripcmd;
    Button order;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userID;
    FirebaseFirestore fStore;
    String username;

    public static final String Extra_username = "TailleurName";
    private static final String TAG = "AddCommand";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_post);
        Intent intent = getIntent();
        final String postUrl = intent.getStringExtra(Extra_pUrl);
        final String tailId = intent.getStringExtra(Extra_tailID);
        final String tailName= intent.getStringExtra(Extra_username);
        descripcmd = findViewById(R.id.mesures);
        order = findViewById(R.id.orderPost);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


        user = fAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final DocumentReference documentReference = fStore.collection("users").document(userID);

            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        username=documentSnapshot.getString("username");
                    } else {
                        Log.d(TAG, "Document do not exist");
                    }
                }
            });

        }
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog( CommandPost.this);
                progressDialog.setMessage("Progressing");
                progressDialog.show();
                //ajouter au firestore client
                DocumentReference documentReference = fStore.collection("users").document(userID);
                CollectionReference collectionReference = documentReference.collection("commandes");
                DocumentReference docRef = collectionReference.document();
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("comdUrl", postUrl);
                hashMap.put("despComd", descripcmd.getText().toString());
                hashMap.put("etat", "nonConfirme");
                hashMap.put("tailorID", tailId);
                hashMap.put("Ntailor", tailName);
                docRef.set(hashMap);

                String postIdUser = docRef.getId();

                //ajouter au firebase tailor
                DocumentReference documentReference1 = fStore.collection("tailors").document(tailId);
                CollectionReference collectionReference1 = documentReference1.collection("commandes");
                DocumentReference docRef1 = collectionReference1.document();
                Map<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("comdUrl", postUrl);
                hashMap1.put("despComd", descripcmd.getText().toString());
                hashMap1.put("etat", "nonConfirme");
                hashMap1.put("userID", userID);
                hashMap1.put("postIdUser", postIdUser);
                hashMap1.put("Nclient", username);

                docRef1.set(hashMap1);

                String postIdTailor = docRef1.getId();
                Map<String, Object> hashMap2 = new HashMap<>();
                hashMap2.put("postIdUser", postIdTailor);
                docRef.update(hashMap2);

                progressDialog.dismiss();
                Toast.makeText(CommandPost.this, "command is done", Toast.LENGTH_SHORT).show();
                onBackPressed();

            }
        });

    }
}

