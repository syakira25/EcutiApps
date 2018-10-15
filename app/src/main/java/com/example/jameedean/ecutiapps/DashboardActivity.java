package com.example.jameedean.ecutiapps;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jameedean.ecutiapps.data.Reference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardActivity extends AppCompatActivity {

    private CardView btn_adduser;
    private CardView btn_apply;
    private CardView btn_approve;
    private CardView btn_viewstatus;

    // Firebase Authentication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();

        //get current user
        mCurrentUser = mFirebaseAuth.getCurrentUser();

        setContentView(R.layout.activity_dash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn_adduser = (CardView) findViewById(R.id.adduserId);
        btn_apply = (CardView) findViewById(R.id.applyId);
        btn_approve = (CardView) findViewById(R.id.approveId);
        btn_viewstatus = (CardView) findViewById(R.id.viewId);


        btn_adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, StaffMain_Activity.class));
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ApplyLeaves_Activity.class));
            }
        });

        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ApproveMainActivity.class));
            }
        });

        btn_viewstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ViewStatusMainActivity.class));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
                                            Toast.makeText(DashboardActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                                            final FirebaseDatabase fireBaseUpdatePublicHoliday = FirebaseDatabase.getInstance();
                                            DatabaseReference fireBasePublicHoliday = fireBaseUpdatePublicHoliday.getReference(Reference.USER_DB + "/" + Reference.USER_INFO + "/" + mCurrentUser.getUid() + "/password");
                                            String newpass = newPass.getText().toString().trim();
                                            fireBasePublicHoliday.setValue(newpass);
                                        } else {
                                            Toast.makeText(DashboardActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
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