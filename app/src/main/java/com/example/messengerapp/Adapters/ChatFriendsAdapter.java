package com.example.messengerapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerapp.Models.Friend;
import com.example.messengerapp.R;

import java.util.List;

public class ChatFriendsAdapter extends RecyclerView.Adapter<ChatFriendsAdapter.FriendViewHolder> {

    private List<Friend> friendsList;
    private List<String> selectedFriends;

    public ChatFriendsAdapter(List<Friend> friendsList, List<String> selectedFriends) {
        this.friendsList = friendsList;
        this.selectedFriends = selectedFriends;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        holder.friendName.setText(friend.getNickname());

        // Установим checkbox в зависимости от того, выбран ли друг
        holder.checkBox.setChecked(selectedFriends.contains(friend.getUid()));

        holder.checkBox.setOnClickListener(v -> {
            if (selectedFriends.contains(friend.getUid())) {
                selectedFriends.remove(friend.getUid());
                holder.checkBox.setChecked(false);
            } else {
                selectedFriends.add(friend.getUid());
                holder.checkBox.setChecked(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        CheckBox checkBox;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friend_name);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
