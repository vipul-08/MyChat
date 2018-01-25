package vipul.in.mychat.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import vipul.in.mychat.GetTimeAgo;
import vipul.in.mychat.ModalClasses.Messages;
import vipul.in.mychat.R;

/**
 * Created by vipul on 22/1/18.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    Context context;

    public MessagesAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        Messages m = mMessageList.get(position);
        String from_user = m.getFrom();
        String message_type = m.getType();
        holder.time_text.setText(GetTimeAgo.getTimeAgo(m.getTime(),context));
        holder.displayName.setText(from_user);
        if (message_type.equals("text")) {

            holder.messagetext.setText(m.getMessage());

        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView displayName,time_text;
        public EmojiconTextView messagetext;
        public ImageView profileImage;

        public MessageViewHolder(View v) {

            super(v);

            messagetext = v.findViewById(R.id.message_text_layout);
            profileImage = v.findViewById(R.id.message_profile_layout);
            displayName = v.findViewById(R.id.name_text_layout);
            time_text = v.findViewById(R.id.time_text_layout);

        }

    }
}
