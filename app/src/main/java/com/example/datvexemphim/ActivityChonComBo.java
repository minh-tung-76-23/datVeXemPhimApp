package com.example.datvexemphim;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.adapter.ComboAdapter;
import com.example.datvexemphim.ui.model.ComboItem;
import com.example.datvexemphim.ui.model.ThanhToan;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Environment;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;


public class ActivityChonComBo extends AppCompatActivity {
    private static final int REQUEST_CODE_WRITE_STORAGE = 1001;

    private RecyclerView recyclerViewCombo;
    private ComboAdapter comboAdapter;
    private List<ComboItem> comboItemList;
    private ProgressDialog progressDialog;

    private TextView textTotalPrice; // TextView để hiển thị tổng giá trị
    private double initialTotalPrice; // Giá trị ban đầu
    private Button btnThanhToan; // Nút thanh toán

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chon_com_bo);
        Intent intent = getIntent();
        if (intent != null) {
            String nameFilm = intent.getStringExtra("name_film");
            String durationFilm = intent.getStringExtra("duration_film");
            String cinemaName = intent.getStringExtra("cinema_name");
            String cinemaAddress = intent.getStringExtra("cinema_address");
            String timeDate = intent.getStringExtra("time_date");
            String selectedTime = intent.getStringExtra("time");
            String seatIds = intent.getStringExtra("seat_ids");

            String totalPriceStr = intent.getStringExtra("total_price");
            String numericString = totalPriceStr.replaceAll("[^\\d]", "");

            // Chuyển đổi thành số nguyên
            initialTotalPrice = Double.parseDouble(numericString);

            // Ánh xạ các TextView từ layout XML
            TextView textFilmName = findViewById(R.id.textFilmName);
            TextView textDurationFilm = findViewById(R.id.textDurationFilm);
            TextView textCinemaName = findViewById(R.id.textCinemaName);
            TextView textTimeDate = findViewById(R.id.textTimeDate);
            TextView textSelectedTime = findViewById(R.id.textSelectedTime);
            TextView textSeatIds = findViewById(R.id.textSeatIds);
            textTotalPrice = findViewById(R.id.textTotalPriceBill); // Ánh xạ TextView tổng giá trị
            btnThanhToan = findViewById(R.id.btnThanhToan); // Ánh xạ nút thanh toán

            // Set các giá trị vào TextViews
            textFilmName.setText(nameFilm);
            textDurationFilm.setText(durationFilm);
            textCinemaName.setText(cinemaName);
            textTimeDate.setText(timeDate);
            textSelectedTime.setText(selectedTime);
            textSeatIds.setText(seatIds);
            updateTotalPrice(initialTotalPrice); // Hiển thị giá trị ban đầu

            // Kiểm tra và yêu cầu quyền WRITE_EXTERNAL_STORAGE nếu chưa có
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Yêu cầu quyền
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_WRITE_STORAGE);
            }

            progressDialog = new ProgressDialog(this);

            // Khởi tạo RecyclerView
            recyclerViewCombo = findViewById(R.id.recyclerViewCombo);
            recyclerViewCombo.setHasFixedSize(true);
            recyclerViewCombo.setLayoutManager(new LinearLayoutManager(this));

            // Khởi tạo Firebase Realtime Database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference comboRef = database.getReference("qlrapphim/comBo");

            // List chứa dữ liệu combo items
            comboItemList = new ArrayList<>();

            // Đọc dữ liệu từ Firebase
            comboRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comboItemList.clear(); // Xóa dữ liệu cũ

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ComboItem comboItem = snapshot.getValue(ComboItem.class);
                        comboItemList.add(comboItem);
                    }

                    // Cập nhật RecyclerView Adapter
                    comboAdapter = new ComboAdapter(ActivityChonComBo.this, comboItemList, initialTotalPrice);
                    recyclerViewCombo.setAdapter(comboAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                    Toast.makeText(ActivityChonComBo.this, "Failed to load combo items.", Toast.LENGTH_SHORT).show();
                }
            });

            btnThanhToan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Lấy id của tài khoản đang đăng nhập
                    progressDialog.setMessage("Đang thanh toán...");
                    progressDialog.show();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        // Xử lý khi người dùng chưa đăng nhập
                        return;
                    }
                    String userId = user.getUid(); // Lấy UID của người dùng đang đăng nhập
                    String userEmail = user.getEmail();

                    // Lấy các thông tin từ Intent
                    String nameFilm = intent.getStringExtra("name_film");
                    String cinemaName = intent.getStringExtra("cinema_name");
                    String cinemaAddress = intent.getStringExtra("cinema_address");
                    String timeDate = intent.getStringExtra("time_date");
                    String selectedTime = intent.getStringExtra("time");
                    String seatIds = intent.getStringExtra("seat_ids");
                    String[] parts = seatIds.split(":\n");
                    String seatsPart = parts[1]; // Lấy phần chứa danh sách ghế đã chọn

                    // Tách các ghế đã chọn thành một mảng
                    String[] seatArray = seatsPart.split(", ");
                    StringBuilder seatsStringBuilder = new StringBuilder();
                    for (String seat : seatArray) {
                        seatsStringBuilder.append(seat).append(", ");
                    }
                    String selectedSeats = seatsStringBuilder.toString();

                    // Lấy tổng số tiền từ Intent
                    String totalPriceStr = textTotalPrice.getText().toString();
                    // Lấy số tiền thực sự (loại bỏ phần chuỗi không phải số và khoảng trắng)
                    String actualTotalPriceStr = totalPriceStr.replaceAll("[^\\d.]", "");
                    double totalAmount = Double.parseDouble(actualTotalPriceStr);

                    // Lấy danh sách các combo đã chọn từ Adapter
                    List<ComboItem> selectedCombos = comboAdapter.getSelectedCombosWithQuantity();
                    String selectedCombo = getSelectedCombosString(selectedCombos);

                    // Tạo đối tượng dữ liệu để lưu vào Firebase Database
                    ThanhToan thanhToan = new ThanhToan(userId, userEmail, nameFilm, cinemaName, cinemaAddress,
                            timeDate, selectedTime, selectedSeats, actualTotalPriceStr, selectedCombo);

                    // Thực hiện lưu dữ liệu vào Firebase Database
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    database.child("qlrapphim/veXemPhim").push().setValue(thanhToan)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Cập nhật trạng thái các ghế đã chọn từ "trống" thành "đã bán"
                                    for (String seatId : seatArray) {
                                        database.child("qlrapphim").child("gheNgoi").child(seatId).child("tinhTrang").setValue("đã bán")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progressDialog.dismiss();
                                                        // Xử lý khi cập nhật trạng thái ghế thành công
                                                        Toast.makeText(ActivityChonComBo.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

                                                        // Tạo AlertDialog.Builder
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityChonComBo.this);
                                                        builder.setTitle("Thông tin vé đã đặt");
                                                        builder.setMessage("Email: " + userEmail + "\n" +
                                                                "Tên phim: " + nameFilm + "\n" +
                                                                "Rạp: " + cinemaName + "\n" +
                                                                "Địa chỉ rạp: " + cinemaAddress + "\n" +
                                                                "Ngày giờ chiếu: " + timeDate + " " + selectedTime + "\n" +
                                                                "Ghế đã chọn: " + selectedSeats + "\n" +
                                                                "Danh sách combo đã chọn:" + getSelectedCombosString(selectedCombos) + "\n" +
                                                                "Tổng số tiền: " + totalPriceStr + "\n");

                                                        // Tạo ScrollView để cuộn nội dung
                                                        ScrollView scrollView = new ScrollView(ActivityChonComBo.this);
                                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                        scrollView.setLayoutParams(layoutParams);
                                                        scrollView.setFillViewport(false); // Không làm cuộn ảnh

                                                        // Tạo LinearLayout để chứa ImageView và nội dung
                                                        LinearLayout linearLayout = new LinearLayout(ActivityChonComBo.this);
                                                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                                                        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL); // Căn giữa các thành phần

                                                        // Tạo ImageView và đặt ảnh từ drawable
                                                        ImageView imageView = new ImageView(ActivityChonComBo.this);
                                                        imageView.setImageResource(R.drawable.hot_cinema);

                                                        // Đặt kích thước cho ImageView
                                                        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(200, 200); // Thay đổi kích thước ảnh
                                                        imageView.setLayoutParams(imageParams);
                                                        imageView.setAdjustViewBounds(true); // Đảm bảo giữ nguyên tỷ lệ khung hình

                                                        // Thêm ImageView vào LinearLayout
                                                        linearLayout.addView(imageView);

                                                        // Thêm LinearLayout vào ScrollView
                                                        scrollView.addView(linearLayout);

                                                        // Đặt ScrollView vào AlertDialog
                                                        builder.setView(scrollView);

                                                        // Thiết lập nút Positive cho AlertDialog
//                                                        builder.setPositiveButton("In hóa đơn", new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialog, int which) {
//                                                                // Tạo đối tượng PDF và đặt tên file
//                                                                String fileName = "HoaDon_" + System.currentTimeMillis() + ".pdf";
//                                                                File pdfFile = new File(Environment.getExternalStorageDirectory(), fileName);
//
//                                                                try {
//                                                                    // Tạo tệp PDF mới
//                                                                        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfFile));
//
//                                                                    // Tạo một trang mới
//                                                                    Document document = new Document(pdfDocument);
//
//                                                                    String content = "Thông tin vé đã đặt\n" +
//                                                                            "Email: " + userEmail + "\n" +
//                                                                            "Tên phim: " + nameFilm + "\n" +
//                                                                            "Rạp: " + cinemaName + "\n" +
//                                                                            "Địa chỉ rạp: " + cinemaAddress + "\n" +
//                                                                            "Ngày giờ chiếu: " + timeDate + " " + selectedTime + "\n" +
//                                                                            "Ghế đã chọn: " + selectedSeats + "\n" +
//                                                                            "Danh sách combo đã chọn:" + getSelectedCombosString(selectedCombos) + "\\n" +
//                                                                            "Tổng số tiền: " + totalPriceStr + "\n";
//
//                                                                    // Thêm nội dung vào tài liệu PDF
//                                                                    document.add(new Paragraph(content.replaceAll("\\$\\{([^}]*)}", "")));
//
//                                                                    // Đóng tài liệu PDF
//                                                                    document.close();
//
//                                                                    // Hiển thị thông báo khi in thành công
//                                                                    Toast.makeText(ActivityChonComBo.this, "In hóa đơn thành công! Đường dẫn: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
//                                                                } catch (IOException e) {
//                                                                    e.printStackTrace();
//                                                                    // Xử lý nếu có lỗi khi tạo PDF
//                                                                    Toast.makeText(ActivityChonComBo.this, "Lỗi khi tạo hóa đơn PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                                }
//
//                                                                // Đóng AlertDialog
//                                                                dialog.dismiss();
//                                                            }
//                                                        });

                                                        // Thiết lập nút Negative cho AlertDialog
                                                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(ActivityChonComBo.this, ActivityHome.class);
                                                                startActivity(intent);
                                                                finishAffinity();
                                                                dialog.dismiss();
                                                            }
                                                        });

                                                        // Tạo và hiển thị AlertDialog
                                                        AlertDialog dialog = builder.create();
                                                        dialog.show();

                                                        // Đảm bảo ScrollView cuộn được khi nội dung quá lớn
                                                        scrollView.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                scrollView.fullScroll(View.FOCUS_UP);
                                                            }
                                                        });
                                                    }

                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Xử lý khi cập nhật trạng thái ghế thất bại
                                                        Toast.makeText(ActivityChonComBo.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Xử lý khi lưu thất bại
                                    Toast.makeText(ActivityChonComBo.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
    }

    // Phương thức để lấy chuỗi danh sách các combo đã chọn
    private String getSelectedCombosString(List<ComboItem> selectedCombos) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ComboItem combo : selectedCombos) {
            if (combo.getQuantity() > 0) { // Chỉ hiển thị các combo có số lượng lớn hơn 0
                Locale locale = new Locale("vi", "VN"); // Đặt locale cho tiền Việt Nam
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
                String formattedPrice = currencyFormatter.format(combo.getGiaCombo());
                stringBuilder.append("\n- ")
                        .append(combo.getTenCombo())
                        .append(" - Giá: ")
                        .append(formattedPrice)
                        .append("\nSố lượng: ")
                        .append(combo.getQuantity() + " \n");
            }
        }
        return stringBuilder.toString();
    }

    // Phương thức để cập nhật tổng giá trị
    public void updateTotalPrice(double totalPrice) {
        Locale locale = new Locale("vi", "VN"); // Đặt locale cho tiền Việt Nam
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        String formattedPrice = currencyFormatter.format(totalPrice);
        textTotalPrice.setText(formattedPrice);
    }
}

