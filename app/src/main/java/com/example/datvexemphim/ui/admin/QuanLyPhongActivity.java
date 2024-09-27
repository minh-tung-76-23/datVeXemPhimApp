package com.example.datvexemphim.ui.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.admin.Adapter.PhongAdapter;
import com.example.datvexemphim.ui.admin.model.Phong;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuanLyPhongActivity extends AppCompatActivity {
    private RecyclerView rcvListphong;
    private PhongAdapter phongAdapter;
    private DatabaseReference databaseReference;
    private List<Phong> phongList;
    private Button buttonAddPhong, buttonExit;
    private ProgressDialog progressDialog;
    private Dialog addPhongDialog;
    private AlertDialog dialogPhongInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_phong);

        progressDialog = new ProgressDialog(this);
        buttonAddPhong = findViewById(R.id.buttonAddPhong);
        buttonExit = findViewById(R.id.buttonExit);

        buttonExit.setOnClickListener(view -> {
            Intent intent = new Intent(QuanLyPhongActivity.this, AdminActivity.class);
            startActivity(intent);
        });

        buttonAddPhong.setOnClickListener(view -> showAddPhongDialog());

        // Initialize RecyclerView and PhongAdapter
        rcvListphong = findViewById(R.id.rcv_list_phong);
        rcvListphong.setLayoutManager(new LinearLayoutManager(this));
        phongList = new ArrayList<>();
        phongAdapter = new PhongAdapter(this, phongList);
        rcvListphong.setAdapter(phongAdapter);

        phongAdapter.setOnItemClickListener(this::showPhongDetails);

        // Connect to Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("qlrapphim").child("phongChieu");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phongList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Phong phong = snapshot.getValue(Phong.class);
                    if (phong != null) {
                        phongList.add(phong);
                        Log.d("FirebaseData", "Phong ID: " + phong.getIdPhong());  // Kiểm tra xem dữ liệu có được tải lên đúng không
                    }
                }
                phongAdapter.notifyDataSetChanged();  // Cập nhật RecyclerView khi có dữ liệu mới
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("QuanLyPhongActivity", "Error fetching data: " + databaseError.getMessage());
            }
        });

    }

    private void showPhongDetails(Phong phong) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_info_phong, null);
        builder.setView(dialogView);

        // Populate data into EditText fields
        EditText idPhong = dialogView.findViewById(R.id.idPhong);
        EditText soDay = dialogView.findViewById(R.id.soday);
        EditText soCot = dialogView.findViewById(R.id.socot);

        idPhong.setText(phong.getIdPhong());
        soDay.setText(String.valueOf(phong.getsoDay()));
        soCot.setText(String.valueOf(phong.getsoCot()));

        dialogPhongInfo = builder.create();
        dialogPhongInfo.show();

        // Button actions in the dialog
        Button btnXoa = dialogView.findViewById(R.id.btnXoa);
        Button btnSua = dialogView.findViewById(R.id.btnSua);
        Button btnThoat = dialogView.findViewById(R.id.btnThoat);

        btnThoat.setOnClickListener(v -> dialogPhongInfo.dismiss());

        btnSua.setOnClickListener(v -> {
            String idp = idPhong.getText().toString();
            String sod = soDay.getText().toString();
            String soc = soCot.getText().toString();

            if (idp.isEmpty() || sod.isEmpty() || soc.isEmpty()) {
                Toast.makeText(QuanLyPhongActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.setMessage("Đang cập nhật...");
            progressDialog.show();

            int sodayInt = Integer.parseInt(sod);
            int socotInt = Integer.parseInt(soc);

            updatePhong(idp, sodayInt, socotInt);
        });

        btnXoa.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(QuanLyPhongActivity.this);
            builder1.setTitle("Xác nhận xóa");
            builder1.setMessage("Bạn có chắc chắn muốn xóa phòng này không?");
            builder1.setPositiveButton("OK", (dialog, which) -> {
                String id = idPhong.getText().toString();
                progressDialog.setMessage("Đang xóa...");
                progressDialog.show();
                deletePhong(id);
            });
            builder1.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder1.create();
            alertDialog.show();
        });
    }

    private void updatePhong(String idPhong, int soDay, int soCot) {
        Phong updatedPhong = new Phong(idPhong, soDay, soCot);
        databaseReference.child(idPhong).setValue(updatedPhong)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(QuanLyPhongActivity.this, "Cập nhật phòng thành công", Toast.LENGTH_SHORT).show();
                    dialogPhongInfo.dismiss();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(QuanLyPhongActivity.this, "Cập nhật phòng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deletePhong(String idPhong) {
        databaseReference.child(idPhong).removeValue()
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(QuanLyPhongActivity.this, "Đã xóa phòng thành công", Toast.LENGTH_SHORT).show();
                    dialogPhongInfo.dismiss();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(QuanLyPhongActivity.this, "Xóa phòng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showAddPhongDialog() {
        addPhongDialog = new Dialog(QuanLyPhongActivity.this);
        addPhongDialog.setContentView(R.layout.layout_dialog_add_phong);
        addPhongDialog.setCancelable(true);

        EditText addIdPhong = addPhongDialog.findViewById(R.id.addIdPhong);
        EditText addSoDay = addPhongDialog.findViewById(R.id.addsoDay);
        EditText addSoCot = addPhongDialog.findViewById(R.id.addsoCot);
        Button dialogButtonOK = addPhongDialog.findViewById(R.id.btnThem);
        Button dialogButtonCancel = addPhongDialog.findViewById(R.id.btnThoat);

        dialogButtonCancel.setOnClickListener(v -> addPhongDialog.dismiss());

        dialogButtonOK.setOnClickListener(v -> {
            String id = addIdPhong.getText().toString().trim();
            String soDayStr = addSoDay.getText().toString().trim();
            String soCotStr = addSoCot.getText().toString().trim();

            if (id.isEmpty() || soDayStr.isEmpty() || soCotStr.isEmpty()) {
                Toast.makeText(QuanLyPhongActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int soDay = Integer.parseInt(soDayStr);
            int soCot = Integer.parseInt(soCotStr);

            progressDialog.setMessage("Đang thêm phòng...");
            progressDialog.show();
            addPhong(id, soDay, soCot);
            addPhongDialog.dismiss();
        });

        addPhongDialog.show();
    }

    private void addPhong(String id, int soDay, int soCot) {
        Phong newPhong = new Phong(id, soDay, soCot);
        databaseReference.child(id).setValue(newPhong)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(QuanLyPhongActivity.this, "Thêm phòng thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(QuanLyPhongActivity.this, "Thêm phòng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
