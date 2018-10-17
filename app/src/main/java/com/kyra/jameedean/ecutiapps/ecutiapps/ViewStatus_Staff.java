package com.kyra.jameedean.ecutiapps.ecutiapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.kyra.jameedean.ecutiapps.ecutiapps.model.Approve;

public class ViewStatus_Staff extends AppCompatActivity {

    private TextView mTVemail;
    private TextView mTVname;
    private TextView mTVAL, mTVtotal;
    private TextView mItemSelected, mTVstatus;
    private TextView mTVreasons;

    // Firebase Authentication
    private String mId;
    private DatabaseReference mReference, mReference1, mReference2;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;

    protected static TextView displayCurrentTime;
    protected static TextView displayCurrentTime2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewstatus_staff);
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
        mTVAL = (TextView) findViewById(R.id.displayed_totalLeves);
        mTVreasons = (TextView) findViewById(R.id.et_description);
        mItemSelected = (TextView) findViewById(R.id.displayed_typesleave);
        mTVtotal = (TextView) findViewById(R.id.displayed_total);
        mTVstatus = (TextView) findViewById(R.id.displayed_status);
        displayCurrentTime = (TextView) findViewById(R.id.selected_time);
        displayCurrentTime2 = (TextView) findViewById(R.id.selected_time2);

        mReference = FirebaseDatabase.getInstance().getReference(Reference.USER_DB);
        mReference1 = mReference.child(Reference.USER_INFO);
        mReference2 = FirebaseDatabase.getInstance().getReference(Reference.LEAVES_RECORD);

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
                            mTVreasons.setText(model.getMessage());
                            mTVstatus.setText(model.getStatus());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        // Load record
        mReference1.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Approve model = dataSnapshot.getValue(Approve.class);
                if (model != null) {
                    mTVAL.setText("\n" + "ANNUAL LEAVES : " + dataSnapshot.child("annual").getValue().toString() + "\n\n" + "MEDICAL LEAVE : " + dataSnapshot.child("mc").getValue().toString() + "\n\n" + "EMERGENCY LEAVE : " + dataSnapshot.child("el").getValue().toString() + "\n\n" + "PUBLIC HOLIDAY : " + dataSnapshot.child("public_leave").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

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
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void actionNotification(DatabaseError error, int successResourceId) {
        // close activity
        if (error == null) {
            Toast.makeText(ViewStatus_Staff.this, successResourceId, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(ViewStatus_Staff.this, error.getCode(), Toast.LENGTH_SHORT).show();
        }
    }

}

