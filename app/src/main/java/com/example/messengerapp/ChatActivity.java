package com.example.messengerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerapp.Adapters.MessagesAdapter;
import com.example.messengerapp.Models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private MessagesAdapter messagesAdapter;
    private List<Message> messageList = new ArrayList<>();
    private FirebaseHelper firebaseHelper;
    private String chatId;
    private static final int PICK_FILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        messagesAdapter = new MessagesAdapter(messageList, this, this);
        recyclerViewMessages.setAdapter(messagesAdapter);

        firebaseHelper = new FirebaseHelper();

        chatId = getIntent().getStringExtra("chatId");
        loadMessages();

        EditText editTextMessage = findViewById(R.id.edit_text_message);
        ImageButton buttonSend = findViewById(R.id.button_send);
        ImageButton buttonAttach = findViewById(R.id.button_attach);

        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString();
            if (!messageText.isEmpty()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String senderId = user.getUid();
                    firebaseHelper.sendMessage(chatId, messageText, senderId);
                    editTextMessage.setText("");
                    loadMessages();
                }
            }
        });

        buttonAttach.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && fileUri != null) {
                String senderId = user.getUid();
                firebaseHelper.sendFile(chatId, fileUri, senderId, "file");
                loadMessages();
            }
        }
    }

    private void loadMessages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Messages")
                .whereEqualTo("chatId", chatId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messageList.clear();
                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Message message = document.toObject(Message.class);
                            Log.d("ChatActivity", "Loaded message: " + message.getMessage());
                            message.setMessageId(document.getId());

                            Map<String, Boolean> deletedFor = (Map<String, Boolean>) document.get("deletedFor");
                            if (deletedFor == null || !deletedFor.containsKey(currentUserId) || !deletedFor.get(currentUserId)) {
                                messageList.add(message);
                            }
                        }

                        Collections.sort(messageList, new Comparator<Message>() {
                            @Override
                            public int compare(Message o1, Message o2) {
                                return o1.getCreatedAt().compareTo(o2.getCreatedAt());
                            }
                        });
                        messagesAdapter.notifyDataSetChanged();
                        recyclerViewMessages.scrollToPosition(messageList.size() - 1); // Прокрутка к последнему сообщению
                    } else {
                        Log.e("ChatActivity", "Error getting messages: ", task.getException());
                    }
                });
    }

}



