package com.example.datvexemphim.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.R;
import com.example.datvexemphim.adapter.CinemaAdapter;
import com.example.datvexemphim.databinding.FragmentGalleryBinding;
import com.example.datvexemphim.ui.api.ApiService;
import com.example.datvexemphim.ui.model.Cinema;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SlideshowFragment extends Fragment {

    private RecyclerView recyclerView;
    private CinemaAdapter cinemaAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        recyclerView = root.findViewById(R.id.rCVRap);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1)); // 1 cột
        recyclerView.setHasFixedSize(true);

        cinemaAdapter = new CinemaAdapter(requireContext()); // Sử dụng requireContext() ở đây
        recyclerView.setAdapter(cinemaAdapter);

        // Gọi API để lấy danh sách rạp chiếu phim
        callApi();

        return root;
    }

    private void callApi() {
        ApiService.apiService.getAllCinemas().enqueue(new Callback<List<Cinema>>() {
            @Override
            public void onResponse(Call<List<Cinema>> call, Response<List<Cinema>> response) {
                if (response.isSuccessful()) {
                    List<Cinema> cinemaList = response.body();
                    if (cinemaList != null && !cinemaList.isEmpty()) {
                        // Cập nhật danh sách rạp trong Adapter
                        cinemaAdapter.setCinemaList(cinemaList);
                    } else {
                        Toast.makeText(getActivity(), "Không có dữ liệu rạp", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cinema>> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

