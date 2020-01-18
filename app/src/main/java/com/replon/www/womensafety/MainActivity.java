package com.replon.www.womensafety;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AccelerometerListener {

    public static final String TAG = "MainActivity";
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 87;
    public static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 18;
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 69;

    RelativeLayout layout_police,layout_register_yourself,layout_register_guardian,layout_instructions, layout_view_registered;
    TextView tv_police_station;
    LocationTrack locationTrack;
    double longitude,latitude;
    String name, address, googleLink;

    ArrayList<ContentsGuardian> guardianArrayList;
    Boolean guardianDataFound,selfDataFound;

    String self_name, self_phone,message,phoneNo;

    DatabaseHelperGuardians myDbGuardians;
    DatabaseHelperSelf myDbSelf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), MainActivity.this);

//        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        ShakeDetector sd = new ShakeDetector(this);
//        sd.start(sensorManager);

        setContentView(R.layout.activity_main);

        //changing statusbar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }

        init();

        //Asking location permission in start so sms permission can be asked later
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            }
        }


        layout_police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
//
//                    } else {
//                        ActivityCompat.requestPermissions(MainActivity.this,
//                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                                MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
//
//                    }
//                }
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                    }
                } else{
                    locationTrack = new LocationTrack(MainActivity.this);

                    if (locationTrack.canGetLocation()) {

                        longitude = locationTrack.getLongitude();
                        latitude = locationTrack.getLatitude();

                        Log.i(TAG,"Latitude UPP" + latitude);

                        String link = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=police%station&inputtype=textquery&fields=formatted_address,name,opening_hours&locationbias=circle:2000@"+latitude+","+longitude+"&key=AIzaSyBAqc3xiqhQSsZmIlUCsrJE9ta1-PSrhY0";
                        new gettingDataAsync().execute(link);
                    }else {
                        Toast.makeText(getApplicationContext(), "CANNOT FIND YOUR LOCATION", Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });

        tv_police_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String map = "http://maps.google.co.in/maps?q=" + name + " " + address;
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(i);
            }
        });

        layout_register_yourself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterYourself.class);
                startActivity(intent);
            }
        });

        layout_register_guardian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterGuardian.class);
                startActivity(intent);
            }
        });

        layout_view_registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ViewRegistered.class);
                startActivity(intent);
            }
        });

        layout_instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Instructions.class);
                startActivity(intent);
            }
        });
    }


    private void init(){
        layout_police = (RelativeLayout) findViewById(R.id.layout_police);
        layout_instructions = (RelativeLayout) findViewById(R.id.layout_instructions);
        layout_register_yourself = (RelativeLayout) findViewById(R.id.layout_register_yourself);
        layout_register_guardian = (RelativeLayout) findViewById(R.id.layout_register_guardian);
        layout_view_registered = (RelativeLayout) findViewById(R.id.layout_view_registered);

        tv_police_station = (TextView) findViewById(R.id.tv_police_station);

        guardianArrayList=new ArrayList<>();

        myDbGuardians = new DatabaseHelperGuardians(getApplicationContext());
        myDbSelf=new DatabaseHelperSelf(getApplicationContext());

    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {

        //Auto Generated Method
    }

    @Override
    public void onShake(float force) {

        Toast.makeText(this, "Shake Detected", Toast.LENGTH_SHORT).show();
        Log.i(TAG,"Shake detected");

            layout_police.performClick();
            if(locationTrack == null){
                locationTrack = new LocationTrack(getApplicationContext());
            }
            latitude = locationTrack.getLatitude();
            longitude = locationTrack.getLongitude();



        if(guardianDataFound && guardianArrayList.size()!=0){

            for(int i=0;i<guardianArrayList.size();i++) {
                sendSMSMessage("+91"+guardianArrayList.get(i).getPhone());
            }
            AccelerometerManager.stopListening();
        }
        else {
            Toast.makeText(getApplicationContext(),"No Guardian Registered. Please follow instructions",Toast.LENGTH_SHORT).show();
        }

    }


    private class gettingDataAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG,"IN DO BG");
            FileContentReader fcr= new FileContentReader(getApplicationContext());

            String data= fcr.getContentFromUrl("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=police%station&inputtype=textquery&fields=formatted_address,name,opening_hours&locationbias=circle:2000@"+latitude+","+longitude+"&key=AIzaSyBAqc3xiqhQSsZmIlUCsrJE9ta1-PSrhY0");

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tv_police_station.setVisibility(View.VISIBLE);
            if(!s.isEmpty())
            {
                try{
                    JSONObject obj = new JSONObject(s);

                    JSONArray array = obj.getJSONArray("candidates");
                    JSONObject object = array.getJSONObject(0);
                    name = object.getString("name");
                    address = object.getString("formatted_address");

                    tv_police_station.setText("Name: " +name+"\nAddress: "+address);

                }catch(JSONException e)
                {
                    e.printStackTrace();}

            }

        }
    }

    public class FileContentReader {
        private Context appContext;

        public FileContentReader(Context context){
            this.appContext=context;
        }
        public String getContentFromUrl(String url)
        {
            StringBuilder content = new StringBuilder();
            try {
                URL u = new URL(url);
                HttpURLConnection uc = (HttpURLConnection) u.openConnection();
                if (uc.getResponseCode()==HttpURLConnection.HTTP_OK) {

                    InputStream is = uc.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    String line;
                    while ((line = br.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }else{

                    throw new IOException(uc.getResponseMessage());
                }
            } catch(Exception s){
                s.printStackTrace();
            } catch(Error e){
                e.printStackTrace();
            }
            return content.toString();


        }
    }



    public void viewRegisteredData() {

        Log.i(TAG,"viewRegisteredData running");

        guardianArrayList.clear();

        StringBuffer buffer=new StringBuffer();
        if(myDbGuardians.getAllData()!=null) {

            Cursor res = myDbGuardians.getAllData();
            if (res.getCount() == 0) {
                Log.i(TAG, "There is no stored data");
                guardianDataFound = false;

            }
            else {
                guardianDataFound = true;
                while (res.moveToNext()) {


                    buffer.append("ID: " + res.getString(0) + "\n");
                    buffer.append("name: " + res.getString(1) + "\n");
                    buffer.append("phone: " + res.getString(2) + "\n\n");

                    guardianArrayList.add(new ContentsGuardian(
                            res.getString(0),
                            res.getString(1),
                            res.getString(2)


                    ));

                }
                Log.i(TAG,"\n"+buffer.toString());

            }


            Cursor self_res = myDbSelf.getAllData();
            if (self_res.getCount() == 0) {
                Log.i(TAG, "There is no stored data");
                selfDataFound = false;

            }
            else {
                selfDataFound = true;
                while (self_res.moveToNext()) {


                    buffer.append("ID: " + self_res.getString(0) + "\n");
                    buffer.append("name: " + self_res.getString(1) + "\n");
                    buffer.append("phone: " + self_res.getString(2) + "\n\n");

                    self_name =  self_res.getString(1);
                    self_phone = self_res.getString(2);


                }
                Log.i(TAG,"\n"+buffer.toString());

            }

        }

    }

    private void sendSMSMessage(String phone){

        googleLink = "https://maps.google.com/?q="+latitude+","+longitude;
        message = "I am in danger. Please Help. This is where I am now: "+googleLink;
        phoneNo = phone;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);

            }
        }else{
            Log.i(TAG,"Sending message to "+phoneNo);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Messages sent.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG,"Sending message to "+phoneNo);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS Permission Denied, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }

                break;
            }

            case  MY_PERMISSIONS_REQUEST_FINE_LOCATION:{

                if (grantResults!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    locationTrack = new LocationTrack(MainActivity.this);

                    if (locationTrack.canGetLocation()) {

                        longitude = locationTrack.getLongitude();
                        latitude = locationTrack.getLatitude();

                        Log.i(TAG,"Latitude UPP" + latitude);

                        String link = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=police%station&inputtype=textquery&fields=formatted_address,name,opening_hours&locationbias=circle:2000@"+latitude+","+longitude+"&key=AIzaSyBAqc3xiqhQSsZmIlUCsrJE9ta1-PSrhY0";
                        new gettingDataAsync().execute(link);
                    }else {
                        Toast.makeText(getApplicationContext(), "CANNOT FIND YOUR LOCATION", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Location Permission Denied, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();


        //Check device supported Accelerometer sensor or not
        if (AccelerometerManager.isSupported(this)) {

            //Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }

        viewRegisteredData();
    }

    @Override
    public void onStop() {
        super.onStop();

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();


        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Sensor", "Service  distroy");

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();

        }

    }





}
