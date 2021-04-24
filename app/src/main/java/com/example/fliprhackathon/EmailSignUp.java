package com.example.fliprhackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.example.fliprhackathon.FacebookLogin;
import com.google.firebase.auth.FirebaseUser;

public class EmailSignUp extends AppCompatActivity {

    private static final String TAG = "EmailSignUp";

    private TextInputLayout emailbox, pwdbox;
    private EditText email, pwd;
    private Button register, login, phonesign;
//    private FloatingActionButton fbsign, phonesign;
    private LoginButton fbsign;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

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
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        fbsign.setReadPermissions("email", "public_profile");
        fbsign.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
//                info.setText("User ID: " + loginResult.getAccessToken().getUserId() + "\n" + "Auth Token: " + loginResult.getAccessToken().getToken());
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(EmailSignUp.this, "Login cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(EmailSignUp.this, "Login failed!", Toast.LENGTH_SHORT).show();
            }
        });

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
                                    } else
                                        Toast.makeText(getApplicationContext(), "Please verify email", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

//        fbsign.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(EmailSignUp.this, "FB sign in", Toast.LENGTH_SHORT).show();
//            }
//        });

        phonesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailSignUp.this, PhoneAuthActivity.class));
            }
        });
    }

    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END on_activity_result]

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(EmailSignUp.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(EmailSignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_facebook]

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