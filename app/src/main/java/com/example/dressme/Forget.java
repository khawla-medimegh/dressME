package com.example.dressme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class Forget extends AppCompatActivity {
    //variables
    EditText fgmail;
    Button next ;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        fAuth = FirebaseAuth.getInstance();
        fgmail = findViewById(R.id.forg_mail) ;
        next = findViewById(R.id.forg_log) ;
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all the values
                String fgemail = fgmail.getText().toString();
                if (TextUtils.isEmpty(fgemail)){
                    Toast.makeText(Forget.this, "Email is required!", Toast.LENGTH_SHORT).show();
                } else {
                    fAuth.sendPasswordResetEmail(fgemail).addOnSuccessListener(new OnSuccessListener<Void>(){
                        @Override
                        public void onSuccess(Void aVoid){
                            Toast.makeText(Forget.this, "Reset Link sent to your Email!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),Login.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e){
                            Toast.makeText(Forget.this, "Error!"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

}