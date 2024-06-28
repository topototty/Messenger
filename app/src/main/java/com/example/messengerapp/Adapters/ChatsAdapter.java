package com.example.messengerapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {

    private List<Map<String, Object>> chatList;
    private OnChatInteractionListener listener;

    public interface OnChatInteractionListener {
        void onChatClick(String chatId);
        void onDeleteChatClick(String chatId);
    }

    public ChatsAdapter(List<Map<String, Object>> chatList, OnChatInteractionListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Map<String, Object> chat = chatList.get(position);
        String chatId = (String) chat.get("chatId");
        String chatName = (String) chat.get("chatName");
        String lastMessage = (String) chat.get("lastMessage");

        holder.chatTitle.setText(chatName);
        holder.chatLastMessage.setText(lastMessage);

        holder.itemView.setOnClickListener(v -> listener.onChatClick(chatId));
        holder.deleteChatButton.setOnClickListener(v -> listener.onDeleteChatClick(chatId));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView chatAvatar;
        TextView chatTitle;
        TextView chatLastMessage;
        ImageButton deleteChatButton;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatAvatar = itemView.findViewById(R.id.chat_avatar);
            chatTitle = itemView.findViewById(R.id.chat_title);
            chatLastMessage = itemView.findViewById(R.id.chat_last_message);
            deleteChatButton = itemView.findViewById(R.id.delete_chat_button);
        }
    }
}
