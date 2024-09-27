package com.example.datvexemphim.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.model.ThanhToan;

import java.util.List;

public class VeDaDatAdapter extends RecyclerView.Adapter<VeDaDatAdapter.ViewHolder> {
    private List<ThanhToan> thanhToanList; // Danh sách các vé xem phim
    private Context context;

    public VeDaDatAdapter(Context context, List<ThanhToan> thanhToanList) {
        this.context = context;
        this.thanhToanList = thanhToanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ve_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThanhToan thanhToan = thanhToanList.get(position);
        String time = thanhToan.getSelectedTime() + " | " + thanhToan.getTimeDate();

        holder.tvCinemaName.setText(thanhToan.getCinemaName());
        holder.tvNameFilm.setText(thanhToan.getNameFilm());
        holder.tvViTriGhe.setText("Vị trí: " + thanhToan.getSelectedSeats());
        holder.tvTime.setText(time);

        // Xử lý sự kiện click cho itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVeDetailDialog(thanhToan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return thanhToanList.size();
    }

    // Phương thức hiển thị dialog chi tiết vé
    private void showVeDetailDialog(ThanhToan thanhToan) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_info_ve_user);

        TextView idVe = dialog.findViewById(R.id.idVe);
        TextView email = dialog.findViewById(R.id.email);
        TextView nameFilm = dialog.findViewById(R.id.nameFilm);
        TextView nameCinema = dialog.findViewById(R.id.nameCinema);
        TextView timeDate = dialog.findViewById(R.id.timeDate);
        TextView selectedSeats = dialog.findViewById(R.id.selectedSeats);
        TextView selectedCombo = dialog.findViewById(R.id.selectedCombo);
        TextView totalPrice = dialog.findViewById(R.id.totalPrice);
        Button btnThoat = dialog.findViewById(R.id.btnThoat);

        // Đổ dữ liệu vào các TextView trong dialog
        idVe.setText("ID User: " + thanhToan.getUserId());
        email.setText("Email: " + thanhToan.getUserEmail());
        nameFilm.setText("Tên phim: " + thanhToan.getNameFilm());
        nameCinema.setText("Tên rạp: " + thanhToan.getCinemaName());
        timeDate.setText("Thời gian: " + thanhToan.getSelectedTime() + " | " + thanhToan.getTimeDate());
        selectedSeats.setText("Vị trí ghế: " + thanhToan.getSelectedSeats());
        selectedCombo.setText("Combo: " + thanhToan.getSelectedCombo());
        totalPrice.setText("Thành tiền: " + thanhToan.getTotalPrice() + " VND");

        // Xử lý sự kiện cho button "Thoát"
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Hiển thị dialog
        dialog.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCinemaName, tvNameFilm, tvViTriGhe, tvTime;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCinemaName = itemView.findViewById(R.id.tv_cinemaName);
            tvNameFilm = itemView.findViewById(R.id.nameFilm);
            tvViTriGhe = itemView.findViewById(R.id.viTriGhe);
            tvTime = itemView.findViewById(R.id.tv_Time);
        }
    }
}

