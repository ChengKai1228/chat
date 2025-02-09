package com.gaiabit.gaiabit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gaiabit.gaiabit.Model.ShelterModel;
import com.gaiabit.gaiabit.R;
import com.gaiabit.gaiabit.TestActivity;

import java.util.List;

public class ShelterAdapter extends RecyclerView.Adapter<ShelterAdapter.HomeHolder> {


    private Context mcontext;
    private List<ShelterModel> mData;

    public ShelterAdapter(Context mcontext, List<ShelterModel> mData) {
        this.mcontext = mcontext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ShelterAdapter.HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater=LayoutInflater.from(mcontext);
        v=inflater.inflate(R.layout.shelter_items,parent,false);
        return new HomeHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShelterAdapter.HomeHolder holder, int position) {
        final ShelterModel currentAnimal = mData.get(position);

        holder.id.setText(currentAnimal.getShelter_name());
        holder.name.setText(currentAnimal.getShelter_address());

        Glide.with(mcontext).load(mData.get(position).getAlbum_file()).into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(mcontext, TestActivity.class);
                intent.putExtra("Shelter_name",currentAnimal.getShelter_name());
                intent.putExtra("Shelter_address",currentAnimal.getShelter_address());
                intent.putExtra("Animal_image", currentAnimal.getAlbum_file());
                mcontext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class HomeHolder extends RecyclerView.ViewHolder {

        TextView id,name;
        ImageView img;
        public HomeHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.name);
            id=itemView.findViewById(R.id.email);
            img=itemView.findViewById(R.id.imageview);
        }
    }
}
