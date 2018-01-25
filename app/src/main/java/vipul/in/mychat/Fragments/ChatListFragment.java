package vipul.in.mychat.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import vipul.in.mychat.Adapters.ChatListAdapter;
import vipul.in.mychat.ModalClasses.SingleChat;
import vipul.in.mychat.R;


public class ChatListFragment extends Fragment {

    RecyclerView chatListRecycler;
    List<SingleChat> singleChats;
    ChatListAdapter chatListAdapter;
    Context mContext;
    DatabaseReference mRef;
    String currUid;
    HashMap hashMap2;

    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat_list, container, false);

        currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatListRecycler = rootView.findViewById(R.id.chatListRecycler);
        singleChats = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(rootView.getContext(),singleChats);
        chatListRecycler.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        chatListRecycler.setHasFixedSize(true);
        chatListRecycler.setAdapter(chatListAdapter);

        mContext = container.getContext();
        fetch_chats();
        return rootView;
    }

    private void fetch_chats() {


        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        HashMap<String, String> hm = new HashMap<String, String>();
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");
            if (phoneNumber.charAt(0) != '+') {
                phoneNumber = "+91" + phoneNumber;
            }
            hm.put(phoneNumber, name);
        }

        hashMap2 = sortByValues(hm);
        Log.d("All contacts: ", hashMap2.toString());
        phones.close();


        mRef = FirebaseDatabase.getInstance().getReference();

        mRef.child("Chats").child(currUid).orderByChild("timestamp").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final SingleChat singleChat = dataSnapshot.getValue(SingleChat.class);
                Log.d("ChatList"," "+dataSnapshot.getValue().toString());
                String uid = dataSnapshot.getKey();
                singleChat.setKey(uid);

                mRef.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot ds) {

                        Iterator iterator = hashMap2.entrySet().iterator();
                        while(iterator.hasNext()) {

                            Map.Entry record = (Map.Entry) iterator.next();
                            if (ds.child("phoneNum").getValue(String.class).equals(record.getKey().toString())) {

                                singleChat.setChatWith(record.getValue().toString());
                                singleChats.add(0,singleChat);
                                chatListAdapter.notifyDataSetChanged();
                                break;

                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {

                final SingleChat singleChat = dataSnapshot.getValue(SingleChat.class);
                Log.d("ChatChange",s+" "+dataSnapshot.getValue().toString());
                String uid = dataSnapshot.getKey();
                singleChat.setKey(uid);

                ListIterator i = singleChats.listIterator();
                while (i.hasNext()) {

                    SingleChat sc = (SingleChat) i.next();
                    if(sc.getKey().equals(uid)) {

                        if(sc.getTimestamp() == singleChat.getTimestamp()) {

                            singleChat.setChatWith(sc.getChatWith());
                            singleChats.remove(i.nextIndex()-1);
                            singleChats.add(i.nextIndex()-1,singleChat);
                            chatListAdapter.notifyDataSetChanged();
                            break;

                        }
                        else {

                            singleChat.setChatWith(sc.getChatWith());
                            singleChats.remove(i.nextIndex()-1);
                            singleChats.add(0,singleChat);
                            chatListAdapter.notifyDataSetChanged();
                            break;

                        }


                    }

                }


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

    private HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

}
