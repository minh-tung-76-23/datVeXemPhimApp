package com.example.datvexemphim;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.adapter.CinemaAdapter;
import com.example.datvexemphim.adapter.FilmAdapter;
import com.example.datvexemphim.ui.api.ApiService;
import com.example.datvexemphim.ui.model.Cinema;
import com.example.datvexemphim.ui.model.filmAPI;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityChiTietFilm extends AppCompatActivity {

    public static final String EXTRA_FILM_ID = "extra_film_id";
    private RecyclerView recyclerView;
    private CinemaAdapter cinemaAdapter;

    private TextView nameFilm, timeFilm;
    private ImageView imgFilm;
    private Button btnChiTietFilm;
    private TextView[] dateTextViews = new TextView[4];
    private TextView selectedTextView;

    private Spinner spinnerLocations;
    private String selectedLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_film);

        initUi();
        spinnerLocations = findViewById(R.id.spinnerLocations);
        spinnerLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy dữ liệu được chọn từ Spinner
                selectedLocation = parent.getItemAtPosition(position).toString();

                // Gọi lại API với location mới được chọn
                callApi(selectedLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý khi không có gì được chọn (nếu cần)
            }
        });

        // Tiến hành gọi API ban đầu với dữ liệu mặc định
        callApi(selectedLocation);

        int filmId = getIntent().getIntExtra(EXTRA_FILM_ID, 0);
        FilmAdapter filmAdapter = new FilmAdapter(this);
        filmAdapter.getFilmById(filmId, new FilmAdapter.OnFilmLoadedListener() {
            @Override
            public void onFilmLoaded(filmAPI film) {
                nameFilm.setText(film.getName());
                timeFilm.setText(String.valueOf(film.getDuration()));

                String posterUrl = handlePosterUrl(film.getPoster());
                Picasso.get()
                        .load(posterUrl)
                        .placeholder(R.drawable.img)
                        .error(R.drawable.hot_cinema)
                        .into(imgFilm);

                // Set filmId and nameFilm to CinemaAdapter
                cinemaAdapter.setFilmInfo(filmId, film.getName());

                displayDates();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ActivityChiTietFilm.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        btnChiTietFilm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityChiTietFilm.this, DetailFilmActivity.class);
                intent.putExtra(EXTRA_FILM_ID, filmId);
                startActivity(intent);
            }
        });
    }

    private void initUi() {
        nameFilm = findViewById(R.id.name_film);
        timeFilm = findViewById(R.id.time_film);
        imgFilm = findViewById(R.id.img_film);
        btnChiTietFilm = findViewById(R.id.btn_chiTietFilm);

        dateTextViews[0] = findViewById(R.id.text_date1);
        dateTextViews[1] = findViewById(R.id.text_date2);
        dateTextViews[2] = findViewById(R.id.text_date3);
        dateTextViews[3] = findViewById(R.id.text_date4);

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

                    // Truyền thông tin qua CinemaAdapter
                    cinemaAdapter.setSelectedDate(dateStr);
                    cinemaAdapter.setDayOfWeek(dayOfWeek);
                    cinemaAdapter.notifyDataSetChanged();
                }
            });
        }

        recyclerView = findViewById(R.id.rCVRap);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        cinemaAdapter = new CinemaAdapter(this);
        recyclerView.setAdapter(cinemaAdapter);
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

    private String handlePosterUrl(String posterUrl) {
        if (!TextUtils.isEmpty(posterUrl) && !posterUrl.startsWith("http")) {
            posterUrl = "https://rapchieuphim.com" + posterUrl;
        }
        return posterUrl;
    }

    private void callApi(String location) {
        ApiService.apiService.getAllCinemas().enqueue(new Callback<List<Cinema>>() {
            @Override
            public void onResponse(Call<List<Cinema>> call, Response<List<Cinema>> response) {
                if (response.isSuccessful()) {
                    List<Cinema> cinemaList = response.body();
                    if (cinemaList != null && !cinemaList.isEmpty()) {
                        // Lọc danh sách rạp phim có city giống với location
                        List<Cinema> filteredCinemas = new ArrayList<>();
                        for (Cinema cinema : cinemaList) {
                            if (cinema.getCity() != null && cinema.getCity().equalsIgnoreCase(location)) {
                                filteredCinemas.add(cinema);
                            }
                        }

                        if (!filteredCinemas.isEmpty()) {
                            // Hiển thị danh sách rạp phim lên RecyclerView
                            cinemaAdapter.setData(filteredCinemas);
                        } else {
                            Toast.makeText(ActivityChiTietFilm.this, "Không có rạp phim nào ở " + location, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ActivityChiTietFilm.this, "Không có dữ liệu rạp phim", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityChiTietFilm.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cinema>> call, Throwable t) {
                Toast.makeText(ActivityChiTietFilm.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

