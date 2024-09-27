package com.example.datvexemphim.ui.admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.admin.model.Phong;

import java.util.List;

public class PhongAdapter extends RecyclerView.Adapter<PhongAdapter.PhongViewHolder> {
    private List<Phong> phongList;
    private Context context;
    private OnItemClickListener listener;

    public PhongAdapter(Context context, List<Phong> phongList) {
        this.context = context;
        this.phongList = phongList;
    }

    // Interface xử lý sự kiện click vào item
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Phong phong);
    }

    @NonNull
    @Override
    public PhongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho item của RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_phong, parent, false);
        return new PhongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhongViewHolder holder, int position) {
        // Lấy thông tin từ phongList
        Phong phong = phongList.get(position);

        // Set dữ liệu vào các TextView
        holder.textIdPhong.setText("Phòng: " + phong.getIdPhong());
        holder.textsoDay.setText("Số dãy: " + phong.getsoDay());
        holder.textsoCot.setText("Số cột: " + phong.getsoCot());
    }

    @Override
    public int getItemCount() {
        return phongList.size();
    }

    // ViewHolder giữ các view cần sử dụng
    public class PhongViewHolder extends RecyclerView.ViewHolder {
        TextView textIdPhong, textsoDay, textsoCot;

        public PhongViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ view từ layout
            textIdPhong = itemView.findViewById(R.id.textIdPhong);
            textsoDay = itemView.findViewById(R.id.textsoDay);
            textsoCot = itemView.findViewById(R.id.textsoCot);

            // Set click listener cho item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(phongList.get(position));
                }
            });
        }
    }
}
