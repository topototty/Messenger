package com.example.messengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerapp.Adapters.FriendsAdapter;
import com.example.messengerapp.Models.Friend;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FriendsFragment extends Fragment implements FriendsAdapter.OnFriendListener {

    private RecyclerView friendsRecyclerView;
    private FriendsAdapter friendsAdapter;
    private List<Friend> friendsList;
    private FloatingActionButton addFriendButton;

    private FirebaseHelper firebaseHelper;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendsRecyclerView = view.findViewById(R.id.friendsRecyclerView);
        addFriendButton = view.findViewById(R.id.addFriendButton);
        firebaseHelper = new FirebaseHelper();

        friendsList = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(getContext(), friendsList, this);

        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsRecyclerView.setAdapter(friendsAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Friend friend = friendsList.get(position);
                removeFriend(friend.getUid(), position);
            }
        }).attachToRecyclerView(friendsRecyclerView);

        loadFriends();

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriendsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadFriends() {
        firebaseHelper.listFriends(new FirebaseHelper.FirebaseHelperCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                friendsList.clear();
                for (Map<String, Object> data : result) {
                    Friend friend = new Friend(
                            (String) data.get("uid"),
                            (String) data.get("firstName"),
                            (String) data.get("lastName"),
                            (String) data.get("nickname"),
                            (String) data.get("avatarUrl")
                    );
                    friendsList.add(friend);
                }
                friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load friends: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFriend(String friendId, int position) {
        firebaseHelper.removeFriend(friendId, new FirebaseHelper.FirebaseHelperCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                friendsList.remove(position);
                friendsAdapter.notifyItemRemoved(position);
                Toast.makeText(getContext(), "Friend removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to remove friend: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFriendClick(int position) {
        // Implement action on friend click if needed
    }
}
