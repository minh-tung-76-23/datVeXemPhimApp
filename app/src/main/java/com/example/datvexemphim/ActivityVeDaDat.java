package com.example.datvexemphim;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.ui.model.ThanhToan;
import com.example.datvexemphim.adapter.VeDaDatAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityVeDaDat extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VeDaDatAdapter adapter;
    private List<ThanhToan> thanhToanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ve_da_dat);

        // Khởi tạo RecyclerView và Adapter
        recyclerView = findViewById(R.id.recyclerViewVeDaDat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        thanhToanList = new ArrayList<>(); // Khởi tạo danh sách
        adapter = new VeDaDatAdapter(this, thanhToanList);
        recyclerView.setAdapter(adapter);

        // Đọc dữ liệu từ Firebase và thêm vào danh sách
        loadVeXemPhimFromFirebase();
    }

    private void loadVeXemPhimFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // Xử lý khi người dùng chưa đăng nhập
            return;
        }
        String userId = user.getUid(); // Lấy UID của người dùng đang đăng nhập
        String userEmail = user.getEmail();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("qlrapphim/veXemPhim");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ThanhToan thanhToan = snapshot.getValue(ThanhToan.class);
                    if (thanhToan != null && thanhToan.getUserId().equals(userId) && thanhToan.getUserEmail().equals(userEmail)) {
                        thanhToanList.add(thanhToan);
                    }
                }
                adapter.notifyDataSetChanged(); // Cập nhật dữ liệu lên RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ActivityVeDaDat.this, "Lỗi khi đọc dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}