package com.gaiabit.gaiabit.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gaiabit.gaiabit.Model.ChatModel;
import com.gaiabit.gaiabit.R;
import com.gaiabit.gaiabit.adapter.ChatAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    FirebaseUser user;
    CircleImageView imageView;
    TextView name, status;
    TextView typingStatusTV;
    EditText chatET;
    ImageView sendBtn;
    RecyclerView recyclerView;

    ChatAdapter adapter;
    List<ChatModel> list;

    String chatID;
    ImageView imageSendBtn;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        loadUserData();
        loadMessages();
        setupTypingStatus();
        imageSendBtn.setOnClickListener(v -> openImageSelector());
        sendBtn.setOnClickListener(v -> sendMessage());
        chatET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                        .update("typing", true);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void setupTypingStatus() {
        String oppositeUID = getIntent().getStringExtra("uid");
        FirebaseFirestore.getInstance().collection("Users").document(oppositeUID)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        boolean isTyping = Boolean.TRUE.equals(documentSnapshot.getBoolean("typing"));
                        typingStatusTV.setVisibility(isTyping ? View.VISIBLE : View.GONE);
                    }
                });
    }

    private void sendMessage() {
        String message = chatET.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");
        Map<String, Object> map = new HashMap<>();
        map.put("lastMessage", message);
        map.put("time", FieldValue.serverTimestamp());
        reference.document(chatID).update(map);
        String messageID = reference.document(chatID).collection("Messages").document().getId();
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("id", messageID);
        messageMap.put("message", message);
        messageMap.put("senderID", user.getUid());
        messageMap.put("time", FieldValue.serverTimestamp());
        messageMap.put("read", false);
        reference.document(chatID).collection("Messages").document(messageID).set(messageMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatET.setText("");
                        updateTypingStatus(false);
                    } else {
                        Toast.makeText(ChatActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .update("typing", false);
    }

    private void sendMessageWithImage(String imageUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("image", imageUrl);
        map.put("senderID", user.getUid());
        map.put("time", FieldValue.serverTimestamp());
        map.put("read", false); // 確保初始設置為未讀
        FirebaseFirestore.getInstance().collection("Messages").document(chatID)
                .collection("Messages").add(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Image sent successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Failed to send image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("chat_images");
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> sendMessageWithImage(uri.toString()));
        }).addOnFailureListener(e -> {
            Toast.makeText(ChatActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    void init() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        imageView = findViewById(R.id.profileImage);
        name = findViewById(R.id.nameTV);
        status = findViewById(R.id.statusTV);
        chatET = findViewById(R.id.chatET);
        sendBtn = findViewById(R.id.sendBtn);
        recyclerView = findViewById(R.id.recyclerView);
        list = new ArrayList<>();
        adapter = new ChatAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        typingStatusTV = findViewById(R.id.typingStatusTV);
        imageSendBtn = findViewById(R.id.imageSendBtn);
    }

    void loadUserData() {
        String oppositeUID = getIntent().getStringExtra("uid");
        FirebaseFirestore.getInstance().collection("Users").document(oppositeUID)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null || !value.exists()) return;
                    boolean isOnline = Boolean.TRUE.equals(value.getBoolean("online"));
                    status.setText(isOnline ? "Online" : "Offline");
                    Glide.with(getApplicationContext()).load(value.getString("profileImage")).into(imageView);
                    name.setText(value.getString("name"));
                });
    }

    private void updateTypingStatus(boolean isTyping) {
        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .update("typing", isTyping);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .update("typing", false);
        updateChatActivityStatus(true, chatID);  // 當用戶打開聊天界面
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateTypingStatus(false);  // 當用戶離開聊天界面
    }

    private void updateChatActivityStatus(boolean isActive, String chatId) {
        Map<String, Object> status = new HashMap<>();
        status.put("isChatActive", isActive);
        status.put("currentChatId", isActive ? chatId : null);
        FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update(status)
                .addOnFailureListener(e -> Log.e("Update Status", "Failed to update user status", e));
    }

    private void markMessagesAsRead(CollectionReference messagesRef) {
        messagesRef.whereEqualTo("read", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().update("read", true);
                        }
                    }
                });
    }

    void loadMessages() {
        chatID = getIntent().getStringExtra("id");
        String oppositeUID = getIntent().getStringExtra("uid");

        CollectionReference reference = FirebaseFirestore.getInstance()
                .collection("Messages")
                .document(chatID)
                .collection("Messages");

        FirebaseFirestore.getInstance().collection("Users").document(oppositeUID)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        boolean isActive = documentSnapshot.getBoolean("isChatActive");
                        String activeChatId = documentSnapshot.getString("currentChatId");
                        if (isActive && chatID.equals(activeChatId)) {
                            markMessagesAsRead(reference); // 對方在當前聊天界面，標記消息為已讀
                        }
                    }
                });

        reference.orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null || value.isEmpty()) return;

                    list.clear();
                    for (QueryDocumentSnapshot snapshot : value) {
                        ChatModel model = snapshot.toObject(ChatModel.class);
                        list.add(model);
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(list.size() - 1);
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }
}