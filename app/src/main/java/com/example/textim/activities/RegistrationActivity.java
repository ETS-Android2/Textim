package com.example.textim.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.textim.databinding.ActivityRegistrationBinding;
import com.example.textim.utilities.Constants;
import com.example.textim.utilities.PrefManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;
    private PrefManager prefManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(getApplicationContext());
        setListeners();
    }

    private void setListeners(){
        binding.loginFromSignUp.setOnClickListener(v -> onBackPressed());
        binding.registerButton.setOnClickListener(v -> {
            if(isDataValid()){
                signUp();
            }
        });
        binding.layOutImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            chooseImg.launch(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.username.getEditText().getText().toString());
        user.put(Constants.KEY_EMAIL, binding.email.getEditText().getText().toString());
        user.put(Constants.KEY_PASS, binding.password.getEditText().getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);
        db.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    prefManager.putBoolean(Constants.KEY_LOGGED_IN,true);
                    prefManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    prefManager.putString(Constants.KEY_NAME, binding.username.getEditText().getText().toString());
                    prefManager.putString(Constants.KEY_IMAGE, encodedImage);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStreamStream = new ByteArrayOutputStream();

        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStreamStream);
        byte[] bytes = byteArrayOutputStreamStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    // Setting The profile image
    private final ActivityResultLauncher<Intent> chooseImg = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imgUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imgUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.profileImage.setImageBitmap(bitmap);
                            binding.uploadImgText.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private Boolean isDataValid(){
        if (encodedImage == null) {
            showToast("Upload A Profile Image");
            return false;
        } else if (binding.username.getEditText().getText().toString().trim().isEmpty()) {
            showToast("Type a username");
            return false;
        } else if (binding.email.getEditText().getText().toString().trim().isEmpty()) {
            showToast("Type your email address");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()) {
            showToast("Type a valid email address");
            return false;
        } else if (binding.password.getEditText().getText().toString().trim().isEmpty()) {
            showToast("Type a password");
            return false;
        } else if (binding.verifyPass.getEditText().getText().toString().trim().isEmpty()) {
            showToast("Confirm your password");
            return false;
        } else if (!binding.password.getEditText().getText().toString().equals(binding.verifyPass.getEditText().getText().toString())){
            showToast("Password doesn't match");
            return false;
        } else return true;
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.registerButton.setVisibility(View.INVISIBLE);
            binding.progBar.setVisibility(View.VISIBLE);
        } else {
            binding.progBar.setVisibility(View.INVISIBLE);
            binding.registerButton.setVisibility(View.VISIBLE);
        }
    }
}