package com.example.datvexemphim.ui.admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.datvexemphim.ui.admin.model.PhongChieu;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<PhongChieu> {
    private Context context;
    private List<PhongChieu> phongChieuList;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<PhongChieu> phongChieuList) {
        super(context, resource, phongChieuList);
        this.context = context;
        this.phongChieuList = phongChieuList;
    }

    @Override
    public int getCount() {
        return phongChieuList.size();
    }

    @Nullable
    @Override
    public PhongChieu getItem(int position) {
        return phongChieuList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(phongChieuList.get(position).getIdPhong());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(phongChieuList.get(position).getIdPhong());

        return convertView;
    }
}
