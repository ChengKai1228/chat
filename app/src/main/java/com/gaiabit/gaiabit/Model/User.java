//package com.gaiabit.gaiabit.Model;
//
//public class User {
//    private String uid;
//    private String name;
//    private String email;
//
//    public User() {
//        // Firestore 需要一个无参数构造函数
//    }
//
//    public User(String uid, String name, String email) {
//        this.uid = uid;
//        this.name = name;
//        this.email = email;
//    }
//
//    // getter 和 setter 方法
//    public String getUid() {
//        return uid;
//    }
//
//    public void setUid(String uid) {
//        this.uid = uid;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//}
package com.gaiabit.gaiabit.Model;

public class User {

    private String uid;
    private String name;
    private String email;
    private String profileImage;
    private String status;
    private boolean isChatActive;
    private boolean typing;
    private String currentChatId;

    public User() {
        // Firestore 需要一个无参数构造函数
    }

    public User(String uid, String name, String email, String profileImage, String status, boolean isChatActive, boolean typing, String currentChatId) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.status = status;
        this.isChatActive = isChatActive;
        this.typing = typing;
        this.currentChatId = currentChatId;
    }

    // getter 和 setter 方法
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isChatActive() {
        return isChatActive;
    }

    public void setChatActive(boolean chatActive) {
        isChatActive = chatActive;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

    public String getCurrentChatId() {
        return currentChatId;
    }

    public void setCurrentChatId(String currentChatId) {
        this.currentChatId = currentChatId;
    }
}

