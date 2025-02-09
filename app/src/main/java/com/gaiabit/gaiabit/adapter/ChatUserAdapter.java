package com.gaiabit.gaiabit.adapter;

import android.app.Activity;
import android.os.Build;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gaiabit.gaiabit.Model.ChatUserModel;
import com.gaiabit.gaiabit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ChatUserHolder> {


    public OnStartChat startChat;
    Activity context;
    List<ChatUserModel> list;

    public ChatUserAdapter(Activity context, List<ChatUserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_items, parent, false);
        return new ChatUserHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ChatUserHolder holder, int position) {

        fetchImageUrl(list.get(position).getUid(), holder);


        holder.time.setText(calculateTime(list.get(position).getTime()));

        holder.lastMessage.setText(list.get(position).getLastMessage());

        holder.itemView.setOnClickListener(v ->
                startChat.clicked(position, list.get(position).getUid(), list.get(position).getId()));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    String calculateTime(Date date) {
        if (date == null) {
            // 处理 null 的情况，例如返回一个默认字符串或当前时间等
            return "Unknown"; // 或者根据你的应用需求返回适当的值
        }
        long millis = date.toInstant().toEpochMilli();
        return DateUtils.getRelativeTimeSpanString(millis, System.currentTimeMillis(), 60000, DateUtils.FORMAT_ABBREV_TIME).toString();
    }


    void fetchImageUrl(List<String> uids, ChatUserHolder holder) {

        String oppositeUID;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        if (!uids.get(0).equalsIgnoreCase(user.getUid())) {
            oppositeUID = uids.get(0);
        } else {
            oppositeUID = uids.get(1);
        }

        FirebaseFirestore.getInstance().collection("Users").document(oppositeUID)
                .get().addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        DocumentSnapshot snapshot = task.getResult();

                        Glide.with(context.getApplicationContext()).load(snapshot.getString("profileImage")).into(holder.imageView);
                        holder.name.setText(snapshot.getString("name"));

                    } else {
                        assert task.getException() != null;
                        Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });


    }

    public void OnStartChat(OnStartChat startChat) {
        this.startChat = startChat;
    }

    public interface OnStartChat {
        void clicked(int position, List<String> uids, String chatID);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatUserHolder extends RecyclerView.ViewHolder {

        CircleImageView imageView;
        TextView name, lastMessage, time, count;
        public ChatUserHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.nameTV);
            lastMessage = itemView.findViewById(R.id.messageTV);
            time = itemView.findViewById(R.id.timeTv);
            count = itemView.findViewById(R.id.messageCountTV);

            count.setVisibility(View.GONE);
        }
    }
}
