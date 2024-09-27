package com.example.datvexemphim;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datvexemphim.adapter.FilmAdapter;
import com.example.datvexemphim.ui.model.filmAPI;
import com.squareup.picasso.Picasso;

public class DetailFilmActivity extends AppCompatActivity {

    private TextView nameFilmDetail, timeFilmDetail, countryFilmDetail, keywordFilmDetail, detailFilm;
    private ImageView img_poster;
    private WebView webView;
    private String trailerUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_film);

        nameFilmDetail = findViewById(R.id.nameFilmDetail);
        img_poster = findViewById(R.id.imagePlaceholder);
        timeFilmDetail = findViewById(R.id.timeFilmDetail);
        countryFilmDetail = findViewById(R.id.countryFilmDetail);
        keywordFilmDetail = findViewById(R.id.keywordFilmDetail);
        detailFilm = findViewById(R.id.detailedInfo);

        // Initialize WebView
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Hide loading indicator or perform other operations if needed
            }
        });

        // Get film ID from intent
        int filmId = getIntent().getIntExtra(ActivityChiTietFilm.EXTRA_FILM_ID, 0);

        // Load film details from API
        FilmAdapter filmAdapter = new FilmAdapter(this);
        filmAdapter.getFilmById(filmId, new FilmAdapter.OnFilmLoadedListener() {
            @Override
            public void onFilmLoaded(filmAPI film) {
                nameFilmDetail.setText(film.getName());
                timeFilmDetail.setText("Thời lượng: " + film.getDuration());
                countryFilmDetail.setText("Quốc gia: " + film.getCountry());
                keywordFilmDetail.setText("Từ khoá: " + film.getSeo_keywords());
                detailFilm.setText(film.getSeo_description());

                String posterUrl = handlePosterUrl(film.getPoster());
                Picasso.get()
                        .load(posterUrl)
                        .placeholder(R.drawable.img)
                        .error(R.drawable.hot_cinema)
                        .into(img_poster);

                // Set trailer URL
                trailerUrl = film.getTrailer();

                // Load trailer into WebView
                if (!TextUtils.isEmpty(trailerUrl)) {
                    if (trailerUrl.startsWith("//")) {
                        // Handle YouTube URL without scheme (http/https)
                        trailerUrl = "https:" + trailerUrl;
                    }

                    // Check if the URL is a valid YouTube URL
                    if (trailerUrl.contains("youtube.com")) {
                        // Load the YouTube video using YouTube embed URL
                        String youtubeLink = trailerUrl.replace("https:", "");
                        webView.loadDataWithBaseURL("https://www.youtube.com", "<iframe width=\"100%\" height=\"100%\" src=\"" + youtubeLink + "\" frameborder=\"0\" allowfullscreen></iframe>", "text/html", "utf-8", null);
                    } else {
                        // For other URLs, load normally
                        webView.loadUrl(trailerUrl);
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(DetailFilmActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String handlePosterUrl(String posterUrl) {
        if (!TextUtils.isEmpty(posterUrl) && !posterUrl.startsWith("http")) {
            posterUrl = "https://rapchieuphim.com" + posterUrl;
        }
        return posterUrl;
    }
}
