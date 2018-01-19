package vipul.in.mychat.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import vipul.in.mychat.Adapters.ContactsAdapter;
import vipul.in.mychat.GetTimeAgo;
import vipul.in.mychat.MainActivity;
import vipul.in.mychat.R;

import static android.support.v4.content.PermissionChecker.checkCallingOrSelfPermission;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;


public class Contacts extends Fragment {

    private RecyclerView contacts_recyclerView;
    private static ContactsAdapter adapter;
    private static List<vipul.in.mychat.ModalClasses.Contacts> list;

    String currUid;
    private static Activity activity;

    private static DatabaseReference mRef;
    private static Context mContext;



    @Override
    public void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference().child("Users").child(currUid).child("isOnline").setValue("true");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contacts,container,false);

        list = new ArrayList<>();
        adapter = new ContactsAdapter(rootView.getContext(),list);
        contacts_recyclerView = rootView.findViewById(R.id.contacts_recyclerView);
        contacts_recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        contacts_recyclerView.setHasFixedSize(true);
        contacts_recyclerView.setAdapter(adapter);
        activity = getActivity();
        mContext = container.getContext();

        if( ContextCompat.checkSelfPermission(mContext,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            if(shouldShowRequestPermissionRationale( Manifest.permission.READ_CONTACTS)) {

                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Contact permission necessary");
                alertBuilder.setMessage("We need contacts permission to display friends");

                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(new String[] {Manifest.permission.READ_CONTACTS},0);
                        AlertDialog alert = alertBuilder.create();
                        alert.show();

                    }
                });
            }
            else {
                requestPermissions(new String[] {Manifest.permission.READ_CONTACTS},0);

            }
        }
        else {
            fetch_data();
        }
        currUid = FirebaseAuth.getInstance().getUid();
        return rootView;

    }

    public static void fetch_data() {

        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mRef.keepSynced(true);
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

        final HashMap hashMap2 = sortByValues(hm);
        Log.d("All contacts: ", hashMap2.toString());
        phones.close();

        Iterator iterator = hashMap2.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry record = (Map.Entry) iterator.next();
            mRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    vipul.in.mychat.ModalClasses.Contacts contact = dataSnapshot.getValue(vipul.in.mychat.ModalClasses.Contacts.class);
                    String k = dataSnapshot.child("phoneNum").getValue(String.class);
                    if (k.equals(record.getKey().toString())) {
                        contact.setName(record.getValue().toString());
                        list.add(contact);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    vipul.in.mychat.ModalClasses.Contacts contacts = dataSnapshot.getValue(vipul.in.mychat.ModalClasses.Contacts.class);
                    ListIterator<vipul.in.mychat.ModalClasses.Contacts> it = list.listIterator();
                    int tempFlag = 0;
                    while (it.hasNext()) {
                        if(it.next().getPhoneNum().equals(contacts.getPhoneNum()))
                        {
                            int index = it.nextIndex();
                            String tempName = list.get(index-1).getName();
                            list.remove(index-1);
                            contacts.setName(tempName);
                            list.add(index-1,contacts);
                            Log.d("Changed","Changed "+index);
                            tempFlag = 1;
                        }
                        if(tempFlag == 1) {
                            adapter.notifyDataSetChanged();
                            break;
                        }

                    }
                    //Log.d("Changed Values : ",contacts.getName()+"\n"+contacts.getPhoneNum()+"\n"+contacts.isOnline()+"\n"+contacts.getDevice_token());
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
//            mRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        String k = ds.child("phoneNum").getValue(String.class);
//                        boolean onlineStatus;
//                        if (k.equals(record.getKey().toString())) {
//                            DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(ds.getKey());
//                            tempRef.child("name").setValue(record.getValue());
//                            tempRef.child("number").setValue(record.getKey());
//                            break;
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//
//
//            });

        }

    }

    private static HashMap sortByValues(HashMap map) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetch_data();
                }
                else {

                }
                return;
            }

        }

    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        if(currUid != null) {
//            FirebaseDatabase.getInstance().getReference().child("Users").child(currUid).child("isOnline").setValue(String.valueOf(ServerValue.TIMESTAMP));
//        }
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if(currUid != null) {
//            FirebaseDatabase.getInstance().getReference().child("Users").child(currUid).child("isOnline").setValue(String.valueOf(ServerValue.TIMESTAMP));
//        }
//    }
}
