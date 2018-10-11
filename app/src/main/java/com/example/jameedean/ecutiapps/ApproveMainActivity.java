package com.example.jameedean.ecutiapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.jameedean.ecutiapps.adapter.ApproveLeaves_Adapter;
import com.example.jameedean.ecutiapps.adapter.StatusLeaves_Adapter;
import com.example.jameedean.ecutiapps.data.Reference;
import com.example.jameedean.ecutiapps.model.ApplyLeaves_Model;
import com.example.jameedean.ecutiapps.model.Approve;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ApproveMainActivity extends AppCompatActivity {

    private ApproveLeaves_Adapter mAdapter;

    private ArrayList<String> mKeys;
    // Firebase Authentication
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mReference, mReference1, mReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();

        setContentView(R.layout.activity_approvemain);
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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_leaves);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ApproveLeaves_Adapter(this, new ApproveLeaves_Adapter.OnItemClick() {
            @Override
            public void onClick(int pos) {
                // Open back note activity with data
                Intent intent = new Intent(getApplicationContext(), ApproveActivity.class);
                intent.putExtra(Reference.LEAVES_ID, mKeys.get(pos));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(mAdapter);

        mReference = FirebaseDatabase.getInstance().getReference(Reference.USER_DB);
        //mReference1 = mReference.child(mCurrentUser.getUid());
        mReference2 = FirebaseDatabase.getInstance().getReference(Reference.LEAVES_RECORD);

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
        }

        return true;
    }
}
