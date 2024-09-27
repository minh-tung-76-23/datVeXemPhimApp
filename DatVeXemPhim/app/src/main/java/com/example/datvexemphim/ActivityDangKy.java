package com.example.datvexemphim;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivityDangKy extends AppCompatActivity {
    EditText editTextEmail, editTextPassword, editTextName, editTextRepassWord;
    Button buttonThoat, buttonDangKy;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ky);

        initUi();
        initListener();
    }

    private void initUi() {
        buttonThoat = findViewById(R.id.buttonThoat);
        buttonDangKy = findViewById(R.id.buttonDangKy);
        editTextEmail = findViewById(R.id.edTEmail);
        editTextPassword = findViewById(R.id.edTPass);
        editTextName = findViewById(R.id.edTHoten);
        editTextRepassWord = findViewById(R.id.edTRePass);

        progressDialog = new ProgressDialog(this);
    }

    private void initListener() {
        buttonDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUp();
            }
        });

        // Thiết lập sự kiện click cho buttonThoat
        buttonThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void onClickSignUp() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String repassword = editTextRepassWord.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || repassword.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(repassword)) {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore store = FirebaseFirestore.getInstance();
        progressDialog.setMessage("Đang đăng ký...");
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(ActivityDangKy.this, "Đăng ký thành công.",
                                    Toast.LENGTH_SHORT).show();
                            DocumentReference userRef = store.collection("users").document(user.getUid());
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("fullName", editTextName.getText().toString());
                            userData.put("email", editTextEmail.getText().toString());
                            userData.put("soDienThoai", "");
                            userData.put("avatar", "");
                            userData.put("ngaySinh", "");
                            userData.put("isUser", "1");
                            userRef.set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + userRef.getId());
                                            Intent intent = new Intent(ActivityDangKy.this, MainActivity.class);
                                            startActivity(intent);
                                            finishAffinity();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                            Toast.makeText(ActivityDangKy.this, "Lỗi khi lưu dữ liệu người dùng: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(ActivityDangKy.this, "Đăng ký thất bại: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
