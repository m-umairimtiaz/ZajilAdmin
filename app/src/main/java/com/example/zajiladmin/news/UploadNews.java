package com.example.zajiladmin.news;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.zajiladmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class UploadNews extends AppCompatActivity {
    private static final String TAG = "UploadNews";
    private CardView addImage;
    private ImageView newsImageView;
    private EditText newsTitle;

    private Button uploadNewsBtn;

    private final int REQ = 1;
    private Bitmap bitmap;
    private DatabaseReference reference, dbRef;
    private StorageReference storageReference;
    String downloadUrl = "";
    private ProgressDialog pd;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_news);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("News");


        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        pd = new ProgressDialog(this);

        addImage = findViewById(R.id.addImage);
        newsImageView = findViewById(R.id.newsImageView);
        newsTitle = findViewById(R.id.newsTitle);
        uploadNewsBtn = findViewById(R.id.uploadNewsBtn);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        uploadNewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsTitle.getText().toString().isEmpty()) {
                    newsTitle.setError("Empty");
                    newsTitle.requestFocus();
                } else if (bitmap == null) {
                    uploadData();
                } else {
                    uploadImage();
                }
            }

            private void uploadImage() {
                pd.setMessage("Uploading.......");
                pd.show();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] finalimg = baos.toByteArray();
                final StorageReference filepath;
                Log.d(TAG, "uploadImage: " + finalimg.toString());
                filepath = storageReference.child("News").child(finalimg + "jpg");
                final UploadTask uploadTask = filepath.putBytes(finalimg);
                uploadTask.addOnCompleteListener(UploadNews.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            downloadUrl = String.valueOf(uri);
                                            uploadData();
                                        }
                                    });
                                }
                            });

                        } else {
                            pd.dismiss();
                            Log.d(TAG, "onComplete:error  " + task.getException());
                            Toast.makeText(UploadNews.this, "Something Went Wrong" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            private void uploadData() {
                dbRef = reference.child("News");
                final String uniqueKey = dbRef.push().getKey();

                String title = newsTitle.getText().toString();

                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
                String date = currentDate.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                java.text.SimpleDateFormat currentTime = new java.text.SimpleDateFormat("hh:mm a");
                String time = currentTime.format(calForTime.getTime());

                NewsData newsData = new NewsData(title, downloadUrl, date, time, uniqueKey);

                dbRef.child(uniqueKey).setValue(newsData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pd.dismiss();
                        Toast.makeText(UploadNews.this, "Notice Uploaded", Toast.LENGTH_SHORT).show();
                        newsTitle.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(UploadNews.this, "something went wrong1" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            newsImageView.setImageBitmap(bitmap);
        }
    }

}
