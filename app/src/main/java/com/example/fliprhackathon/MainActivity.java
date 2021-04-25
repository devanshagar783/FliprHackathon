package com.example.fliprhackathon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView userName;
    private ImageView userDP;
    private TextView textView;
    private Button btn;
    RecyclerView upcomingMachesRV;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userName = findViewById(R.id.userName);
        userDP = findViewById(R.id.userDP);
        if (getIntent().getBooleanExtra("fb", false)) {
            Uri userID = Profile.getCurrentProfile().getProfilePictureUri(120, 120);
            userName.setText("Welcome, " + currentUser.getDisplayName());
            Glide.with(this)
                    .asBitmap()
                    .load(userID)
                    .into(userDP);
        } else {
            userName.setVisibility(View.INVISIBLE);
            userDP.setVisibility(View.INVISIBLE);
        }

//        btn = findViewById(R.id.buildteam);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), BuildTeamActivity.class));
//            }
//        });

        textView = findViewById(R.id.textview);
        upcomingMachesRV = findViewById(R.id.upcomingMatchesRV);
//        readExcelSheet();
//        readSheet2();
        readJsonNames();
    }

    void readExcelSheet() {
        try {
            InputStream myInput;
            // initialize asset manager
            AssetManager assetManager = getAssets();
            //  open excel file name as myexcelsheet.xls
            myInput = assetManager.open("IPL_Data.xls");
            // Create a POI File System object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // Get the first sheet from workbook
            //this is to get the first excel sheet
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            // to iterate through the cells.
            Iterator<Row> rowIter = mySheet.rowIterator();
            int rowno = 0;
            textView.append("\n");
            while (rowIter.hasNext()) {
                Log.e(TAG, " row no " + rowno);
                HSSFRow myRow = (HSSFRow) rowIter.next();
                if (rowno != 0) {
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno = 0;
                    String name = "", points = "";
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno == 0) {
                            name = myCell.toString();
                        } else if (colno == 1) {
                            points = myCell.toString();
                        }
                        colno++;
                        Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }
                    textView.append(name + "  " + points + "\n");
                }
                rowno++;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() + "error", Toast.LENGTH_SHORT).show();
        }
    }

    void readSheet2() {
        try {
            InputStream myInput;
            // initialize asset manager
            AssetManager assetManager = getAssets();
            //  open excel file name as myexcelsheet.xls
            myInput = assetManager.open("IPL_Data.xls");
            // Create a POI File System object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // Get the first sheet from workbook
            //this is to get the first excel sheet
            HSSFSheet mySheet = myWorkBook.getSheetAt(1);

            HashMap<String, String> hashMap = new HashMap<String, String>();

            Iterator<Row> rowIter = mySheet.rowIterator();
            int rowno = 0;
            textView.append("\n");
            while (rowIter.hasNext()) {
                //Log.e(TAG, " row no "+ rowno );
                HSSFRow myRow = (HSSFRow) rowIter.next();
                if (rowno != 0) {
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno = 0;
                    String name = "", points = "";
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno == 0) {
                            name = myCell.toString();
                        } else if (colno == 1) {
                            points = myCell.toString();
                        }
                        colno++;
                        //Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }
                    //textView.append(name + "  " + points + "\n");
                    Log.d(TAG, "readSheet2: " + name + "    " + points);
                    hashMap.put(name, points);
                }
                rowno++;
            }
            textView.append("\n");
            for (Map.Entry map : hashMap.entrySet()) {
                textView.append(map.getKey() + " - " + map.getValue() + "\n");
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() + "error", Toast.LENGTH_SHORT).show();
        }
    }

    void readJsonNames() {
        try {
            InputStream myInput;
            // initialize asset manager
            AssetManager assetManager = getAssets();
            //  open excel file name as myexcelsheet.xls
            myInput = assetManager.open("IPL_Data.xls");
            // Create a POI File System object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // Get the first sheet from workbook
            //this is to get the first excel sheet
            HSSFSheet mySheet = myWorkBook.getSheetAt(2);

            String jsonfiles[] = new String[231];
            int index = 0;

            Iterator<Row> rowIter = mySheet.rowIterator();
            int rowno = 0;
            textView.append("\n");
            while (rowIter.hasNext()) {
                //Log.e(TAG, " row no "+ rowno );
                HSSFRow myRow = (HSSFRow) rowIter.next();
                if (rowno != 0) {
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno = 0;
                    String name = "";
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        name = myCell.toString();
                        jsonfiles[index++] = name;
                    }
                }
                rowno++;
            }

            String rand = jsonfiles[new Random().nextInt(jsonfiles.length)];
            String jsonf = rand.substring(0, rand.length() - 2) + ".json";
            String jsonfile = loadJSONFromAsset(jsonf);


            HashMap<String, String> hashMap = new HashMap<String, String>();
            List<String> teams = new ArrayList<>();
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject = new JSONObject(jsonfile);
                Log.d(TAG, "readJsonNames: " + rand);
                JSONObject info = jsonObject.getJSONObject("info");
                JSONArray teamName = info.getJSONArray("teams");
                for (int i = 0; i < teamName.length(); ++i) {
                    //Log.d(TAG, "readJsonNames: "+teamName);
                    Log.d(TAG, "readJsonNames: " + teamName.getString(i));
                    teams.add(teamName.getString(i));
                }
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
                                while (keys1.hasNext()) {
                                    String key1 = (String) keys1.next();
                                    if (delivery.get(key1) instanceof JSONObject) {
                                        JSONObject del = new JSONObject(delivery.get(key1).toString());
                                        hashMap.put(del.getString("batsman"), teamname1);
                                        hashMap.put(del.getString("non_striker"), teamname1);
                                        hashMap.put(del.getString("bowler"), teamname2);

                                    }
                                }
                            }
                            teams.add(teamname1);
                        }
                    }
                }
                HashMap<String, List<String>> teamsname = new HashMap<String, List<String>>();
                List list1 = new ArrayList();
                List list2 = new ArrayList();
                for (Map.Entry map : hashMap.entrySet()) {
//                    Log.d(TAG, "readJsonNames: " + (map.getKey() + " - " + map.getValue() + "\n"));

                    if(map.getValue().equals((teamName.get(0)))){
                        list1.add(map.getKey());
                    }
                    if(map.getValue().equals((teamName.get(1)))){
                        list2.add(map.getKey());
                    }
                }

                teamsname.put(teamName.get(0).toString(), list1);
                teamsname.put(teamName.get(1).toString(), list2);

                Log.d(TAG, "readJsonNames: "+teamsname);




                JSONObject object = new JSONObject(hashMap);
                //Log.d(TAG, "readJsonNames: "+object);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "readJsonNames: " + e.getMessage());
            }
            finally {
                RVAdapter rvAdapter = new RVAdapter(getApplicationContext(), jsonObject, "upcoming_matches");
                upcomingMachesRV.setAdapter(rvAdapter);
                upcomingMachesRV.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() + "error", Toast.LENGTH_LONG).show();
        }
    }

    public String loadJSONFromAsset(String jsonfile) {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open(jsonfile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }
}