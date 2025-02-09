package com.gaiabit.gaiabit.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatModel {
    private String id;
    private String message;
    private String senderID;
    private String image;
    private boolean read;
    private @ServerTimestamp Date time;

    public ChatModel() {
        // 無參數構造函數
    }

    public ChatModel(String id, String message, String senderID, String image, boolean read, Date time) {
        this.id = id;
        this.message = message;
        this.senderID = senderID;
        this.image = image;
        this.read = read;
        this.time = time;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
