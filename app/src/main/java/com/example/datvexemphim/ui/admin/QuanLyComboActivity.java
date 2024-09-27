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

public class QuanLyComboActivity extends AppCompatActivity {
    private RecyclerView rcvListCombo;
    private ComboAdapter comboAdapter;
    private DatabaseReference databaseReference;
    private List<Combo> comboList;
    private Button buttonAddCombo, buttonExit;
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
        setContentView(R.layout.activity_quan_ly_combo);

        progressDialog = new ProgressDialog(this);
        buttonAddCombo = findViewById(R.id.buttonAddCombo);
        buttonExit = findViewById(R.id.buttonExit);

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuanLyComboActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });

        buttonAddCombo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddComboDialog();
            }
        });

        // Initialize RecyclerView and ComboAdapter
        rcvListCombo = findViewById(R.id.rcv_list_combo);
        rcvListCombo.setLayoutManager(new LinearLayoutManager(this));
        comboList = new ArrayList<>();
        comboAdapter = new ComboAdapter(this, comboList);
        rcvListCombo.setAdapter(comboAdapter);

        comboAdapter.setOnItemClickListener(new ComboAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Combo combo) {
                showComboDetails(combo);
            }
        });

        // Connect to Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("qlrapphim").child("comBo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comboList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Combo combo = snapshot.getValue(Combo.class);
                    if (combo != null) {
                        comboList.add(combo);
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
        View dialogView = inflater.inflate(R.layout.layout_dialog_info_combo, null);
        builder.setView(dialogView);

        // Đổ dữ liệu vào các EditText trong dialog
        EditText idCombo = dialogView.findViewById(R.id.idCombo);
        EditText tenCombo = dialogView.findViewById(R.id.tenCombo);
        EditText giaCombo = dialogView.findViewById(R.id.giaCombo);
        EditText moTaCombo = dialogView.findViewById(R.id.moTaCombo);
        imageViewCombo = dialogView.findViewById(R.id.anhChonCombo);

        idCombo.setText(combo.getIdCombo());
        tenCombo.setText(combo.getTenCombo());
        giaCombo.setText(String.valueOf(combo.getGiaCombo()));
        moTaCombo.setText(combo.getMoTa());
        Picasso.get()
                .load(combo.getAnhCombo()) // Đường dẫn của ảnh từ combo.getAnhCombo()
                .placeholder(R.drawable.hot_cinema) // Hình ảnh placeholder nếu cần
                .error(R.drawable.hot_cinema) // Hình ảnh lỗi nếu xảy ra lỗi
                .into(imageViewCombo);

        // Tạo và hiển thị dialog
        dialogComboInfo = builder.create();
        dialogComboInfo.show();

        // Thiết lập sự kiện cho các button trong dialog
        Button buttonChooseImage = dialogView.findViewById(R.id.chonAnhCombo);
        Button btnXoa = dialogView.findViewById(R.id.btnXoa);
        Button btnSua = dialogView.findViewById(R.id.btnSua);
        Button btnThoat = dialogView.findViewById(R.id.btnThoat);

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

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
                String idCb = idCombo.getText().toString();
                String tenCb = tenCombo.getText().toString();
                String giaCb = giaCombo.getText().toString();
                int giaCombo = Integer.parseInt(giaCb);
                String moTaCb = moTaCombo.getText().toString();

                upDateCombo(idCb, tenCb, giaCombo, moTaCb);
                
            }
        });

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị AlertDialog xác nhận xóa
                AlertDialog.Builder builder = new AlertDialog.Builder(QuanLyComboActivity.this);
                builder.setTitle("Xác nhận xóa");
                builder.setMessage("Bạn có chắc chắn muốn xóa combo này không?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện xóa combo trên Realtime Database
                        String idCb = idCombo.getText().toString();
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

    private void upDateCombo(String idCombo, String tenCombo, int giaCombo, String moTaCombo) {
        // Tham chiếu đến Firebase Storage và Realtime Database
        storageReference = FirebaseStorage.getInstance().getReference("uploads/" + System.currentTimeMillis() + ".jpg");
        DatabaseReference comboRef = databaseReference.child(idCombo);

        // Upload ảnh lên Firebase Storage
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Lấy đường dẫn của ảnh đã lưu
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Cập nhật đường dẫn ảnh vào Realtime Database
                                String imageUrl = uri.toString();
                                Combo updatedCombo = new Combo(idCombo, tenCombo, giaCombo, moTaCombo, imageUrl);

                                // Cập nhật dữ liệu của combo trong Realtime Database
                                comboRef.setValue(updatedCombo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Cập nhật thành công
                                                progressDialog.dismiss();
                                                Toast.makeText(QuanLyComboActivity.this, "Đã cập nhật combo thành công", Toast.LENGTH_SHORT).show();
                                                dialogComboInfo.dismiss(); // Đóng dialog sau khi cập nhật thành công
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Cập nhật thất bại
                                                progressDialog.dismiss();
                                                Toast.makeText(QuanLyComboActivity.this, "Cập nhật combo thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Lưu ảnh vào Storage thất bại
                        progressDialog.dismiss();
                        Toast.makeText(QuanLyComboActivity.this, "Lưu ảnh vào Storage thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void delCombo(String idCombo) {
        // Tham chiếu đến node Combos trên Realtime Database
        // Xóa dữ liệu của combo
        databaseReference.child(idCombo).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Xóa thành công
                        progressDialog.dismiss();
                        Toast.makeText(QuanLyComboActivity.this, "Đã xóa combo thành công", Toast.LENGTH_SHORT).show();
                        dialogComboInfo.dismiss(); // Đóng dialog sau khi xóa thành công

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xóa thất bại
                        progressDialog.dismiss();
                        Toast.makeText(QuanLyComboActivity.this, "Xóa combo thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showAddComboDialog() {
        isAddingCombo = true;
        // Initialize dialog
        addComboDialog = new Dialog(QuanLyComboActivity.this);
        addComboDialog.setContentView(R.layout.layout_dialog_add_combo);
        addComboDialog.setCancelable(true); // Set if you want the dialog to be canceled when tapping outside

        // Initialize views inside dialog
        EditText addIdCombo = addComboDialog.findViewById(R.id.addIdCombo);
        EditText addTenCombo = addComboDialog.findViewById(R.id.addTenCombo);
        EditText addGiaCombo = addComboDialog.findViewById(R.id.addGiaCombo);
        EditText addMoTaCombo = addComboDialog.findViewById(R.id.addMoTaCombo);
        imageViewAddImageCombo = addComboDialog.findViewById(R.id.addImageCombo);
        Button buttonChooseImage = addComboDialog.findViewById(R.id.anhCombo);
        Button dialogButtonOK = addComboDialog.findViewById(R.id.btnThem);
        Button dialogButtonCancel = addComboDialog.findViewById(R.id.btnThoat);

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

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
                String idCombo = addIdCombo.getText().toString().trim();
                String tenCombo = addTenCombo.getText().toString().trim();
                String giaComboStr = addGiaCombo.getText().toString().trim();
                String moTaCombo = addMoTaCombo.getText().toString().trim();

                // Validate input
                if (idCombo.isEmpty() || tenCombo.isEmpty() || giaComboStr.isEmpty() || moTaCombo.isEmpty() || imageUri == null) {
                    Toast.makeText(QuanLyComboActivity.this, "Vui lòng điền đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Thực hiện thêm tài khoản vào Firebase Authentication và Firestore
                progressDialog.setMessage("Đang thêm Combo...");
                progressDialog.show();

                // Chuyển đổi giá combo từ string sang int
                int giaCombo = Integer.parseInt(giaComboStr);

                // Upload ảnh vào Firebase Storage và lưu vào Realtime Database
                uploadImageAndSaveCombo(idCombo, tenCombo, giaCombo, moTaCombo);

                // Dismiss dialog
                addComboDialog.dismiss();
            }
        });

        // Show dialog
        addComboDialog.show();
    }

    // Tải ảnh lên Firebase Storage và lưu thông tin combo vào Realtime Database
    private void uploadImageAndSaveCombo(String idCombo, String tenCombo, int giaCombo, String moTaCombo) {
        // Tạo tham chiếu đến Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference("uploads/" + System.currentTimeMillis() + ".jpg");

        // Tải ảnh lên Firebase Storage
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy URL của ảnh đã upload
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageURL = uri.toString();

                        // Tạo đối tượng Combo mới
                        Combo combo = new Combo(idCombo, tenCombo, giaCombo, moTaCombo, imageURL);

                        // Lưu vào Firebase Realtime Database
                        databaseReference.child(idCombo).setValue(combo)
                                .addOnSuccessListener(aVoid -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(QuanLyComboActivity.this, "Thêm combo thành công", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(QuanLyComboActivity.this, "Thêm combo thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(QuanLyComboActivity.this, "Lỗi khi tải ảnh lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Set the selected image to ImageView in the appropriate dialog
            if (isAddingCombo) {
                // Set image in add combo dialog
                imageViewAddImageCombo.setImageURI(imageUri);
            } else {
                // Load image using Picasso in combo details dialog
                Picasso.get()
                        .load(imageUri)
                        .placeholder(R.drawable.hot_cinema)
                        .error(R.drawable.hot_cinema)
                        .into(imageViewCombo);
            }
        }
    }

}
