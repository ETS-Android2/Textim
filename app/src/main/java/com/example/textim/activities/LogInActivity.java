package com.example.textim.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.textim.databinding.ActivityLogInBinding;
import com.example.textim.utilities.Constants;
import com.example.textim.utilities.PrefManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity {

    // ActivityLogInBinding Automatically generated from activity_log_in
    private ActivityLogInBinding binding;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getApplicationContext());
        if (prefManager.getBoolean(Constants.KEY_LOGGED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.RegisterFromLogin.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class)));
        binding.loginButton.setOnClickListener(v -> {
            if (isValidData()) {
                LogIn();
            }
        });
    }

    private void loading(Boolean isLoading){
        if (isLoading) {
            binding.loginButton.setVisibility(View.INVISIBLE);
            binding.progBar.setVisibility(View.VISIBLE);
        } else {
            binding.progBar.setVisibility(View.INVISIBLE);
            binding.loginButton.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void LogIn() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.email.getEditText().getText().toString())
                .whereEqualTo(Constants.KEY_PASS,binding.password.getEditText().getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        prefManager.putBoolean(Constants.KEY_LOGGED_IN,true);
                        prefManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        prefManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        prefManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Email or Password is wrong");
                    }
                });
    }

    private Boolean isValidData() {
        if (binding.email.getEditText().getText().toString().trim().isEmpty()) {
            showToast("Type your email address");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()) {
            showToast("Type a valid email address");
            return false;
        } else if (binding.password.getEditText().getText().toString().trim().isEmpty()) {
            showToast("Type your password");
            return false;
        } else {
            return true;
        }
    }

}