package com.gaiabit.gaiabit.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gaiabit.gaiabit.Fragments.Comment;
import com.gaiabit.gaiabit.Fragments.add;
import com.gaiabit.gaiabit.Model.HomeModel;
import com.gaiabit.gaiabit.R;
import com.gaiabit.gaiabit.ReplacerActivity;
import com.gaiabit.gaiabit.updateActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {

    FirebaseFirestore db;
    private final List<HomeModel> list;

    Activity context;

    String myUid,id;

    OnPressed onPressed;





    public HomeAdapter(List<HomeModel> list, Activity context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_items, parent, false);
        return new HomeHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        myUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db=FirebaseFirestore.getInstance();


        holder.userNameTv.setText(list.get(position).getName());
        holder.timeTv.setText("" + list.get(position).getTimestamp());

        List<String> likeList = list.get(position).getLikes();

       int count = likeList.size();

       if (count == 0) {
           holder.likeCountTv.setText("0 Like");
       } else if (count == 1) {
            holder.likeCountTv.setText(count + " Like");
        } else {
          holder.likeCountTv.setText(count + " Likes");
       }

       assert user != null;
       holder.likeCheckBox.setChecked(likeList.contains(user.getUid()));



        holder.descriptionTv.setText(list.get(position).getDescription());


        Random random = new Random();

        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getProfileImage())
                .placeholder(R.drawable.baseline_person_24)
                .timeout(6500)
                .into(holder.profileImage);

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getImageUrl())
                .placeholder(new ColorDrawable(color))
                .timeout(7000)
                .into(holder.imageView);

        holder.clickListener(position,
                list.get(position).getId(),
                list.get(position).getName(),
                list.get(position).getUid(),
                list.get(position).getLikes(),
                list.get(position).getImageUrl()
        );

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert= new AlertDialog.Builder(context);
                alert.setTitle("刪除?").setMessage("確定刪除?").setNegativeButton("No",null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String postID=list.get(position).getId();

                                db.collection("Users/"+myUid+"/Post Images").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot snapshot:task.getResult()){
                                            db.collection("Users/"+myUid+"/Post Images").document(postID).delete();
                                            Toast.makeText(context,"貼文刪除成功",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                alert.show();
            }
        });


        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, updateActivity.class);
                String documentId=list.get(position).getId();
                intent.putExtra("DOCUMENT_ID", documentId);
                context.startActivity(intent);
            }
        });



    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    public void OnPressed(OnPressed onPressed) {
        this.onPressed = onPressed;
    }

    public interface OnPressed {
        void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked);

        void setCommentCount(TextView textView);

    }




    class HomeHolder extends RecyclerView.ViewHolder {


        private CircleImageView profileImage;
        private TextView userNameTv, timeTv, likeCountTv, descriptionTv;
        private ImageView imageView;
        private ImageButton commentBtn, shareBtn,deleteBtn,editBtn;
        private CheckBox likeCheckBox;


        public HomeHolder(@NonNull View itemView) {
            super(itemView);

            editBtn=itemView.findViewById(R.id.edit_btn);
            deleteBtn=itemView.findViewById(R.id.delete_btn);
            profileImage = itemView.findViewById(R.id.profileImage);
            imageView = itemView.findViewById(R.id.imageView);
            userNameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            likeCountTv = itemView.findViewById(R.id.likeCountTv);
            likeCheckBox = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            descriptionTv = itemView.findViewById(R.id.descTv);

            TextView commentTV = itemView.findViewById(R.id.commentTV);
            onPressed.setCommentCount(commentTV);
        }

        public void clickListener(final int position, final String id, String name, final String uid, final List<String> likes, final String imageUrl) {

            commentBtn.setOnClickListener(v -> {

                Intent intent = new Intent(context, ReplacerActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("uid", uid);
                intent.putExtra("isComment", true);


                context.startActivity(intent);

            });

            likeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> onPressed.onLiked(position, id, uid, likes, isChecked));


            shareBtn.setOnClickListener(v -> {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, imageUrl);
                intent.setType("text/*");
                context.startActivity(Intent.createChooser(intent, "Share link using..."));

            });



        }
    }

}
