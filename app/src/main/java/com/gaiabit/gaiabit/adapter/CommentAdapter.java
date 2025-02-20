package com.gaiabit.gaiabit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gaiabit.gaiabit.Model.ChatModel;
import com.gaiabit.gaiabit.Model.CommentModel;
import com.gaiabit.gaiabit.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    Context context;
    List<CommentModel> list;

    public CommentAdapter(Context context, List<CommentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.comment_items, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentHolder holder, int position) {
        Glide.with(context)
                .load(list.get(position).getProfileImageUrl())
                .into(holder.profileImage);

        holder.nameTv.setText(list.get(position).getName());
        holder.commentTv.setText(list.get(position).getComment());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView nameTv, commentTv;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            nameTv = itemView.findViewById(R.id.nameTV);
            commentTv = itemView.findViewById(R.id.commentTV);

        }
    }
}
