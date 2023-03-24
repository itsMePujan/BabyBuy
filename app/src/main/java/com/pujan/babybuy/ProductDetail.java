package com.pujan.babybuy;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductDetail extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailPrice, txtLocation;
    ImageView detailImage;
    FloatingActionButton deleteButton, editButton;
    String key = "";
    String imageUrl = "";
    Button BtnShare, btnShowMap;
    String shareLocation, shareTtl, shareDesc, sharePrice, lat, lang, address, productId;
    FirebaseFirestore firestore;
    boolean ispurchased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        firestore = FirebaseFirestore.getInstance();
        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        detailPrice = findViewById(R.id.detailPrice);
        BtnShare = findViewById(R.id.BtnShare);
        btnShowMap = findViewById(R.id.prdlocation);
        txtLocation = findViewById(R.id.txtLocation);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailPrice.setText(bundle.getString("Price"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            txtLocation.setText(bundle.getString("address"));
            lat = bundle.getString("lat");
            lang = bundle.getString("lang");
            productId = bundle.getString("productId");
            ispurchased= bundle.getBoolean("ispurchased");
            if (!bundle.getString("Image").equals("")){
                Glide.with(this).load(bundle.getString("Image")).into(detailImage);
            }

        }
        shareTtl = bundle.getString("Title");
        shareDesc = bundle.getString("Description");
        sharePrice = bundle.getString("Price");
        shareLocation = bundle.getString("address");

        address = bundle.getString("address");

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BabyBuy");
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                firestore.collection("data").document(productId)
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(ProductDetail.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), DashActivity.class));
                                    finish();
                                }

                            }
                        });
            }
        });


//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BabyBuy");
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
//                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//
//                        Toast.makeText(ProductDetail.this, "Deleted", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(getApplicationContext(), DashActivity.class));
//                        finish();
//                    }
//                });
//            }
//        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetail.this, UpdateProduct.class)
                        .putExtra("Title", detailTitle.getText().toString())
                        .putExtra("Description", detailDesc.getText().toString())
                        .putExtra("Price", detailPrice.getText().toString())
                        .putExtra("Image", imageUrl)
                        .putExtra("lat", lat)
                        .putExtra("productId", productId)
                        .putExtra("address", address)
                        .putExtra("lang", lang)
                        .putExtra("ispurchased",ispurchased)
                        .putExtra("Key", key);
                startActivity(intent);
            }
        });

        BtnShare.setOnClickListener(new View.OnClickListener() { // share data using  messaging app
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                String shareString = Html.fromHtml(
                        "Product Detail<br>Product Name:<b> " + shareTtl + "</b><br>" +
                                "Product Description :<b> " + shareDesc + "</b><br>" +
                                "Product Price:<b> " + sharePrice + "</b><br>" +
                                "<br><>br>You Can find this Product at : " + shareLocation + "</p>"
                ).toString();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Product Detail");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareString);


                if (sharingIntent.resolveActivity(ProductDetail.this.getPackageManager()) != null)
                    ProductDetail.this.startActivity(Intent.createChooser(sharingIntent, "Share using"));
                else {
                    Toast.makeText(ProductDetail.this
                            , "No app found on your phone which can perform this action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(ProductDetail.this,MapActivity.class)
//                        .putExtra("lang",lang)
//                        .putExtra("lat",lat)
//                       .putExtra("from","detail");
//                startActivity(intent);
//            }


                //send product latitude longitude value to map page
                Intent intent = new Intent(ProductDetail.this, MapActivity.class);
                Double latitude, longitude;

                if (bundle != null) {
                    Bundle params = new Bundle();
                    latitude = Double.valueOf(lat);
                    longitude = Double.valueOf(lang);
                    params.putDouble("latitude", latitude);
                    params.putDouble("longitude", longitude);
                    params.putString("page", "detailPage");
                    intent.putExtras(params);
                }
                startActivity(intent);
            }
        });
    }
}