package com.example.textim.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.textim.databinding.ActivityRegistrationBinding;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.loginFromSignUp.setOnClickListener(v -> onBackPressed());
    }
}