package vipul.in.mychat;

import android.app.Fragment;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import vipul.in.mychat.Adapters.ViewPagerAdapter;
import vipul.in.mychat.Fragments.ChatListFragment;
import vipul.in.mychat.Fragments.Contacts;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private android.support.v4.app.Fragment contacts,chatListFragment;

    private FirebaseUser currentUser;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //Button mBtn;
    //TextView mTextView;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position,false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //mBtn = findViewById(R.id.query);
        //mTextView = findViewById(R.id.list);
        //mRef = FirebaseDatabase.getInstance().getReference().child("Users");

        /*mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            String k = ds.child("phoneNum").getValue(String.class);
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
                            while (phones.moveToNext())
                            {
                                String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                phoneNumber = phoneNumber.replace(" ","");
                                Log.d(name,phoneNumber+""+k);
                                if(k.contains(phoneNumber)) {
                                    Log.d("Matched","Matched");
                                    mTextView.setText(mTextView.getText().toString()+"\n"+name+": "+k);
                                    break;
                                }
                            }
                            phones.close();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });*/
        setupViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        contacts = new Contacts();
        chatListFragment = new ChatListFragment();
        adapter.addFragment(chatListFragment,"CHATS");
        adapter.addFragment(contacts,"CONTACTS");
        //adapter.addFragment(contacts,"CONTACTS");
        //adapter.addFragment(contacts,"CONTACTS");
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            startActivity(new Intent(MainActivity.this,AuthActivity.class));
            finish();
        }
        else {

            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("isOnline").setValue("true");

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Tag","mainActivity_onPause");
        if(currentUser!=null) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("isOnline").setValue("false");
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d("Tag","mainActivity_onStop");
//        if(currentUser!=null)
//            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("isOnline").setValue(String.valueOf(ServerValue.TIMESTAMP));
//
//    }
}
