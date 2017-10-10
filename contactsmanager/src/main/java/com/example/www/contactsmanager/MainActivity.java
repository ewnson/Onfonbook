package com.example.www.contactsmanager;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    final String LOG = "MainActivity";
    private DatabaseReference mDatabase;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> vCard;
    String vfile;
    public static final int RequestPermissionCode = 1;
    Button button;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    ConstraintLayout homelayout;
    private String phoneNumber = null;
    private String email = null;

    public Context context;
    public int i;
    Button loadBtn;
    private ListView mListView;
    private ProgressDialog pDialog;
    private Handler updateBarHandler;
    ArrayList<String> contactList;
    Cursor cursor;
    int counter;
    private DatabaseReference contactEndPoint;
    private DatabaseReference tagCloudEndPoint;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private long dateModified;

    private ArrayList<Contacts> contactsList;
    private TextView textview_android_contact_name;
    private TextView textview_android_contact_number;
    private TextView textview_android_contact_email;

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.example.www.contactsmanager.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.example.www.contactsmanager";


    // Content provider scheme
    public static final String SCHEME = "content://";
    // Content provider authority
    public static final String TABLE_PATH = "contacts/databases";
    // Account
    public static final String ACCOUNT = "mAccount";
    // Global variables
    // A content URI for the content provider's data table
    Uri mUri;
    // A content resolver for accessing the provider
    ContentResolver mResolver;
    Account mAccount;

    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    public class TableObserver extends ContentObserver {
        /*
         * Define a method that's called when data in the
         * observed content provider changes.
         * This method signature is provided for compatibility with
         * older platforms.
         */
        public TableObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            /*
             * Invoke the method signature available as of
             * Android platform version 4.1, with a null URI.
             */
            onChange(selfChange, null);
        }
        /*
         * Define a method that's called when data in the
         * observed content provider changes.
         */
        @Override
        public void onChange(boolean selfChange, Uri changeUri) {
            /*
             * Ask the framework to run your sync adapter.
             * To maintain backward compatibility, assume that
             * changeUri is null.
             */
            if (mAccount != null) {
                ContentResolver.requestSync(mAccount, AUTHORITY, null);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mAccount = CreateSyncAccount(this);

        // Get the content resolver object for your app
        mResolver = getContentResolver();
        // Construct a URI that points to the content provider data table
        mUri = new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .path(TABLE_PATH)
                .build();
        /*
         * Create a content observer object.
         * Its code does not mutate the provider, so set
         * selfChange to "false"
         */
        TableObserver observer = new TableObserver(null);
        /*
         * Register the observer for the data table. The table's path
         * and any of its subpaths trigger the observer.
         */
        mResolver.registerContentObserver(mUri, true, observer);

        homelayout = (ConstraintLayout) findViewById(R.id.homelayout);

        listView = (ListView) findViewById(R.id.listview1);

        button = (Button) findViewById(R.id.pbReadBtn);
        final Button vcfButton = (Button) findViewById(R.id.vcfButton);
        final Button uploadbutton = (Button) findViewById(R.id.uploadButton);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton fabSearch = (FloatingActionButton) findViewById(R.id.fabSearch);
        TextView textview_android_contact_name = (TextView) findViewById(R.id.textview_android_contact_name);
        TextView textview_android_contact_number = (TextView) findViewById(R.id.textview_android_contact_number);
        TextView textview_android_contact_email = (TextView) findViewById(R.id.textview_android_contact_email);

        contactList = new ArrayList<>();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String Token = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "New token: " + Token);
        contactEndPoint = mDatabase.child("contacts");
        tagCloudEndPoint = mDatabase.child("tags");

        EnableRuntimePermission();

        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener,
                PhoneStateListener.LISTEN_CALL_STATE);


        mListView = (ListView) findViewById(R.id.listview1);
        loadBtn = (Button) findViewById(R.id.pbReadBtn);
        updateBarHandler = new Handler();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        // Since reading contacts takes more time, let's run it on a separate thread.
        loadBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*button.setVisibility(View.GONE);*/
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Reading contacts...");
                pDialog.setCancelable(false);
                pDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getContacts();
                    }
                }).

                        start();
                Toast.makeText(MainActivity.this, "Phonebook CONTACTS loaded", Toast.LENGTH_LONG).show();
            }
        });

        // Set onclicklistener to the list_item item.
        /*mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO Do whatever you want with the list_item data
                Toast.makeText(getApplicationContext(), "item clicked : \n" + contactList.get(position), Toast.LENGTH_SHORT).show();
            }
        });*/


        vcfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    /*vcfButton.setVisibility(View.GONE);*/
                    vfile = "Contacts" + "_" + System.currentTimeMillis() + ".vcf";
                    try {
                        getVcardString();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "CONTACTS are now saved as a VCf file in your SD card", Toast.LENGTH_LONG).show();

            }
        });
        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Reading CONTACTS from Database", Toast.LENGTH_LONG).show();
                String read = "http://192.168.3.38/contacts/contact.php";
                mListView = (ListView) findViewById(R.id.listview1);

                PostResponseAsyncTask readContacts = new PostResponseAsyncTask(MainActivity.this, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                contactsList = new JsonConverter<Contacts>().toArrayList(s, Contacts.class);

                BindDictionary<Contacts> dict = new BindDictionary<Contacts>();
                dict.addStringField(R.id.textview_android_contact_name, new StringExtractor<Contacts>() {
                    @Override
                    public String getStringValue(Contacts contact, int position) {
                        return contact.name;
                    }
                });

                dict.addStringField(R.id.textview_android_contact_number, new StringExtractor<Contacts>() {
                    @Override
                    public String getStringValue(Contacts contact, int position) {
                        return contact.pnumber;
                    }
                });

                dict.addStringField(R.id.textview_android_contact_email, new StringExtractor<Contacts>() {
                    @Override
                    public String getStringValue(Contacts contact, int position) {
                        return contact.email;
                    }
                });
                        FunDapter<Contacts> adapter = new FunDapter<>(MainActivity.this, contactsList, R.layout.contactlist_android_items, dict);
                        mListView.setAdapter(adapter);
                    }

                });
                readContacts.execute(read);
                readContacts.setEachExceptionsHandler(new EachExceptionsHandler() {
                    @Override
                    public void handleIOException(IOException e) {
                        Toast.makeText(MainActivity.this, "Error with internet or web server.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void handleMalformedURLException(MalformedURLException e) {
                        Toast.makeText(MainActivity.this, "Error with the URL.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void handleProtocolException(ProtocolException e) {
                        Toast.makeText(MainActivity.this, "Error with protocol.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                        Toast.makeText(MainActivity.this, "Error with text encoding.", Toast.LENGTH_LONG).show();
                    }
                });

                contactEndPoint.setValue(contactsList);
                Toast.makeText(MainActivity.this, "CONTACTS are now synced with the server", Toast.LENGTH_LONG).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, InsertActivity.class));
        }
        });

        fabSearch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, SearchContactsActivity.class));
            }
        });

        FragmentManager fm = getFragmentManager();
        LoginFragment dialogFragment = new LoginFragment();
        dialogFragment.show(fm, "Login");
        dialogFragment.setCancelable(false);

    }

    @Override
    public void processFinish(String s) {
        contactsList = new JsonConverter<Contacts>().toArrayList(s, Contacts.class);

        BindDictionary<Contacts> dict = new BindDictionary<Contacts>();
        dict.addStringField(R.id.textview_android_contact_name, new StringExtractor<Contacts>() {
            @Override
            public String getStringValue(Contacts contact, int position) {
                return contact.name;
            }
        });

        dict.addStringField(R.id.textview_android_contact_number, new StringExtractor<Contacts>() {
            @Override
            public String getStringValue(Contacts contact, int position) {
                return contact.pnumber;
            }
        });

        dict.addStringField(R.id.textview_android_contact_email, new StringExtractor<Contacts>() {
            @Override
            public String getStringValue(Contacts contact, int position) {
                return contact.email;
            }
        });

        FunDapter<Contacts> adapter = new FunDapter<>(MainActivity.this, contactsList, R.layout.contactlist_android_items, dict);
        listView.setAdapter(adapter);

        }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended, need detect flag
                // from CALL_STATE_OFFHOOK
                Log.i(TAG, "IDLE number");

                if (isPhoneCalling) {

                    Handler handler = new Handler();

                    //Put in delay because call log is not updated immediately when state changed
                    // The dialler takes a little bit of time to write to it 500ms seems to be enough
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // get start of cursor
                            Log.i("CallLogDetailsActivity", "Getting Log activity...");
                            String[] projection = new String[]{CallLog.Calls.NUMBER};
                            try {
                                Cursor cur = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DATE + " desc");
                            cur.moveToFirst();
                            String lastCallnumber = cur.getString(0);
                            } catch (SecurityException ex){
                                //Handle
                            }
                        }
                    },500);

                    isPhoneCalling = false;
                }

            }
        }
    }

    public void getContacts() {
        contactList = new ArrayList<String>();
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        StringBuffer output;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {
            counter = 0;
            while (cursor.moveToNext()) {
                output = new StringBuffer();
                // Update the progress message
                updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("Reading contacts : " + counter++ + "/" + cursor.getCount());
                    }
                });
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                final String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    output.append("\n Name:" + name);
                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    if (phoneCursor.moveToNext()){
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(DATA));
                        output.append("\n Phone number:" + phoneNumber);
                    } else {
                        phoneNumber = "";
                        output.append("\n Number:" + phoneNumber);
                    }
                    phoneCursor.close();
                    // Read every email id associated with the contact
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                    if (emailCursor.moveToNext()){
                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                            output.append("\n Email:" + email);
                    } else {
                        email = "";
                        output.append("\n Email:" + email);
                    }
                    emailCursor.close();
                }
                // Add the contact to the ArrayList
                contactList.add(output.toString());

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.3.242/contacts/insert.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(MainActivity.this, "Upload Complete"+response, Toast.LENGTH_SHORT).show();
                        Log.i("My success",""+response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(MainActivity.this, "Error Uploading :"+error, Toast.LENGTH_LONG).show();
                        Log.i("My error",""+error);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String,String> map = new HashMap<String, String>();

                        map.put("txtName", name);
                        map.put("txtPnumber", phoneNumber);
                        map.put("txtEmail", email);

                        return map;
                    }
                };
                queue.add(request);

            }
            // ListView has to be updated using a ui thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.contactlist_android_items, R.id.textview_android_contact_name, contactList);
                    mListView.setAdapter(adapter);
                }
            });
            // Dismiss the progressbar after 500 millisecondds
            updateBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.cancel();
                }
            }, 500);
        }
    }

    /*public String makeJSON() {
        Gson gson = new GsonBuilder().create();
        try (FileWriter file = new FileWriter(Environment.getExternalStorageDirectory() + "/" + "contacts.json")) {
            gson.toJson(contactList, file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Environment.getExternalStorageDirectory() + "/" + "contacts.json";
    }*/



    public class Tag {
        private String tagId;
        private String tagName;
        private int contactCount;
    }


    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this,
                Manifest.permission.READ_CONTACTS)) {

            Toast.makeText(MainActivity.this, "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MainActivity.this, "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    private void getVcardString() throws IOException {
        // TODO Auto-generated method stub
        vCard = new ArrayList<String>();  // Its global....
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            int i;
            String storage_path = Environment.getExternalStorageDirectory().toString() + File.separator + vfile;
            FileOutputStream mFileOutputStream = new FileOutputStream(storage_path, false);
            cursor.moveToFirst();
            for (i = 0; i < cursor.getCount(); i++) {
                get(cursor);
                Log.d("TAG", "Contact " + (i + 1) + "VcF String is" + vCard.get(i));
                cursor.moveToNext();
                mFileOutputStream.write(vCard.get(i).toString().getBytes());
            }
            mFileOutputStream.close();
            cursor.close();
        } else {
            Log.d("TAG", "No Contacts in Your Device");
        }
    }

    public void get(Cursor cursor) {
        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
        AssetFileDescriptor fd;
        try {
            fd = this.getContentResolver().openAssetFileDescriptor(uri, "r");

            FileInputStream fis = fd.createInputStream();
            byte[] buf = new byte[(int) fd.getDeclaredLength()];
            fis.read(buf);
            String vcardstring = new String(buf);
            vCard.add(vcardstring);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}