package com.example.datvexemphim.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.model.Seat;

import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private Context context;
    private List<Seat> seatList;

    public SeatAdapter(Context context, List<Seat> seatList) {
        this.context = context;
        this.seatList = seatList;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_ghe, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seatList.get(position);

        // Hiển thị thông tin ghế lên các thành phần giao diện của item_seat.xml
        holder.loaiGhe.setText(seat.getLoaiGhe());
        holder.trangThai.setText(seat.getTrangThai());

        // Xử lý hình ảnh ghế dựa trên trạng thái (ví dụ: màu sắc khác nhau cho từng trạng thái)
        if (seat.getTrangThai().equals("Đã đặt")) {
            holder.imageSeat.setImageResource(R.drawable.ghethuong);
            holder.imageSeat.setColorFilter(context.getResources().getColor(android.R.color.darker_gray));
        } else if (seat.getTrangThai().equals("Đang chọn")) {
            holder.imageSeat.setImageResource(R.drawable.ghethuong);
            holder.imageSeat.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            holder.imageSeat.setImageResource(R.drawable.ghethuong);
            holder.imageSeat.setColorFilter(context.getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public void setSeatList(List<Seat> seatList) {
        this.seatList = seatList;
        notifyDataSetChanged(); // Cập nhật lại RecyclerView khi có thay đổi dữ liệu
    }

    public static class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView loaiGhe, trangThai;
        ImageView imageSeat;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSeat = itemView.findViewById(R.id.imageSeat);
        }
    }
}
