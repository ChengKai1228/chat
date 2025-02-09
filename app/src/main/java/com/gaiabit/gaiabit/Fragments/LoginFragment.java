package com.gaiabit.gaiabit.Fragments;

import static com.gaiabit.gaiabit.Fragments.CreateAccountFragment.Email_REGEX;

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

import com.gaiabit.gaiabit.MainActivity;
import com.gaiabit.gaiabit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment {

    private EditText emailEt,passwordEt;
    private TextView signUpTv,forgotTv;
    private Button loginBtn,googleSignInBtn;
    private ProgressBar progressBar;

    private FirebaseAuth auth;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        auth = FirebaseAuth.getInstance();
        clickListener();
    }

    private void clickListener() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email =emailEt.getText().toString();
                String password=passwordEt.getText().toString();

                if(email.isEmpty() || !email.matches(Email_REGEX)){
                    emailEt.setError("請重新輸入電子郵件");
                    return;
                }
                if (password.isEmpty()||password.length()<6){
                    passwordEt.setError("密碼強度不足");
                    return;

                }

                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser user=auth.getCurrentUser();
                            if(!user.isEmailVerified()){
                                Toast.makeText(getContext(),"歡迎",Toast.LENGTH_SHORT).show();

                            }
                            sendUserToMainActivity();

                        }else{
                            String exception="Error:"+task.getException().getMessage();
                            Toast.makeText(getContext(),exception,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }


        });
    }


    private void init(View view){

        emailEt=view.findViewById(R.id.et_1);
        passwordEt=view.findViewById(R.id.et_2);
        loginBtn=view.findViewById(R.id.bt_1);
        signUpTv=view.findViewById(R.id.tv_1);
        forgotTv=view.findViewById(R.id.tv_2);
        progressBar=view.findViewById(R.id.progressBar);

    }

    private void sendUserToMainActivity(){
        if(getActivity() ==null)
            return;
        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
        getActivity().finish();
    }
}