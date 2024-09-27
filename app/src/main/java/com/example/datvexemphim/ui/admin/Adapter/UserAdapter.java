package com.example.datvexemphim.ui.admin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.admin.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_account, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvID;
        TextView tvEmail;
        TextView tvUserRole;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tv_idUser);
            tvEmail = itemView.findViewById(R.id.tv_emailAccount);
            tvUserRole = itemView.findViewById(R.id.tv_isUser);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        listener.onItemClick(userList.get(getAdapterPosition()));
                    }
                }
            });
        }

        public void bind(User user) {
            tvID.setText("ID: " + user.getUid());
            tvEmail.setText("Email: " + user.getEmail());
            String isUser = user.getIsUser();
            int userType = Integer.parseInt(isUser);

            if (userType == 1) {
                tvUserRole.setText("Quyền: Người dùng");
            } else {
                tvUserRole.setText("Quyền: Admin");
            }
        }
    }
}


