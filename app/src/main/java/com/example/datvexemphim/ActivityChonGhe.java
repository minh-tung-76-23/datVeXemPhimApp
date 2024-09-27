package com.example.datvexemphim;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.adapter.SeatAdapter;
import com.example.datvexemphim.adapter.SelectedFilmAdapter;
import com.example.datvexemphim.ui.api.ApiService;
import com.example.datvexemphim.ui.gallery.GridSpacingItemDecoration;
import com.example.datvexemphim.ui.model.Seat;

import com.example.datvexemphim.ui.model.filmAPI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityChonGhe extends AppCompatActivity {

    private TextView textFilmName, textCinemaName, textTimeDate, textSeatId, textTotalPrice;
    private RecyclerView recyclerViewSeats;
    private SeatAdapter seatAdapter;
    private DatabaseReference mDatabase;
    private List<Seat> seatList; // Dữ liệu ghế từ Firebase
    private Button btnTiepTuc, buttonOK;
    private AlertDialog dialogGetDatTime, dialogGetFilm;
    private TextView[] dateTextViews = new TextView[4];
    private TextView selectedTextView;
    String selectedDate,selectedDayOfWeek, selectedTimeDate, textTime, durationFilm, nameFilm;
    private SelectedFilmAdapter selectedFilmAdapter;
    private List<filmAPI> filmList = new ArrayList<>();

    private static final String TAG = ActivityChonGhe.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chon_ghe);

        // Khởi tạo các TextView
        textFilmName = findViewById(R.id.textfilmName);
        textCinemaName = findViewById(R.id.textCinemaName);
        textTimeDate = findViewById(R.id.textTime);
        textSeatId = findViewById(R.id.txtSeatId);
        textTotalPrice = findViewById(R.id.textTotalPrice);
        btnTiepTuc = findViewById(R.id.btnTiepTuc);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            nameFilm = intent.getStringExtra("name_film");
            durationFilm = intent.getStringExtra("duration_film");
            String cinemaName = intent.getStringExtra("cinema_name");
            String cinemaAddress = intent.getStringExtra("cinema_address");
            selectedDayOfWeek = intent.getStringExtra("selected_dayOfWeek");
            selectedDate = intent.getStringExtra("selected_date");
            String selectedTime = intent.getStringExtra("selected_time");
            selectedTimeDate = selectedDayOfWeek +", "+ selectedDate;
            textTime = selectedTime + " | " + selectedTimeDate;

            // Hiển thị dữ liệu lên TextViews
            textFilmName.setText(nameFilm);
            textCinemaName.setText(cinemaName);
            textTimeDate.setText(textTime);

            if (nameFilm == null || durationFilm == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityChonGhe.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.layout_dialog_sel_movie, null);
                builder.setView(dialogView);

                EditText editTextSearch = dialogView.findViewById(R.id.textViewSearch);
                RecyclerView rcvListSelFilm = dialogView.findViewById(R.id.rcv_list_sel_film);
                selectedFilmAdapter = new SelectedFilmAdapter(ActivityChonGhe.this);

                // Thiết lập LinearLayoutManager với hướng dọc
                LinearLayoutManager layoutManager = new LinearLayoutManager(ActivityChonGhe.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rcvListSelFilm.setLayoutManager(layoutManager);

                rcvListSelFilm.addItemDecoration(new DividerItemDecoration(ActivityChonGhe.this, DividerItemDecoration.VERTICAL));
                rcvListSelFilm.setHasFixedSize(true);
                rcvListSelFilm.setAdapter(selectedFilmAdapter);

                // Tạo và hiển thị dialog
                dialogGetFilm = builder.create();
                dialogGetFilm.setCancelable(false);
                dialogGetFilm.show();

                editTextSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // Không cần xử lý trước khi thay đổi văn bản
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // Xử lý khi văn bản trong EditText thay đổi
                        String searchText = charSequence.toString().toLowerCase(Locale.getDefault());
                        selectedFilmAdapter.filter(searchText); // Gọi phương thức filter của adapter để lọc danh sách phim
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // Sau khi văn bản đã thay đổi
                    }
                });

                callAPI(); // Gọi API để lấy danh sách phim từ server
                // Thiết lập sự kiện click vào item trong Adapter
                selectedFilmAdapter.setOnItemClickListener(new SelectedFilmAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(filmAPI film) {
                        // Hiển thị dialog và truyền thông tin phim vào
                        showFilmDetailDialog(film.getName(), film.getDuration());
                    }
                });
            }

            if (selectedDayOfWeek == null && selectedDate == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.layout_dialog_get_datetime, null);
                builder.setView(dialogView);


                dateTextViews[0] = dialogView.findViewById(R.id.text_date1);
                dateTextViews[1] = dialogView.findViewById(R.id.text_date2);
                dateTextViews[2] = dialogView.findViewById(R.id.text_date3);
                dateTextViews[3] = dialogView.findViewById(R.id.text_date4);
                buttonOK = dialogView.findViewById(R.id.btnOK);
                // Tạo và hiển thị dialog
                dialogGetDatTime = builder.create();
                dialogGetDatTime.setCancelable(false);
                dialogGetDatTime.show();

                for (TextView textView : dateTextViews) {
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (selectedTextView != null) {
                                selectedTextView.setTextColor(Color.BLACK);
                                selectedTextView.setBackgroundColor(Color.WHITE);
                            }
                            // Xác định TextView được chọn và lưu vào selectedTextView
                            selectedTextView = (TextView) v;

                            // Lấy dữ liệu từ TextView được chọn
                            String dateText = selectedTextView.getText().toString();
                            String[] parts = dateText.split("\n");
                            String dateStr = parts[0]; // Ngày tháng
                            String dayOfWeek = parts[1]; // Thứ của ngày tháng

                            // Cập nhật màu sắc cho TextView được chọn
                            selectedTextView.setTextColor(Color.WHITE);
                            selectedTextView.setBackgroundColor(Color.LTGRAY);

                            selectedDate = dateStr;
                            selectedDayOfWeek = dayOfWeek;
                        }
                    });
                }
                displayDates();

                buttonOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedTimeDate = selectedDayOfWeek +", "+ selectedDate;
                        textTime = selectedTime + " | " + selectedTimeDate;
                        textTimeDate.setText(textTime);
                        dialogGetDatTime.dismiss();
                    }
                });
            }

            // Khởi tạo Firebase Database
            mDatabase = FirebaseDatabase.getInstance().getReference().child("qlrapphim").child("gheNgoi");

            // Khởi tạo seatList (dữ liệu từ Firebase)
            seatList = new ArrayList<>();

            // Khởi tạo RecyclerView và Adapter
            recyclerViewSeats = findViewById(R.id.recyclerViewSeats);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 10); // 9 ghế mỗi hàng

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int itemCount = seatAdapter.getItemCount();
                    int lastRowCount = itemCount % 10; // Số ghế trong hàng cuối cùng
                    if (position < itemCount - lastRowCount) {
                        return 1; // 1 ghế cho các hàng không phải hàng cuối cùng
                    } else {
                        return 2; // Số ghế cần để điền vào hàng cuối cùng
                    }
                }
            });

            recyclerViewSeats.setLayoutManager(gridLayoutManager);
            seatAdapter = new SeatAdapter(this, seatList, textSeatId, textTotalPrice, btnTiepTuc);
            recyclerViewSeats.setAdapter(seatAdapter);

            // Lấy dữ liệu từ Firebase
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    seatList.clear(); // Xóa dữ liệu cũ để đảm bảo không trùng lặp
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Seat seat = snapshot.getValue(Seat.class);
                        seatList.add(seat);
                    }
                    seatAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView khi có dữ liệu mới
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Failed to read value.", databaseError.toException());
                }
            });

            btnTiepTuc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Lấy thông tin từ TextViews
                    String nameFilm = textFilmName.getText().toString();
                    String cinemaName = textCinemaName.getText().toString();
                    String timeDate = textTimeDate.getText().toString();
                    String seatIds = textSeatId.getText().toString();
                    String totalPrice = textTotalPrice.getText().toString();

                    // Tạo Intent để chuyển sang ActivityChonCombo
                    Intent intent = new Intent(ActivityChonGhe.this, ActivityChonComBo.class);
                    intent.putExtra("name_film", nameFilm);
                    intent.putExtra("cinema_name", cinemaName);
                    intent.putExtra("duration_film", durationFilm);
                    intent.putExtra("cinema_address", cinemaAddress);
                    intent.putExtra("time_date", selectedTimeDate);
                    intent.putExtra("time", selectedTime);
                    intent.putExtra("seat_ids", seatIds);
                    intent.putExtra("total_price", totalPrice);
                    startActivity(intent);
                }
            });

        }
    }
    private void displayDates() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

        for (int i = 0; i < dateTextViews.length; i++) {
            String dateStr = dateFormat.format(currentDate);
            String dayOfWeek = getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));

            dateTextViews[i].setText(String.format("%s\n%s", dateStr, dayOfWeek));

            if (i == 0) {
                dateTextViews[i].setTextColor(Color.WHITE);
                dateTextViews[i].setBackgroundColor(Color.LTGRAY);
                selectedTextView = dateTextViews[i];
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            currentDate = calendar.getTime();
        }
    }

    private String getDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "Chủ nhật";
            case Calendar.MONDAY:
                return "Thứ hai";
            case Calendar.TUESDAY:
                return "Thứ ba";
            case Calendar.WEDNESDAY:
                return "Thứ tư";
            case Calendar.THURSDAY:
                return "Thứ năm";
            case Calendar.FRIDAY:
                return "Thứ sáu";
            case Calendar.SATURDAY:
                return "Thứ bảy";
            default:
                return "";
        }
    }

    private void callAPI() {
        ApiService.apiService.selectApiFilm().enqueue(new Callback<List<filmAPI>>() {
            @Override
            public void onResponse(Call<List<filmAPI>> call, Response<List<filmAPI>> response) {
                if (response.isSuccessful()) {
                    List<filmAPI> films = response.body();
                    if (films != null) {
                        // Xóa dữ liệu cũ và cập nhật mới
                        filmList.clear();
                        filmList.addAll(films);
                        selectedFilmAdapter.setFilmList(filmList); // Cập nhật danh sách phim trong adapter
                    } else {
                        Toast.makeText(ActivityChonGhe.this, "Không có dữ liệu phim", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityChonGhe.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<filmAPI>> call, Throwable t) {
                Toast.makeText(ActivityChonGhe.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFilmDetailDialog(String name, String duration) {
        nameFilm = name;
        durationFilm = duration;
        textFilmName.setText(nameFilm);
        dialogGetFilm.dismiss();
    }

}




