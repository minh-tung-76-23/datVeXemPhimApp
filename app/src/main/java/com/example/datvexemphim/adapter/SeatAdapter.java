package com.example.datvexemphim.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.model.Seat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private Context context;
    private List<Seat> seatList;
    private TextView textSeatId, textTotalPrice;
    private Button btnTiepTuc;
    private List<Integer> selectedPositions = new ArrayList<>();
    private DatabaseReference loaiGheRef; // Tham chiếu đến bảng loaiGhe trong Firebase Realtime Database
    private int totalPrice = 0;

    public SeatAdapter(Context context, List<Seat> seatList, TextView textSeatId, TextView textTotalPrice, Button btnTiepTuc) {
        this.context = context;
        this.seatList = seatList;
        this.textSeatId = textSeatId;
        this.textTotalPrice = textTotalPrice;
        this.btnTiepTuc = btnTiepTuc;
        this.loaiGheRef = FirebaseDatabase.getInstance().getReference().child("qlrapphim").child("loaiGhe");
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

        holder.textSeatId.setText(seat.getId());

        switch (seat.getLoaiGhe()) {
            case "GT":
                holder.imageSeat.setImageResource(R.drawable.ghethuong);
                holder.imageSeat.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                break;
            case "GD":
                holder.imageSeat.setImageResource(R.drawable.ghedoi);
                int widthInPixels = (int) (64 * context.getResources().getDisplayMetrics().density);
                holder.imageSeat.getLayoutParams().width = widthInPixels;
                break;
            case "GV":
                holder.imageSeat.setImageResource(R.drawable.ghevip);
                holder.imageSeat.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                break;
            default:
                holder.imageSeat.setImageResource(R.drawable.ghethuong);
                holder.imageSeat.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                break;
        }

        if (seat.getTinhTrang() != null) {
            switch (seat.getTinhTrang()) {
                case "trống":
                    holder.imageSeat.setColorFilter(ContextCompat.getColor(context, R.color.colorEmptySeat));
                    break;
                case "đã bán":
                    holder.imageSeat.setColorFilter(ContextCompat.getColor(context, R.color.colorSoldSeat));
                    break;
                default:
                    holder.imageSeat.setColorFilter(ContextCompat.getColor(context, R.color.colorEmptySeat));
                    break;
            }
        } else {
            holder.imageSeat.setColorFilter(ContextCompat.getColor(context, R.color.black));
        }

        // Handle seat selection
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Seat clickedSeat = seatList.get(adapterPosition);
                    if (clickedSeat.getTinhTrang() != null && clickedSeat.getTinhTrang().equals("trống")) {
                        // Toggle selection
                        if (selectedPositions.contains(adapterPosition)) {
                            selectedPositions.remove(Integer.valueOf(adapterPosition));
                            holder.imageSeat.setColorFilter(ContextCompat.getColor(context, R.color.colorEmptySeat));
                        } else {
                            selectedPositions.add(adapterPosition);
                            holder.imageSeat.setColorFilter(ContextCompat.getColor(context, R.color.colorSeatting));
                        }

                        // Update textSeatId with selected seat names
                        updateSelectedSeatsText();

                        // Calculate total price
                        calculateTotalPrice();
                    }
                }
            }
        });

        // Highlight selected seats
        if (selectedPositions.contains(position)) {
            holder.imageSeat.setColorFilter(ContextCompat.getColor(context, R.color.colorSeatting));
        } else {
            if (seat.getTinhTrang() != null && seat.getTinhTrang().equals("đã bán")) {
                holder.imageSeat.setColorFilter(ContextCompat.getColor(context, R.color.colorSoldSeat));
            } else {
                holder.imageSeat.setColorFilter(ContextCompat.getColor(context, R.color.colorEmptySeat));
            }
        }

        holder.imageSeat.requestLayout();
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public List<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    private void updateSelectedSeatsText() {
        if (selectedPositions.isEmpty()) {
            // Không có ghế nào được chọn, ẩn các thành phần
            textSeatId.setVisibility(View.GONE);
            textTotalPrice.setVisibility(View.GONE);
            btnTiepTuc.setVisibility(View.GONE);
        } else {
            // Có ghế được chọn, hiển thị các thành phần
            StringBuilder selectedSeatNames = new StringBuilder("Ghế đã chọn:\n");
            for (int position : selectedPositions) {
                Seat seat = seatList.get(position);
                selectedSeatNames.append(seat.getId()).append(", ");
            }
            if (selectedSeatNames.length() > 0) {
                selectedSeatNames.setLength(selectedSeatNames.length() - 2); // Remove the last ", "
            }
            textSeatId.setText(selectedSeatNames.toString());
            textSeatId.setVisibility(View.VISIBLE);
            textTotalPrice.setVisibility(View.VISIBLE);
            btnTiepTuc.setVisibility(View.VISIBLE);
        }
    }

    private void calculateTotalPrice() {
        totalPrice = 0; // Đặt lại tổng tiền về 0 trước khi tính toán lại
        for (int position : selectedPositions) {
            Seat seat = seatList.get(position);
            String loaiGheId = seat.getLoaiGhe();

            // Query loaiGheRef to get giaGhe
            loaiGheRef.child(loaiGheId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int giaGhe = dataSnapshot.child("giaGhe").getValue(Integer.class);
                        totalPrice += giaGhe;
                        Locale locale = new Locale("vi", "VN"); // Đặt locale cho tiền Việt Nam
                        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
                        String formattedPrice = currencyFormatter.format(totalPrice);
                        textTotalPrice.setText("Tổng tiền:\n" + formattedPrice);
                        textTotalPrice.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("SeatAdapter", "Failed to read value.", databaseError.toException());
                }
            });
        }
    }

    public static class SeatViewHolder extends RecyclerView.ViewHolder {
        ImageView imageSeat;
        TextView textSeatId;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSeat = itemView.findViewById(R.id.imageSeat);
            textSeatId = itemView.findViewById(R.id.textSeatId);
        }
    }
}




