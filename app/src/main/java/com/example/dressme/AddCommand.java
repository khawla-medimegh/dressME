package com.example.dressme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaDrm;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



public class AddCommand extends AppCompatActivity {
    private static final String TAG = "AddCommand";

    ImageView cmdphoto;
    Button order;
    EditText descripcmd;

    FirebaseAuth fAuth;
    FirebaseUser user;
    String userID;
    FirebaseFirestore fStore;
    static final int PICK_IMAGE = 1;
    Uri imageUri;
    String username;

    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    public static final String Extra_tID = "TailleurID";
    public static final String Extra_username = "TailleurName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_command);

        Intent intent = getIntent();
        final String tailId = intent.getStringExtra(Extra_tID);
        final String tailName= intent.getStringExtra(Extra_username);
        cmdphoto = findViewById(R.id.image_cmd);
        order = findViewById(R.id.order);
        descripcmd = findViewById(R.id.descripcmd);

        storageReference = FirebaseStorage.getInstance().getReference("Commandes");
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
        cmdphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseFile();
            }

        });



        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(AddCommand.this);
                progressDialog.setMessage("Progressing");
                progressDialog.show();
                if (imageUri != null) {
                    final StorageReference filerefrence = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
                    uploadTask = filerefrence.putFile(imageUri);
                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if (!task.isComplete()) {
                                throw task.getException();
                            }
                            return filerefrence.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                myUrl = downloadUri.toString();

                                DocumentReference documentReference1 = fStore.collection("tailors").document(tailId);

                                //ajouter au firestore client
                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                CollectionReference collectionReference = documentReference.collection("commandes");
                                DocumentReference docRef = collectionReference.document();
                                Map<String, Object> hashMap = new HashMap<>();
                                hashMap.put("comdUrl", myUrl);
                                hashMap.put("despComd", descripcmd.getText().toString());
                                hashMap.put("etat", "nonConfirme");
                                hashMap.put("tailorID", tailId);
                                hashMap.put("Ntailor", tailName);
                                docRef.set(hashMap);

                                String postIdUser = docRef.getId();

                                //ajouter au firebase tailor


                                CollectionReference collectionReference1 = documentReference1.collection("commandes");
                                DocumentReference docRef1 = collectionReference1.document();

                                Map<String, Object> hashMap1 = new HashMap<>();
                                hashMap1.put("comdUrl", myUrl);
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
                                Toast.makeText(AddCommand.this, "command is done", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(AddCommand.this, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddCommand.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }



    public void onChooseFile() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            cmdphoto.setImageURI(imageUri);


        } else {
            Toast.makeText(this, "something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}
