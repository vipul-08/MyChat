package vipul.in.mychat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class OnContactClick extends AppCompatActivity {

    static TextView clickedUser;
    static int temp = 0;
    static String getExtra;
    static DatabaseReference mReference;
    static TextView person_name,person_last_seen;
    static String phoneNumber;
    static String dest_uid;

    static DatabaseReference mRef;
    static Toolbar chat_toolbar;
    static String currUid;

    @Override
    protected void onStart() {
        super.onStart();
        currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(currUid).child("isOnline").setValue("true");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_contact_click);
        getExtra = getIntent().getStringExtra("clicked");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        mReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.child("phoneNum").getValue(String.class).equals(phoneNumber)) {

                        dest_uid = ds.getKey();
                        Log.d("Tag","Destination: "+dest_uid);
                        temp = 1;
                        break;

                    }
                }
                if(temp == 1)
                    watchLastSeen();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        clickedUser = findViewById(R.id.clickedUserName);
        clickedUser.setText(getExtra);
        chat_toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);

        chat_toolbar.setTitle(null);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_app_bar,null);

        actionBar.setCustomView(action_bar_view);


        person_name = findViewById(R.id.custom_person_name);
        person_last_seen = findViewById(R.id.custom_person_lastSeen);

        person_name.setText(getExtra);

    }


    public void watchLastSeen() {

        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(dest_uid);
        mRef.child("isOnline").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if("true".equals(dataSnapshot.getValue(String.class))) {
                    person_last_seen.setText("Online");
                }
                else {
                    mRef.child("lastSeen").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long timeStamp = dataSnapshot.getValue(Long.class);
                            Log.d("Long"," "+timeStamp);
                            person_last_seen.setText(GetTimeAgo.getTimeAgo(timeStamp,OnContactClick.this));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(currUid).child("isOnline").setValue("false");
        FirebaseDatabase.getInstance().getReference().child("Users").child(currUid).child("lastSeen").setValue(ServerValue.TIMESTAMP);

    }


}
