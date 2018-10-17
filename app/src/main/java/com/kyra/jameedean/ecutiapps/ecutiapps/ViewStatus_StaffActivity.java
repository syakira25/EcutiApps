package com.kyra.jameedean.ecutiapps.ecutiapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.kyra.jameedean.ecutiapps.ecutiapps.adapter.StatusLeaves_Adapter;
import com.kyra.jameedean.ecutiapps.ecutiapps.data.Reference;
import com.kyra.jameedean.ecutiapps.ecutiapps.model.Approve;

import java.util.ArrayList;

public class ViewStatus_StaffActivity extends AppCompatActivity{

    private StatusLeaves_Adapter mAdapter;

    private ArrayList<String> mKeys;

    // Firebase Authentication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mReference, mReference1, mReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();

        //get current user
        mCurrentUser = mFirebaseAuth.getCurrentUser();

        setContentView(R.layout.activity_viewstaff);
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

        mKeys = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_statusleaves);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new StatusLeaves_Adapter(this, new StatusLeaves_Adapter.OnItemClick() {
            @Override
            public void onClick(int pos) {
                // Open back note activity with data
                Intent intent = new Intent(getApplicationContext(), ViewStatus_Staff.class);
                intent.putExtra(Reference.LEAVES_ID, mKeys.get(pos));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(mAdapter);

        mReference = FirebaseDatabase.getInstance().getReference(Reference.USER_DB);
        mReference1 = mReference.child(mCurrentUser.getUid());
        mReference2 = FirebaseDatabase.getInstance().getReference(mCurrentUser.getUid()).child(Reference.LEAVES_RECORD);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // listening for changes
        mReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // clear table
                mKeys.clear();
                mAdapter.clear();
                // load data
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Approve model = noteSnapshot.getValue(Approve.class);
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
                                            Toast.makeText(ViewStatus_StaffActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ViewStatus_StaffActivity.this, MainActivity.class));
                                            final FirebaseDatabase fireBaseUpdatePublicHoliday = FirebaseDatabase.getInstance();
                                            DatabaseReference fireBasePublicHoliday = fireBaseUpdatePublicHoliday.getReference(Reference.USER_DB + "/" + Reference.USER_INFO + "/" + mCurrentUser.getUid() + "/password");
                                            String newpass = newPass.getText().toString().trim();
                                            fireBasePublicHoliday.setValue(newpass);
                                        } else {
                                            Toast.makeText(ViewStatus_StaffActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
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


