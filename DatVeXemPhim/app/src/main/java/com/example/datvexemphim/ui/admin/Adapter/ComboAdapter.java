package com.example.datvexemphim.ui.admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.admin.model.Combo;
import com.example.datvexemphim.ui.admin.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ComboAdapter extends RecyclerView.Adapter<ComboAdapter.ComboViewHolder> {
    private List<Combo> comboList;
    private Context context;
    private OnItemClickListener listener;

    public ComboAdapter(Context context, List<Combo> comboList) {
        this.context = context;
        this.comboList = comboList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Combo combo);
    }

    @NonNull
    @Override
    public ComboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_combo, parent, false);
        return new ComboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboViewHolder holder, int position) {
        Combo combo = comboList.get(position);
        holder.textComboName.setText(combo.getTenCombo());
        holder.textComboPrice.setText(String.valueOf(combo.getGiaCombo()));

        // Load image using Picasso, or set default image if anhCombo is null or empty
        if (combo.getAnhCombo() != null && !combo.getAnhCombo().isEmpty()) {
            Picasso.get().load(combo.getAnhCombo()).into(holder.imageCombo);
        } else {
            holder.imageCombo.setImageResource(R.drawable.hot_cinema); // Set your default image resource here
        }
    }

    @Override
    public int getItemCount() {
        return comboList.size();
    }

    public class ComboViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCombo;
        TextView textComboName, textComboPrice;

        public ComboViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCombo = itemView.findViewById(R.id.imageCombo);
            textComboName = itemView.findViewById(R.id.textComboName);
            textComboPrice = itemView.findViewById(R.id.textComboPrice);

            // Set click listener to the whole item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(comboList.get(position));
                    }
                }
            });
        }
    }
}


