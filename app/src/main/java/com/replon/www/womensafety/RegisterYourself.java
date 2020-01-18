package com.replon.www.womensafety;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class RegisterYourself extends AppCompatActivity {

    public static final String TAG = "RegisterSelfActivity";

    ImageView back;
    Button btn_save_details;
    EditText et_name, et_phone;

    DatabaseHelperSelf myDb;
    Boolean data_found;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_yourself);

        //changing statusbar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.pink));
            window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        viewSelfData();
        btn_save_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });


    }

    private void init(){
        back = (ImageView) findViewById(R.id.back);

        btn_save_details = (Button) findViewById(R.id.btn_save);
        et_name = (EditText) findViewById(R.id.et_name);

        et_phone = (EditText) findViewById(R.id.et_phone);

        myDb = new DatabaseHelperSelf(getApplicationContext());
    }

    private void saveData(){

        String name = et_name.getText().toString().trim();
        String phone =et_phone.getText().toString().trim();

        if(phone.isEmpty() || name.isEmpty() ){
            showMessage("Error","Kindly fill all details");
        }else{
            if(!data_found){
                boolean isInserted= myDb.insertData(name,phone);

                if(isInserted){
                    showMessage("Success","You have registered yourself successfully");

                }else{
                    Log.i(TAG,"Unable to register");

                }
            }else{
                boolean isUpdated= myDb.updateData(name,phone);

                if(isUpdated){
                    showMessage("Success","You have updated your data successfully");
                }else{
                    Log.i(TAG,"Unable to update data");

                }
            }
        }
    }


    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(RegisterYourself.this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void viewSelfData() {

        Log.i(TAG,"viewSelfData running");


        StringBuffer buffer=new StringBuffer();
        if(myDb.getAllData()!=null) {

            Cursor res = myDb.getAllData();
            if (res.getCount() == 0) {
                Log.i(TAG, "There is no stored data");
                data_found = false;

            }
            else {
                data_found = true;
                while (res.moveToNext()) {


                    buffer.append("ID: " + res.getString(0) + "\n");
                    buffer.append("name: " + res.getString(1) + "\n");
                    buffer.append("phone: " + res.getString(2) + "\n\n");

                    et_name.setText(res.getString(1));
                    et_phone.setText(res.getString(2));

                }
                Log.i(TAG,"\n"+buffer.toString());

            }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
