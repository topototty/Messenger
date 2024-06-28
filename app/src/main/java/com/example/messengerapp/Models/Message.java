package com.example.messengerapp.Models;

import java.util.Date;

public class Message {
    private String messageId;
    private String chatId;
    private String senderId;
    private String message;
    private String fileUrl;
    private String fileName; // Новое поле для имени файла
    private String type; // "text" or "file"
    private Date createdAt;
    private Date updatedAt;

    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String messageId, String chatId, String senderId, String message, String fileUrl, String fileName, String type, Date createdAt, Date updatedAt) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.message = message;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
