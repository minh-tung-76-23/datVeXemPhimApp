package com.example.datvexemphim.ui.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.admin.Adapter.VeAdapter;
import com.example.datvexemphim.ui.admin.model.VeXemPhim;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuanLyVeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VeAdapter adapter;
    private List<VeXemPhim> veXemPhimList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quan_ly_ve);

        // Khởi tạo RecyclerView và Adapter
        recyclerView = findViewById(R.id.recyclerViewVeHotCinema);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        veXemPhimList = new ArrayList<>(); // Khởi tạo danh sách
        adapter = new VeAdapter(this, veXemPhimList);
        recyclerView.setAdapter(adapter);

        // Đọc dữ liệu từ Firebase và thêm vào danh sách
        loadVeXemPhimFromFirebase();
    }

    private void loadVeXemPhimFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("qlrapphim/veXemPhim");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                veXemPhimList.clear(); // Xóa danh sách hiện tại để tránh trùng lặp dữ liệu
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    VeXemPhim veXemPhim = snapshot.getValue(VeXemPhim.class);
                    veXemPhim.setKey(snapshot.getKey()); // Thiết lập key cho VeXemPhim
                    veXemPhimList.add(veXemPhim);
                }
                adapter.notifyDataSetChanged(); // Cập nhật dữ liệu lên RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuanLyVeActivity.this, "Lỗi khi đọc dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

}