package com.example.assessment04;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private CircleImageView profilePicture;
    private EditText fullNameInput, emailInput, phoneInput, addressInput;
    private Button uploadPictureButton, saveProfileButton;

    private String email = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Adjust layout to handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = getIntent().getStringExtra("email");

        // Initialize Views
        profilePicture = findViewById(R.id.profile_picture);
        fullNameInput = findViewById(R.id.full_name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        addressInput = findViewById(R.id.address_input);
        uploadPictureButton = findViewById(R.id.upload_picture_button);
        saveProfileButton = findViewById(R.id.save_profile_button);

        // Handle Profile Picture Upload
        uploadPictureButton.setOnClickListener(v -> openFileChooser());

        // Save Profile Data
        saveProfileButton.setOnClickListener(v -> saveProfileData());

        emailInput.setText(email);

        // Load Existing Profile Data
        loadProfileData();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri); // Set the selected image in the ImageView
        }
    }

    private void saveProfileData() {
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim(); // Make sure this is the logged-in user's email
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save image to internal storage and get the file path
        String imagePath = null;
        if (imageUri != null) {
            imagePath = saveImageToInternalStorage(imageUri);
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean success = dbHelper.saveProfile(fullName, email, phone, address, imagePath);

        if (success) {
            Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
            Log.d("DatabaseHelper", "Saving profile with image path: " + imageUri);

        } else {
            Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("Range")
    private void loadProfileData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getProfileFor(email);

        if (cursor != null && cursor.moveToFirst()) {
            fullNameInput.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FULL_NAME)));
            emailInput.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL_PROFILE)));
            phoneInput.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE)));
            addressInput.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS)));

            String imagePath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_URI));
            Log.d("ProfileActivity", "Retrieved image path: " + imagePath);

            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    imageUri = Uri.fromFile(new File(imagePath));
                    profilePicture.setImageBitmap(bitmap);
                    Log.d("ProfileActivity", "Image loaded successfully from: " + imagePath);
                } else {
                    Log.e("ProfileActivity", "Image file does not exist at: " + imagePath);
                    profilePicture.setImageResource(R.drawable.baseline_account_circle_24); // Fallback image
                }
            } else {
                profilePicture.setImageResource(R.drawable.baseline_account_circle_24); // Fallback image
            }

            cursor.close();
        }
    }



    @Nullable
    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            File file = new File(getFilesDir(), "profile_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            Log.d("ProfileActivity", "Image saved at: " + file.getAbsolutePath());
            return file.getAbsolutePath(); // Return the file path
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ProfileActivity", "Failed to save image: " + e.getMessage());
            return null;
        }
    }

}
