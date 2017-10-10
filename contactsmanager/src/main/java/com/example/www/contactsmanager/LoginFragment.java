package com.example.www.contactsmanager;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.HashMap;

public class LoginFragment extends DialogFragment implements View.OnClickListener {

    final String LOG = "MainActivity";
    Button btnlogin;
    EditText etUsername, etPassword;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_dialog, container, false);
        getDialog().setTitle("Login");
        getDialog().setCanceledOnTouchOutside(false);

        Button dismiss = (Button) rootView.findViewById(R.id.dismiss);
        etUsername = (EditText) rootView.findViewById(R.id.etUsername);
        etPassword = (EditText) rootView.findViewById(R.id.etPassword);
        btnlogin = (Button) rootView.findViewById(R.id.btnLogin);

        btnlogin.setOnClickListener(this);

        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        HashMap postData = new HashMap();

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        postData.put("txtUsername",username);
        postData.put("txtPassword",password);
        String serverurl = "http://192.168.3.78/contacts/index.php";
        PostResponseAsyncTask logintask = new PostResponseAsyncTask(getActivity(), postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                Log.d(LOG, s);
                if (s.contains("Success!")) {
                    Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_LONG).show();
                    dismiss();
                }
                else{
                    Toast.makeText(getActivity(), "Login Unsuccessful, Try Again", Toast.LENGTH_LONG).show();
                }
            }
        });
        logintask.execute(serverurl);
    }
}
