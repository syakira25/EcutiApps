package com.example.jameedean.ecutiapps;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jameedean.ecutiapps.adapter.StaffAdapter;
import com.example.jameedean.ecutiapps.data.Reference;
import com.example.jameedean.ecutiapps.model.ApplyLeaves_Model;
import com.example.jameedean.ecutiapps.model.Staff;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ApplyLeaves_Activity extends AppCompatActivity {

    private TextView mTVemail;
    private TextView mTVname;

    private StaffAdapter mAdapter;

    private ArrayList<String> mKeys;

    // Firebase Authentication
    private String uId;
    private String mId;
    private DatabaseReference mReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;

    protected  static TextView displayCurrentTime;
    protected  static TextView displayCurrentTime2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyleaves);

        //Get Firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();

        mTVname = (TextView)findViewById(R.id.name_field);
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

        mReference = FirebaseDatabase.getInstance().getReference(Reference.USER_DB);
        uId = mCurrentUser.getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // listening for changes
        mReference.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    // load data
                   Staff staff = noteSnapshot.getValue(Staff.class);
                   mTVname.setText(staff.getName());
                }
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
            case R.id.action_save:
                // What to do when save
                ApplyLeaves_Model model = new ApplyLeaves_Model(
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
                    mReference.child(mId).removeValue(new DatabaseReference.CompletionListener() {
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

    /***
     * Save record to firebase
     * @param model
     */
    private void save(ApplyLeaves_Model model,
                      DatabaseReference.CompletionListener listener) {

        if(mId == null) {
            // generate id
            mId = mReference.push().getKey();
        }

        mReference.child(mId).setValue(model, listener);
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