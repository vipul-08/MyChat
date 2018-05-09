package vipul.in.mychat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import vipul.in.mychat.ModalClasses.SingleChat;
import vipul.in.mychat.OnContactClick;
import vipul.in.mychat.R;

import static android.graphics.Typeface.BOLD;

/**
 * Created by vipul on 23/1/18.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {


    Context mContext;
    List<SingleChat> singleChats;

    public ChatListAdapter(Context context, List<SingleChat> singleChats) {

        this.mContext = context;
        this.singleChats = singleChats;

    }

    @Override
    public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.users_single_layout,null);

        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatListViewHolder holder, int position) {

        final SingleChat singleChat = singleChats.get(position);
        holder.name_from.setText(singleChat.getChatWith());
        holder.last_message.setText(singleChat.getLastMessage());

        if(!singleChat.isSeen()) {
            if(!singleChat.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                holder.name_from.setTypeface(null, BOLD);
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, OnContactClick.class);
                intent.putExtra("clicked",singleChat.getChatWith());
                intent.putExtra("uid",singleChat.getKey());
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return singleChats.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayout;
        TextView name_from,last_message;
        public ChatListViewHolder(View itemView) {

            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeSingleChat);
            name_from = itemView.findViewById(R.id.user_single_name);
            last_message = itemView.findViewById(R.id.user_last_msg);

        }
    }


}
