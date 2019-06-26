package com.example.android.messanger;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> mMessageList;
    private  FirebaseAuth mAuth;

    public MessageAdapter(List<Messages>mMessageList)
    {
        this.mMessageList=mMessageList;
    }



    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);

        return new MessageAdapter.MessageViewHolder(v);


    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public CircleImageView mProfileImage;
        public MessageViewHolder(View view)
        {
            super(view);
            messageText=(TextView) view.findViewById(R.id.message_text_layout);
            mProfileImage=view.findViewById(R.id.message_profile_layout);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int i) {
        mAuth=FirebaseAuth.getInstance();
        String current_user_id=mAuth.getCurrentUser().getUid();
        Messages c=mMessageList.get(i);

        String from_user=c.getFrom();
        if (from_user.equals(current_user_id))
        {
            holder.messageText.setBackgroundResource(R.color.grey);
            holder.messageText.setTextColor(Color.BLACK);
            holder.messageText.setBackgroundResource(R.drawable.message_receiver_background);


        }
        else {
             holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);

        }
        holder.messageText.setText(c.getMessage());


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}
