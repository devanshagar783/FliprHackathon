package com.example.fliprhackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BuildTeamActivity extends AppCompatActivity {

    private static final String TAG = "BuildTeamActivity";
    private CircularProgressIndicator indicator;
    private TextView coins, team1name, team2name, team1, team2;
    Thread uithread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_team);
        uithread = Thread.currentThread();
        indicator = findViewById(R.id.coinsremcircular);
        coins = findViewById(R.id.coinsremtext);
        team1name = findViewById(R.id.team1name);
        team2name = findViewById(R.id.team2name);
        team1 = findViewById(R.id.team1name1);
        team2 = findViewById(R.id.team2name2);
//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    while (true) {
//                        sleep(3000);
//                        indicator.setProgress(indicator.getProgress() - 5, true);
//                        Log.d(TAG, "run: " + indicator.getProgress());
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                coins.setText(indicator.getProgress() + "");
//                            }
//                        });
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        thread.start();


        String json = getIntent().getStringExtra("data");

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                List<String> teams = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    //Log.d(TAG, "onCreate: "+jsonObject);
                    JSONObject info = jsonObject.getJSONObject("info");
                    JSONArray teamName = info.getJSONArray("teams");
                    for (int i = 0; i < teamName.length(); ++i) {
                        teams.add(teamName.getString(i));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            team1name.setText(teams.get(0));
                            //Log.d(TAG, "onCreate: " + team1name);
                            team2name.setText(teams.get(1));
                            team1.setText(team1name.getText());
                            team2.setText(team2name.getText());
                            coins.setText("abc");
                            Log.d(TAG, "run: "+coins.getText());

                        }
                    });
                    Log.d(TAG, "run: "+coins.getText());
                    JSONArray jsonArray = jsonObject.getJSONArray("innings");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo_inside = jsonArray.getJSONObject(i);
                        Iterator<String> keys = jo_inside.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            if (jo_inside.get(key) instanceof JSONObject) {
                                JSONObject inning = new JSONObject(jo_inside.get(key).toString());
                                String teamname1 = inning.getString("team");
                                teams.remove(teamname1);
                                String teamname2 = teams.get(0);
                                JSONArray deliveriesArray = inning.getJSONArray("deliveries");
                                for (int j = 0; j < deliveriesArray.length(); ++j) {
                                    JSONObject delivery = deliveriesArray.getJSONObject(j);
                                    Iterator<String> keys1 = delivery.keys();
                                    //Log.d(TAG, "onCreate: "+keys1);
                                    while (keys1.hasNext()) {
                                        TimeUnit.SECONDS.sleep(5);
                                        String key1 = (String) keys1.next();
                                        if (delivery.get(key1) instanceof JSONObject) {
                                            Log.d(TAG, "onCreate: overs"+key1);
                                            JSONObject del = new JSONObject(delivery.get(key1).toString());
//                                    hashMap.put(del.getString("batsman"), teamname1);
//                                    hashMap.put(del.getString("non_striker"), teamname1);
//                                    hashMap.put(del.getString("bowler"), teamname2);

                                        }
                                    }


//                            Thread thread = new Thread() {
//                                @Override
//                                synchronized public void run() {
//                                    try {
//                                        while (keys1.hasNext()) {
//                                            sleep(5000);
////                                            Log.d(TAG, "run: id " + thread.getId());
//                                            indicator.setProgress(indicator.getProgress() - 5, true);
//                                            Log.d(TAG, "run: progress" + indicator.getProgress());
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    coins.setText(indicator.getProgress() + "");
//                                                }
//                                            });
//
////                                            while (keys1.hasNext()) {
////                                                String key1 = (String) keys1.next();
////                                                if (delivery.get(key1) instanceof JSONObject) {
////                                                    JSONObject del = new JSONObject(delivery.get(key1).toString());
////                                                    Log.d(TAG, "run: overs"+key1);
//////                                                    hashMap.put(del.getString("batsman"), teamname1);
//////                                                    hashMap.put(del.getString("non_striker"), teamname1);
//////                                                    hashMap.put(del.getString("bowler"), teamname2);
////                                                }
//
////                                            }
//
//
//
//                                        }
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            };
//                            thread.start();


//                            while (keys1.hasNext()) {
//                                String key1 = (String) keys1.next();
//                                if (delivery.get(key1) instanceof JSONObject) {
//                                    JSONObject del = new JSONObject(delivery.get(key1).toString());
//                                    hashMap.put(del.getString("batsman"), teamname1);
//                                    hashMap.put(del.getString("non_striker"), teamname1);
//                                    hashMap.put(del.getString("bowler"), teamname2);
//                                }
//                            }
                                }
                                teams.add(teamname1);
                            }
                        }
                    }
                    String t1 = "", t2 = "";
//            for (Map.Entry map : hashMap.entrySet()) {
//                Log.d(TAG, "readJsonNames: " + (map.getKey() + " - " + map.getValue() + "\n"));
//                if (map.getValue().equals(team1.getText().toString()))
//                    t1 += map.getKey() + "\n";
//                if (map.getValue().equals(team2.getText().toString()))
//                    t2 += map.getKey() + "\n";
//            }
//
//            Log.d(TAG, "onCreate: " + t1);
//            Log.d(TAG, "onCreate: " + t2);


                } catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}