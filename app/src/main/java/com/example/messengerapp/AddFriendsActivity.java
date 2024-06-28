package com.example.messengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerapp.Adapters.FriendsAdapter;
import com.example.messengerapp.Models.Friend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddFriendsActivity extends AppCompatActivity implements FriendsAdapter.OnFriendListener {

    private RecyclerView allUsersRecyclerView;
    private FriendsAdapter allUsersAdapter;
    private List<Friend> allUsersList;
    private List<Friend> friendsList;

    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        allUsersRecyclerView = findViewById(R.id.allUsersRecyclerView);

        firebaseHelper = new FirebaseHelper();

        allUsersList = new ArrayList<>();
        friendsList = new ArrayList<>();
        allUsersAdapter = new FriendsAdapter(this, allUsersList, this);

        allUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allUsersRecyclerView.setAdapter(allUsersAdapter);

        loadAllUsers();
    }

    private void loadAllUsers() {
        firebaseHelper.listAllUsers(new FirebaseHelper.FirebaseHelperCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                allUsersList.clear();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String currentUserId = currentUser.getUid();
                    for (Map<String, Object> data : result) {
                        String userId = (String) data.get("uid");
                        if (!currentUserId.equals(userId)) {
                            Friend friend = new Friend(
                                    userId,
                                    (String) data.get("firstName"),
                                    (String) data.get("lastName"),
                                    (String) data.get("nickname"),
                                    (String) data.get("avatarUrl")
                            );
                            allUsersList.add(friend);
                        }
                    }
                    loadFriends();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AddFriendsActivity.this, "Failed to load users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                removeFriendsAndCurrentUser();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AddFriendsActivity.this, "Failed to load friends: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeFriendsAndCurrentUser() {
        // Удаление друзей из allUsersList
        for (Friend friend : friendsList) {
            Iterator<Friend> iterator = allUsersList.iterator();
            while (iterator.hasNext()) {
                Friend user = iterator.next();
                if (user.getUid() != null && user.getUid().equals(friend.getUid())) {
                    iterator.remove();
                }
            }
        }

        // Удаление текущего пользователя из allUsersList по uid
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Iterator<Friend> iterator = allUsersList.iterator();
            while (iterator.hasNext()) {
                Friend friend = iterator.next();
                if (friend.getUid() != null && friend.getUid().equals(user.getUid())) {
                    iterator.remove();
                    break;
                }
            }
        }

        allUsersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddFriendsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFriendClick(int position) {
        Friend friend = allUsersList.get(position);
        firebaseHelper.addFriend(friend.getNickname());
        Toast.makeText(this, "Friend added: " + friend.getFirstName() + " " + friend.getLastName(), Toast.LENGTH_SHORT).show();
    }
}
