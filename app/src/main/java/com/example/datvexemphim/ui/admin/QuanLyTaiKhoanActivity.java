package com.example.datvexemphim.ui.admin;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.example.datvexemphim.ActivityDangKy;
import com.example.datvexemphim.MainActivity;
import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.admin.Adapter.UserAdapter;
import com.example.datvexemphim.ui.admin.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuanLyTaiKhoanActivity extends AppCompatActivity {

    private static final String TAG = "QuanLyTaiKhoanActivity";

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private ProgressDialog progressDialog;
    private Button buttonThoat;
    private Button buttonAddAccount;

    // Khởi tạo Firebase Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Dialog hiển thị thông tin tài khoản
    private AlertDialog dialogAccountInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_tai_khoan);

        progressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.rcv_list_account);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        adapter = new UserAdapter(userList);
        recyclerView.setAdapter(adapter);
        buttonThoat = findViewById(R.id.buttonExit);
        buttonAddAccount = findViewById(R.id.buttonAddAccount);

        loadAccount();

        adapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                showAccountDetails(user);
            }
        });

        buttonThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuanLyTaiKhoanActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });

        buttonAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAccount();
            }
        });
    }

    private void addAccount() {
        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_add_user, null);
        builder.setView(dialogView);

        // Tạo editText
        EditText addEmail = dialogView.findViewById(R.id.addEmail);
        EditText addPassWord = dialogView.findViewById(R.id.addPassWord); // Đã thêm password
        EditText addFullName = dialogView.findViewById(R.id.addFullName);
        EditText addNgaySinh = dialogView.findViewById(R.id.addNgaySinh);
        EditText addSoDienThoai = dialogView.findViewById(R.id.addSoDienThoai);
        EditText addRole = dialogView.findViewById(R.id.addRole);

        // Tạo và hiển thị dialog
        dialogAccountInfo = builder.create();
        dialogAccountInfo.show();

        // Thiết lập sự kiện cho các button trong dialog
        Button btnThem = dialogView.findViewById(R.id.btnThem);
        Button btnThoat = dialogView.findViewById(R.id.btnThoat);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các EditText
                String email = addEmail.getText().toString().trim();
                String password = addPassWord.getText().toString(); // Lấy mật khẩu
                String fullName = addFullName.getText().toString().trim();
                String ngaySinh = addNgaySinh.getText().toString().trim();
                String soDienThoai = addSoDienThoai.getText().toString().trim();
                String role = addRole.getText().toString().trim();
                String isRole = role.equals("Admin") ? "0" : "1"; // Mặc định là User nếu không phải Admin

                // Kiểm tra và validate dữ liệu
                if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || ngaySinh.isEmpty() || soDienThoai.isEmpty() || role.isEmpty()) {
                    Toast.makeText(QuanLyTaiKhoanActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thực hiện thêm tài khoản vào Firebase Authentication và Firestore
                progressDialog.setMessage("Đang thêm tài khoản...");
                progressDialog.show();

                // Tạo đối tượng User từ dữ liệu nhập vào
                User newUser = new User(fullName, email, ngaySinh, soDienThoai, "", isRole);

                // Thêm tài khoản vào Authentication và lưu thông tin vào Firestore
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    if (firebaseUser != null) {
                                        // Lưu thông tin người dùng vào Firestore
                                        saveUserDataToFirestore(firebaseUser.getUid(), newUser);
                                    } else {
                                        // Handle null user
                                        progressDialog.dismiss();
                                        Toast.makeText(QuanLyTaiKhoanActivity.this, "Lỗi khi thêm tài khoản", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(QuanLyTaiKhoanActivity.this, "Lỗi khi thêm tài khoản: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                dialogAccountInfo.dismiss(); // Đóng dialog sau khi xử lý
            }
        });

        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng dialog khi nhấn nút "Thoát"
                dialogAccountInfo.dismiss();
            }
        });
    }

    // Phương thức lưu thông tin người dùng vào Firestore
    private void saveUserDataToFirestore(String userId, User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Convert User object thành Map để lưu vào Firestore
        Map<String, Object> userData = user.toMap();

        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(QuanLyTaiKhoanActivity.this, "Thêm tài khoản thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(QuanLyTaiKhoanActivity.this, "Lỗi khi lưu thông tin người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAccount() {
        // Truy vấn dữ liệu từ Firestore
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userList.clear(); // Xóa danh sách hiện tại để tránh duplicate
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                user.setUid(document.getId());
                                userList.add(user);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void showAccountDetails(User user) {
        // Hiển thị dialog thông tin tài khoản
        showAccountInfoDialog(user);
    }

    // Phương thức hiển thị dialog thông tin tài khoản
    private void showAccountInfoDialog(User user) {
        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_account, null);
        builder.setView(dialogView);

        // Đổ dữ liệu tài khoản vào các EditText trong dialog
        EditText idAccount = dialogView.findViewById(R.id.idAccount);
        EditText editEmail = dialogView.findViewById(R.id.editEmail);
        EditText editFullName = dialogView.findViewById(R.id.editFullName);
        EditText editNgaySinh = dialogView.findViewById(R.id.editNgaySinh);
        EditText editSoDienThoai = dialogView.findViewById(R.id.editSoDienThoai);
        EditText editRole = dialogView.findViewById(R.id.editRole);

        idAccount.setText(user.getUid());
        editEmail.setText(user.getEmail());
        editFullName.setText(user.getFullName());
        editNgaySinh.setText(user.getNgaySinh());
        editSoDienThoai.setText(user.getSoDienThoai());
        String isUser = user.getIsUser();
        int userType = Integer.parseInt(isUser);

        if (userType == 1) {
            editRole.setText("Người dùng");
        } else {
            editRole.setText("Admin");
        }

        // Tạo và hiển thị dialog
        dialogAccountInfo = builder.create();
        dialogAccountInfo.show();

        // Thiết lập sự kiện cho các button trong dialog
        Button btnXoa = dialogView.findViewById(R.id.btnXoa);
        Button btnSua = dialogView.findViewById(R.id.btnSua);
        Button btnThoat = dialogView.findViewById(R.id.btnThoat);

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý chức năng Xóa
                deleteAccount(user);
                dialogAccountInfo.dismiss();
            }
        });

        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý chức năng Sửa
                //Lấy dữ liệu từ các editText
                String idAcc = idAccount.getText().toString();
                String fullName = editFullName.getText().toString();
                String ngaySinh = editNgaySinh.getText().toString();
                String soDienThoai = editSoDienThoai.getText().toString();
                String role = editRole.getText().toString();
                String isRole;
                if (role.equals("Admin")) {
                    isRole = "0";
                } else {
                    isRole = "1";
                }
                progressDialog.setMessage("Đang kiểm tra...");
                progressDialog.show();
                updateAccount(idAcc, fullName,ngaySinh, soDienThoai, isRole);
                dialogAccountInfo.dismiss();
            }
        });

        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng dialog
                dialogAccountInfo.dismiss();
            }
        });
    }

    // Phương thức cập nhật thông tin tài khoản vào Firestore
    private void updateAccount(String idAcc, String fullName, String ngaySinh, String soDienThoai, String isRole) {
        // Lấy tham chiếu đến tài khoản trong Firestore
        DocumentReference userRef = db.collection("users").document(idAcc);

        // Cập nhật thông tin trên Firestore
        userRef.update("fullName", fullName,
                        "ngaySinh", ngaySinh,
                        "soDienThoai", soDienThoai,
                        "isUser", isRole)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(QuanLyTaiKhoanActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            loadAccount(); // Cập nhật lại danh sách tài khoản sau khi cập nhật
                        } else {
                            Log.d(TAG, "Error updating document", task.getException());
                            Toast.makeText(QuanLyTaiKhoanActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // Phương thức xóa tài khoản khỏi Firestore và Authentication
    private void deleteAccount(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa tài khoản");
        builder.setMessage("Bạn có chắc chắn muốn xóa tài khoản này?");

        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xóa tài khoản khỏi Firestore
                db.collection("users").document(user.getUid())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadAccount();
                                    // Xóa tài khoản khỏi Authentication (Nếu cần)
                                    FirebaseAuth.getInstance().getCurrentUser().delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "User account deleted.");
                                                        Toast.makeText(QuanLyTaiKhoanActivity.this, "Xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                                                        loadAccount(); // Cập nhật lại danh sách tài khoản sau khi xóa
                                                    } else {
                                                        Log.d(TAG, "Error deleting user account", task.getException());
                                                        Toast.makeText(QuanLyTaiKhoanActivity.this, "Xóa tài khoản thất bại.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Log.d(TAG, "Error deleting document", task.getException());
                                    Toast.makeText(QuanLyTaiKhoanActivity.this, "Xóa tài khoản thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}


