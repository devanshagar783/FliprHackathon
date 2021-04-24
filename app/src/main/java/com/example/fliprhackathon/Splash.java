package com.example.fliprhackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            startActivity(new Intent(this, PhoneAuthActivity.class));
        else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}