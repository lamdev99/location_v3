package com.example.location_v4;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class FirebaseService {
    private final String TAG = FirebaseService.class.getSimpleName();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    void saveFileToFirebase(Context context,String filePath,String fileName) {
        StorageReference riversRef = storageRef.child(fileName);
        Uri file = Uri.fromFile(new File(filePath));
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(exception -> {
            Log.e(TAG,exception.toString());
        }).addOnSuccessListener(taskSnapshot -> Toast.makeText(context, "File upload successfully", Toast.LENGTH_SHORT).show());
    }
}
