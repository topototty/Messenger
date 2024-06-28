package com.example.messengerapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerapp.Models.Friend;
import com.example.messengerapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private Context context;
    private List<Friend> friendsList;
    private OnFriendListener onFriendListener;

    public FriendsAdapter(Context context, List<Friend> friendsList, OnFriendListener onFriendListener) {
        this.context = context;
        this.friendsList = friendsList;
        this.onFriendListener = onFriendListener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view, onFriendListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        holder.nameTextView.setText(friend.getFirstName() + " " + friend.getLastName());

        if (friend.getAvatarUrl() != null && !friend.getAvatarUrl().isEmpty()) {
            Picasso.get().load(friend.getAvatarUrl()).into(holder.avatarImageView);
        } else {
            holder.avatarImageView.setImageResource(R.drawable.ic_profile); // замените на ваш ресурс по умолчанию
        }
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTextView;
        ImageView avatarImageView;
        OnFriendListener onFriendListener;

        public FriendViewHolder(@NonNull View itemView, OnFriendListener onFriendListener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            this.onFriendListener = onFriendListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onFriendListener.onFriendClick(getAdapterPosition());
        }
    }

    public interface OnFriendListener {
        void onFriendClick(int position);
    }
}
