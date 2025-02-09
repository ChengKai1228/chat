package com.gaiabit.gaiabit.Fragments;



import android.Manifest;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.gaiabit.gaiabit.Model.GalleryImages;
import com.gaiabit.gaiabit.Model.PostImageModel;
import com.gaiabit.gaiabit.R;
import com.gaiabit.gaiabit.adapter.GalleryAdapter;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class add extends Fragment {
    private EditText descET;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private ImageButton backBtn,nextBtn;
    private GalleryAdapter adapter;
    private List<GalleryImages>list;
    private FirebaseUser User;

    private Button btn;
    private Uri imageUri;
    Dialog dialog;



    public add() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        ActivityResultLauncher<Intent> activityResultLauncher= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode()==Activity.RESULT_OK){
                            Intent data=result.getData();
                            imageUri=data.getData();
                            imageView.setImageURI(imageUri);
                        }else {
                            Toast.makeText(getContext(),"沒有選擇照片",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker=new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri!=null){
                   uploadToFirebase(imageUri);
                }else{
                    dialog.dismiss();
                    Toast.makeText(getContext(),"上傳失敗",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void uploadToFirebase(Uri uri){
        FirebaseStorage storage =FirebaseStorage.getInstance();

        final StorageReference storageReference=storage.getReference().child("Post Images/"+System.currentTimeMillis());

        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                          uploadData(uri.toString());

                    }
                });

            }
        });
    }
//    private String getFileExtension(Uri fileUri){
//        ContentResolver contentResolver=getContext().getContentResolver();
//        MimeTypeMap mime=MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
//    }

   private void uploadData(String imageURL) {

        CollectionReference reference=FirebaseFirestore.getInstance().collection("Users")
                .document(User.getUid()).collection("Post Images");

        String description =descET.getText().toString();
        String id =reference.document().getId();

       List<String> list = new ArrayList<>();

        Map<String,Object>map=new HashMap<>();
        map.put("id",id);
        map.put("description",description);
        map.put("imageUrl",imageURL);
        map.put("timestamp", FieldValue.serverTimestamp());
        map.put("name",User.getDisplayName());
        map.put("profileImage",String.valueOf(User.getPhotoUrl()));
        map.put("likes",list);
        map.put("uid",User.getUid());
        reference.document(id).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            System.out.println();
                            Toast.makeText(getContext(),"上傳成功",Toast.LENGTH_SHORT).show();


                        }else{
                            Toast.makeText(getContext(),"Error:"+task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });

    }



    private void init(View view){
        descET=view.findViewById(R.id.descriptionET);
        imageView=view.findViewById(R.id.imageview);

        backBtn=view.findViewById(R.id.backBtn);
        nextBtn=view.findViewById(R.id.nextBtn);

        btn=view.findViewById(R.id.btn1);

        User=FirebaseAuth.getInstance().getCurrentUser();

        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.dialog_bg,null));
        dialog.setCancelable(false);
    }




}