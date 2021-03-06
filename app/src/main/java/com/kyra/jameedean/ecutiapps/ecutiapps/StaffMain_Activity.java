package com.kyra.jameedean.ecutiapps.ecutiapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kyra.jameedean.ecutiapps.ecutiapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyra.jameedean.ecutiapps.ecutiapps.adapter.StaffAdapter;
import com.kyra.jameedean.ecutiapps.ecutiapps.data.Reference;
import com.kyra.jameedean.ecutiapps.ecutiapps.model.Staff;

import java.util.ArrayList;

public class StaffMain_Activity extends AppCompatActivity {

    private StaffAdapter mAdapter;

    private final static int STAFF_ADD = 1000;

    private ArrayList<String> mKeys;

    // Firebase Authentication
    private DatabaseReference mReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();

        //get current user
        mCurrentUser = mFirebaseAuth.getCurrentUser();

        setContentView(R.layout.activity_staff);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        mKeys = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterStaff_Activity.class);
                startActivityForResult(intent, STAFF_ADD);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_staff);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StaffAdapter(this, new StaffAdapter.OnItemClick() {
            @Override
            public void onClick(int pos) {
                // Open back note activity with data
                Intent intent = new Intent(getApplicationContext(), RegisterStaff_Activity.class);
                intent.putExtra(Reference.STAFF_ID, mKeys.get(pos));
                startActivity(intent);
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(StaffMain_Activity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        // And now set it to the RecyclerView
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        mReference = FirebaseDatabase.getInstance().getReference(Reference.USER_DB).child(Reference.USER_INFO);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // listening for changes
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // clear table
                mKeys.clear();
                mAdapter.clear();
                // load data
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Staff model = noteSnapshot.getValue(Staff.class);
                    mAdapter.addData(model);
                    mKeys.add(noteSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stop listening
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                mFirebaseAuth.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.action_changepass:
                updatePassword();
                break;
        }
        return true;
    }

    private void updatePassword() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_changepass, null);
        dialogBuilder.setView(dialogView);

        final Button chgpassfr = (Button) dialogView.findViewById(R.id.chgbtn);
        final Button cancel = (Button) dialogView.findViewById(R.id.cancelbtn);
        final EditText newPass = (EditText) dialogView.findViewById(R.id.newpass);

        final AlertDialog dlg = dialogBuilder.create();

        chgpassfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentUser != null && !newPass.getText().toString().trim().equals("")) {
                    if (newPass.getText().toString().trim().length() < 6) {
                        newPass.setError("Password too short, enter minimum 6 characters");
                    } else {
                        mCurrentUser.updatePassword(newPass.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(StaffMain_Activity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(StaffMain_Activity.this, MainActivity.class));
                                            final FirebaseDatabase fireBaseUpdatePublicHoliday = FirebaseDatabase.getInstance();
                                            DatabaseReference fireBasePublicHoliday = fireBaseUpdatePublicHoliday.getReference(Reference.USER_DB + "/" + Reference.USER_INFO + "/" + mCurrentUser.getUid() + "/password");
                                            String newpass = newPass.getText().toString().trim();
                                            fireBasePublicHoliday.setValue(newpass);
                                        } else {
                                            Toast.makeText(StaffMain_Activity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else if (newPass.getText().toString().trim().equals("")) {
                    newPass.setError("Enter password");
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
        dlg.show();
    }

}
















