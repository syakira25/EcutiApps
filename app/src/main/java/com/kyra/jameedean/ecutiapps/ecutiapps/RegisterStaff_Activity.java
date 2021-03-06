package com.kyra.jameedean.ecutiapps.ecutiapps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kyra.jameedean.ecutiapps.ecutiapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyra.jameedean.ecutiapps.ecutiapps.data.Reference;
import com.kyra.jameedean.ecutiapps.ecutiapps.model.Staff;

import java.util.ArrayList;
import java.util.List;

public class RegisterStaff_Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone;
    private EditText editTextAnnual, editTextMc, editTextEl, editTextPublic, editTextjobposition;
    private TextView mTVrole;
    private Spinner roleuser;
    private ProgressDialog progressDialog;

    private String mId;

    // Firebase Authentication
    private DatabaseReference mReference,mReference1, mReference2;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addstaff);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Get Firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();

        editTextName = findViewById(R.id.edit_text_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextPhone = findViewById(R.id.edit_text_phone);
        editTextjobposition = findViewById(R.id.edit_job_position);
        editTextAnnual = findViewById(R.id.leave_textView2);
        editTextMc = findViewById(R.id.mc_leave_textView);
        editTextEl = findViewById(R.id.el_leaves);
        editTextPublic = findViewById(R.id.pc_leaves);
        mTVrole = findViewById(R.id.role_leaves);
        roleuser = findViewById(R.id.userrole);

        // Spinner click listener
        roleuser.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Admin");
        //categories.add("Management");
        categories.add("Staff");
        categories.add("Others");

        // Creating adapter for spinner
        roleuser.setPrompt("Users Roles");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        roleuser.setAdapter(dataAdapter);

        mReference = FirebaseDatabase.getInstance().getReference(Reference.USER_DB).child(Reference.USER_INFO);
        mReference1 = FirebaseDatabase.getInstance().getReference(Reference.USER_DB);
        mReference2 = mReference1.child(mCurrentUser.getUid());

        Intent intent = getIntent();
        // Load record
        if(intent != null) {
            mId = intent.getStringExtra(Reference.STAFF_ID);
            if(mId != null) {
                mReference.child(mId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Staff model = dataSnapshot.getValue(Staff.class);
                        if(model != null) {
                            editTextName.setText(model.getName());
                            editTextEmail.setText(model.getEmail());
                            editTextPassword.setText(model.getPassword());
                            editTextPhone.setText(model.getPhone());
                            editTextjobposition.setText(model.getJob_position());
                            editTextAnnual.setText(model.getAnnual());
                            editTextMc.setText(model.getMc());
                            editTextEl.setText(model.getEl());
                            editTextPublic.setText(model.getPublic_leave());
                            mTVrole.setText(model.getRole());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mFirebaseAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                registerUser();
                //sendEmailVerification();
                break;
            case R.id.action_delete:
                if(!mId.isEmpty()) {
                    mReference.child(mId).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            actionNotification(databaseError, R.string.done_deleted);
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
    private void save(Staff model,
                      DatabaseReference.CompletionListener listener) {

        if(mId == null) {
            // generate id
            mId = mReference.push().getKey();
        }

        //mReference.child(mId).setValue(model, listener);
    }

    private void actionNotification(DatabaseError error, int successResourceId) {
        // close activity
        if(error == null) {
            Toast.makeText(RegisterStaff_Activity.this, successResourceId, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(RegisterStaff_Activity.this, error.getCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String job_position = editTextjobposition.getText().toString().trim();
        final String annual = editTextAnnual.getText().toString().trim();
        final String mc = editTextMc.getText().toString().trim();
        final String el = editTextEl.getText().toString().trim();
        final String public_leave = editTextPublic.getText().toString().trim();
        final String user_role = mTVrole.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.input_error_name));
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.input_error_email));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.input_error_email_invalid));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.input_error_password));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.input_error_password_length));
            editTextPassword.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError(getString(R.string.input_error_phone));
            editTextPhone.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            editTextPhone.setError(getString(R.string.input_error_phone_invalid));
            editTextPhone.requestFocus();
            return;
        }

        //requesting Firebase server
        showProcessDialog();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            sendEmailVerification();
                            Toast.makeText(RegisterStaff_Activity.this, "Registration successful. Verification Email Send.", Toast.LENGTH_LONG).show();
                            // What to do when save
                            Staff model = new Staff(
                                   name,
                                    email,
                                    password,
                                    phone,
                                    job_position,
                                    FirebaseAuth.getInstance().getUid(),
                                    annual,
                                    mc,
                                    el,
                                    public_leave,
                                    user_role
                            );
                            save(model, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    actionNotification(databaseError, R.string.done_saved);
                                }
                            });

                            FirebaseDatabase.getInstance().getReference("Users").child(Reference.USER_INFO)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterStaff_Activity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterStaff_Activity.this, StaffMain_Activity.class));
                                    } else {
                                        //display a failure message
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterStaff_Activity.this, getString(R.string.registration_failed), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RegisterStaff_Activity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });

    }

    private void showProcessDialog() {
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setMessage("Register a new account...");
        progressDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        mTVrole.setText(item);

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void sendEmailVerification(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // email sent

                    Toast.makeText(RegisterStaff_Activity.this,"Check email to verify.",Toast.LENGTH_LONG).show();
                    // after email is sent just logout the user and finish this activity
//                    FirebaseAuth.getInstance().signOut();
//                    startActivity(new Intent(RegisterStaff_Activity.this, MainActivity.class));
//                    finish();
                }
                else
                {
                    // email not sent, so display message and restart the activity or do whatever you wish to do
                    //restart this activity
                    overridePendingTransition(0, 0);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());

                }
            }
        });

    }
}
