package com.example.android.messanger;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> mMessageList;
    private  FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

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
        private ImageView messageImage;
        public MessageViewHolder(View view)
        {
            super(view);
            messageText=(TextView) view.findViewById(R.id.message_text_layout);
            mProfileImage=view.findViewById(R.id.message_profile_layout);
            messageImage=view.findViewById(R.id.messageImage);


        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.MessageViewHolder holder, final int i) {
        mAuth=FirebaseAuth.getInstance();
        String current_user_id=mAuth.getCurrentUser().getUid();
        Messages c=mMessageList.get(i);

        String from_user=c.getFrom();
        final String message_type=c.getType();

        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String image=dataSnapshot.child("thumb_image").getValue().toString();
                Picasso.get().load(image).placeholder(R.drawable.images).into(holder.mProfileImage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
            if (message_type.equals("text"))
            {

                holder.messageText.setText(c.getMessage());
                holder.messageImage.setVisibility(View.INVISIBLE);

            }
            else
            {
                holder.messageText.setVisibility(View.INVISIBLE);
                Picasso.get().load(c.getMessage()).resize(20,20).placeholder(R.drawable.images).into(holder.messageImage);

            }
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
