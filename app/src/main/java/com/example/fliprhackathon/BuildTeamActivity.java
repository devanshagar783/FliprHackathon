package com.example.fliprhackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class BuildTeamActivity extends AppCompatActivity {

    private static final String TAG = "BuildTeamActivity";
    private CircularProgressIndicator indicator;
    private TextView coins;
    Thread uithread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_team);
        uithread = Thread.currentThread();
        indicator = findViewById(R.id.coinsremcircular);
        coins = findViewById(R.id.coinsremtext);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        sleep(3000);
                        indicator.setProgress(indicator.getProgress() - 5, true);
                        Log.d(TAG, "run: " + indicator.getProgress());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                coins.setText(indicator.getProgress() + "");
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}