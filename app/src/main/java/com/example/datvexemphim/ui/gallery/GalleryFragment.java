package com.example.datvexemphim.ui.gallery;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datvexemphim.adapter.FilmAdapter;
import com.example.datvexemphim.databinding.FragmentGalleryBinding;
import com.example.datvexemphim.ui.api.ApiService;
import com.example.datvexemphim.ui.model.FilmListResponse;
import com.example.datvexemphim.ui.model.filmAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private RecyclerView recyclerView;
    private FilmAdapter filmAdapter;
    private List<filmAPI> filmList = new ArrayList<>(); // Danh sách phim từ API

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.rCVLichChieu;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 8, true));
        recyclerView.setHasFixedSize(true);

        // Khởi tạo Adapter và truyền context
        filmAdapter = new FilmAdapter(getContext());
        recyclerView.setAdapter(filmAdapter);

        EditText editTextSearch = binding.textViewSearch;
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Không cần xử lý trước khi thay đổi văn bản
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Xử lý khi văn bản trong EditText thay đổi
                String searchText = charSequence.toString().toLowerCase(Locale.getDefault());
                filmAdapter.filter(searchText); // Gọi phương thức filter của adapter để lọc danh sách phim
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Sau khi văn bản đã thay đổi
            }
        });

        // Gọi API để lấy danh sách phim
        callAPI();

        return root;
    }

    private void callAPI() {
        ApiService.apiService.selectApiFilm().enqueue(new Callback<List<filmAPI>>() {
            @Override
            public void onResponse(Call<List<filmAPI>> call, Response<List<filmAPI>> response) {
                if (response.isSuccessful()) {
                    List<filmAPI> films = response.body();
                    if (films != null) {
                        filmList.clear();
                        filmList.addAll(films);
                        filmAdapter.setFilmList(filmList); // Cập nhật danh sách phim trong adapter
                    } else {
                        Toast.makeText(getActivity(), "Không có dữ liệu phim", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<filmAPI>> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}



