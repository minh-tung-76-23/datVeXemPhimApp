package com.example.datvexemphim.ui.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.admin.Adapter.ComboAdapter;
import com.example.datvexemphim.ui.admin.Adapter.UserAdapter;
import com.example.datvexemphim.ui.admin.model.Combo;
import com.example.datvexemphim.ui.admin.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class QuanLyVeActivity extends AppCompatActivity {
    private RecyclerView rcvListve;
    private ComboAdapter comboAdapter;
    private DatabaseReference databaseReference;
    private List<Combo> veList;
    private Button buttonAddVe, buttonExit;
    private ProgressDialog progressDialog;
    private Dialog addComboDialog;
    private ImageView imageViewAddImageCombo;
    private ImageView imageViewCombo;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private boolean isAddingCombo = false;

    // Dialog hiển thị thông tin tài khoản
    private AlertDialog dialogComboInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_ve);

        progressDialog = new ProgressDialog(this);
        buttonAddVe = findViewById(R.id.buttonAddVe);
        buttonExit = findViewById(R.id.buttonExit);

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuanLyVeActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });

        buttonAddVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddComboDialog();
            }
        });

        // Initialize RecyclerView and ComboAdapter
        rcvListve = findViewById(R.id.rcv_list_ve);
        rcvListve.setLayoutManager(new LinearLayoutManager(this));
        veList = new ArrayList<>();
        comboAdapter = new ComboAdapter(this, veList);
        rcvListve.setAdapter(comboAdapter);

        comboAdapter.setOnItemClickListener(new ComboAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Combo combo) {
                showComboDetails(combo);
            }
        });

        // Connect to Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("qlrapphim").child("giaVe");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                veList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Combo combo = snapshot.getValue(Combo.class);
                    if (combo != null) {
                        veList.add(combo);
                    }
                }
                comboAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("QuanLyComboActivity", "Error fetching combo data: " + databaseError.getMessage());
            }
        });
    }

    private void showComboDetails(Combo combo) {
        isAddingCombo = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_info_qlve, null);
        builder.setView(dialogView);

        // Đổ dữ liệu vào các EditText trong dialog
        EditText idVe = dialogView.findViewById(R.id.idVe);
        EditText loaiVe = dialogView.findViewById(R.id.loaiVe);
        EditText giaVe = dialogView.findViewById(R.id.giaVe);


        idVe.setText(combo.getIdCombo());
        loaiVe.setText(combo.getTenCombo());
        giaVe.setText(String.valueOf(combo.getGiaCombo()));


        // Tạo và hiển thị dialog
        dialogComboInfo = builder.create();
        dialogComboInfo.show();

        // Thiết lập sự kiện cho các button trong dialog
        Button btnXoa = dialogView.findViewById(R.id.btnXoa);
        Button btnSua = dialogView.findViewById(R.id.btnSua);
        Button btnThoat = dialogView.findViewById(R.id.btnThoat);


        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng dialog
                dialogComboInfo.dismiss();
            }
        });

        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Đang kiểm tra...");
                progressDialog.show();
                // Thực hiện sửa combo trên Realtime Database
                String idv = idVe.getText().toString();
                String tenv = loaiVe.getText().toString();
                String giaCb = giaVe.getText().toString();
                int giav = Integer.parseInt(giaCb);
                //upDateCombo(idv, tenv, giav);

            }
        });


        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị AlertDialog xác nhận xóa
                AlertDialog.Builder builder = new AlertDialog.Builder(QuanLyVeActivity.this);
                builder.setTitle("Xác nhận xóa");
                builder.setMessage("Bạn có chắc chắn muốn xóa vé này không?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện xóa combo trên Realtime Database
                        String idCb = idVe.getText().toString();
                        // Thực hiện thêm tài khoản vào Firebase Authentication và Firestore
                        progressDialog.setMessage("Đang kiểm tra...");
                        progressDialog.show();

                        delCombo(idCb);
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng dialog và không làm gì cả
                        dialog.dismiss();
                    }
                });

                // Hiển thị AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    private void delCombo(String idVe) {
        // Tham chiếu đến node Combos trên Realtime Database
        // Xóa dữ liệu của combo
        databaseReference.child(idVe).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Xóa thành công
                        progressDialog.dismiss();
                        Toast.makeText(QuanLyVeActivity.this, "Đã xóa vé thành công", Toast.LENGTH_SHORT).show();
                        dialogComboInfo.dismiss(); // Đóng dialog sau khi xóa thành công

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xóa thất bại
                        progressDialog.dismiss();
                        Toast.makeText(QuanLyVeActivity.this, "Xóa vé thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showAddComboDialog() {
        isAddingCombo = true;
        // Initialize dialog
        addComboDialog = new Dialog(QuanLyVeActivity.this);
        addComboDialog.setContentView(R.layout.layout_dialog_add_ve);
        addComboDialog.setCancelable(true); // Set if you want the dialog to be canceled when tapping outside

        // Initialize views inside dialog
        EditText addIdVe = addComboDialog.findViewById(R.id.addIdVe);
        EditText addLoaiVe = addComboDialog.findViewById(R.id.addLoaiVe);
        EditText addGiaVe = addComboDialog.findViewById(R.id.addGiaVe);
        Button dialogButtonOK = addComboDialog.findViewById(R.id.btnThem);
        Button dialogButtonCancel = addComboDialog.findViewById(R.id.btnThoat);


        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle cancel button click inside dialog
                addComboDialog.dismiss();
            }
        });

        dialogButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy các giá trị từ EditText
                String idv = addIdVe.getText().toString().trim();
                String tenv = addLoaiVe.getText().toString().trim();
                String giaveStr = addGiaVe.getText().toString().trim();

                // Validate input
                if (idv.isEmpty() || tenv.isEmpty() || giaveStr.isEmpty() ) {
                    Toast.makeText(QuanLyVeActivity.this, "Vui lòng điền đầy đủ thông tin ", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Thực hiện thêm tài khoản vào Firebase Authentication và Firestore
                progressDialog.setMessage("Đang thêm vé...");
                progressDialog.show();

                // Chuyển đổi giá combo từ string sang int
                int giaCombo = Integer.parseInt(giaveStr);

                // Dismiss dialog
                addComboDialog.dismiss();
            }
        });

        // Show dialog
        addComboDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
        }
    }

}
