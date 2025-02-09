package com.gaiabit.gaiabit;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.gaiabit.gaiabit.Model.GalleryImages;
import com.gaiabit.gaiabit.Model.HomeModel;
import com.gaiabit.gaiabit.adapter.GalleryAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class updateActivity extends AppCompatActivity {
    private EditText descET;

    FirebaseFirestore db;
    private ImageView imageView;

    String myUid;
    private ImageButton backBtn, nextBtn;
    private Button btn;
    private Uri imageUri;
    private Dialog dialog;
    private FirebaseUser User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update); // 注意这里使用的布局文件名称可能需要更改


        init(); // 初始化视图和变量

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        imageView.setImageURI(imageUri);
                    } else {
                        Toast.makeText(updateActivity.this, "沒有選擇照片", Toast.LENGTH_SHORT).show();
                    }
                });

        btn.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_GET_CONTENT);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        nextBtn.setOnClickListener(view -> {
            if (imageUri != null) {
                uploadToFirebase(imageUri);
            } else {
                dialog.dismiss();
                Toast.makeText(updateActivity.this, "上傳失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadToFirebase(Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReference().child("Update Images/" + System.currentTimeMillis());
       dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> uploadData(uri1.toString()))).addOnFailureListener(e -> {
            Toast.makeText(updateActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void uploadData(String imageURL) {

        String documentId = getIntent().getStringExtra("DOCUMENT_ID");
        String myUid = User.getUid(); // 假設這是當前用戶的UID



        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("description", descET.getText().toString());
        updateMap.put("imageUrl", imageURL); // 確保imageURL是一個有效的字符串
        updateMap.put("timestamp", FieldValue.serverTimestamp());
        updateMap.put("name", User.getDisplayName());
        updateMap.put("profileImage", String.valueOf(User.getPhotoUrl()));

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("Users").document(myUid)
                .collection("Post Images").document(documentId);

        docRef.update(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // 更新成功時的處理
                Toast.makeText(updateActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 更新失敗時的處理
                Toast.makeText(updateActivity.this, "更新失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void init() {
        descET = findViewById(R.id.descriptionET);
        imageView = findViewById(R.id.imageview);
        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);
        btn = findViewById(R.id.btn1);
        User = FirebaseAuth.getInstance().getCurrentUser();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dialog_bg, null));
        dialog.setCancelable(false);
        db=FirebaseFirestore.getInstance();
        myUid=FirebaseAuth.getInstance().getUid();
    }
}
