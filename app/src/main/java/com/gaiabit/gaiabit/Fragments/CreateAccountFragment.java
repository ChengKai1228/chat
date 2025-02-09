package com.gaiabit.gaiabit.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gaiabit.gaiabit.FragmentReplacerActivity;
import com.gaiabit.gaiabit.MainActivity;
import com.gaiabit.gaiabit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class CreateAccountFragment extends Fragment {

    private EditText name_et,email_et,passwd_et,confirm_et;
    private Button signUpBtn;

    private TextView login;
    private FirebaseAuth auth;

    private ProgressBar progressBar;
    public static final String Email_REGEX="^(.+)@(.+)$";



    public CreateAccountFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        clickListener();
    }

    private void init(View view){
        name_et=view.findViewById(R.id.et_1);
        email_et=view.findViewById(R.id.et_2);
        passwd_et=view.findViewById(R.id.et_3);
        confirm_et=view.findViewById(R.id.et_4);
        signUpBtn=view.findViewById(R.id.bt_1);
        login=view.findViewById(R.id.tv_1);
        progressBar=view.findViewById(R.id.progressBar);


        auth=FirebaseAuth.getInstance();

    }

    private void clickListener(){


//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ((FragmentReplacerActivity)getActivity()).setFragment(new LoginFragment());
//            }
//
//        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=name_et.getText().toString();
                String email=email_et.getText().toString();
                String password=passwd_et.getText().toString();
                String confirm=confirm_et.getText().toString();


                if(name.isEmpty()|| name.equals("")){
                    name_et.setError("請輸入暱稱");
                    return;
                }
                if(email.isEmpty()|| !email.matches(Email_REGEX)){
                    email_et.setError("電子信箱請再次設定");
                    return;
                }
                if(password.isEmpty()|| password.length()<6){
                    passwd_et.setError("密碼請再次設定");
                    return;
                }
                if(!password.equals(confirm)){
                    passwd_et.setError("密碼不相同");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                createAccount(name,email,password);


            }
        });
    }

    private void createAccount(final String name,final String email,String password){

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    FirebaseUser user=auth.getCurrentUser();


                    UserProfileChangeRequest.Builder request= new UserProfileChangeRequest.Builder();
                    request.setDisplayName(name);
                    user.updateProfile(request.build());

                    user.sendEmailVerification().
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getContext(),"驗證信已送出",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    uploadUser(user,name,email);

                }else{
                    progressBar.setVisibility(View.GONE);
                    String exception =task.getException().getMessage();
                    Toast.makeText(getContext(),"ERROR:"+exception,Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void uploadUser(FirebaseUser user,String name,String email){

        Map<String,Object>map =new HashMap<>();

        map.put("name", name);
        map.put("email", email);
        map.put("profileImage", "");
        map.put("status"," ");
        map.put("uid", user.getUid());

        FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            assert getActivity() !=null;
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                            getActivity().finish();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(),"Error:"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

}