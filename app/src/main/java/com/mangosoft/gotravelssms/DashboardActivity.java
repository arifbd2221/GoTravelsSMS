package com.mangosoft.gotravelssms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

import com.mangosoft.gotravelssms.adapter.ContactList;
import com.mangosoft.gotravelssms.database.DatabaseHelper;
import com.mangosoft.gotravelssms.model.Person;


import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    CalendarView calendarView;
    RecyclerView smsHistory;

    List<Person> personList = new ArrayList<>();
    DatabaseHelper databaseHelper;

    ContactList contactListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        databaseHelper = new DatabaseHelper(this);



        calendarView = findViewById(R.id.date);
        smsHistory = findViewById(R.id.historylist);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                Log.d("onSelectedDayChange", ""+calendarView.getDate());

                String m = "-0";
                String d = "-0";

                if (month<10){
                    m = m+month;
                }
                else {
                    m = ""+month;
                }

                if (dayOfMonth<10){
                    d = d+dayOfMonth;
                }
                else {
                    d = ""+dayOfMonth;
                }

                String date = year+m+d;
                Log.d("personList",""+date);

                personList = databaseHelper.getAllSMSContacts(date);

                contactListAdapter = new ContactList(personList,DashboardActivity.this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                smsHistory.setLayoutManager(layoutManager);
                smsHistory.setItemAnimator(new DefaultItemAnimator());
                smsHistory.setAdapter(contactListAdapter);

                Log.d("personList",""+personList.size());
            }
        });

    }
}
