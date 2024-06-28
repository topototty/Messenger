package com.example.messengerapp.Models;

import java.util.Date;
import java.util.List;

public class Chat {
    private String chatId;
    private List<String> members;
    private String lastMessage;
    private Date createdAt;
    private Date updatedAt;

    public Chat() {
        // Default constructor required for Firebase
    }

    public Chat(String chatId, List<String> members, String lastMessage, Date createdAt, Date updatedAt) {
        this.chatId = chatId;
        this.members = members;
        this.lastMessage = lastMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
