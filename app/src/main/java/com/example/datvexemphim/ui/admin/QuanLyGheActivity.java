package com.example.datvexemphim.ui.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.admin.Adapter.LoaiGheAdapter;
import com.example.datvexemphim.ui.admin.Adapter.SeatAdapter;
import com.example.datvexemphim.ui.admin.Adapter.SpinnerAdapter;
import com.example.datvexemphim.ui.admin.model.GheNgoi;
import com.example.datvexemphim.ui.admin.model.PhongChieu;
import com.example.datvexemphim.ui.admin.model.loaiGhe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuanLyGheActivity extends AppCompatActivity {
    private RecyclerView recyclerViewLoaiGhe, recyclerViewGheNgoi;
    private LoaiGheAdapter adapter;
    private SeatAdapter seatAdapter;
    private Spinner spinnerPhong;
    private List<PhongChieu> phongChieuList;
    private List<GheNgoi> gheNgoiList;
    private SpinnerAdapter spinnerAdapter;
    private Button upDateTinhTrangGhe;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quan_ly_ghe);
        progressDialog = new ProgressDialog(this);
        loadLoaiGhe();
        loadPhongChieu();
        loadGheNgoiFromFirebase();

        upDateTinhTrangGhe = findViewById(R.id.btnUpdateTinhTrangGhe);
        upDateTinhTrangGhe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(QuanLyGheActivity.this);
                builder.setTitle("Xác nhận cập nhật");
                builder.setMessage("Bạn có chắc chắn muốn cập nhật tình trạng ghế thành 'trống'?");
                builder.setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog.setMessage("Đang cập nhật...");
                        progressDialog.show();

                        DatabaseReference gheNgoiRef = FirebaseDatabase.getInstance().getReference().child("qlrapphim").child("gheNgoi");

                        gheNgoiRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String gheKey = snapshot.getKey();
                                    gheNgoiRef.child(gheKey).child("tinhTrang").setValue("trống")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Cập nhật thành công, có thể thông báo hoặc làm gì đó ở đây
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Xảy ra lỗi khi cập nhật
                                                    Toast.makeText(QuanLyGheActivity.this, "Failed to update seat status.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                                // Sau khi hoàn thành cập nhật, thông báo là đã cập nhật thành công
                                loadGheNgoiFromFirebase(); // Cập nhật lại danh sách ghế ngồi
                                progressDialog.dismiss(); // Đóng ProgressDialog khi hoàn thành
                                Toast.makeText(QuanLyGheActivity.this, "Đã cập nhật tình trạng thành 'trống'.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu
                                Toast.makeText(QuanLyGheActivity.this, "Failed to load seat data.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss(); // Đóng ProgressDialog nếu có lỗi
                            }
                        });
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Hủy bỏ thao tác cập nhật nếu người dùng chọn hủy
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

    }

    private void loadPhongChieu() {
        spinnerPhong = findViewById(R.id.spinnerPhong);
        phongChieuList = new ArrayList<>();
        spinnerAdapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, phongChieuList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhong.setAdapter(spinnerAdapter);

        // Lấy dữ liệu từ Firebase Realtime Database
        FirebaseDatabase.getInstance().getReference().child("qlrapphim").child("phongChieu")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        phongChieuList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            PhongChieu phongChieu = snapshot.getValue(PhongChieu.class);
                            if (phongChieu != null) {
                                phongChieuList.add(phongChieu);
                            }
                        }
                        spinnerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý khi có lỗi xảy ra
                        Toast.makeText(QuanLyGheActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadLoaiGhe() {
        recyclerViewLoaiGhe = findViewById(R.id.recyclerViewLoaiGhe);
        recyclerViewLoaiGhe.setLayoutManager(new LinearLayoutManager(this));
        // Lắng nghe sự thay đổi trong Firebase Realtime Database
        FirebaseDatabase.getInstance().getReference().child("qlrapphim/loaiGhe")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Lấy dữ liệu từ snapshot và cập nhật vào RecyclerView
                        List<loaiGhe> loaiGheList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            loaiGhe item = snapshot.getValue(loaiGhe.class);
                            loaiGheList.add(item);
                        }
                        adapter = new LoaiGheAdapter(loaiGheList, QuanLyGheActivity.this);
                        recyclerViewLoaiGhe.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xử lý khi có lỗi xảy ra
                    }
                });
    }

    private void loadGheNgoiFromFirebase() {
        recyclerViewGheNgoi = findViewById(R.id.recyclerViewGheNgoi);
        recyclerViewGheNgoi.setLayoutManager(new LinearLayoutManager(this));
        gheNgoiList = new ArrayList<>();
        seatAdapter = new SeatAdapter(QuanLyGheActivity.this, gheNgoiList);
        recyclerViewGheNgoi.setAdapter(seatAdapter);

        // Lấy dữ liệu từ Firebase Realtime Database
        FirebaseDatabase.getInstance().getReference().child("qlrapphim").child("gheNgoi")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        gheNgoiList.clear(); // Xóa danh sách cũ trước khi thêm mới
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            GheNgoi gheNgoi = snapshot.getValue(GheNgoi.class);
                            gheNgoiList.add(gheNgoi);
                        }
                        seatAdapter.notifyDataSetChanged(); // Thông báo cho adapter là dữ liệu đã thay đổi
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý khi có lỗi xảy ra
                        Toast.makeText(QuanLyGheActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
