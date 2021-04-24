package com.example.fliprhackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailSignUp extends AppCompatActivity {

    private static final String TAG = "EmailSignUp";

    private TextInputLayout emailbox, pwdbox;
    private EditText email, pwd;
    private Button register, login;
    private FloatingActionButton fbsign, phonesign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);

        emailbox = findViewById(R.id.emailidbox);
        pwdbox = findViewById(R.id.passwdbox);
        email = findViewById(R.id.emailid);
        pwd = findViewById(R.id.passwd);
        register = findViewById(R.id.registerem);
        login = findViewById(R.id.loginem);
        fbsign = findViewById(R.id.fbsign);
        phonesign = findViewById(R.id.phonesign);
        setWatcher(emailbox, email);
        setWatcher(pwdbox, pwd);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    emailbox.setError(null);
                    pwdbox.setError(null);
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), pwd.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(EmailSignUp.this, "Verification email sent", Toast.LENGTH_LONG).show();
                                                        } else
                                                            Toast.makeText(EmailSignUp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    } else {
                                        System.out.println("Error");
                                        task.getException().printStackTrace();
                                        Toast.makeText(EmailSignUp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } catch (Exception e) {
                    if (email.getText().toString().length() < 12)
                        emailbox.setError("Enter Valid email address");
                    if (pwd.getText().toString().length() < 6)
                        pwdbox.setError("Password should e greater than 6 characters");
                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), pwd.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    } else
                                        Toast.makeText(getApplicationContext(), "Please verify email", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        fbsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EmailSignUp.this, "FB sign in", Toast.LENGTH_SHORT).show();
            }
        });

        phonesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailSignUp.this, PhoneAuthActivity.class));
            }
        });
    }

    public void setWatcher(TextInputLayout layout, EditText editText) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        editText.addTextChangedListener(watcher);
    }
}