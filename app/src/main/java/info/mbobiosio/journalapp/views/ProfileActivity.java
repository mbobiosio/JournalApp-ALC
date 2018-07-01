package info.mbobiosio.journalapp.views;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import info.mbobiosio.journalapp.R;
import info.mbobiosio.journalapp.model.ProfileModel;
import info.mbobiosio.journalapp.utils.Constants;


public class ProfileActivity extends BaseActivity {

    @BindView(R.id.avatar)
    CircleImageView mAvatar;
    @BindView(R.id.username)
    TextInputEditText mUsername;
    @BindView(R.id.email)
    TextInputEditText mEmail;
    @BindView(R.id.update)
    FloatingActionButton mUpdateProfile;

    private Uri resultUri = null;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, mProfile;
    private String mUid;

    private StorageReference mStorageReference, imagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        initFirebase();
        initUI();

        mUpdateProfile.setOnClickListener(v -> {
            updateProfile();
        });

        mAvatar.setOnClickListener(v -> {
            cropImage();
        });

        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            mEmail.setText(mUser.getEmail());
            mProfile.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        ProfileModel profileModel = dataSnapshot.getValue(ProfileModel.class);
                        mUsername.setText(profileModel.getUsername());
                        try {
                            imagesRef = mStorageReference.child(profileModel.getPhotoUrl());
                            imagesRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(ProfileActivity.this).load(uri).into(mAvatar))
                                    .addOnFailureListener(e -> Glide.with(ProfileActivity.this).load(Constants.DEFAULT_AVATAR).into(mAvatar));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

    }

    public void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProfile = mDatabase.child(Constants.USERS);
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    public void cropImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager
                    .PERMISSION_GRANTED) {
                errorToast(getString(R.string.storage_permission_denied));
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]
                        {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(ProfileActivity.this);
            }
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(ProfileActivity.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                mAvatar.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public void updateProfile() {
        if (mUsername.getText().toString().isEmpty()) {
            errorToast(getString(R.string.add_username));
            return;
        }
        if (resultUri == null) {
            errorToast(getString(R.string.no_image));
            return;
        }
        if (!TextUtils.isEmpty(mUsername.getText()) && resultUri != null) {
            mProgress.show();
            StorageReference filepath = mStorageReference.child(Constants.AVATAR).child(mUid + ".jpg");
            filepath.putFile(resultUri).addOnSuccessListener(taskSnapshot -> {
                String downloadUrl = taskSnapshot.getMetadata().getPath();
                String username = mUsername.getText().toString();
                Map<String, Object> mUserProfile = new HashMap<>();
                mUserProfile.put("avi", downloadUrl);
                mUserProfile.put("time", ServerValue.TIMESTAMP);
                mUserProfile.put("username", username);
                mProfile.child(mUid).setValue(mUserProfile).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        successToast(getString(R.string.update_profile_successfully));
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        errorToast(getString(R.string.update_profile_failed));
                    }
                });
            });
        } else {
            errorToast(getString(R.string.oops_error));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mUid = user.getUid();
        } else {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
