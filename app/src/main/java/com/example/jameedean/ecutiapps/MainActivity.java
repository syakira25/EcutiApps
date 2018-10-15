package com.example.jameedean.ecutiapps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jameedean.ecutiapps.data.Reference;
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

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements TextWatcher, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = MainActivity.class.getName();
    private EditText inputEmail, inputPassword;
    private Button btnLogin, btnReset;
    private CheckBox rem_userpass;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASS = "password";

    // firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mReference, mReference1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        inputEmail = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        rem_userpass = (CheckBox) findViewById(R.id.chkBox1);
        btnLogin = (Button) findViewById(R.id.btn_signIn);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        if (sharedPreferences.getBoolean(KEY_REMEMBER, false))
            rem_userpass.setChecked(true);
        else
            rem_userpass.setChecked(false);

        inputEmail.setText(sharedPreferences.getString(KEY_USERNAME, ""));
        inputPassword.setText(sharedPreferences.getString(KEY_PASS, ""));

        inputEmail.addTextChangedListener(this);
        inputPassword.addTextChangedListener(this);
        rem_userpass.setOnCheckedChangeListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.ftv_font);
        rem_userpass.setTypeface(typeface);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
                //startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
               // final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, " ", "Proccessing...", true);

                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();

                //authenticate user
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressDialog.dismiss();
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError("Minimum length password 6");
                                    } else {
                                        Log.e("ERROR", task.getException().toString());

                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                } else {

                                    successLog();
                                }
                            }
                        });
            }
        });
    }

    private void successLog() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(MainActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference;
        reference = database.getReference("/Users/Users_info/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, dataSnapshot.toString());
                String role;
                String email;
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    role = (childDataSnapshot.child("role").getValue() != null) ? (String.valueOf(childDataSnapshot.child("role").getValue())) : "";
                    email = (childDataSnapshot.child("email").getValue() != null) ? (String.valueOf(childDataSnapshot.child("email").getValue())) : "";
                    Log.v(TAG, "role:" + role);
                    Log.v(TAG, "email" + email);
                    Log.v(TAG, "email input" + inputEmail.getText().toString());
                    Class<?> cls = null;
                    if( email.equals(inputEmail.getText().toString())) {
                        switch(role){
                            case "Admin":
                                cls =  DashboardActivity.class;
                                        break;
                            case "Staff":
                                cls = DashboardStaffActivity.class;
                            break;
                            case "Others":
                                cls = DashboardStaffActivity.class;
                                break;
                        }
                        Intent intent = new Intent(MainActivity.this, cls);

                        startActivity(intent);
                    }else{
                        //Toast.makeText(getApplication(), "Email not found", Toast.LENGTH_SHORT).show();
                        Log.v(TAG,"Email not found");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value", databaseError.toException());
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        managePrefs();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        managePrefs();
    }

    private void managePrefs() {
        if (rem_userpass.isChecked()) {
            editor.putString(KEY_USERNAME, inputEmail.getText().toString().trim());
            editor.putString(KEY_PASS, inputPassword.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
        } else {
            editor.putBoolean(KEY_REMEMBER, false);
            editor.remove(KEY_PASS);//editor.putString(KEY_PASS,"");
            editor.remove(KEY_USERNAME);//editor.putString(KEY_USERNAME, "");
            editor.apply();
        }
    }

    private void resetPassword() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_resetpassword, null);
        dialogBuilder.setView(dialogView);

        final EditText editEmail = (EditText) dialogView.findViewById(R.id.email);
        final Button btnReset = (Button) dialogView.findViewById(R.id.btn_reset_password);
        final ProgressBar progressBar1 = (ProgressBar) dialogView.findViewById(R.id.progressBar);
        final Button btnCancel = (Button) dialogView.findViewById(R.id.button_close);

        //dialogBuilder.setTitle("Send Photos");
        final AlertDialog dialog = dialogBuilder.create();

        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar1.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                                progressBar1.setVisibility(View.GONE);
                                dialog.dismiss();
                            }
                        });
            }
        });

        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}

