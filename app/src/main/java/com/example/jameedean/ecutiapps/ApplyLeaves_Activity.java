package com.example.jameedean.ecutiapps;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

public class ApplyLeaves_Activity extends AppCompatActivity {

    private TextView email;
    private TextView name;
    private TextView userId;

    // Firebase Authentication
    private String mId2;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mReference;

    protected  static TextView displayCurrentTime;
    protected  static TextView displayCurrentTime2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyleaves);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(user);
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ApplyLeaves_Activity.this, MainActivity.class));
                    finish();
                }
            }
        };
        displayCurrentTime = (TextView)findViewById(R.id.selected_time);
        displayCurrentTime2 = (TextView)findViewById(R.id.selected_time2);
        ImageButton displayTimeButton = (ImageButton)findViewById(R.id.select_time);
        ImageButton displayTimeButton2 = (ImageButton)findViewById(R.id.select_time2);

        assert  displayTimeButton != null;

        displayTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragement mDatePicker = new DatePickerFragement();
                mDatePicker.show(getFragmentManager()," ");
            }
        });

        displayTimeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragement2 mDatePicker = new DatePickerFragement2();
                mDatePicker.show(getFragmentManager()," ");
            }
        });

    }

    public static class DatePickerFragement2 extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c2 = Calendar.getInstance();
            int year2 = c2.get(Calendar.YEAR);
            int month2 = c2.get(Calendar.MONTH);
            int day2 = c2.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year2, month2, day2);
        }

        public void onDateSet (DatePicker view, int year2, int month2, int day2){
            //displayCurrentTime.setText(String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day));
            displayCurrentTime2.setText(String.valueOf(year2)+"-"+String.valueOf(month2)+"-"+String.valueOf(day2));
        }
    }

    public static class DatePickerFragement extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet (DatePicker view, int year, int month, int day){
            displayCurrentTime.setText(String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day));
            //displayCurrentTime2.setText(String.valueOf(year2)+"-"+String.valueOf(month2)+"-"+String.valueOf(day2));
        }
    }

    @SuppressLint("SetTextI18n")
    private void setDataToView(FirebaseUser user) {

    }

    // this listener will be called when there is change in firebase user session
    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(ApplyLeaves_Activity.this, MainActivity.class));
                finish();
            } else {
                setDataToView(user);
            }
        }


    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_delete);

        if(mId2 == null) {
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
            case R.id.action_save:

                break;
            case R.id.action_delete:
                if(!mId2.isEmpty()) {
                    mReference.child(mId2).removeValue(new DatabaseReference.CompletionListener() {
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