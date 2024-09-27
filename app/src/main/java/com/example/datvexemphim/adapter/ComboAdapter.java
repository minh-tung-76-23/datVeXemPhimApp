package com.example.datvexemphim.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.ActivityChonComBo;
import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.model.ComboItem;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ComboAdapter extends RecyclerView.Adapter<ComboAdapter.ComboViewHolder> {

    private List<ComboItem> comboItemList;
    private Context context;
    private Map<String, Integer> quantityMap; // Map lưu trữ số lượng của từng combo
    private double initialTotalPrice; // Giá trị ban đầu

    public ComboAdapter(Context context, List<ComboItem> comboItemList, double initialTotalPrice) {
        this.context = context;
        this.comboItemList = comboItemList;
        this.quantityMap = new HashMap<>(); // Khởi tạo Map
        this.initialTotalPrice = initialTotalPrice;

        // Khởi tạo số lượng ban đầu cho từng combo
        for (ComboItem comboItem : comboItemList) {
            quantityMap.put(comboItem.getIdCombo(), 0); // Số lượng ban đầu là 0
        }
    }

    @NonNull
    @Override
    public ComboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.combo_item, parent, false);
        return new ComboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboViewHolder holder, int position) {
        ComboItem comboItem = comboItemList.get(position);

        // Set dữ liệu vào các view trong item_combo
        holder.textComboName.setText(comboItem.getTenCombo());
        holder.textMotaCombo.setText(comboItem.getMoTa());
        Locale locale = new Locale("vi", "VN"); // Đặt locale cho tiền Việt Nam
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        String formattedPrice = currencyFormatter.format(comboItem.getGiaCombo());
        holder.textComboPrice.setText(formattedPrice);

        // Load ảnh từ URL sử dụng thư viện Picasso hoặc Glide
        Picasso.get().load(comboItem.getAnhCombo()).into(holder.imageCombo);

        // Set số lượng vào TextView từ Map
        holder.textQuantity.setText(String.valueOf(quantityMap.get(comboItem.getIdCombo())));

        // Xử lý sự kiện cộng và trừ số lượng
        holder.textDecrease.setOnClickListener(v -> {
            int quantity = quantityMap.get(comboItem.getIdCombo());
            if (quantity > 0) {
                quantity--;
                quantityMap.put(comboItem.getIdCombo(), quantity);
                holder.textQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        holder.textIncrease.setOnClickListener(v -> {
            int quantity = quantityMap.get(comboItem.getIdCombo());
            quantity++;
            quantityMap.put(comboItem.getIdCombo(), quantity);
            holder.textQuantity.setText(String.valueOf(quantity));
            updateTotalPrice();
        });
    }

    @Override
    public int getItemCount() {
        return comboItemList.size();
    }

    public static class ComboViewHolder extends RecyclerView.ViewHolder {

        ImageView imageCombo;
        TextView textComboName, textMotaCombo, textComboPrice;
        TextView textDecrease, textIncrease;
        TextView textQuantity;

        public ComboViewHolder(View itemView) {
            super(itemView);
            imageCombo = itemView.findViewById(R.id.imageCombo);
            textComboName = itemView.findViewById(R.id.textComboName);
            textMotaCombo = itemView.findViewById(R.id.textMotaCombo);
            textComboPrice = itemView.findViewById(R.id.textComboPrice);
            textDecrease = itemView.findViewById(R.id.textDecrease);
            textIncrease = itemView.findViewById(R.id.textIncrease);
            textQuantity = itemView.findViewById(R.id.textQuantity);
        }
    }

    // Phương thức để cập nhật tổng giá trị
    private void updateTotalPrice() {
        double totalPrice = initialTotalPrice;

        for (ComboItem comboItem : comboItemList) {
            int quantity = quantityMap.get(comboItem.getIdCombo());
            totalPrice += quantity * comboItem.getGiaCombo();
        }

        ((ActivityChonComBo) context).updateTotalPrice(totalPrice);
    }

    public List<ComboItem> getSelectedCombosWithQuantity() {
        List<ComboItem> selectedCombos = new ArrayList<>();
        for (ComboItem comboItem : comboItemList) {
            int quantity = quantityMap.get(comboItem.getIdCombo());
            if (quantity > 0) {
                comboItem.setQuantity(quantity); // Set số lượng vào ComboItem
                selectedCombos.add(comboItem);
            }
        }
        return selectedCombos;
    }
}


