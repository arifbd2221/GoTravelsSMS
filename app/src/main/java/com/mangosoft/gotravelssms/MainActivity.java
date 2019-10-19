package com.mangosoft.gotravelssms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mangosoft.gotravelssms.adapter.ContactList;
import com.mangosoft.gotravelssms.database.DatabaseHelper;
import com.mangosoft.gotravelssms.model.Person;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    EditText message;
    Button send;

    FloatingActionButton invokeContacts;

    RecyclerView contactList;
    ContactList contactListAdapter;

    ProgressBar progressBar;

    List<Person> persons = new ArrayList<>();

    SmsManager sms = SmsManager.getDefault();

    DatabaseHelper databaseHelper;

    private static final int CONTACT_PICKER_REQUEST = 991;
    private ArrayList<ContactResult> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        initviews();

    }


    private void initviews(){

        message = findViewById(R.id.msg);
        send = findViewById(R.id.send);
        invokeContacts = findViewById(R.id.contactinvoker);
        contactList = findViewById(R.id.list);

        progressBar = findViewById(R.id.progress);

        progressBar.setVisibility(View.INVISIBLE);


        message.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.msg) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String messageString = message.getText().toString();

                if (!messageString.isEmpty()){


                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                    builder.setTitle("Are you sure to send all?");
                    builder.setMessage("This process can not be undone.If you are sure then tap on confirm");

                    String positiveText = "Confirm";
                    builder.setPositiveButton(positiveText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // dismiss alert dialog, update preferences with game score and restart play fragment

                                    ArrayList<String> parts = sms.divideMessage(messageString);

                                    for (Person person : persons){
                                        String number = person.getPhone();
                                        String name = person.getName();
                                        sms.sendMultipartTextMessage(number, null, parts, null, null);
                                        databaseHelper.insertSMSContact(name,number);
                                    }

                                    Toast.makeText(MainActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();

                                    Log.d("myTag", "positive button clicked");
                                    dialog.dismiss();
                                }
                            });

                    String negativeText = "Cancel";
                    builder.setNegativeButton(negativeText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // dismiss dialog, start counter again
                                    dialog.dismiss();
                                    Log.d("myTag", "negative button clicked");
                                }
                            });



                }

                else {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    message.setText("Write something!");
                }

            }
        });


        invokeContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getContactList();

                //AsyncTaskActivity asyncTaskActivity = new AsyncTaskActivity();
                //asyncTaskActivity.execute("00");


                if (checkAndRequestPermissions(MainActivity.this)){

                    new MultiContactPicker.Builder(MainActivity.this) //Activity/fragment context
                            .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                            .hideScrollbar(false) //Optional - default: false
                            .showTrack(true) //Optional - default: true
                            .searchIconColor(Color.WHITE) //Optional - default: White
                            .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                            .handleColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                            .bubbleColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                            .bubbleTextColor(Color.WHITE) //Optional - default: White
                            .setTitleText("Select Contacts") //Optional - only use if required
                            .setSelectedContacts(results) //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
                            .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                            .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                            .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out) //Optional - default: No animation overrides
                            .showPickerForResult(CONTACT_PICKER_REQUEST);


                    }

                }


        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_PICKER_REQUEST){
            if(resultCode == RESULT_OK) {
                results = MultiContactPicker.obtainResult(data);

                Log.d("MyTag",""+results.size());

                for (ContactResult var : results){
                    try{
                        Log.d("MyTag","Phone: "+ var.getPhoneNumbers().get(0).getNumber());
                    }catch (IndexOutOfBoundsException ie){
                        continue;
                    }

                }

                AsyncTaskActivity asyncTaskActivity = new AsyncTaskActivity();
                asyncTaskActivity.execute("00");

            } else if(resultCode == RESULT_CANCELED){
                Toast.makeText(MainActivity.this, "You closed the contact picker without selecting contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }





    public static boolean checkAndRequestPermissions(final Activity context) {
        int ExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        int internetPermission = ContextCompat.checkSelfPermission(context,Manifest.permission.INTERNET);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (ExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (internetPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.INTERNET);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                } else if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    //doWork();
                }
                break;
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.dashboard) {
            startActivity(new Intent(this,DashboardActivity.class));
            return true;
        }

        if (id == R.id.about){
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AsyncTaskActivity extends AsyncTask<String, String, List<Person>> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Person> doInBackground(String... strings) {

            ArrayList<Person> personlist = new ArrayList<>();

            for (ContactResult person : results){
                try{
                    personlist.add(new Person(person.getDisplayName(),person.getPhoneNumbers().get(0).getNumber()));
                }catch (IndexOutOfBoundsException ie){
                    continue;
                }

            }

            return personlist;
        }

        @Override
        protected void onPostExecute(List<Person> s) {
            progressBar.setVisibility(View.INVISIBLE);
            persons = s;
            contactListAdapter = new ContactList(s,MainActivity.this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            contactList.setLayoutManager(layoutManager);
            contactList.setItemAnimator(new DefaultItemAnimator());
            contactList.setAdapter(contactListAdapter);
        }

    }

}




