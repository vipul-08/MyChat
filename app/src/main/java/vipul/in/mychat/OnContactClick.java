package vipul.in.mychat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import vipul.in.mychat.Adapters.MessagesAdapter;
import vipul.in.mychat.ModalClasses.Messages;

public class OnContactClick extends AppCompatActivity implements RewardedVideoAdListener {

    int temp = 0;
    String getExtra;
    DatabaseReference mReference;
    TextView person_name,person_last_seen;
    //String phoneNumber;
    static String dest_uid;
    LinearLayoutManager mLinearLayout;
    MessagesAdapter mAdapter;
    private final List<Messages> msgList = new ArrayList<>();

    ImageButton imageButton,emojiButton;
    EmojiconTextView textView;
    EmojiconEditText editText;
    View rootView;

    InterstitialAd mInterstitialAd;
    RewardedVideoAd mRewardedVideoAd;

    RecyclerView chatRecyclerView;

    private DatabaseReference mRef,mRootRef;
    Toolbar chat_toolbar;
    String currUid;

    @Override
    protected void onStart() {

        Log.d("OnStart","OnClick");
        super.onStart();
        currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dest_uid = getIntent().getStringExtra("uid");
        FirebaseDatabase.getInstance().getReference().child("Users").child(currUid).child("isOnline").setValue("true");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("OnCreate","OnClick");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_contact_click);

        MobileAds.initialize(this,"ca-app-pub-6712400715312717~1651070161");
        imageButton = findViewById(R.id.imageButton);
        emojiButton = findViewById(R.id.emojiButton);
        editText = findViewById(R.id.editTextEmoji);
        rootView = findViewById(R.id.root_view);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6712400715312717/2525168130");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        mRewardedVideoAd.loadAd("ca-app-pub-6712400715312717/4390227221",
                new AdRequest.Builder().build());

        mAdapter = new MessagesAdapter(msgList);
        chatRecyclerView = findViewById(R.id.messageList);

        chatRecyclerView.setHasFixedSize(true);
        mLinearLayout = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(mLinearLayout);
        chatRecyclerView.setAdapter(mAdapter);


        EmojIconActions emojIcon = new EmojIconActions(this,rootView,editText,emojiButton);
        emojIcon.ShowEmojIcon();

        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");

            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });

        mRootRef = FirebaseDatabase.getInstance().getReference();

        getExtra = getIntent().getStringExtra("clicked");
        dest_uid = getIntent().getStringExtra("uid");
        //phoneNumber = getIntent().getStringExtra("phoneNumber");
        mReference = FirebaseDatabase.getInstance().getReference().child("Users");
        currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chat_toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);

        chat_toolbar.setTitle(null);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_app_bar,null);

        actionBar.setCustomView(action_bar_view);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }
                else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
                Log.d("Hey","Hey");
            }
        });

        mRootRef.child("Chats").child(currUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(dest_uid).exists())
                    mRootRef.child("Chats").child(currUid).child(dest_uid).child("seen").setValue(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                // Code to be executed when when the interstitial ad is closed.
            }
        });
        /*mRootRef.child("Chats").child(currUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(dest_uid)) {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("lastMessage","nulla");
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chats/" + currUid + "/" + dest_uid, chatAddMap);
                    chatUserMap.put("Chats/" + dest_uid + "/" + currUid, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {

                                Log.d("Error: ", databaseError.getMessage().toString());

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        person_name = findViewById(R.id.custom_person_name);
        person_last_seen = findViewById(R.id.custom_person_lastSeen);
        person_name.setText(getExtra);
        mRootRef.child("Users").child(dest_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                watchLastSeen();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        loadMessages();

    }

    private void loadMessages() {

        Log.d("UIDs",currUid + " " + dest_uid);

        mRootRef.child("Messages").child(currUid).child(dest_uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                if(messages.getFrom().equals(currUid)) {
                    messages.setFrom("Me");
                }
                else {
                    messages.setFrom(getExtra);
                }
                msgList.add(messages);
                mAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(msgList.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void sendMessage() {

        String msg = editText.getText().toString();
        Log.d("message",msg);

        if(!TextUtils.isEmpty(msg)) {

            String curr_user_ref = "Messages/"+currUid+"/"+dest_uid;
            String chat_user_ref = "Messages/"+dest_uid+"/"+currUid;

            DatabaseReference user_msg_push = mRootRef.child("Messages").child(currUid).child(dest_uid).push();
            String push_id = user_msg_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message",msg);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",currUid);

            Map messageUserMap = new HashMap();

            messageUserMap.put(curr_user_ref+ "/" +push_id , messageMap);
            messageUserMap.put(chat_user_ref+ "/" +push_id , messageMap);

            editText.setText("");

            mRootRef.child("Chats").child(currUid).child(dest_uid).child("seen").setValue(true);
            mRootRef.child("Chats").child(currUid).child(dest_uid).child("lastMessage").setValue(msg);
            mRootRef.child("Chats").child(currUid).child(dest_uid).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chats").child(dest_uid).child(currUid).child("seen").setValue(false);
            mRootRef.child("Chats").child(dest_uid).child(currUid).child("lastMessage").setValue(msg);
            mRootRef.child("Chats").child(dest_uid).child(currUid).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null) {

                        Log.d("CHAT_LOG",databaseError.getMessage().toString());

                    }
                }
            });


        }

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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRewardedVideoAd.pause(this);
        currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(currUid).child("isOnline").setValue("false");
        FirebaseDatabase.getInstance().getReference().child("Users").child(currUid).child("lastSeen").setValue(ServerValue.TIMESTAMP);

    }
    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        //Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                //Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        //Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        //Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }
}
