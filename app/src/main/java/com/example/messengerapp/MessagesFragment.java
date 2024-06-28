package com.example.messengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerapp.Adapters.ChatsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessagesFragment extends Fragment implements ChatsAdapter.OnChatInteractionListener {

    private RecyclerView recyclerViewChats;
    private ChatsAdapter chatsAdapter;
    private FirebaseHelper firebaseHelper;
    private List<Map<String, Object>> chatList = new ArrayList<>();
    private FloatingActionButton buttonAddChat;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewChats = view.findViewById(R.id.recycler_view_chats);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseHelper = new FirebaseHelper();
        chatsAdapter = new ChatsAdapter(chatList, this);
        recyclerViewChats.setAdapter(chatsAdapter);

        buttonAddChat = view.findViewById(R.id.button_add_chat);
        buttonAddChat.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddChatActivity.class);
            startActivity(intent);
        });

        loadChats();
    }

    private void loadChats() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Chats").whereArrayContains("members", uid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            chatList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> chat = document.getData();
                                chat.put("chatId", document.getId());
                                chatList.add(chat);
                            }
                            chatsAdapter.notifyDataSetChanged();
                        } else {
                            // Handle error
                        }
                    });
        }
    }

    @Override
    public void onChatClick(String chatId) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("chatId", chatId);
        startActivity(intent);
    }

    @Override
    public void onDeleteChatClick(String chatId) {
        firebaseHelper.deleteChat(chatId);
        loadChats();
    }
}
