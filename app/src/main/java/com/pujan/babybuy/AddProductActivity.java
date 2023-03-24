package com.pujan.babybuy;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

import java.util.Calendar;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {
    Button saveButton,prdlocation;
    ImageView prdImage;
    EditText  prdTitle,prdDesc,prdPrice;
    String title, desc, price;
    TextView locationTxt;
    String imageUrl = "";
    Uri uri;
    FirebaseAuth auth;
    String lat,lang,address,productId;
    FirebaseFirestore firestore;
    List<DataClass> dataList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        saveButton = findViewById(R.id.prdSave);
        prdlocation = findViewById(R.id.prdlocation);
        prdImage = findViewById(R.id.prdImage);
        prdTitle = findViewById(R.id.prdTitle);
        prdDesc = findViewById(R.id.prdDesc);
        prdPrice = findViewById(R.id.prdPrice);
        locationTxt = findViewById(R.id.locationTxt);
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                           prdImage.setImageURI(uri);
                        } else {
                            Toast.makeText(AddProductActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        // when location button click redirect to map page
        prdlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddProductActivity.this,MapActivity.class);
                startActivityForResult(intent,101);
            }
        });

        prdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        // when save button click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri == null){
                    uploadData(); //if imageurl null
                }else{
                    saveData(); // if imgeurl has value
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101&& resultCode == 201){ //back to this page with map data
            locationTxt.setText(data.getStringExtra("address"));
            lat = data.getStringExtra("latitude");
            lang = data.getStringExtra("longitude");
            address = data.getStringExtra("address");
        }
    }

    public void saveData(){ // add product to firebase storage image
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images")
                .child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }
    public void uploadData(){//add product to firestore
        title = prdTitle.getText().toString().trim();
        desc = prdDesc.getText().toString().trim();
        price = prdPrice.getText().toString();
        String userId = auth.getUid();
        boolean  ispurchased = false;
        productId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        DataClass dataClass = new DataClass(userId ,title, desc, price, imageUrl,lat,lang,address,productId,ispurchased);
        MyAdapter adapter = new MyAdapter(AddProductActivity.this, dataList);

        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        Task<Void> docRef = firestore.collection("data")
                .document(productId)
                .set(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(AddProductActivity.this, "Product added Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddProductActivity.this, DashActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProductActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}