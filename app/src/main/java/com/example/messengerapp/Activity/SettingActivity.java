package com.example.messengerapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messengerapp.ModelClass.Users;
import com.example.messengerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    CircleImageView setting_image;
    EditText setting_name, setting_status;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ImageView save;
    Uri selctedImageUri;
    String email;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        setting_image = findViewById(R.id.setting_image);
        setting_name = findViewById(R.id.setting_name);
        setting_status = findViewById(R.id.setting_status);
        save=findViewById(R.id.save);


        DatabaseReference reference=database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
        StorageReference storageReference=storage.getReference().child("uplod").child(auth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               email= Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                String name= Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String status= Objects.requireNonNull(snapshot.child("status").getValue()).toString();
                String image=snapshot.child("imageUri").getValue().toString();

                setting_name.setText(name);
                setting_status.setText(status);
                Picasso.get().load(image).into(setting_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        save.setOnClickListener(v -> {

            progressDialog.show();

            String name=setting_name.getText().toString();
            String status=setting_status.getText().toString();

            if (selctedImageUri != null)
            {
                storageReference.putFile(selctedImageUri).addOnCompleteListener(task -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String finalImageUri=uri.toString();
                    Users users=new Users(auth.getUid(),name,email,finalImageUri,status);

                    reference.setValue(users).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SettingActivity.this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SettingActivity.this,HomeActivity.class));
                        }
                        else {
                             progressDialog.dismiss();

                            Toast.makeText(SettingActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }));
            }
            else {

                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String finalImageUri=uri.toString();
                    Users users=new Users(auth.getUid(),name,email,finalImageUri,status);

                    reference.setValue(users).addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SettingActivity.this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SettingActivity.this,HomeActivity.class));
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(SettingActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }

        });

        setting_image.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10)
        {
            if(data!=null)
            {
                selctedImageUri =data.getData();
                setting_image.setImageURI(selctedImageUri);
            }
        }
    }
}