package com.example.datvexemphim.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.datvexemphim.ActivityChiTietFilm;
import com.example.datvexemphim.R;
import com.example.datvexemphim.ui.api.ApiService;
import com.example.datvexemphim.ui.model.filmAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectedFilmAdapter extends RecyclerView.Adapter<SelectedFilmAdapter.ViewHolder> {

    private List<filmAPI> filmList = new ArrayList<>();
    private List<filmAPI> filteredList = new ArrayList<>();
    private Context context;
    private OnItemClickListener itemClickListener; // Sử dụng interface đã định nghĩa

    public SelectedFilmAdapter(Context context) {
        this.context = context;
    }

    public void setFilmList(List<filmAPI> filmList) {
        this.filmList = filmList;
        this.filteredList = new ArrayList<>(filmList); // Sao chép danh sách phim ban đầu
        notifyDataSetChanged();
    }

    // Phương thức lấy thông tin phim theo ID
    public void getFilmById(int filmId, OnFilmLoadedListener listener) {
        // Gọi phương thức từ ApiService để lấy dữ liệu phim
        ApiService.apiService.selectApiFilm().enqueue(new Callback<List<filmAPI>>() {
            @Override
            public void onResponse(Call<List<filmAPI>> call, Response<List<filmAPI>> response) {
                if (response.isSuccessful()) {
                    List<filmAPI> filmList = response.body();
                    if (filmList != null && !filmList.isEmpty()) {
                        for (filmAPI film : filmList) {
                            if (film.getId() == filmId) {
                                // Gọi callback khi tìm thấy phim có id tương ứng
                                listener.onFilmLoaded(film);
                                return;
                            }
                        }
                    }
                    // Gọi callback lỗi khi không tìm thấy phim
                    listener.onError("Không tìm thấy thông tin phim");
                } else {
                    // Xử lý lỗi khi không thành công
                    listener.onError("Lỗi: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<filmAPI>> call, Throwable t) {
                // Xử lý lỗi khi không kết nối được đến server
                listener.onError("Không thể kết nối đến server");
            }
        });
    }

    // Interface để xử lý sự kiện khi load phim thành công hoặc lỗi
    public interface OnFilmLoadedListener {
        void onFilmLoaded(filmAPI film);

        void onError(String errorMessage);
    }

    // Phương thức lọc danh sách phim dựa trên từ khóa
    public void filter(String text) {
        filteredList.clear();
        if (TextUtils.isEmpty(text)) {
            filteredList.addAll(filmList);
        } else {
            String searchText = text.toLowerCase().trim();
            for (filmAPI film : filmList) {
                if (film.getName().toLowerCase().contains(searchText)) {
                    filteredList.add(film);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iem_movie_sel_cinema, parent, false);
        return new ViewHolder(view);
    }

    // Thiết lập OnItemClickListener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    // Interface để xử lý sự kiện click vào item phim
    public interface OnItemClickListener {
        void onItemClick(filmAPI film);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final filmAPI movie = filteredList.get(position); // Sử dụng final để sử dụng trong phương thức onClick
        holder.tvDuration.setText(String.valueOf(movie.getDuration()));
        holder.tvName.setText(movie.getName());

        String posterUrl = handlePosterUrl(movie.getPoster());
        // Sử dụng Glide để tải ảnh vào ImageView
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.img) // Ảnh mặc định trong lúc chờ
                .error(R.drawable.hot_cinema) // Ảnh hiển thị khi có lỗi
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Lưu cache
        Glide.with(context)
                .load(posterUrl)
                .apply(requestOptions)
                .into(holder.ivPoster);

        // Xử lý sự kiện click vào item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(movie); // Gọi callback khi click vào item
                }
            }
        });
    }

    // Phương thức để xử lý đường dẫn ảnh trước khi sử dụng Glide
    private String handlePosterUrl(String posterUrl) {
        // Kiểm tra nếu đường dẫn không có domain hoặc scheme, thêm domain vào đầu
        if (!TextUtils.isEmpty(posterUrl) && !posterUrl.startsWith("http")) {
            posterUrl = "https://rapchieuphim.com" + posterUrl;
        }
        return posterUrl;
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDuration, tvName;
        ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvName = itemView.findViewById(R.id.tvName);
            ivPoster = itemView.findViewById(R.id.ivPoster);
        }
    }
}


