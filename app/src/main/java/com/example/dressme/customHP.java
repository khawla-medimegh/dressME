package com.example.dressme;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class customHP extends AppCompatActivity {
    CircleImageView proft;
    TextView nomtl;
    ImageView imgp1, imgp2, imgp3;
    Button conf;
    String Url1 = "";
    String Url2 = "";
    String Url3 = "";
    String url11 = "";
    String url12 = "";
    String url13 = "";
    Uri downloadUri1, downloadUri2, downloadUri3;

    public Uri imageUri1, imageUri2, imageUri3;
    public int ann;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String tailID;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    DocumentReference documentReference;

    StorageTask uploadTask;
    String imgUrl, Ntailor;
    String appear = "no";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_h_p);
        proft = findViewById(R.id.imgt);
        nomtl = findViewById(R.id.nomtl);
        imgp1 = findViewById(R.id.imgp1);
        imgp2 = findViewById(R.id.imgp2);
        imgp3 = findViewById(R.id.imgp3);
        conf = findViewById(R.id.conf_cust);

        // bel intent bech njib tailorId, profilUrl, nom
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        if (user != null) {
            tailID = user.getUid();
        }

        storageReference = FirebaseStorage.getInstance().getReference("Customize");
        fStore = FirebaseFirestore.getInstance();
        documentReference = fStore.collection("tailors").document(tailID);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    Ntailor=documentSnapshot.getString("username");
                    nomtl.setText(Ntailor);
                    imgUrl = documentSnapshot.getString("imageUrl");
                    Glide.with(customHP.this).load(imgUrl).into(proft);
                    appear = documentSnapshot.getString("appear");


                } else {
                    Log.d("custom", "Document do not exist");
                }
            }
        });


        Query collectionReference = fStore.collection("customize").whereEqualTo("tailID", tailID);
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        url11 = snapshot.getString("Url1");
                        url12 = snapshot.getString("Url2");
                        url13 = snapshot.getString("Url3");
                        Glide.with(customHP.this).load(url11).into(imgp1);
                        Glide.with(customHP.this).load(url12).into(imgp2);
                        Glide.with(customHP.this).load(url13).into(imgp3);
                    }
                }
            }

        });


        imgp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ann = 1;
                onChooseFile();

            }

        });

        imgp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ann = 2;
                onChooseFile();

            }

        });
        imgp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ann = 3;
                onChooseFile();

            }

        });

        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImages();

            }
        });


    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public void onChooseFile() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            switch (ann) {
                case 1:
                    imageUri1 = result.getUri();
                    imgp1.setImageURI(imageUri1);
                    final StorageReference filerefrence1 = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri1));

                    uploadTask = filerefrence1.putFile(imageUri1);
                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if (!task.isComplete()) {
                                throw task.getException();
                            }
                            return filerefrence1.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {

                                downloadUri1 = task.getResult();
                                Url1 = downloadUri1.toString();
                            }
                        }
                    });


                    break;
                case 2:
                    imageUri2 = result.getUri();
                    imgp2.setImageURI(imageUri2);
                    final StorageReference filerefrence2 = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri2));
                    uploadTask = filerefrence2.putFile(imageUri2);
                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if (!task.isComplete()) {
                                throw task.getException();
                            }
                            return filerefrence2.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {

                                downloadUri2 = task.getResult();
                                Url2 = downloadUri2.toString();
                            }
                        }
                    });


                    break;
                case 3:
                    imageUri3 = result.getUri();
                    imgp3.setImageURI(imageUri3);
                    final StorageReference filerefrence3 = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri3));
                    uploadTask = filerefrence3.putFile(imageUri3);
                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if (!task.isComplete()) {
                                throw task.getException();
                            }
                            return filerefrence3.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {

                                downloadUri3 = task.getResult();
                                Url3 = downloadUri3.toString();
                            }
                        }
                    });


                    break;
            }


        } else {
            Toast.makeText(this, "something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImages() {


        if (appear.equals("no")) {

            if ("".equals(Url1) && "".equals(Url2) && "".equals(Url3)) {
                Toast.makeText(customHP.this, "You have to add 3 pictures to appear to clients!", Toast.LENGTH_SHORT).show();
            }

            if (!"".equals(Url1) && !"".equals(Url2) && !"".equals(Url3)) {

                CollectionReference collectionReference = fStore.collection("customize");
                DocumentReference docRef = collectionReference.document(tailID);
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("Url1", Url1);
                hashMap.put("Url2", Url2);
                hashMap.put("Url3", Url3);
                hashMap.put("tailID",tailID);
                hashMap.put("Ntailor",Ntailor);
                hashMap.put("imgUrl",imgUrl);
                docRef.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(customHP.this, "customized !", Toast.LENGTH_SHORT).show();
                    }
                });
                Map<String, Object> edited = new HashMap<>();
                edited.put("appear", "yes");
                documentReference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(customHP.this, "Appears to clients!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } else {
            if (Url1 != "") {
                CollectionReference collectionReference = fStore.collection("customize");
                DocumentReference docRef = collectionReference.document(tailID);
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("Url1", Url1);
                docRef.update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(customHP.this, "image updated !", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            if (Url2 != "") {
                CollectionReference collectionReference = fStore.collection("customize");
                DocumentReference docRef = collectionReference.document(tailID);
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("Url2", Url2);
                docRef.update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(customHP.this, "image updated !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (Url3 != "") {
                CollectionReference collectionReference = fStore.collection("customize");
                DocumentReference docRef = collectionReference.document(tailID);
                Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("Url3", Url3);
                docRef.update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(customHP.this, "image updated !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}