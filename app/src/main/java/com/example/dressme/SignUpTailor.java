package com.example.dressme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.HashMap;
import java.util.Map;

public class SignUpTailor extends AppCompatActivity {
    public static final String TAG = "TAG";
    //variables
    ImageView logoTail_image;
    Button regTailBtn, regTailToLoginBtn;
    EditText regTailUsername, regTailPassword, regTailEmail, regTailPhoneNo, regTailName;
    FirebaseAuth fAuth;
    String userID;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_tailor);
        //Hooks
        regTailName = findViewById(R.id.regTail_Name) ;
        regTailUsername = findViewById(R.id.regTail_Username) ;
        regTailPhoneNo = findViewById(R.id.regTail_PhoneNo);
        regTailPassword = findViewById(R.id.regTail_Password) ;
        regTailEmail = findViewById(R.id.regTail_Email) ;
        regTailBtn = findViewById(R.id.regTail_btn) ;
        regTailToLoginBtn = findViewById(R.id.loginTail_btn);
        logoTail_image = findViewById(R.id.logTail_image);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }
        //save data on firebase on button click
        regTailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all the values
                final String name = regTailName.getText().toString();
                final String username = regTailUsername.getText().toString();
                final String email = regTailEmail.getText().toString();
                final String phoneNo = regTailPhoneNo.getText().toString();
                String password = regTailPassword.getText().toString();


                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ){
                    Toast.makeText(SignUpTailor.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6){
                    Toast.makeText(SignUpTailor.this, "Password must have 6 characters.", Toast.LENGTH_SHORT).show();
                }
                else {
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpTailor.this, new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpTailor.this, "user created.", Toast.LENGTH_SHORT).show();
                                userID = fAuth.getCurrentUser().getUid();

                                DocumentReference documentReference = fStore.collection("tailors").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("fullname", name);
                                user.put("username", username);
                                user.put("email", email);
                                user.put("phone", phoneNo);
                                user.put("description", "");
                                user.put("appear", "no");
                                user.put("search", username.toLowerCase());
                                user.put("imageUrl","https://firebasestorage.googleapis.com/v0/b/dressme-c70f6.appspot.com/o/ProfilePictures%2Fdefault_photo.jpg?alt=media&token=463a60f5-3f49-4bde-9156-cbbcf132c036");
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: user profile is created for"+ userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure"+ e.toString());
                                    }
                                });

                                startActivity(new Intent(getApplicationContext(), Login.class));
                            } else {
                                Toast.makeText(SignUpTailor.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    } //onCreate method end


    public void callLoginScreent(View view) {
        Intent intent = new Intent(getApplicationContext(),Login.class);
        startActivity(intent);
    }
}