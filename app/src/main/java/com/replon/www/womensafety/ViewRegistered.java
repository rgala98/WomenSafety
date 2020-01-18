package com.replon.www.womensafety;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;

public class ViewRegistered extends AppCompatActivity {

    public static final String TAG = "ViewRegisteredActi";
    ImageView back;

    RecyclerView recyclerView;
    GuardianAdapter adapter;
    ArrayList<ContentsGuardian> guardianList;

    DatabaseHelperGuardians myDb;
    Boolean data_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_registered);

        //changing statusbar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.darkPink));
            window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }

    private void init(){
        back = (ImageView) findViewById(R.id.back);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        guardianList = new ArrayList<>();

        myDb = new DatabaseHelperGuardians(getApplicationContext());

        viewRegisteredData();


    }

    public void viewRegisteredData() {

        Log.i(TAG, "viewRegisteredData running");


        StringBuffer buffer = new StringBuffer();
        if (myDb.getAllData() != null) {

            Cursor res = myDb.getAllData();
            if (res.getCount() == 0) {
                Log.i(TAG, "There is no stored data");
                data_found = false;
                recyclerView.setVisibility(View.GONE);

            } else {
                data_found = true;
                recyclerView.setVisibility(View.VISIBLE);
                while (res.moveToNext()) {


                    buffer.append("ID: " + res.getString(0) + "\n");
                    buffer.append("name: " + res.getString(1) + "\n");
                    buffer.append("phone: " + res.getString(2) + "\n\n");

                    guardianList.add(new ContentsGuardian(res.getString(0),res.getString(1),res.getString(2)));

                }
                Log.i(TAG, "\n" + buffer.toString());
                adapter = new GuardianAdapter(this,guardianList);
                recyclerView.setAdapter(adapter);

            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
