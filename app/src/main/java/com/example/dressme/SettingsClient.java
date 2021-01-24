package com.example.dressme;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsClient extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener  {

    private static final String TAG="Settings";
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView change;
    EditText fullname, username, email, phone ;
    Button confirmBtn, resetpasswordBtn;
    CircleImageView ProfileImage;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userID;
    FirebaseFirestore fStore;
    static final int PICK_IMAGE = 1;
    Uri imageUri;

    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_settingsclient);
        ProfileImage = findViewById(R.id.client_image);
        fullname= findViewById(R.id.fullname_client);
        username= findViewById(R.id.username_client);
        email = findViewById(R.id.email_client);
        phone = findViewById(R.id.phone_client);
        resetpasswordBtn = findViewById(R.id.reset_passwordclient);
        confirmBtn = findViewById(R.id.confirm_buttonclient);
        change = findViewById(R.id.changePicture);
        storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures");
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        if (user != null){
            userID = user.getUid();}
        drawerLayout = findViewById(R.id.drawerLayoutClient);
        navigationView = findViewById(R.id.nav_viewClient);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //navig drawer menu
        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        if (user != null) {
            updateNavHeader();
        }
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseFile();
            }

        });

        final DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    fullname.setText(documentSnapshot.getString("fullname"));
                    username.setText(documentSnapshot.getString("username"));
                    email.setText(documentSnapshot.getString("email"));
                    phone.setText(documentSnapshot.getString("phone"));
                    String imgUrl = documentSnapshot.getString("imageUrl");
                    Glide.with(SettingsClient.this).load(imgUrl).into(ProfileImage);

                } else {
                    Log.d(TAG, "Document do not exist");
                }
            }
        });

        resetpasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid){
                        Toast.makeText(SettingsClient.this, "Reset Link sent to your Email!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),SettingsClient.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e){
                        Toast.makeText(SettingsClient.this, "Error!"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateall();
            }
        });

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void updateall(){
        final ProgressDialog progressDialog = new ProgressDialog( this);
        progressDialog.setMessage("Updating");
        progressDialog.show();
        if (imageUri != null){
            final StorageReference filerefrence = storageReference.child(System.currentTimeMillis()+ "."+ getFileExtension(imageUri));
            uploadTask = filerefrence.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()){
                        throw task.getException();
                    }
                    return filerefrence.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        Map<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageUrl", myUrl);
                        documentReference.update(hashMap);
                    } else {
                        Toast.makeText(SettingsClient.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SettingsClient.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if ( fullname.getText().toString().isEmpty() || username.getText().toString().isEmpty() || email.getText().toString().isEmpty() || phone.getText().toString().isEmpty()){
            Toast.makeText(SettingsClient.this, "One or many fields are empty. ", Toast.LENGTH_SHORT).show();
            return;
        }
        final String profilemail = email.getText().toString();
        user.updateEmail(profilemail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DocumentReference docRef = fStore.collection("users").document(userID);
                Map<String, Object> edited = new HashMap<>();
                edited.put("fullname", fullname.getText().toString());
                edited.put("username", username.getText().toString());
                edited.put("email", email.getText().toString());
                edited.put("phone", phone.getText().toString());
                docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SettingsClient.this, "Profil updated.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure"+ e.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsClient.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onChooseFile () {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            ProfileImage.setImageURI(imageUri);


        } else {
            Toast.makeText(this, "something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch ( menuItem.getItemId()){
            case R.id.nav_explore:
                Intent intent2 = new Intent(getApplicationContext(), HomePage.class);
                startActivity(intent2);
                break;
            case R.id.nav_favourites :
                Intent intent1 = new Intent(getApplicationContext(), Favourites.class);
                startActivity(intent1);
                break;
            case R.id.nav_Cart :
                Intent intent3 = new Intent(getApplicationContext(), cart_client.class);
                startActivity(intent3);
                break;
            case R.id.nav_settingsClient :
                break;
            case R.id.nav_logoutClient :
                FirebaseAuth.getInstance().signOut();
                Intent intentlogin = new Intent(this,Login.class);
                startActivity(intentlogin);
                finish();

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout1 = findViewById(R.id.drawerLayoutClient);
        if (drawerLayout1.isDrawerOpen(GravityCompat.START)){
            drawerLayout1.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void updateNavHeader() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_viewClient);
                    final CircleImageView headerImage = findViewById(R.id.header_imageclient);
                    TextView headerUser = findViewById(R.id.header_usernameclient);
                    View headerView = navigationView.getHeaderView(0);
                    headerUser.setText(documentSnapshot.getString("username"));
                    String imgUrl = documentSnapshot.getString("imageUrl");
                    Glide.with(headerView).load(imgUrl).into(headerImage);
                }
            });
        }
    }

}