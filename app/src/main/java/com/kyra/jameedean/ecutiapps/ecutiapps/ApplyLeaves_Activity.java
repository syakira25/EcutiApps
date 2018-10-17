package com.kyra.jameedean.ecutiapps.ecutiapps;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kyra.jameedean.ecutiapps.ecutiapps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyra.jameedean.ecutiapps.ecutiapps.data.Reference;
import com.kyra.jameedean.ecutiapps.ecutiapps.model.ApplyLeaves_Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ApplyLeaves_Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private TextView mTVemail;
    private TextView mTVname;
    private TextView mTVAL, mTVtotal;
    private TextView mItemSelected;
    private EditText mTVreasons;
    private Spinner mTypeButton;
    int year, year2;
    int month, month2;
    int day, day2;

    Date startDate;
    Date endDate;

    // Firebase Authentication
    private String mId;
    private DatabaseReference mReference, mReference1, mReference2, mReference3;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;

    protected  static TextView displayCurrentTime;
    protected  static TextView displayCurrentTime2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_apply);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        //Get Firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();

        mTVname = (TextView)findViewById(R.id.name_field);
        mTVemail = (TextView)findViewById(R.id.displayed_email);
        mTVAL = (TextView) findViewById(R.id.displayed_totalLeves);
        mTVreasons = (EditText)findViewById(R.id.et_description);
        mItemSelected = (TextView) findViewById(R.id.displayed_typesleave);
        mTypeButton = (Spinner) findViewById(R.id.btnOrder);
        mTVtotal =  (TextView) findViewById(R.id.displayed_total);
        displayCurrentTime = (TextView)findViewById(R.id.selected_time);
        displayCurrentTime2 = (TextView)findViewById(R.id.selected_time2);
        ImageButton displayTimeButton = (ImageButton)findViewById(R.id.select_time);
        ImageButton displayTimeButton2 = (ImageButton)findViewById(R.id.select_time2);

        // Spinner click listener
        mTypeButton.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Annual Leaves");
        categories.add("Medical Leaves");
        categories.add("Emergency Leaves");
        categories.add("Unpaid Leaves");

        // Creating adapter for spinner
        mTypeButton.setPrompt("Select one leaves");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mTypeButton.setAdapter(dataAdapter);

        assert  displayTimeButton != null;
        displayTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragement mDatePicker = new DatePickerFragement();
                mDatePicker.show(getFragmentManager()," ");
            }
        });

        assert  displayTimeButton2 != null;
        displayTimeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragement2 mDatePicker = new DatePickerFragement2();
                mDatePicker.show(getFragmentManager()," ");
            }
        });

        mReference = FirebaseDatabase.getInstance().getReference(Reference.USER_DB);
        mReference1 = mReference.child(Reference.USER_INFO).child(mCurrentUser.getUid());
        mReference2 = FirebaseDatabase.getInstance().getReference(mCurrentUser.getUid()).child(Reference.LEAVES_RECORD);
        mReference3 = FirebaseDatabase.getInstance().getReference(Reference.LEAVES_RECORD);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        mItemSelected.setText(item);

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onStart() {
        super.onStart();
        // listening for changes
        mReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               mTVname.setText(dataSnapshot.child("name").getValue().toString());
               mTVemail.setText(dataSnapshot.child("email").getValue().toString());
               mTVAL.setText("\n"+"ANNUAL LEAVES : "+dataSnapshot.child("annual").getValue().toString()+"\n\n"+ "MEDICAL LEAVE : "+dataSnapshot.child("mc").getValue().toString()+"\n\n"+"EMERGENCY LEAVE : "+dataSnapshot.child("el").getValue().toString()+"\n\n"+"UNPAID LEAVES : "+dataSnapshot.child("public_leave").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stop listening
                Toast.makeText(getApplicationContext(),"Network ERROR. Please check your connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragement2 extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c2 = Calendar.getInstance();
            year2 = c2.get(Calendar.YEAR);
            month2 = c2.get(Calendar.MONTH);
            day2 = c2.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year2, month2, day2);
        }

        public void onDateSet (DatePicker view, int year2, int month2, int day2){
            month2++;
            //displayCurrentTime.setText(String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day));
            displayCurrentTime2.setText(String.valueOf(day2)+"-"+String.valueOf(month2)+"-"+String.valueOf(year2));
            SimpleDateFormat dateParser = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            String x =String.valueOf(day2)+"/"+String.valueOf(month2)+"/"+String.valueOf(year2);
            Log.i("End",x);
            try {
                endDate = dateParser.parse(x);
                Log.i("s",String.valueOf(startDate.getTime()));
                Log.i("e",String.valueOf(endDate.getTime()));
                Date dayAfter = new Date(endDate.getTime() + TimeUnit.DAYS.toMillis(1));
                Log.i("e",String.valueOf(dayAfter.getTime()));
                Long y = dayAfter.getTime() -startDate.getTime();

                Long j  =   TimeUnit.DAYS.convert(y, TimeUnit.MILLISECONDS);

                Log.i("Diff",String.valueOf(j));
                mTVtotal.setText(String.valueOf(j));
            }catch (Exception exception){
                Log.e("Sistem","tarikh = "+startDate);
            }
        }
    }
    @SuppressLint("ValidFragment")
    public class DatePickerFragement extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        public void onDateSet (DatePicker view, int year, int month, int day){
            month++;
            displayCurrentTime.setText(String.valueOf(day)+"-"+String.valueOf(month)+"-"+String.valueOf(year));
            //dipsplayCurrentTime2.setText(String.valueOf(year2)+"-"+String.valueOf(month2)+"-"+String.valueOf(day2));
            SimpleDateFormat dateParser = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            String x =String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year);
            Log.i("Start",x);
            try {
                startDate = dateParser.parse(x);
            }catch (Exception exception){
                Log.e("Sistem","tarikh = "+startDate);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_delete);

        if(mId == null) {
            item.setEnabled(false);
            item.setVisible(false);
        } else {
            item.setEnabled(true);
            item.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_staff, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_staff_action_save:
                // What to do when save

                int  x= getDays(startDate,endDate);
                ApplyLeaves_Model model = new ApplyLeaves_Model(
                        mTVname.getText().toString(),
                        mTVemail.getText().toString(),
                        mItemSelected.getText().toString(),
                        displayCurrentTime.getText().toString(),
                        displayCurrentTime2.getText().toString(),
                        mTVreasons.getText().toString(),
                        String.valueOf(x),
                        FirebaseAuth.getInstance().getUid()
                );

                save(model, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        actionNotification(databaseError, R.string.done_saved);
                    }
                });
                break;
            case R.id.action_delete:
                if(!mId.isEmpty()) {
                    mReference1.child(mId).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            actionNotification(databaseError, R.string.done_deleted2);
                        }
                    });
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private static int getDays(Date startDate, Date endDate) {
        Calendar startCal;
        Calendar endCal;
        startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        ArrayList<String> holidays = new ArrayList<>();
        // contoh hari sultan kedah.. 22/10/2018 .. jangan gatal kerja

        holidays.add("2018-10-22");


        int workDays = 0;

    /*If start date is coming after end date, Then shuffling Dates and storing dates
by incrementing upto end date in do-while part.*/
        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }

        do {
            String dateYmd = (startCal.get(Calendar.YEAR)+"-"+AppendZero(String.valueOf(startCal.get(Calendar.MONTH)+1))+"-"+AppendZero(String.valueOf(startCal.get(Calendar.DATE))));

            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                    startCal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY &&
                    !holidays.contains(dateYmd)
                    ) {
                ++workDays;
                System.out.println("date "+dateYmd);
            }
            startCal.add(Calendar.DAY_OF_MONTH, 1);
        } while (startCal.getTimeInMillis() <= endCal.getTimeInMillis());

        return workDays;
    }
    private  static String AppendZero(String  string ){
        if(string.length()==1){
            string  = "0"+string;
        }
        return string;
    }
    /***
     * Save record to firebase
     * @param model
     */
    private void save(ApplyLeaves_Model model,
                      DatabaseReference.CompletionListener listener) {

        if(mId == null) {
//             generate id
            mId = mReference2.push().getKey();
        }

        mReference3.child(mId).setValue(model,listener);
        mReference2.child(mId).setValue(model, listener);
    }

    private void actionNotification(DatabaseError error, int successResourceId) {
        // close activity
        if(error == null) {
            Toast.makeText(ApplyLeaves_Activity.this, successResourceId, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(ApplyLeaves_Activity.this, error.getCode(), Toast.LENGTH_SHORT).show();
        }
    }
}