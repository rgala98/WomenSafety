package com.replon.www.womensafety;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegisterGuardian extends AppCompatActivity {

    public static final String TAG = "RegisterGuardianActivi";
    ImageView back;

    Button btn_save_details;
    EditText et_name, et_phone;
    LinearLayout layout_view;

    DatabaseHelperGuardians myDb;
    Boolean data_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_guardian);

        //changing statusbar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.purple));
            window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        layout_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ViewRegistered.class);
                startActivity(intent);
            }
        });

    }

    private void init(){
        back = (ImageView) findViewById(R.id.back);

        btn_save_details = (Button) findViewById(R.id.btn_save);

        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);

        layout_view = (LinearLayout) findViewById(R.id.layout_view);

        myDb = new DatabaseHelperGuardians(getApplicationContext());
    }

    private void saveData(){
        String name = et_name.getText().toString().trim();
        String phone =et_phone.getText().toString().trim();

        if(phone.isEmpty() || name.isEmpty() ){
            showMessage("Error","Kindly fill all details");
        }else{

            boolean isInserted= myDb.insertData(name,phone);

            if(isInserted){
                showMessage("Success","You have registered "+name+"'s details successfully");

            }else{
                Log.i(TAG,"Unable to register");

            }

        }
    }


    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(RegisterGuardian.this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }






    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
