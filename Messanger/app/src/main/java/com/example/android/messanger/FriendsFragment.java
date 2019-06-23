package com.example.android.messanger;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FriendsFragment extends Fragment {
    private RecyclerView mFriendList;
    private DatabaseReference mFriendDatabase,mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;

    public FriendsFragment() {
        //requires empty friend constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);


        mFriendList = (RecyclerView) mMainView.findViewById(R.id.FriendList);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true );
        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(mFriendDatabase, Friends.class)
                        .build();
        super.onStart();
        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int i, @NonNull final Friends friends) {
                friendsViewHolder.setDate(friends.getDate());
                final String list_user_id = getRef(i).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String userName=dataSnapshot.child("name").getValue().toString();
                        String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();

                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(thumb_image);
                        if (dataSnapshot.hasChild("online"))
                        {
                        boolean user_online=(boolean)dataSnapshot.child("online").getValue();
                            friendsViewHolder.setUserOnline(user_online);

                        }





                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new FriendsViewHolder(view);
            }
        };

        friendsRecyclerAdapter.startListening();
        mFriendList.setAdapter(friendsRecyclerAdapter);

    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date) {

            TextView userStatusView = mView.findViewById(R.id.users_single_status);
            userStatusView.setText(date);

        }

        public void setName(String name)
        {
            TextView userNameView = mView.findViewById(R.id.users_single_name);
            userNameView.setText(name);

        }
        public void setUserImage(String thumb_image) {
            CircleImageView thumb_imageView=mView.findViewById(R.id.circleImageView);
            Picasso.get().load(thumb_image).placeholder(R.drawable.images).resize(250,250).into(thumb_imageView);
        }


        public void setUserOnline(boolean user_online) {
            ImageView userOnlineView=mView.findViewById(R.id.online_status);
            if (user_online==true)
            {
                userOnlineView.setVisibility(View.VISIBLE);
            }
            else
            {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
