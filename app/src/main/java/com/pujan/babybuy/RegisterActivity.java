package com.pujan.babybuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    TextView txtLogin;
    EditText  signupEmail , signupPassword,signupPhone,signupName;
    Button registerBtn;
    FirebaseDatabase database;
    FirebaseFirestore firestore;
    List<UserClass> dataList;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
       // getSupportActionBar().hide();

        txtLogin = findViewById(R.id.txtLogin);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");
        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        registerBtn = findViewById(R.id.registerBtn);
        signupName = findViewById(R.id.signupName);
        signupPhone = findViewById(R.id.signupPhone);

        firestore = FirebaseFirestore.getInstance();

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(inte);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String name = signupName.getText().toString().trim();
                String phone = signupPhone.getText().toString().trim();
                if (user.isEmpty()){ // check whether its empty or not
                    signupEmail.setError("Email cannot be empty");
                }
                if (name.isEmpty()){// check whether its empty or not
                    signupName.setError("Name cannot be empty");
                }
                if (phone.isEmpty()){// check whether its empty or not
                    signupPhone.setError("Phone cannot be empty");
                }
                if (pass.isEmpty()){// check whether its empty or not
                    signupPassword.setError("Password cannot be empty");
                } else{
                    // first register on fireauth and  user data to firestore
                  auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          if (task.isSuccessful()) {
                              String userid = auth.getUid();
                              UserClass dataClass = new UserClass(userid,name,phone,user);
                              Task<Void> docRef = firestore.collection("userInfo")
                                      .document(auth.getUid())
                                      .set(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() { // set data for firestore
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) { // sigup successful
                                              Toast.makeText(RegisterActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                              startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                          }
                                      }).addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                              Toast.makeText(RegisterActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                          }
                                      });
                          } else {
                              Toast.makeText(RegisterActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                          }
                      }
                  });
                }
            }
        });
    }
}