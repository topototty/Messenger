package com.example.messengerapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView avatarImageView;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText nicknameEditText;
    private Button uploadAvatarButton;
    private Button updateProfileButton;
    private Button logoutButton; // Добавляем кнопку выхода
    private Uri imageUri;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        avatarImageView = view.findViewById(R.id.avatarImageView);
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        nicknameEditText = view.findViewById(R.id.nicknameEditText);
        uploadAvatarButton = view.findViewById(R.id.uploadAvatarButton);
        updateProfileButton = view.findViewById(R.id.updateProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton); // Инициализируем кнопку выхода

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("avatars");

        avatarImageView.setOnClickListener(v -> openFileChooser());
        uploadAvatarButton.setOnClickListener(v -> uploadImage());
        updateProfileButton.setOnClickListener(v -> updateProfile());
        logoutButton.setOnClickListener(v -> logoutUser()); // Устанавливаем слушатель для кнопки выхода

        loadUserProfile();

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                avatarImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                StorageReference fileReference = storageReference.child(userId + ".jpg");

                fileReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            updateUserProfileImage(downloadUrl);
                        }))
                        .addOnFailureListener(e -> Toast.makeText(getActivity(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getActivity(), "User is not authenticated", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserProfileImage(String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Users").document(userId);

            userRef.update("avatarUrl", imageUrl)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Profile image updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to update profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void loadUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Users").document(userId);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String avatarUrl = documentSnapshot.getString("avatarUrl");
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");
                    String nickname = documentSnapshot.getString("nickname");

                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Picasso.get().load(avatarUrl).into(avatarImageView);
                    }
                    if (firstName != null) {
                        firstNameEditText.setText(firstName);
                    }
                    if (lastName != null) {
                        lastNameEditText.setText(lastName);
                    }
                    if (nickname != null) {
                        nicknameEditText.setText(nickname);
                    }
                }
            }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to load profile", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Users").document(userId);

            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String nickname = nicknameEditText.getText().toString();

            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(nickname)) {
                Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            userRef.update("firstName", firstName,
                            "lastName", lastName,
                            "nickname", nickname,
                            "updatedAt", FieldValue.serverTimestamp())
                    .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish(); // Закрываем текущую активность
    }
}
