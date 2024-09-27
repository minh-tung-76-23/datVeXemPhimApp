package com.example.datvexemphim.ui.home;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.datvexemphim.ActivityHome;
import com.example.datvexemphim.R;
import com.example.datvexemphim.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static ImageView imgAvatar;
    private EditText editTextEmail, editTextFullName, editTextDate, editTextPhone;
    private Button btnUpdateProfile, btnChangePass;
    private Uri mUri;
    private ActivityHome mActivityHome;
    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initUi();
        mActivityHome = (ActivityHome) getActivity();
        progressDialog = new ProgressDialog(getActivity());
        setUserInfomation();
        initListener();

//        final TextView textView = binding.edtEmail;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void setUserInfomation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        // Lấy DocumentReference của người dùng từ Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(user.getUid());

        // Đọc dữ liệu từ Firestore
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Đổ dữ liệu vào các trường thông tin
                    String email = documentSnapshot.getString("email");
                    String fullName = documentSnapshot.getString("fullName");
                    String dateOfBirth = documentSnapshot.getString("ngaySinh");
                    String phoneNumber = documentSnapshot.getString("soDienThoai");
                    String photoUrl = documentSnapshot.getString("photoUrl");

                    editTextEmail.setText(email);
                    editTextFullName.setText(fullName);
                    editTextDate.setText(dateOfBirth);
                    editTextPhone.setText(phoneNumber);

                    // Load ảnh đại diện từ URL sử dụng Glide
                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(requireContext()).load(photoUrl).error(R.drawable.img).into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(R.drawable.img); // Nếu không có ảnh, sử dụng ảnh mặc định
                    }
                } else {
                    // Nếu không tìm thấy tài liệu người dùng trong Firestore
                    Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Lỗi khi đọc dữ liệu người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initListener() {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCLickRequestPermission();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateProfile();
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOpenDialogChangePass();
            }
        });
    }

    private void onClickOpenDialogChangePass() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_change_pass);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);

//        EditText oldPass = dialog.findViewById(R.id.old_pass);
        EditText newPass = dialog.findViewById(R.id.new_pass);
        EditText reNewPass = dialog.findViewById(R.id.re_new_pass);
        Button btnChangePassWord = dialog.findViewById(R.id.btn_change_pass);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 dialog.dismiss();
             }
        });

        btnChangePassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChangePass();
            }

            private void onClickChangePass() {
//                String oldPassword = oldPass.getText().toString().trim();
                String newPassword = newPass.getText().toString().trim();
                String reNewPassword = reNewPass.getText().toString().trim();

                if (!newPassword.equals(reNewPassword)) {
                    Toast.makeText(getActivity(), "Mật khẩu mới và nhập lại mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return; // Không tiếp tục nếu mật khẩu không khớp
                }

                progressDialog.show();
                progressDialog.setMessage("Đang cập nhật mật khẩu...");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.updatePassword(reNewPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss(); // Đóng hộp thoại sau khi thành công
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                    //Shpw dialog->reAuthenticate
                                    reAuthenticate();
                                }
                            }
                        });
            }
            private void reAuthenticate() {
                final Dialog reAuthDialog = new Dialog(getActivity());
                reAuthDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                reAuthDialog.setContentView(R.layout.layout_dialog_re_info); // Thiết lập layout cho dialog

                EditText edtEmail = reAuthDialog.findViewById(R.id.re_email);
                EditText edtPassword = reAuthDialog.findViewById(R.id.re_password);
                Button btnReAuthenticate = reAuthDialog.findViewById(R.id.btn_change_info);
                Button btnCancel = reAuthDialog.findViewById(R.id.btn_cancel);
                btnReAuthenticate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = edtEmail.getText().toString().trim();
                        String password = edtPassword.getText().toString().trim();

                        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                            Toast.makeText(getActivity(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                        // Xác thực lại người dùng
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Xác thực thành công", Toast.LENGTH_SHORT).show();
                                            reAuthDialog.dismiss();
                                            // Sau khi xác thực thành công, tiến hành cập nhật lại mật khẩu
                                            onClickChangePass();
                                        } else {
                                            Toast.makeText(getActivity(), "Xác thực thất bại", Toast.LENGTH_SHORT).show();
                                            // Nếu xác thực thất bại, người dùng có thể thử lại
                                        }
                                    }
                                });
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reAuthDialog.dismiss(); // Đóng dialog khi người dùng huỷ bỏ
                    }
                });

                reAuthDialog.show();
            }
        });


        dialog.show();
    }

    private void onClickUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        progressDialog.setMessage("Đang cập nhật...");
        progressDialog.show();
        String fullName = editTextFullName.getText().toString().trim();
        String sdt = editTextPhone.getText().toString().trim();
        String ngaySinh = editTextDate.getText().toString().trim();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .setPhotoUri(mUri) // Ensure mUri is updated with the selected image URI
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Cập nhật lên Firestore
                            updateUserInfoInFirestore(user.getUid(), fullName, mUri, sdt, ngaySinh);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUserInfoInFirestore(String uid, String fullName, Uri photoUri, String sdt, String ngaySinh) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        // Tạo một HashMap chứa thông tin cập nhật
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", fullName);
        updates.put("ngaySinh", ngaySinh);
        updates.put("soDienThoai", sdt);
        if (photoUri != null) {
            updates.put("photoUrl", photoUri.toString());
        }

        // Thực hiện cập nhật lên Firestore
        userRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        mActivityHome.showUserInformation();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void onCLickRequestPermission() {
        if(mActivityHome == null) {
            return;
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mActivityHome.openGallery();
            return;
        }

        if(getActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mActivityHome.openGallery();
        } else {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        }
    }

    private void initUi() {
        imgAvatar = binding.imgAvatar;
        editTextEmail = binding.edtEmail;
        editTextFullName = binding.edtFullName;
        editTextDate = binding.edtDate;
        editTextPhone = binding.edtSdt;
        btnUpdateProfile = binding.btnUpdateProfile;
        btnChangePass = binding.btnChangePass;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void setBitmapImgView(Bitmap bitmapImgView) {
        imgAvatar.setImageBitmap(bitmapImgView);
    }

    public static void setUri(HomeFragment fragment, Uri mUri) {
        fragment.mUri = mUri;
        Log.d("HomeFragment", "Selected Image URI: " + mUri.toString());

    }
}