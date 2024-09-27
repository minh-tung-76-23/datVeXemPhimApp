package com.example.datvexemphim.ui.admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.admin.model.GheNgoi;

import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {
    private Context context;
    private List<GheNgoi> gheNgoiList;

    public SeatAdapter(Context context, List<GheNgoi> gheNgoiList) {
        this.context = context;
        this.gheNgoiList = gheNgoiList;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ghe_ngoi, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        GheNgoi gheNgoi = gheNgoiList.get(position);

        /// Bind data to UI elements in item_ghe_ngoi.xml
        holder.tvphongChieu.setText(String.valueOf(gheNgoi.getPhongChieu()));
        holder.tvIdGhe.setText(gheNgoi.getId());
        holder.tvLoaiGhe.setText(gheNgoi.getLoaiGhe());
        holder.tvTinhTrangGhe.setText(gheNgoi.getTinhTrang());

        // Xử lý sự kiện click cho itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thêm mã logic xử lý khi click vào đây
                // Ví dụ: chọn ghế, đặt vé, hiển thị thông tin chi tiết
                Toast.makeText(context, "Clicked position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return gheNgoiList.size();
    }

    public static class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView tvphongChieu, tvIdGhe, tvLoaiGhe, tvTinhTrangGhe;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvphongChieu = itemView.findViewById(R.id.tvphongChieu);
            tvIdGhe = itemView.findViewById(R.id.idGhe);
            tvLoaiGhe = itemView.findViewById(R.id.tvloaiGhe);
            tvTinhTrangGhe = itemView.findViewById(R.id.tinhTrangGhe);
        }
    }
}



