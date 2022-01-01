package com.example.textim.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.textim.R;
import com.example.textim.databinding.ActivityLogInBinding;

public class LogInActivity extends AppCompatActivity {

    // ActivityLogInBinding Automatically generated from activity_log_in
    private ActivityLogInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.RegisterFromLogin.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class)));
    }
}