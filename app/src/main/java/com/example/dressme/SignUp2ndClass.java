package com.example.dressme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SignUp2ndClass extends AppCompatActivity {
    RadioButton tailor,client;
    RadioGroup radioGroup;
    boolean choose = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up2nd_class);

        tailor = (RadioButton)findViewById(R.id.radTailor);
        client = (RadioButton)findViewById(R.id.radClient);
        radioGroup = (RadioGroup)findViewById(R.id.radGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(tailor.isChecked())
                {
                    Toast.makeText(SignUp2ndClass.this, "Tailor", Toast.LENGTH_SHORT).show();
                    choose = false;
                }
                else {
                    Toast.makeText(SignUp2ndClass.this, "Client", Toast.LENGTH_SHORT).show();
                    choose = true;
                }
            }
        });
    }
    public void returnLoginScreen(View view) {
        Intent intent = new Intent(getApplicationContext(),Login.class);
        startActivity(intent);
    }
    public void callNextSignupScreen(View view){
        if (choose){
        Intent intent = new Intent(getApplicationContext(),SignUp.class);
        startActivity(intent);
        }
        else {
            Intent intent = new Intent(getApplicationContext(),SignUpTailor.class);
            startActivity(intent);
        }
    }
}