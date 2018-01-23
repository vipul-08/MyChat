package vipul.in.mychat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import vipul.in.mychat.ModalClasses.Contacts;
import vipul.in.mychat.OnContactClick;
import vipul.in.mychat.R;
/**
 * Created by vipul on 9/1/18.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    private Context mCtx,mContext;
    private List<Contacts> contactsList;
    private DatabaseReference dbRef;
    private FirebaseUser cUser;

    public ContactsAdapter(Context mCtx, List<Contacts> contactsList) {
        this.mCtx = mCtx;
        this.contactsList = contactsList;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =LayoutInflater.from(mCtx);
        View view = layoutInflater.inflate(R.layout.layout_contacts,null);
        view.setClickable(true);
        mContext = parent.getContext();
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactsViewHolder holder, int position) {

        final Contacts contacts = contactsList.get(position);
        holder.name.setText(contacts.getName());
        holder.number.setText(contacts.getPhoneNum());
        if(contacts.isOnline() !=null) {

            if(contacts.isOnline().equals("true")) {

                holder.onlineIndic.setVisibility(View.VISIBLE);

            }
            else {

                holder.onlineIndic.setVisibility(View.INVISIBLE);

            }

        }
        else {

            holder.onlineIndic.setVisibility(View.INVISIBLE);

        }
//        if("true".equals(contacts.isOnline()))
//            holder.onlineIndic.setVisibility(View.VISIBLE);
//        else
//            holder.onlineIndic.setVisibility(View.INVISIBLE);

        cUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OnContactClick.class);
                intent.putExtra("clicked",holder.name.getText().toString());
                intent.putExtra("phoneNumber",holder.number.getText().toString());
                intent.putExtra("uid",contacts.getKey());
                Log.d("Key","Key: "+contacts.getKey());
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        TextView name,number;
        ImageView onlineIndic;

        public ContactsViewHolder(View itemView) {

            super(itemView);
            relativeLayout = itemView.findViewById(R.id.contacts_card);
            name = itemView.findViewById(R.id.name_contact);
            number = itemView.findViewById(R.id.number_contact);
            onlineIndic = itemView.findViewById(R.id.onlineIndicator);

        }
    }

}
