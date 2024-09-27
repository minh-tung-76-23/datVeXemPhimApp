package com.example.datvexemphim.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.ActivityChonGhe;
import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.model.Cinema;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class CinemaAdapter extends RecyclerView.Adapter<CinemaAdapter.ViewHolder> {

    private List<Cinema> cinemaList = new ArrayList<>();
    private Context context; // Context variable
    private int filmId;
    private String nameFilm;
    private String selectedDate; // Thêm biến lưu trữ ngày tháng đã chọn
    private String dayOfWeek; // Thêm biến lưu trữ thứ đã chọn

    // Constructor
    public CinemaAdapter(Context context) {
        this.context = context;
    }

    // Set data method
    public void setCinemaList(List<Cinema> cinemaList) {
        this.cinemaList = cinemaList;
        notifyDataSetChanged();
    }

    // Thêm phương thức để nhận filmId và nameFilm từ ActivityChiTietFilm
    public void setFilmInfo(int filmId, String nameFilm) {
        this.filmId = filmId;
        this.nameFilm = nameFilm;
    }

    // Thêm phương thức để nhận selectedDate từ ActivityChiTietFilm
    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    // Thêm phương thức để nhận dayOfWeek từ ActivityChiTietFilm
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    // ViewHolder creation
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cinema, parent, false);
        return new ViewHolder(view);
    }

    // Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cinema cinema = cinemaList.get(position);
        holder.tvCinemaName.setText(cinema.getName());
        holder.tvCinemaAddress.setText(cinema.getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xử lý khi click vào một item trong RecyclerView
                showTimeSelectionDialog(cinema.getId(), cinema.getName(), cinema.getAddress());
            }
        });
    }

    // Generate time list
    private List<String> generateTimeList() {
        List<String> timeList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        for (int hour = currentHour; hour <= 23; hour++) {
            for (int minute = (hour == currentHour ? (currentMinute < 30 ? 30 : 0) : 0); minute < 60; minute += 30) {
                timeList.add(String.format("%02d:%02d", hour, minute));
                if (hour == 23 && minute == 30) {
                    break;
                }
            }
        }
        return timeList;
    }

    // Show time selection dialog
    public void showTimeSelectionDialog(int cinemaId, String cinemaName, String cinemaAddress) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chọn thời gian");

        List<String> timeList = generateTimeList();
        String[] times = timeList.toArray(new String[0]);

        builder.setItems(times, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedTime = times[i];
                String selectedDateTime = dayOfWeek +", "+ selectedDate;
                // Start ActivityChonGhe and pass data
                Intent intent = new Intent(context, ActivityChonGhe.class);
                intent.putExtra("film_id", filmId);
                intent.putExtra("cinema_id", cinemaId);
                intent.putExtra("name_film", nameFilm);
                intent.putExtra("cinema_name", cinemaName);
                intent.putExtra("cinema_address", cinemaAddress);
                intent.putExtra("selected_datetime", selectedDateTime);
                intent.putExtra("selected_time", selectedTime);
                context.startActivity(intent);

                dialogInterface.dismiss();
            }
        });

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Get item count
    @Override
    public int getItemCount() {
        return cinemaList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCinemaName, tvCinemaAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCinemaName = itemView.findViewById(R.id.tvCinemaName);
            tvCinemaAddress = itemView.findViewById(R.id.tvCinemaAddress);
        }
    }

    // Set data method
    public void setData(List<Cinema> cinemas) {
        cinemaList.clear();
        cinemaList.addAll(cinemas);
        notifyDataSetChanged();
    }
}

