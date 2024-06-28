package com.example.messengerapp.Adapters;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerapp.FirebaseHelper;
import com.example.messengerapp.Models.Message;
import com.example.messengerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private Context context;
    private Activity activity;
    private String currentUserId;
    private FirebaseHelper firebaseHelper;

    public MessagesAdapter(List<Message> messageList, Context context, Activity activity) {
        this.messageList = messageList;
        this.context = context;
        this.activity = activity;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.firebaseHelper = new FirebaseHelper(); // Инициализация FirebaseHelper
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        return currentUserId.equals(message.getSenderId()) ? 1 : 0;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_user, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageText.setVisibility(View.GONE);
        holder.messageFileContainer.setVisibility(View.GONE);

        if ("text".equals(message.getType())) {
            String messageText = message.getMessage();
            if (messageText != null) {
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText(messageText);
            }
        } else if ("file".equals(message.getType())) {
            holder.messageFileContainer.setVisibility(View.VISIBLE);
            String fileName = message.getFileName();
            String fileUrl = message.getFileUrl();
            if (fileName != null && fileUrl != null) {
                holder.messageFileName.setText(fileName);
                holder.messageFileContainer.setOnClickListener(v -> downloadFile(context, fileName, "", Environment.DIRECTORY_DOWNLOADS, fileUrl));
            }
        }

        // Получение и форматирование времени
        Date createdAt = message.getCreatedAt();
        if (createdAt != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault());
            holder.messageTimestamp.setText(sdf.format(createdAt));
        }

        String senderId = message.getSenderId();
        if (senderId != null) {
            getUserData(senderId, holder);
        }

        // Добавление долгого нажатия для отображения контекстного меню
        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.inflate(R.menu.menu_message);
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.delete_for_me) {
                    deleteMessageForMe(message);
                    return true;
                } else if (itemId == R.id.delete_for_everyone) {
                    deleteMessageForEveryone(message);
                    return true;
                } else {
                    return false;
                }
            });
            popup.show();
            return true;
        });
    }

    private void getUserData(String userId, MessageViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Map<String, Object> userData = task.getResult().getData();
                if (userData != null) {
                    String nickname;
                    if (userId.equals(currentUserId)) {
                        nickname = "Вы";
                    } else {
                        nickname = (String) userData.get("nickname");
                    }
                    String avatarUrl = (String) userData.get("avatarUrl");
                    holder.messageSender.setText(nickname);
                    Picasso.get().load(avatarUrl).into(holder.messageAvatar);
                }
            }
        });
    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
    }

    private void deleteMessageForMe(Message message) {
        String messageId = message.getMessageId();
        String chatId = message.getChatId();
        firebaseHelper.deleteMessageForSender(chatId, messageId, currentUserId);
        messageList.remove(message);
        notifyDataSetChanged();
    }

    private void deleteMessageForEveryone(Message message) {
        String messageId = message.getMessageId();
        firebaseHelper.deleteMessageForEveryone(messageId);
        messageList.remove(message);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView messageAvatar;
        TextView messageSender;
        TextView messageText;
        LinearLayout messageFileContainer;
        TextView messageFileName;
        TextView messageTimestamp;
        LinearLayout messageContainer;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageAvatar = itemView.findViewById(R.id.message_avatar);
            messageSender = itemView.findViewById(R.id.message_sender);
            messageText = itemView.findViewById(R.id.message_text);
            messageFileContainer = itemView.findViewById(R.id.message_file_container);
            messageFileName = itemView.findViewById(R.id.message_file_name);
            messageTimestamp = itemView.findViewById(R.id.message_timestamp);
            messageContainer = itemView.findViewById(R.id.message_container);
        }
    }
}
