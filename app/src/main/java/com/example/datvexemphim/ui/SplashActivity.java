package com.example.datvexemphim.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datvexemphim.ActivityHome;
import com.example.datvexemphim.ui.admin.AdminActivity;
import com.example.datvexemphim.MainActivity;
import com.example.datvexemphim.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        }, 2000);
    }

    private void checkUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Lấy giá trị của isUser từ Firestore
                        String isUser = document.getString("isUser");
                        if (isUser != null && isUser.equals("0")) {
                            // Nếu isUser = 0, chuyển sang AdminActivity
                            Intent intent = new Intent(SplashActivity.this, AdminActivity.class);
                            startActivity(intent);
                        } else {
                            // Mặc định hoặc isUser != 0, chuyển sang MainActivity
                            Intent intent = new Intent(SplashActivity.this, ActivityHome.class);
                            startActivity(intent);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                        // Nếu không có tài liệu, chuyển sang MainActivity
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    // Nếu có lỗi, chuyển sang MainActivity
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            });
        } else {
            // Nếu user = null, chuyển sang MainActivity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
