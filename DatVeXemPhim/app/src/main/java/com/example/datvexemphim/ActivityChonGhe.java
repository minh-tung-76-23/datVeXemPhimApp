package com.example.datvexemphim;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.adapter.SeatAdapter;
import com.example.datvexemphim.ui.model.Seat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityChonGhe extends AppCompatActivity {

    private TextView textFilmName, textCinemaName, textTimeDate;
    private RecyclerView recyclerViewSeats;
    private SeatAdapter seatAdapter;
    private List<Seat> seatList;

    private static final String TAG = ActivityChonGhe.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chon_ghe);

        // Khởi tạo các TextView
        textFilmName = findViewById(R.id.textfilmName);
        textCinemaName = findViewById(R.id.textCinemaName);
        textTimeDate = findViewById(R.id.textTime);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            String nameFilm = intent.getStringExtra("name_film");
            String cinemaName = intent.getStringExtra("cinema_name");
            String selectedTimeDate = intent.getStringExtra("selected_datetime");
            String selectedTime = intent.getStringExtra("selected_time");
            String textTime = selectedTime + " | " + selectedTimeDate;

            // Hiển thị dữ liệu lên TextViews
            textFilmName.setText(nameFilm);
            textCinemaName.setText(cinemaName);
            textTimeDate.setText(textTime);

            // Khởi tạo RecyclerView và Adapter
            recyclerViewSeats = findViewById(R.id.recyclerViewSeats);
            recyclerViewSeats.setLayoutManager(new LinearLayoutManager(this));
            seatList = new ArrayList<>();
            seatAdapter = new SeatAdapter(this, seatList);
            recyclerViewSeats.setAdapter(seatAdapter);

            // Kết nối đến Firebase Realtime Database
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("qlrapphim").child("gheNgoi");
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    seatList.clear(); // Xóa danh sách ghế cũ
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getValue() != null) {
                            Seat seat = snapshot.getValue(Seat.class);
                            seatList.add(seat);
                        }
                    }
                    seatAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView khi thay đổi dữ liệu
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi đọc dữ liệu từ Firebase
                    Toast.makeText(ActivityChonGhe.this, "Lỗi đọc dữ liệu từ Firebase: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}


