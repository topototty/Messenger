package com.example.messengerapp.Models;

public class Friend {
    private String uid;
    private String firstName;
    private String lastName;
    private String nickname;
    private String avatarUrl;

    public Friend() {
    }

    public Friend(String uid, String firstName, String lastName, String nickname, String avatarUrl) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }

    // Getters and setters

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
