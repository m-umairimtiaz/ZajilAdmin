package com.example.zajiladmin.member;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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


public class AddMember extends AppCompatActivity {
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private ImageView addMemberImage;
    private EditText addMemberName, addMemberPhone, addMemberPost, addEmail;
    private Spinner addMemberCategory;
    private Button addMemberBtn;
    private final int REQ = 1;
    private Bitmap bitmap = null;
    private String category;
    private String name, phone, post, email, downloadUrl = "";
    private ProgressDialog pd;

    private StorageReference storageReference;
    private DatabaseReference reference, dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Member");

        addMemberImage = findViewById(R.id.addMemberImage);
        addMemberName = findViewById(R.id.addMemberName);
        addMemberPhone = findViewById(R.id.addMemberPhone);
        addMemberPost = findViewById(R.id.addMemberPost);
        addEmail = findViewById(R.id.addEmail);

        addMemberCategory = findViewById(R.id.addMemberCategory);
        addMemberBtn = findViewById(R.id.addMemberBtn);

        pd = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference().child("Member");
        storageReference = FirebaseStorage.getInstance().getReference();

        String[] items = new String[]{"Select Category","Sales Department","Technical Department","IT Department","Operations Department"};
        addMemberCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));

        addMemberCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = addMemberCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addMemberImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });

    }
    private void checkValidation() {
        name = addMemberName.getText().toString();
        phone = addMemberPhone.getText().toString();
        post = addMemberPost.getText().toString();
        email = addEmail.getText().toString();

        if(name.isEmpty()){
            addMemberName.setError(("Empty!"));
            addMemberName.requestFocus();
        } else if (phone.isEmpty()) {
            addMemberPhone.setError("Empty.");
        }else if (post.isEmpty()) {
            addMemberPost.setError("Empty.");
        }else if (email.isEmpty()) {
            addEmail.setError("Empty.");
        }else if (category.equals("Select Category")) {
            Toast.makeText(this, "Please Select Member Category", Toast.LENGTH_SHORT).show();
        }else if (bitmap == null) {
            insertData();
        }else {
            uploadImage();
        }
    }

    private void uploadImage() {
        pd.setMessage("Uploading......");
        pd.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Members").child(finalimg+"jpg");
        final UploadTask uploadTask = filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(AddMember.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = String.valueOf(uri);
                                    insertData();

                                }
                            });
                        }
                    });
                }else {
                    pd.dismiss();
                    //Toasty.error(AddTeacher.this, "Something went wrong", Toasty.LENGTH_LONG).show();
                    Toast.makeText(AddMember.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void insertData() {

        dbRef = reference.child(category);
        final String uniqueKey = dbRef.push().getKey();


        MemberData teacherData = new MemberData(name, phone, post, email, downloadUrl, uniqueKey);

        dbRef.child(uniqueKey).setValue(teacherData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                //Toasty.success(AddTeacher.this, "Member Added", Toasty.LENGTH_SHORT).show();
                Toast.makeText(AddMember.this, "Member Succesfully Added", Toast.LENGTH_SHORT).show();
                addMemberName.setText("");
                addMemberPhone.setText("");
                addMemberPost.setText("");
                addEmail.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                //Toasty.error(AddTeacher.this, "Something went wrong", Toasty.LENGTH_SHORT).show();
                Toast.makeText(AddMember.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage,REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ && resultCode == RESULT_OK){
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            addMemberImage.setImageBitmap(bitmap);
        }
    }
}





