package com.example.messengerapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerapp.Adapters.ChatFriendsAdapter;
import com.example.messengerapp.Models.Friend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFriends;
    private ChatFriendsAdapter friendsAdapter;
    private List<Friend> friendsList = new ArrayList<>();
    private List<String> selectedFriends = new ArrayList<>();
    private EditText editTextChatName;
    private Button buttonCreateChat;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        recyclerViewFriends = findViewById(R.id.recycler_view_friends);
        editTextChatName = findViewById(R.id.edit_text_chat_name);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));

        firebaseHelper = new FirebaseHelper();
        friendsAdapter = new ChatFriendsAdapter(friendsList, selectedFriends);
        recyclerViewFriends.setAdapter(friendsAdapter);

        buttonCreateChat = findViewById(R.id.button_create_chat);
        buttonCreateChat.setOnClickListener(v -> {
            if (!selectedFriends.isEmpty()) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String currentUserId = auth.getCurrentUser().getUid();
                selectedFriends.add(currentUserId);

                String chatName = editTextChatName.getText().toString().trim();
                if (TextUtils.isEmpty(chatName)) {
                    chatName = getDefaultChatName();
                }

                firebaseHelper.createChat(selectedFriends, chatName);
                Toast.makeText(this, "Chat created successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Select at least one friend to create a chat", Toast.LENGTH_SHORT).show();
            }
        });

        loadFriends();
    }

    private void loadFriends() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            firebaseHelper.listFriends(new FirebaseHelper.FirebaseHelperCallback<List<Map<String, Object>>>() {
                @Override
                public void onSuccess(List<Map<String, Object>> result) {
                    friendsList.clear();
                    for (Map<String, Object> friendData : result) {
                        Friend friend = new Friend(
                                (String) friendData.get("uid"),
                                (String) friendData.get("firstName"),
                                (String) friendData.get("lastName"),
                                (String) friendData.get("nickname"),
                                (String) friendData.get("avatarUrl")
                        );
                        friendsList.add(friend);
                    }
                    friendsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AddChatActivity.this, "Failed to load friends", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getDefaultChatName() {
        StringBuilder chatNameBuilder = new StringBuilder();
        for (Friend friend : friendsList) {
            if (selectedFriends.contains(friend.getUid())) {
                if (chatNameBuilder.length() > 0) {
                    chatNameBuilder.append(", ");
                }
                chatNameBuilder.append(friend.getNickname());
            }
        }
        return chatNameBuilder.toString();
    }
}
