package com.example.datvexemphim.ui.admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.admin.model.loaiGhe;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class LoaiGheAdapter extends RecyclerView.Adapter<LoaiGheAdapter.ViewHolder> {
    private List<loaiGhe> loaiGheList;
    private Context context;

    public LoaiGheAdapter(List<loaiGhe> loaiGheList, Context context) {
        this.loaiGheList = loaiGheList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loai_ghe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        loaiGhe item = loaiGheList.get(position);
        holder.tVtenGhe.setText(item.getLoai_ghe());
        Locale locale = new Locale("vi", "VN"); // Đặt locale cho tiền Việt Nam
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        String formattedPrice = currencyFormatter.format(item.getGiaGhe());
        holder.tVGiaGhe.setText(formattedPrice);
    }

    @Override
    public int getItemCount() {
        return loaiGheList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tVtenGhe, tVGiaGhe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tVtenGhe = itemView.findViewById(R.id.tVtenGhe);
            tVGiaGhe = itemView.findViewById(R.id.tVGiaGhe);
        }
    }
}
