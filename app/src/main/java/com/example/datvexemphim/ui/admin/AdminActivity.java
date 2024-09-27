package com.example.datvexemphim.ui.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datvexemphim.MainActivity;
import com.example.datvexemphim.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {
    private Button btnQuanLyTaiKhoan;
    private Button btnQuanLyPhong;
    private Button btnQuanLyGhe;
    private Button btnQuanLyCombo;
    private Button btnQuanLyVe;
    private Button btnDangXuat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initUI();

        btnQuanLyTaiKhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, QuanLyTaiKhoanActivity.class);
                startActivity(intent);
            }
        });

        btnQuanLyPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, QuanLyPhongActivity.class);
                startActivity(intent);
            }
        });


        btnQuanLyGhe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, QuanLyGheActivity.class);
                startActivity(intent);
            }
        });


        btnQuanLyCombo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, QuanLyComboActivity.class);
                startActivity(intent);
            }
        });


        btnQuanLyVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, QuanLyVeActivity.class);
                startActivity(intent);
            }
        });


        btnDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setTitle("Xác nhận đăng xuất");
                builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện đăng xuất: chuyển đến form đăng nhập
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Kết thúc Activity hiện tại sau khi đăng xuất
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng hộp thoại và không làm gì
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void initUI() {
        btnQuanLyTaiKhoan = findViewById(R.id.btnQuanLyTaiKhoan);
        btnQuanLyPhong = findViewById(R.id.btnQuanLyPhong);
        btnQuanLyGhe = findViewById(R.id.btnQuanLyGhe);
        btnQuanLyCombo = findViewById(R.id.btnQuanLyCombo);
        btnQuanLyVe = findViewById(R.id.btnQuanLyVe);
        btnDangXuat = findViewById(R.id.btnDangXuat);
    }
}
