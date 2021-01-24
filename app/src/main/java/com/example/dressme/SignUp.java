package com.example.dressme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUp extends AppCompatActivity {

    public static final String TAG = "TAG";
    //variables
    ImageView logo_image;
    Button regBtn, regToLoginBtn;
    EditText regUsername, regPassword, regEmail, regPhoneNo, regName;
    FirebaseAuth fAuth;
    String userID;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        //Hooks
        regName = findViewById(R.id.reg_Name) ;
        regUsername = findViewById(R.id.reg_Username) ;
        regPhoneNo = findViewById(R.id.reg_PhoneNo);
        regPassword = findViewById(R.id.reg_Password) ;
        regEmail = findViewById(R.id.reg_Email) ;
        regBtn = findViewById(R.id.reg_btn) ;
        regToLoginBtn = findViewById(R.id.login_btn);
        logo_image = findViewById(R.id.log_image);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }
        //save data on firebase on button click
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all the values
                final String name = regName.getText().toString();
                final String username = regUsername.getText().toString();
                final String email = regEmail.getText().toString();
                final String phoneNo = regPhoneNo.getText().toString();
                String password = regPassword.getText().toString();


                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ){
                    Toast.makeText(SignUp.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6){
                    Toast.makeText(SignUp.this, "Password must have 6 characters.", Toast.LENGTH_SHORT).show();
                }
                else {
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "user created.", Toast.LENGTH_SHORT).show();
                                userID = fAuth.getCurrentUser().getUid();

                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("fullname", name);
                                user.put("username", username);
                                user.put("email", email);
                                user.put("phone", phoneNo);
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
                                Toast.makeText(SignUp.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    } //onCreate method end


    public void callLoginScreen(View view) {
        Intent intent = new Intent(getApplicationContext(),Login.class);
        startActivity(intent);
    }


}