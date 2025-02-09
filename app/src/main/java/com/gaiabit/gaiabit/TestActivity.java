package com.gaiabit.gaiabit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gaiabit.gaiabit.Model.Animal;
import com.gaiabit.gaiabit.Model.ShelterModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TestActivity extends AppCompatActivity {

    Button btn1,btn2;
    TextView textView,textView1;

    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btn1=findViewById(R.id.btn);
        btn2=findViewById(R.id.removebtn);
        textView=findViewById(R.id.Age);
        textView1=findViewById(R.id.place);
        imageView=findViewById(R.id.imageView);


        Intent intent=getIntent();
        String animalid=intent.getStringExtra("Shelter_name");
        String animalAge=intent.getStringExtra("Shelter_address");
        String animalImage=intent.getStringExtra("Animal_image");

        textView.setText(animalid);
        textView1.setText(animalAge);

        Glide.with(this).load(animalImage).into(imageView);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 建立一個資料庫的引用
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://gaiabit-42c97-default-rtdb.firebaseio.com");
                DatabaseReference myRef = database.getReference("animals");

                // 檢查用戶是否已登錄
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // 如果用戶已登錄，獲取UID
                    String userUID = user.getUid();

                    // 創建一個新的物件來存儲你的數據
                    Animal animal = new Animal(animalid, animalAge, animalImage, userUID);

                    // 將這個物件存儲到資料庫
                    myRef.setValue(animal);
                    btn1.setVisibility(View.GONE);
                    btn2.setVisibility(View.VISIBLE);
                   Toast.makeText(TestActivity.this,"預約成功!",Toast.LENGTH_SHORT).show();


                } else {
                    // 如果用戶未登錄，處理未登錄的情況
                    // 例如顯示錯誤消息或引導用戶登錄
                    Toast.makeText(TestActivity.this, "用戶未登錄，請先登錄。", Toast.LENGTH_LONG).show();
                }
            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://gaiabit-42c97-default-rtdb.firebaseio.com");
                DatabaseReference myRef = database.getReference("animals");

                myRef.removeValue();
                btn2.setVisibility(View.GONE);
                btn1.setVisibility(View.VISIBLE);
                Toast.makeText(TestActivity.this,"已取消預約!",Toast.LENGTH_SHORT).show();
            }
        });


    }


}