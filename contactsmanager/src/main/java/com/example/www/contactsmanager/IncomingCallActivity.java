package com.example.www.contactsmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class IncomingCallActivity extends Activity{

    final String MyLog = "IncomingCallActivity";
    TextView text;

    String data = "";
    private Button dialog_save;
    String number;
    Context context;
    Contacts person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(MyLog, "IncomingCallActivity: onCreate: ");

        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER | Gravity.CENTER;

        setContentView(R.layout.main);

        final String number = getIntent().getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        final TextView text = (TextView) findViewById(R.id.text);
        final Button dialog_ok = (Button) findViewById(R.id.dialog_ok);

        dialog_ok.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                IncomingCallActivity.this.finish();
//                    this.setFinishOnTouchOutside(false);
                System.exit(0);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(IncomingCallActivity.this);

        // Creating the JsonObjectRequest class called obreq, passing required parameters:
        //GET is used to fetch data from the server, JsonURL is the URL to be fetched from.
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://192.168.3.247/contacts/search.php?txtName=&txtPnumber=" + number +"&btnSubmit=Search", null,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter
                new Response.Listener<JSONArray>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i =0; i<response.length() ; i++) {
                                JSONObject info = response.getJSONObject(i);
                                Log.d(MyLog, "ObjectJSON is " + info);
                                String name = info.getString("name");
                                text.setText("Call Details : " + "\n" + name + "\n" + number);
                            }
                        }
                        // Try and catch are included to handle any errors due to JSON
                        catch (JSONException e) {
                            // If an error occurs, this prints the error to the log
                            e.printStackTrace();
                        }
                    }
                },
                // The final parameter overrides the method onErrorResponse() and passes VolleyError
                //as a parameter
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        text.setText("Call from : " + "\n" + "Unknown" + "\n" + number);
                    }
                }
        );

        queue.add(request);

        dialog_save = (Button)findViewById(R.id.dialog_save);

        dialog_save.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

                String given = text.getText().toString();
                String parts [] = given.split("\\r\\n|\\n|\\r");

                String part1 = parts[0];
                String part2 = parts[1];
                String part3 = parts[2];

                Log.d(MyLog, "The Name is " + part2);
                if (part2.trim().equals ("Unknown")) {
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, "");
                } else {
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, part2);
                }
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, part3);

                startActivity(intent);
            }
        });
    }
}
