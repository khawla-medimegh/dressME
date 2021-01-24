package com.example.dressme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.dressme.R.id.signup_screen;

public class Login extends AppCompatActivity {


    //variables
    Button callSignUp, loginHome;
    EditText mPassword, mEmail;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        callSignUp = findViewById(signup_screen);
        loginHome = findViewById(R.id.loginBtn);
        mPassword = findViewById(R.id.log_password) ;
        mEmail = findViewById(R.id.log_email) ;
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loginHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all the values

                final String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();


                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ){
                    Toast.makeText(Login.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6){
                    Toast.makeText(Login.this, "Password must have 6 characters.", Toast.LENGTH_SHORT).show();
                }
                else {
                    fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                //si tailleur ou utilisateur
                                CollectionReference usersCollectionRef =  fStore.collection("tailors");
                                Query userQuery = usersCollectionRef.whereEqualTo("email" , email);

                                userQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()){
                                            //C'est un tailleur
                                            startActivity(new Intent(getApplicationContext(), ProfilTailor.class));
                                            Toast.makeText(Login.this, "logged in successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            startActivity(new Intent(getApplicationContext(), HomePage.class));
                                            Toast.makeText(Login.this, "logged in successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(Login.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp2ndClass.class);
                startActivity(intent);
            }
        });
    }
    public void forgetPassword(View view) {
        Intent intent = new Intent(getApplicationContext(),Forget.class);
        startActivity(intent);
    }
}
