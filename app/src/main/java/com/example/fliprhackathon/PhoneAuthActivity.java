package com.example.fliprhackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private static final String TAG = "PhoneAuthActivity";
    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private EditText phoneNumber;
    private EditText smsCode;
    private TextInputLayout phonebox, otpbox;

    private Animation resendanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        phoneNumber = findViewById(R.id.fieldPhoneNumber);
        smsCode = findViewById(R.id.fieldVerificationCode);
        mAuth = FirebaseAuth.getInstance();
        phonebox = findViewById(R.id.phonebox);
        otpbox = findViewById(R.id.otpbox);
        setWatcher(phonebox, phoneNumber);
        setWatcher(otpbox, smsCode);

        resendanim = AnimationUtils.loadAnimation(PhoneAuthActivity.this, R.anim.resend_rot);
        signOut();
        Log.d(TAG, "onCreate");


        findViewById(R.id.buttonStartVerification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.d(TAG, "buttonVerify");
                    phonebox.setError(null);
                    otpbox.setError(null);
                    phoneNumber = findViewById(R.id.fieldPhoneNumber);
                    smsCode = findViewById(R.id.fieldVerificationCode);
                    if (!validatePhoneNumber()) {
                        return;
                    }
                    startPhoneNumberVerification(phoneNumber.getText().toString());
                    //auto retrieval of verification code
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseAuthSettings firebaseAuthSettings = auth.getFirebaseAuthSettings();
                    firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber.getText().toString(), smsCode.getText().toString());
                } catch (Exception e) {
                    phonebox.setError("Enter the field");
                    otpbox.setError("Enter the field");
                }
            }
        });

        findViewById(R.id.buttonVerifyPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Otp");
                if (TextUtils.isEmpty(smsCode.getText().toString())) {
                    otpbox.setError("Cannot be empty.");
                    return;
                }
                otpbox.setError(null);
                verifyPhoneNumberWithCode(mVerificationId, smsCode.getText().toString());
            }
        });

        findViewById(R.id.buttonResend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "buttonResend");
                findViewById(R.id.buttonResend).startAnimation(resendanim);
                phoneNumber = findViewById(R.id.fieldPhoneNumber);
                smsCode = findViewById(R.id.fieldVerificationCode);
                resendVerificationCode(phoneNumber.getText().toString(), mResendToken);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NotNull PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NotNull FirebaseException e) {
                Log.d(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    phonebox.setError("Please check the phone number");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    phonebox.setError(null);
                    Toast.makeText(PhoneAuthActivity.this, "Server overload... could not verify", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
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

    private void startPhoneNumberVerification(String phoneNumber) {
        try {
            Log.d(TAG, "startPhoneNumber");
            FirebaseAuth auth = FirebaseAuth.getInstance();
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phoneNumber)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
            mVerificationInProgress = true;
            auth.setLanguageCode("en");
        } catch (Exception e) {
            Toast.makeText(PhoneAuthActivity.this, " here", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.d(TAG, "signInwithPhone");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            checkRegister();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneAuthActivity.this, "Verification failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void verifyPhoneNumberWithCode(String mVerificationId, String code) {
        Log.d(TAG, "verifyPhoneNumberWithcode");
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            Toast.makeText(PhoneAuthActivity.this, " Enter the fields verify", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validatePhoneNumber() {
        Log.d(TAG, "validatePhoneNumber");
        phoneNumber = findViewById(R.id.fieldPhoneNumber);
        if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
            phonebox.setError("Invalid phone number.");
            return false;
        }
        phonebox.setError(null);
        return true;
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        try {
            phonebox.setError(null);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks,         // OnVerificationStateChangedCallbacks
                    token);             // ForceResendingToken from callbacks
        } catch (IllegalArgumentException e) {
            phonebox.setError("Enter your phone number");
        }
    }

    private void signOut() {
        mAuth.signOut();
    }

    public void checkRegister() {
        TextView phone = findViewById(R.id.fieldPhoneNumber);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("number", phone.getText().toString())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "onSucessListner");
                        List<DocumentSnapshot> snapshotsList = queryDocumentSnapshots.getDocuments();
                        if (!snapshotsList.isEmpty()) {
                            //Log.d(TAG, "inside if" + " No record found");
                            Log.d(TAG, "onSuccess: user found");
                            Toast.makeText(PhoneAuthActivity.this, "Verified!.Loggin in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PhoneAuthActivity.this, MainActivity.class));
                        } else {
                            Log.d(TAG, "checkRegister: not found");
                            Toast.makeText(PhoneAuthActivity.this, "Verified! Create your account", Toast.LENGTH_SHORT).show();
                            saveDetails();
                            startActivity(new Intent(PhoneAuthActivity.this, MainActivity.class));
                        }
                    }
                });
    }

    private void saveDetails() {
        Log.d(TAG, "saveDeatils");

        HashMap<String, Object> map = new HashMap<>();
        map.put("number", phoneNumber.getText().toString());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(map, SetOptions.merge());
    }
}