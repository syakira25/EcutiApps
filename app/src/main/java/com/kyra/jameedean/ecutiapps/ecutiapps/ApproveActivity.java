package com.kyra.jameedean.ecutiapps.ecutiapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.kyra.jameedean.ecutiapps.ecutiapps.model.Approve;
import com.kyra.jameedean.ecutiapps.ecutiapps.model.Staff;

import java.util.ArrayList;
import java.util.List;

public class ApproveActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView mTVemail;
    private TextView mTVname;
    private TextView mTVtotal;
    private TextView mItemSelected, mTVstatus;
    private TextView mTVreasons;
    private Spinner status;

    // Firebase Authentication
    private String mId;
    /**
     * user info
     */
    private DatabaseReference mReference1;
    /**
     * leave
     */
    private DatabaseReference mReference2;
    private DatabaseReference mReference3;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;

    protected static TextView displayCurrentTime;
    protected static TextView displayCurrentTime2;

    private String UID;

    //emergency
    int el = 0;
    // anuall leave
    int al = 0;
    // medical
    int medical = 0;
    // public  holiday
    int pb = 0;

    String leaveType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_approve);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Get Firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();

        mTVname = (TextView) findViewById(R.id.name_field);
        mTVemail = (TextView) findViewById(R.id.displayed_email);
        mTVreasons = (TextView) findViewById(R.id.et_description);
        mItemSelected = (TextView) findViewById(R.id.displayed_typesleave);
        status = (Spinner) findViewById(R.id.btnStatus);
        mTVtotal = (TextView) findViewById(R.id.displayed_total);
        mTVstatus = (TextView) findViewById(R.id.displayed_status);
        displayCurrentTime = (TextView) findViewById(R.id.selected_time);
        displayCurrentTime2 = (TextView) findViewById(R.id.selected_time2);

        // Spinner click listener
        status.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Approve");
        categories.add("Rejected");
        categories.add("Processing");
        categories.add("Under Consideration");

        // Creating adapter for spinner
        status.setPrompt("Status Leaves Staff");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        status.setAdapter(dataAdapter);

        mReference1 = FirebaseDatabase.getInstance().getReference(Reference.USER_DB + "/" + Reference.USER_INFO);
        mReference2 = FirebaseDatabase.getInstance().getReference(Reference.LEAVES_RECORD);
        mReference3 = FirebaseDatabase.getInstance().getReference(mCurrentUser.getUid()).child(Reference.LEAVES_RECORD);

        Intent intent = getIntent();
        // Load record
        if (intent != null) {
            mId = intent.getStringExtra(Reference.LEAVES_ID);
            if (mId != null) {
                mReference2.child(mId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Approve model = dataSnapshot.getValue(Approve.class);
                        if (model != null) {
                            mTVname.setText(model.getName());
                            mTVemail.setText(model.getEmail());
                            mItemSelected.setText(model.getTypes_leave());
                            displayCurrentTime.setText(model.getDate_start());
                            displayCurrentTime2.setText(model.getDate_end());
                            mTVtotal.setText(model.getTotal());
                            mTVstatus.setText(model.getStatus());
//                            mTVtotal.setText(dataSnapshot.child("total").getValue().toString());
                            mTVreasons.setText(model.getMessage());
                            UID = model.getUid();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

//    @Override
//  protected void onStart() {
//      super.onStart();
//         listening for changes
//      mReference2.addValueEventListener(new ValueEventListener() {
//          @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                mTVname.setText(dataSnapshot.child("name").getValue().toString());
//                mTVemail.setText(dataSnapshot.child("email").getValue().toString());
//                mItemSelected.setText(dataSnapshot.child("type_leave").getValue().toString());
//                displayCurrentTime.setText(dataSnapshot.child("date_start").getValue().toString());
//                displayCurrentTime2.setText(dataSnapshot.child("date_end").getValue().toString());
//                mTVreasons.setText(dataSnapshot.child("message").getValue().toString());
//                mTVstatus.setText(dataSnapshot.child("status").getValue().toString());
//        }
//        @Override
//            public void onCancelled(DatabaseError databaseError) {
//                stop listening
//                Toast.makeText(getApplicationContext(),"Network ERROR. Please check your connection", Toast.LENGTH_LONG).show();
//           }
//});
//    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_delete);

        if (mId == null) {
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
//        Log.e("harimau",item.toString());
//        if(item.toString().equals("Save")){
//            Log.e("ada","masuk");
//            Log.e("ada",mTVstatus.getText().toString());

//            Approve model = new Approve(
//                    mTVname.getText().toString(),
//                    mTVemail.getText().toString(),
//                    mItemSelected.getText().toString(),
//                    displayCurrentTime.getText().toString(),
//                    displayCurrentTime2.getText().toString(),
//                    mTVreasons.getText().toString(),
//                    mTVtotal.getText().toString(),
//                    mTVstatus.getText().toString()
//            );
//            Log.e("mVStatus",model.getStatus());
//
//            save(model, new DatabaseReference.CompletionListener() {
//                @Override
//                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                    actionNotification(databaseError, R.string.done_saved);
//                }
//            });
//        }
        switch (item.getItemId()) {
            case R.id.menu_staff_action_save:
                // What to do when save
                Approve model = new Approve(
                        mTVname.getText().toString(),
                        mTVemail.getText().toString(),
                        mItemSelected.getText().toString(),
                        displayCurrentTime.getText().toString(),
                        displayCurrentTime2.getText().toString(),
                        mTVreasons.getText().toString(),
                        mTVtotal.getText().toString(),
                        mTVstatus.getText().toString(),
                        UID
                );
                Log.e("mVStatus", model.getStatus());

                save(model, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        actionNotification(databaseError, R.string.done_saved2);
                    }
                });
                break;
            case R.id.action_delete:
                if (!mId.isEmpty()) {
                    mReference2.child(mId).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            actionNotification(databaseError, R.string.done_deleted2);
                        }
                    });
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        //return false;
        return super.onOptionsItemSelected(item);
    }

    /***
     * Save record to firebase
     * @param model
     */
    private void save(Approve model,
                      DatabaseReference.CompletionListener listener) {
        final Approve fn = model;

        if (mId == null) {
            // generate id
            mId = mReference2.push().getKey();
            mId = mReference3.push().getKey();
            //   Intent intent = new Intent();
            //mId = intent.getStringExtra(Reference.LEAVES_ID);

        }
        if (fn.getStatus().equals("Rejected")) {

        } else {
            Log.e("uid", UID);

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Staff modelStaff = dataSnapshot.getValue(Staff.class);
                    Log.e("snapshot", dataSnapshot.toString());
                    //emergency
                    el = Integer.valueOf(modelStaff.getEl());
                    // anuall leave
                    al = Integer.valueOf(modelStaff.getAnnual());
                    // medical
                    medical = Integer.valueOf(modelStaff.getMc());
                    // public  holiday
                    pb = Integer.valueOf(modelStaff.getPublic_leave());
                    leaveType =  fn.getTypes_leave();

                    switch (leaveType) {
                        case "Annual Leaves":
                            Log.e("bal", String.valueOf(al));

                            String alx = String.valueOf(al - Integer.valueOf(fn.getTotal()));
                            Log.e("bfw", String.valueOf(alx));

                            try {
                                FirebaseDatabase fireBaseUpdateYear = FirebaseDatabase.getInstance();
                                DatabaseReference fireBaseYear = fireBaseUpdateYear.getReference(Reference.USER_DB + "/" + Reference.USER_INFO + "/" + UID + "/annual");
                                //DatabaseReference fireBaseYear2 = fireBaseUpdateYear.getReference(mCurrentUser.getUid() + "/" + Reference.LEAVES_RECORD + "/" + "/annual");
                                Log.e("al", (Reference.USER_DB + "/" + Reference.USER_INFO + "/" + UID + "/annual"));
                                fireBaseYear.setValue(alx,new DatabaseReference.CompletionListener(){
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                                        if(databaseError != null){
                                            Log.e("dbe",databaseError.getMessage());
                                        }else{
                                            Log.e("db","boleh save");
                                        }
                                    }

                                });
                                /*fireBaseYear2.setValue(alx,new DatabaseReference.CompletionListener(){
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                                        if(databaseError != null){
                                            Log.e("dbe",databaseError.getMessage());
                                        }else{
                                            Log.e("db","boleh save");
                                        }
                                    }

                                });*/
                            } catch (Exception exception) {
                                Log.e("al", exception.getMessage());
                                Log.e("al", (Reference.USER_DB + "/" + Reference.USER_INFO + "/" + UID + "/annual"));

                            }
                            //fireBaseUpdateYear.goOffline();
                            break;
                        case "Emergency Leaves":
                            Log.e("bal", String.valueOf(el));
                            String elx = (String.valueOf(el - Integer.valueOf(fn.getTotal())));
                            Log.e("bfw", String.valueOf(elx));
                            final FirebaseDatabase fireBaseUpdateLeave = FirebaseDatabase.getInstance();
                            DatabaseReference fireBaseLeave = fireBaseUpdateLeave.getReference(Reference.USER_DB + "/" + Reference.USER_INFO + "/" + UID + "/el");
                            //DatabaseReference fireBaseLeave2 = fireBaseUpdateLeave.getReference(mCurrentUser.getUid() + "/" + Reference.LEAVES_RECORD + "/" + "/el");
                            try {
                                fireBaseLeave.setValue(elx,new DatabaseReference.CompletionListener(){
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                                        if(databaseError != null){
                                            Log.e("dbe",databaseError.getMessage());
                                        }else{
                                            Log.e("db","boleh save");
                                        }
                                    }
                                });
                                /*fireBaseLeave2.setValue(elx,new DatabaseReference.CompletionListener(){
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                                        if(databaseError != null){
                                            Log.e("dbe",databaseError.getMessage());
                                        }else{
                                            Log.e("db","boleh save");
                                        }
                                    }
                                });*/
                            } catch (Exception exception) {
                                Log.e("al", exception.getMessage());
                                Log.e("al", (Reference.USER_DB + "/" + Reference.USER_INFO + "/" + UID + "/el"));

                            }
                            //fireBaseUpdateLeave.goOffline();
                            break;
                        case "Medical Leaves":
                            Log.e("bal", String.valueOf(medical));

                            String mcx = String.valueOf(medical - Integer.valueOf(fn.getTotal()));
                            Log.e("bfw", String.valueOf(mcx));
                            final FirebaseDatabase fireBaseUpdateMedicalLeave = FirebaseDatabase.getInstance();

                            DatabaseReference fireBaseMedicalLeave = fireBaseUpdateMedicalLeave.getReference(Reference.USER_DB + "/" + Reference.USER_INFO + "/" + UID + "/mc");
                            //DatabaseReference fireBaseMedicalLeave2 = fireBaseUpdateMedicalLeave.getReference(mCurrentUser.getUid() + "/" + Reference.LEAVES_RECORD + "/"  + "/mc");

                            try {
                                fireBaseMedicalLeave.setValue(mcx,new DatabaseReference.CompletionListener(){
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                                        if(databaseError != null){
                                            Log.e("dbe",databaseError.getMessage());
                                        }else{
                                            Log.e("db","boleh save");
                                        }
                                    }

                                });
                                /*fireBaseMedicalLeave2.setValue(mcx,new DatabaseReference.CompletionListener(){
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                                        if(databaseError != null){
                                            Log.e("dbe",databaseError.getMessage());
                                        }else{
                                            Log.e("db","boleh save");
                                        }
                                    }

                                });*/
                            } catch (Exception exception) {
                                Log.e("al", exception.getMessage());
                                Log.e("al", (Reference.USER_DB + "/" + Reference.USER_INFO + "/" + UID + "/mc"));

                            }
                            //fireBaseUpdateMedicalLeave.goOffline();
                            break;
                        case "Unpaid Leaves":
                            Log.e("bal", String.valueOf(pb));
                            String pbx = (String.valueOf(pb - Integer.valueOf(fn.getTotal())));
                            Log.e("bfw", String.valueOf(pbx));
                            final FirebaseDatabase fireBaseUpdatePublicHoliday = FirebaseDatabase.getInstance();

                            DatabaseReference fireBasePublicHoliday = fireBaseUpdatePublicHoliday.getReference(Reference.USER_DB + "/" + Reference.USER_INFO + "/" + UID + "/public_leave");
                            //DatabaseReference fireBasePublicHoliday2 = fireBaseUpdatePublicHoliday.getReference(mCurrentUser.getUid() + "/" + Reference.LEAVES_RECORD + "/"  + "/public_leave");
                            try {
                                fireBasePublicHoliday.setValue(pbx,new DatabaseReference.CompletionListener(){
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                                        if(databaseError != null){
                                            Log.e("dbe",databaseError.getMessage());
                                        }else{
                                            Log.e("db","boleh save");
                                        }
                                    }

                                });
                                /*fireBasePublicHoliday2.setValue(pbx,new DatabaseReference.CompletionListener(){
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                                        if(databaseError != null){
                                            Log.e("dbe",databaseError.getMessage());
                                        }else{
                                            Log.e("db","boleh save");
                                        }
                                    }

                                });*/

                            } catch (Exception exception) {
                                Log.e("al", exception.getMessage());
                                Log.e("al", (Reference.USER_DB + "/" + Reference.USER_INFO + "/" + UID + "/public_leave"));
                            }
                            //fireBaseUpdatePublicHoliday.goOffline();
                            break;

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("DB",databaseError.toString());
                }
            };


           // mReference1.child(UID).addValueEventListener(valueEventListener);
            mReference1.child(UID).addListenerForSingleValueEvent(valueEventListener);
            mReference2.child(UID).addListenerForSingleValueEvent(valueEventListener);
          //  mReference1.removeEventListener(valueEventListener);
        }

        mReference2.child(mId).setValue(fn,new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                if(databaseError != null){
                    Log.e("dbe",databaseError.getMessage());
                }else{
                    Log.e("db","boleh save");
                }
            }

        });

        FirebaseDatabase mFirebase = FirebaseDatabase.getInstance();
        DatabaseReference mRefer = mFirebase.getReference(UID + "/" + Reference.LEAVES_RECORD );
        mRefer.child(mId).setValue(model,listener);
        /*mReference3.setValue(fn,new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference){
                if(databaseError != null){
                    Log.e("dbe",databaseError.getMessage());
                }else{
                    Log.e("db","boleh save");
                }
            }

        });*/
    }

    private void actionNotification(DatabaseError error, int successResourceId) {
        // close activity
        if (error == null) {
            Toast.makeText(ApproveActivity.this, successResourceId, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(ApproveActivity.this, error.getCode(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        mTVstatus.setText(item);

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
}