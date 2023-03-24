package com.pujan.babybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    public static int Splash_Timer=2000; //splash timer (loading time)
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (auth.getCurrentUser() !=null){
                    //if user already logged in redirected to users homepage
                    Intent intent = new Intent(SplashActivity.this, DashActivity.class);
                    Toast.makeText(SplashActivity.this, "User Already LoggedIn!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }else{
                    // if not loggedin redirected to login page
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, Splash_Timer);
    }
}