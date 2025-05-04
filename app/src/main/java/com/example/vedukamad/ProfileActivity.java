package com.example.vedukamad;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private static final int READ_STORAGE_PERMISSION_CODE = 101;

    private ImageView profileImageView;
    private FloatingActionButton fabAddPhoto;
    private EditText nameEditText, emailEditText, passwordEditText, locationEditText, phoneEditText;
    private Button saveButton;
    private ProgressBar progressBar;

    private Uri imageUri;
    private FirebaseUser user;
    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        // Initialize UI components
        profileImageView = findViewById(R.id.profile_image);
        fabAddPhoto = findViewById(R.id.fab_add_photo);
        nameEditText = findViewById(R.id.edit_name);
        emailEditText = findViewById(R.id.edit_email);
        passwordEditText = findViewById(R.id.edit_password);
        locationEditText = findViewById(R.id.edit_location);
        phoneEditText = findViewById(R.id.edit_phone);
        saveButton = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);

        // Register image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        Glide.with(this)
                                .load(imageUri)
                                .placeholder(R.drawable.ic_profile)
                                .into(profileImageView);
                    }
                }
        );

        // Load user data
        loadUserData();

        // Set click listeners
        fabAddPhoto.setOnClickListener(v -> checkAndRequestPermission());

        saveButton.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        if (user != null) {
            // Load name and email from Firebase
            if (user.getDisplayName() != null) {
                nameEditText.setText(user.getDisplayName());
            }

            emailEditText.setText(user.getEmail());
            emailEditText.setEnabled(false); // Email cannot be changed

            // Load profile image
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.ic_profile)
                        .into(profileImageView);
            }

            // Load additional data from SharedPreferences
            SharedPreferences prefs = getSharedPreferences("VedukaPrefs", MODE_PRIVATE);
            locationEditText.setText(prefs.getString("location", ""));
            phoneEditText.setText(prefs.getString("phone", ""));

            // Password field should be empty for security reasons
            passwordEditText.setText("");
        }
    }

    private void saveUserData() {
        progressBar.setVisibility(View.VISIBLE);

        String name = nameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        // Save to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("VedukaPrefs", MODE_PRIVATE).edit();
        editor.putString("username", name);
        editor.putString("location", location);
        editor.putString("phone", phone);
        editor.apply();

        // Update Firebase profile
        UserProfileChangeRequest.Builder profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name);

        // Update password if provided
        if (!password.isEmpty()) {
            user.updatePassword(password)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this,
                                    "Password update failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Upload image if selected
        if (imageUri != null) {
            uploadImage();
        } else {
            // Just update profile without new image
            user.updateProfile(profileUpdates.build())
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Failed to update profile: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            // Create a file reference with a unique name
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update profile with the download URL
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nameEditText.getText().toString().trim())
                                    .setPhotoUri(uri)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task -> {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(ProfileActivity.this,
                                                    "Failed to update profile: " + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        // Calculate progress percentage
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ use READ_MEDIA_IMAGES permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        READ_STORAGE_PERMISSION_CODE);
            } else {
                openImagePicker();
            }
        } else {
            // For older Android versions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_STORAGE_PERMISSION_CODE);
            } else {
                openImagePicker();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}