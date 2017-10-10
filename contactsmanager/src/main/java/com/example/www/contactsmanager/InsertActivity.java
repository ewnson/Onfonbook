package com.example.www.contactsmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.HashMap;

public class InsertActivity extends Activity implements View.OnClickListener {
    final String LOG = "InsertActivity";

    private EditText etName, etNumber, etEmail;
    private Button btnInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        etName = (EditText)findViewById(R.id.etName);
        etNumber = (EditText)findViewById(R.id.etNumber);
        etEmail = (EditText)findViewById(R.id.etEmail);
        btnInsert = (Button)findViewById(R.id.btnInsert);

        btnInsert.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        HashMap postData = new HashMap();
        postData.put("txtName", etName.getText().toString());
        postData.put("txtPnumber", etNumber.getText().toString());
        postData.put("txtEmail", etEmail.getText().toString());
        postData.put("mobile", "android");

        PostResponseAsyncTask taskInsert = new PostResponseAsyncTask(InsertActivity.this, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                Log.d(LOG, s);
                if(s.contains("Success")){
                    Toast.makeText(InsertActivity.this, "Inserted Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(InsertActivity.this, MainActivity.class));
                }
            }
        });
        taskInsert.execute("http://192.168.3.38/contacts/insert.php");
    }
}
