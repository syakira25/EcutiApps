package com.kyra.jameedean.ecutiapps.ecutiapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyra.jameedean.ecutiapps.ecutiapps.data.Reference;

public class DetailsUser_Activity extends AppCompatActivity{

    private TextView mTVname;
    private TextView mTVemail;
    private TextView mTVphone;
    private TextView mTVjob_position;
    private TextView mTVal, mTVel;
    private TextView mTVmc, mTVpl;
    private TextView mTVuserrole;

    // Firebase Authentication
    private String mId;
    private DatabaseReference mReference, mReference1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);
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

        //Get Firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();

        mTVname = (TextView) findViewById(R.id.name_tv);
        mTVemail = (TextView) findViewById(R.id.email_tv);
        mTVphone = (TextView) findViewById(R.id.phone_tv);
        mTVjob_position = (TextView) findViewById(R.id.job_tv);
        mTVal = (TextView) findViewById(R.id.leave_textView2);
        mTVmc = (TextView) findViewById(R.id.mc_leave_textView);
        mTVel = (TextView) findViewById(R.id.el_leaves);
        mTVpl = (TextView) findViewById(R.id.pc_leaves);
        mTVuserrole = (TextView) findViewById(R.id.role_leaves);

        mReference = FirebaseDatabase.getInstance().getReference(Reference.USER_DB);
        mReference1 = mReference.child(Reference.USER_INFO).child(mCurrentUser.getUid());
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
                mTVphone.setText(dataSnapshot.child("phone").getValue().toString());
                mTVjob_position.setText(dataSnapshot.child("job_position").getValue().toString());
                mTVal.setText(dataSnapshot.child("annual").getValue().toString()+"   "+"days");
                mTVmc.setText(dataSnapshot.child("mc").getValue().toString()+"   "+"days");
                mTVel.setText(dataSnapshot.child("el").getValue().toString()+"   "+"days");
                mTVpl.setText(dataSnapshot.child("public_leave").getValue().toString()+"   "+"days");
                mTVuserrole.setText(dataSnapshot.child("role").getValue().toString());
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
                                            Toast.makeText(DetailsUser_Activity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(DetailsUser_Activity.this, MainActivity.class));
                                            final FirebaseDatabase fireBaseUpdatePublicHoliday = FirebaseDatabase.getInstance();
                                            DatabaseReference fireBasePublicHoliday = fireBaseUpdatePublicHoliday.getReference(Reference.USER_DB + "/" + Reference.USER_INFO + "/" + mCurrentUser.getUid() + "/password");
                                            String newpass = newPass.getText().toString().trim();
                                            fireBasePublicHoliday.setValue(newpass);
                                        } else {
                                            Toast.makeText(DetailsUser_Activity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
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
