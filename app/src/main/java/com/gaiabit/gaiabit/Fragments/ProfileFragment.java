package com.gaiabit.gaiabit.Fragments;

import static com.gaiabit.gaiabit.MainActivity.IS_SEARCHED_USER;
import static com.gaiabit.gaiabit.MainActivity.USER_ID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gaiabit.gaiabit.Model.PostImageModel;
import com.gaiabit.gaiabit.R;
import com.gaiabit.gaiabit.chat.ChatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    boolean isMyProfile = true;
    String userUID;
    FirestoreRecyclerAdapter<PostImageModel, PostImageHolder> adapter;
    List<String> followersList, followingList, followingList_2;
    boolean isFollowed;
    DocumentReference userRef, myRef;
    int count;
    private TextView nameTv, toolbarNameTv, statusTv, followingCountTv, followersCountTv, postCountTv;
    private CircleImageView profileImage;
    private Button followBtn, startChatBtn;
    private RecyclerView recyclerView;
    private LinearLayout countLayout;
    private FirebaseUser user;
    private ImageButton editProfileBtn;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageReference;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        myRef = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid());

        if (IS_SEARCHED_USER) {
            isMyProfile = false;
            userUID = USER_ID;
            loadData();
        } else {
            isMyProfile = true;
            userUID = user.getUid();
        }

        if (isMyProfile) {
            editProfileBtn.setVisibility(View.VISIBLE);
            followBtn.setVisibility(View.GONE);
            countLayout.setVisibility(View.VISIBLE);
            startChatBtn.setVisibility(View.GONE);
        } else {
            editProfileBtn.setVisibility(View.GONE);
            followBtn.setVisibility(View.VISIBLE);
        }

        userRef = FirebaseFirestore.getInstance().collection("Users").document(userUID);

        loadBasicData();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        loadPostImages();
        recyclerView.setAdapter(adapter);

        clickListener();
    }

    private void loadData() {
        myRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Tag_b", error.getMessage());
                return;
            }

            if (value == null || !value.exists()) {
                return;
            }

            followingList_2 = (List<String>) value.get("following");
        });
    }

    @SuppressLint("SetTextI18n")
    private void clickListener() {
        followBtn.setOnClickListener(v -> {
            if (isFollowed) {
                if (followersList != null) {
                    followersList.remove(user.getUid());
                } else {
                    followersList = new ArrayList<>();
                }

                if (followingList_2 != null) {
                    followingList_2.remove(userUID);
                } else {
                    followingList_2 = new ArrayList<>();
                }

                final Map<String, Object> map_2 = new HashMap<>();
                map_2.put("following", followingList_2);

                Map<String, Object> map = new HashMap<>();
                map.put("followers", followersList);

                userRef.update(map).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        followBtn.setText("Follow");
                        myRef.update(map_2).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getContext(), "UnFollowed", Toast.LENGTH_SHORT).show();
                            } else {
                                if (task1.getException() != null) {
                                    Log.e("Tag_3", task1.getException().getMessage());
                                }
                            }
                        });
                    } else {
                        if (task.getException() != null) {
                            Log.e("Tag", "" + task.getException().getMessage());
                        }
                    }
                });
            } else {
                createNotification();

                if (followersList == null) {
                    followersList = new ArrayList<>();
                }
                followersList.add(user.getUid());

                if (followingList_2 == null) {
                    followingList_2 = new ArrayList<>();
                }
                followingList_2.add(userUID);

                final Map<String, Object> map_2 = new HashMap<>();
                map_2.put("following", followingList_2);

                Map<String, Object> map = new HashMap<>();
                map.put("followers", followersList);

                userRef.update(map).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        followBtn.setText("UnFollow");
                        myRef.update(map_2).addOnCompleteListener(task12 -> {
                            if (task12.isSuccessful()) {
                                Toast.makeText(getContext(), "Followed", Toast.LENGTH_SHORT).show();
                            } else {
                                if (task12.getException() != null) {
                                    Log.e("tag_3_1", task12.getException().getMessage());
                                }
                            }
                        });
                    } else {
                        if (task.getException() != null) {
                            Log.e("Tag", "" + task.getException().getMessage());
                        }
                    }
                });
            }
        });

        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        startChatBtn.setOnClickListener(v -> {
            queryChat();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImageToFirebase();
        }
    }

    void queryChat() {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");
        reference.whereArrayContains("uid", userUID)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot.isEmpty()) {
                            startChat();
                        } else {
                            for (DocumentSnapshot snapshotChat : snapshot) {
                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra("uid", userUID);
                                intent.putExtra("id", snapshotChat.getId());
                                startActivity(intent);
                            }
                        }
                    }
                });
    }

    void startChat() {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");

        List<String> list = new ArrayList<>();
        list.add(user.getUid());
        list.add(userUID);

        String pushID = reference.document().getId();

        Map<String, Object> map = new HashMap<>();
        map.put("id", pushID);
        map.put("lastMessage", "Hi");
        map.put("time", FieldValue.serverTimestamp());
        map.put("uid", list);

        reference.document(pushID).update(map).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                reference.document(pushID).set(map);
            }
        });

        CollectionReference messageRef = FirebaseFirestore.getInstance()
                .collection("Messages")
                .document(pushID)
                .collection("Messages");

        String messageID = messageRef.document().getId();

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("id", messageID);
        messageMap.put("message", "Hi");
        messageMap.put("senderID", user.getUid());
        messageMap.put("time", FieldValue.serverTimestamp());

        messageRef.document(messageID).set(messageMap);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("uid", userUID);
            intent.putExtra("id", pushID);
            startActivity(intent);
        }, 3000);
    }

    private void init(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        nameTv = view.findViewById(R.id.nameTv);
        statusTv = view.findViewById(R.id.statusTV);
        toolbarNameTv = view.findViewById(R.id.toolbarNameTV);
        followersCountTv = view.findViewById(R.id.followersCountTv);
        followingCountTv = view.findViewById(R.id.followingCountTv);
        postCountTv = view.findViewById(R.id.postCountTv);
        profileImage = view.findViewById(R.id.profileImage);
        followBtn = view.findViewById(R.id.followBtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        countLayout = view.findViewById(R.id.countLayout);
        editProfileBtn = view.findViewById(R.id.edit_profileImage);
        startChatBtn = view.findViewById(R.id.startChatBtn);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize storageReference
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @SuppressLint("SetTextI18n")
    private void loadBasicData() {
        userRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Tag_0", error.getMessage());
                return;
            }

            if (value != null && value.exists()) {
                String name = value.getString("name");
                String status = value.getString("status");
                final String profileURL = value.getString("profileImage");

                nameTv.setText(name);
                toolbarNameTv.setText(name);
                statusTv.setText(status);

                followersList = (List<String>) value.get("followers");
                followingList = (List<String>) value.get("following");

                followersCountTv.setText(followersList != null ? "" + followersList.size() : "0");
                followingCountTv.setText(followingList != null ? "" + followingList.size() : "0");

                try {
                    if (getContext() != null) {
                        Glide.with(getContext().getApplicationContext())
                                .load(profileURL)
                                .placeholder(R.drawable.baseline_person_24)
                                .circleCrop()
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .timeout(6500)
                                .into(profileImage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (followersList != null && followersList.contains(user.getUid())) {
                    followBtn.setText("UnFollow");
                    isFollowed = true;
                    startChatBtn.setVisibility(View.VISIBLE);
                } else {
                    isFollowed = false;
                    followBtn.setText("Follow");
                    startChatBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadPostImages() {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users").document(userUID);
        Query query = reference.collection("Post Images");

        FirestoreRecyclerOptions<PostImageModel> options = new FirestoreRecyclerOptions.Builder<PostImageModel>()
                .setQuery(query, PostImageModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PostImageModel, PostImageHolder>(options) {
            @NonNull
            @Override
            public PostImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_image_items, parent, false);
                return new PostImageHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostImageHolder holder, int position, @NonNull PostImageModel model) {
                Glide.with(holder.itemView.getContext().getApplicationContext())
                        .load(model.getImageUrl())
                        .timeout(6500)
                        .into(holder.imageView);
                count = getItemCount();
                postCountTv.setText("" + count);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child("profile_images/" + System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        saveProfileImageInfoToFirestore(downloadUrl);
                        Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfileImageInfoToFirestore(String downloadUrl) {
        Map<String, Object> userProfileUpdates = new HashMap<>();
        userProfileUpdates.put("profileImage", downloadUrl);

        userRef.update(userProfileUpdates)
                .addOnSuccessListener(aVoid -> Log.d("ProfileFragment", "Profile image URL updated successfully"))
                .addOnFailureListener(e -> Log.e("ProfileFragment", "Error updating profile image URL", e));
    }

    void createNotification() {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Notifications");
        String id = reference.document().getId();
        Map<String, Object> map = new HashMap<>();
        map.put("time", FieldValue.serverTimestamp());
        map.put("notification", user.getDisplayName() + " followed you.");
        map.put("id", id);
        map.put("uid", userUID);
        reference.document(id).set(map);
    }

    private static class PostImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PostImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
