package com.example.datvexemphim.ui.admin.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.admin.model.VeXemPhim;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.List;

public class VeAdapter extends RecyclerView.Adapter<VeAdapter.ViewHolder> {
    private List<VeXemPhim> veXemPhimList; // Danh sách các vé xem phim
    private Context context;
    private ProgressDialog progressDialog;

    public VeAdapter(Context context, List<VeXemPhim> veXemPhimList) {
        this.context = context;
        this.veXemPhimList = veXemPhimList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ve_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VeXemPhim veXemPhim = veXemPhimList.get(position);
        String time = veXemPhim.getSelectedTime() + " | " + veXemPhim.getTimeDate();

        holder.tvCinemaName.setText(veXemPhim.getCinemaName());
        holder.tvNameFilm.setText(veXemPhim.getNameFilm());
        holder.tvViTriGhe.setText("Vị trí: " + veXemPhim.getSelectedSeats());
        holder.tvTime.setText(time);

        // Xử lý sự kiện click cho itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVeDetailDialog(veXemPhim);
            }
        });
    }

    @Override
    public int getItemCount() {
        return veXemPhimList.size();
    }

    // Phương thức hiển thị dialog chi tiết vé
    private void showVeDetailDialog(VeXemPhim veXemPhim) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_info_ve_users);

        TextView idVe = dialog.findViewById(R.id.idVe);
        TextView email = dialog.findViewById(R.id.email);
        TextView nameFilm = dialog.findViewById(R.id.nameFilm);
        TextView nameCinema = dialog.findViewById(R.id.nameCinema);
        TextView timeDate = dialog.findViewById(R.id.timeDate);
        TextView selectedSeats = dialog.findViewById(R.id.selectedSeats);
        TextView selectedCombo = dialog.findViewById(R.id.selectedCombo);
        TextView totalPrice = dialog.findViewById(R.id.totalPrice);
        Button btnThoat = dialog.findViewById(R.id.btnThoat);
        Button btnXoa = dialog.findViewById(R.id.btnXoa);

        // Đổ dữ liệu vào các TextView trong dialog
        idVe.setText("ID: " + veXemPhim.getKey());
        email.setText("Email: " + veXemPhim.getUserEmail());
        nameFilm.setText("Tên phim: " + veXemPhim.getNameFilm());
        nameCinema.setText("Tên rạp: " + veXemPhim.getCinemaName());
        timeDate.setText("Thời gian: " + veXemPhim.getSelectedTime() + " | " + veXemPhim.getTimeDate());
        selectedSeats.setText("Vị trí ghế: " + veXemPhim.getSelectedSeats());
        selectedCombo.setText("Combo: " + veXemPhim.getSelectedCombo());
        totalPrice.setText("Thành tiền: " + veXemPhim.getTotalPrice() + " VND");

        // Xử lý sự kiện cho button "Thoát"
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Xử lý sự kiện cho button "Xóa"
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getIdVe = veXemPhim.getKey(); // Lấy key của vé xem phim hiện tại

                // Hiển thị ProgressDialog
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Đang xóa vé...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                deleteVe(getIdVe);
            }

            private void deleteVe(String idVe) {
                // Thực hiện xóa vé
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("qlrapphim/veXemPhim").child(idVe);

                databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Đóng ProgressDialog khi xóa thành công
                        progressDialog.dismiss();
                        // Xóa thành công, có thể thông báo hoặc cập nhật giao diện nếu cần
                        Toast.makeText(context, "Đã xóa vé thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Đóng ProgressDialog khi xóa thất bại
                        progressDialog.dismiss();
                        // Xảy ra lỗi khi xóa
                        Toast.makeText(context, "Lỗi khi xóa vé: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        // Hiển thị dialog chi tiết vé
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
