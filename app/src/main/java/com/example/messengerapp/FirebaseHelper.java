package com.example.messengerapp;

import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.messengerapp.Models.Friend;
import com.example.messengerapp.Models.Message;
import com.example.messengerapp.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseHelper {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCMhd6QYPpQ6/qQ\\nT1+sBdx/+ZrZLF9ppqsso/GZ0zMvJykjjWAPJLfnSJqNZSgsd51GTgeMfDkSAGoP\\nBcp3y9X4QMYbhOOs6DyxD9Tl+BU4v07POMcFNJpP6SGNQLibneYwejF1kenOrhBQ\\n7oMroCjIoP7DG04n2yVycmVUhMB4F/5pXQNhlANt3jOGuCDAdI19/PRFHyFZsWf1\\n6vSqmJHZh8YBFP8eBV/jMofSc8rFwZRdoNIAIW5Fo135rtjsRgPvm7iO8A5dHtSR\\nWp47u9bzzO5lY3Q3L2D0UDvhNgxSr+IjXLDTTiImYsz9G0ErAISjnkp2SCIa7rbx\\nC8AGKopXAgMBAAECggEALOpHGsfciov6sTClqFLRSVhrXboYnyjmMqUOPrgqdopi\\nVFpGV2DJpVPoDoTEQ/5Rl1yufgFgrnfSbo3Pyt2svSZbDAW8ThKG/6NLTdwvUE2Z\\nr5N6zZekMSGXl1IWD6t3HYyZDXa/kE/DqFKsO3/8OTB4bb1nDEc4H24gJFUwPdIY\\nOfKU26qYt4XwhPf6eR5jyjzmfJxaxvpkuHz2uSt5ixXRS6F5y6fEVYsh9ISEoSfQ\\n9uZQOmLwiIMZLy6CuOqhaTWHsYv+xZskI0I+5J91E3+rI84MJFiTZ9Ke2PAIKuPk\\n4zelxZor9XnKI9m1VUJYd3HdukI/C44N802HEVTD0QKBgQDBj29UQVdNB52O4CZY\\nzR1IqW7NIwxT4Fu2pMKeaOxn3FsqbhPARM0LmooOw3kd4rmj2yu3z0auxRzQi6u3\\nV4mAcMJQiWsD9sdXxbCVxxQ6SjvcVTe92zv5DFKpvD+qZ3oYhTns3r8px4puNLKK\\nu7V7NfYB0ILKvfx+2esi4NfZZwKBgQC52oLkm7Tuo55VzTygKe8UYOMUhTTNixC7\\nqd+1/QMptQdzfWnBKOTu3SNM488J0czOe85IoNiPvJvSHVa+FZpSQknlXyib7TIc\\n0zZMS9UMHNH5iS/sUotHlsTsoM/S6MVxPwAyyna9w0Mpi9ye6N8wicetcF8T/bAq\\nFKck/xkBkQKBgEiVgSoXVqWMNYlehQLH6DNXVjWW7wiFGd9j6Zd9wSkxeP5wSPfQ\\n1ROE4ECpDMIoQ3UuuzYyhE8Fm8iYl8wpB2ci4btRzM+vuxEvEQkZnnw9WjVFv6Fy\\nlQrPlDASZ81/AEoxq4coAIeqTD8Hg+yCd4u8sIP9rQ5aMFVhYsuxhUABAoGBAJ5p\\nNXANl6ZgJ7ESgj4x/+nquyLedydLBTQvktLXmT6PYo55sw5FAPzn2BLNicbg4CSP\\nOkCFeJDrPhVVlH3og+ThQ4MSHNbq0e/nxzw41prAZei/me1gXf7i726RkxI9/SyN\\nemnQUG1mZXSgFagM7U+94Ehgo2myrdjwSZcrJ/FBAoGBAIbJg4tEwyyJqyPsv68c\\nO2c+RFkkw2c/fw4nwNrkl4dTzbgZXy8Ax/dMffGUIY1CBayz1NlMUkD91VUbarGK\\n1qJsCVgX9j2KoFEboVtVVNwWR4bRp3zrj9X/UUbYxlgm+0HDBSFwoi7Inl2gZc+Q\\nglGohaaWzq/5avJXK3mY8BSx";
    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void registerUser(String email, String password, String firstName, String lastName, String nickname) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            List<String> friends = new ArrayList<>();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("email", email);
                            userMap.put("firstName", firstName);
                            userMap.put("lastName", lastName);
                            userMap.put("nickname", nickname);
                            userMap.put("avatarUrl", null);
                            userMap.put("friends", friends);
                            userMap.put("createdAt", FieldValue.serverTimestamp());
                            userMap.put("updatedAt", FieldValue.serverTimestamp());

                            DocumentReference userRef = db.collection("Users").document(uid);
                            userRef.set(userMap)
                                    .addOnSuccessListener(aVoid -> Log.d("FirebaseHelper", "User added to Firestore"))
                                    .addOnFailureListener(e -> Log.w("FirebaseHelper", "Error adding user to Firestore", e));
                        }
                    } else {
                        Log.w("FirebaseHelper", "createUserWithEmail:failure", task.getException());
                    }
                });
    }

    public void addFriend(String nickname) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("Users").whereEqualTo("nickname", nickname).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String friendId = document.getId();
                            db.collection("Users").document(uid).update("friends", FieldValue.arrayUnion(friendId))
                                    .addOnSuccessListener(aVoid -> Log.d("FirebaseHelper", "Friend added successfully"))
                                    .addOnFailureListener(e -> Log.w("FirebaseHelper", "Error adding friend", e));
                        } else {
                            Log.d("FirebaseHelper", "No such user with the given nickname");
                        }
                    });
        }
    }

    public void listAllUsers(FirebaseHelperCallback<List<Map<String, Object>>> callback) {
        db.collection("Users").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> users = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Map<String, Object> userData = document.getData();
                            if (userData != null) {
                                String uid = document.getId();
                                userData.put("uid", uid); // Добавляем поле uid в данные пользователя
                                users.add(userData);
                            }
                        }
                        callback.onSuccess(users);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }


    public void listFriends(FirebaseHelperCallback<List<Map<String, Object>>> callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("Users").document(uid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            List<String> friendIds = (List<String>) document.get("friends");
                            if (friendIds != null && !friendIds.isEmpty()) {
                                List<Map<String, Object>> friends = new ArrayList<>();
                                for (String friendId : friendIds) {
                                    if (!friendId.equals(uid)) {
                                        db.collection("Users").document(friendId).get()
                                                .addOnCompleteListener(friendTask -> {
                                                    if (friendTask.isSuccessful() && friendTask.getResult() != null) {
                                                        Map<String, Object> friendData = friendTask.getResult().getData();
                                                        if (friendData != null) {
                                                            friendData.put("uid", friendId);
                                                            friends.add(friendData);
                                                        }
                                                        if (friends.size() == friendIds.size()) { // -1 для исключения текущего пользователя
                                                            callback.onSuccess(friends);
                                                        }
                                                    } else {
                                                        callback.onFailure(friendTask.getException());
                                                    }
                                                });
                                    }
                                }
                            } else {
                                callback.onSuccess(new ArrayList<>());
                            }
                        } else {
                            callback.onFailure(task.getException());
                        }
                    });
        } else {
            callback.onFailure(new Exception("User not authenticated"));
        }
    }

    public void removeFriend(String friendId, FirebaseHelperCallback<Void> callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            Log.d("FirebaseHelper", "Attempting to remove friend with ID: " + friendId);
            db.collection("Users").document(uid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            List<String> friendIds = (List<String>) document.get("friends");
                            if (friendIds != null && friendIds.contains(friendId)) {
                                db.collection("Users").document(uid).update("friends", FieldValue.arrayRemove(friendId))
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("FirebaseHelper", "Friend removed successfully");
                                            callback.onSuccess(null);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w("FirebaseHelper", "Error removing friend", e);
                                            callback.onFailure(e);
                                        });
                            } else {
                                Log.w("FirebaseHelper", "Friend ID not found in user's friend list");
                                callback.onFailure(new Exception("Friend ID not found"));
                            }
                        } else {
                            Log.w("FirebaseHelper", "Failed to retrieve user document", task.getException());
                            callback.onFailure(task.getException());
                        }
                    });
        } else {
            Log.w("FirebaseHelper", "User not authenticated");
            callback.onFailure(new Exception("User not authenticated"));
        }
    }

    public void deleteMessageForEveryone(String messageId) {
        if (messageId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Messages").document(messageId).delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("FirebaseHelper", "Message deleted for everyone");
                } else {
                    Log.e("FirebaseHelper", "Error deleting message for everyone", task.getException());
                }
            });
        } else {
            Log.e("FirebaseHelper", "Message ID is null, cannot delete message for everyone");
        }
    }

    public void deleteMessageForSender(String chatId, String messageId, String userId) {
        if (chatId != null && messageId != null && userId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Messages").document(messageId)
                    .update("deletedFor." + userId, true).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FirebaseHelper", "Message marked as deleted for sender");
                        } else {
                            Log.e("FirebaseHelper", "Error marking message as deleted for sender", task.getException());
                        }
                    });
        } else {
            Log.e("FirebaseHelper", "Chat ID, Message ID, or User ID is null, cannot delete message for sender");
        }
    }


    public void createChat(List<String> memberIds, String chatName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String chatId = db.collection("Chats").document().getId();
        Map<String, Object> chat = new HashMap<>();
        chat.put("createdAt", FieldValue.serverTimestamp());
        chat.put("lastMessage", "Chat created");
        chat.put("members", memberIds);
        chat.put("chatName", chatName);
        chat.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("Chats").document(chatId).set(chat).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Chat created
            } else {
                // Handle error
            }
        });
    }


    public void deleteChat(String chatId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Chats").document(chatId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Chat deleted
            } else {
                // Handle error
            }
        });
    }


    public void sendMessage(String chatId, String messageText, String senderId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String messageId = db.collection("Messages").document().getId();
        Date now = new Date();
        Message message = new Message(messageId, chatId, senderId, messageText, null, null, "text", now, now);

        db.collection("Messages").document(messageId).set(message)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update last message in chat
                        db.collection("Chats").document(chatId)
                                .update("lastMessage", messageText, "updatedAt", FieldValue.serverTimestamp());
                        sendNotificationToChatMembers(chatId, messageText);
                    } else {
                        // Handle error
                    }
                });
    }

    private void sendNotificationToChatMembers(String chatId, String messageText) {
        db.collection("Chats").document(chatId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> memberIds = (List<String>) task.getResult().get("members");
                for (String memberId : memberIds) {
                    db.collection("Users").document(memberId).get().addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful() && userTask.getResult() != null) {
                            String token = userTask.getResult().getString("fcmToken");
                            if (token != null) {
                                new SendFCMNotificationTask(token, messageText).execute();
                            }
                        }
                    });
                }
            }
        });
    }

    private static class SendFCMNotificationTask extends AsyncTask<Void, Void, Void> {
        private final String token;
        private final String messageBody;

        SendFCMNotificationTask(String token, String messageBody) {
            this.token = token;
            this.messageBody = messageBody;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            String json = "{"
                    + "\"to\":\"" + token + "\","
                    + "\"notification\":{"
                    + "\"title\":\"Новое сообщение\","
                    + "\"body\":\"" + messageBody + "\""
                    + "}"
                    + "}";

            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(FCM_API_URL)
                    .post(body)
                    .addHeader("Authorization", "key=" + SERVER_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Log.e("FirebaseHelper", "Error sending FCM notification: " + response);
                }
            } catch (IOException e) {
                Log.e("FirebaseHelper", "Error sending FCM notification", e);
            }

            return null;
        }
    }
    public void sendFile(String chatId, Uri fileUri, String senderId, String fileType) {
        String fileName = fileUri.getLastPathSegment(); // Получаем имя файла
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("chat_files").child(chatId).child(fileName);
        storageRef.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String fileUrl = uri.toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String messageId = db.collection("Messages").document().getId();
                Map<String, Object> message = new HashMap<>();
                message.put("chatId", chatId);
                message.put("createdAt", FieldValue.serverTimestamp());
                message.put("fileUrl", fileUrl);
                message.put("fileName", fileName); // Устанавливаем имя файла
                message.put("message", "File sent");
                message.put("senderId", senderId);
                message.put("type", fileType);
                message.put("updatedAt", FieldValue.serverTimestamp());

                db.collection("Messages").document(messageId).set(message).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Обновить последнее сообщение в чате
                        db.collection("Chats").document(chatId)
                                .update("lastMessage", "File sent", "updatedAt", FieldValue.serverTimestamp());
                    } else {
                        // Обработка ошибки
                    }
                });
            });
        });
    }


    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }


    public interface FirebaseHelperCallback<T> {
        void onSuccess(T result);

        void onFailure(Exception e);
    }
}
