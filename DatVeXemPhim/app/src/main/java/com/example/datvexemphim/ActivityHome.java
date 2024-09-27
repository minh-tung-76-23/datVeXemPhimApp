package com.example.datvexemphim;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.datvexemphim.MainActivity;
import com.example.datvexemphim.R;
import com.example.datvexemphim.databinding.ActivityHomeBinding;
import com.example.datvexemphim.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class ActivityHome extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private NavController navController;
    private FirebaseAuth mAuth;
    private ImageView imgAvatar;
    private DrawerLayout mDrawerLayout;
    private TextView textViewName, textViewEmail;
    private NavigationView mNavigationView;
    private final HomeFragment mHomeFragment = new HomeFragment();
    final private ActivityResultLauncher<Intent> activityResultCaller = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK) {
                        Intent intent = o.getData();
                        if (intent != null) {
                            Uri uri = intent.getData();
                            // Ví dụ về cách gọi phương thức static từ ActivityHome
                            HomeFragment.setUri(mHomeFragment, uri);


                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                HomeFragment.setBitmapImgView(bitmap);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            Glide.with(ActivityHome.this).load(uri).error(R.drawable.img).into(imgAvatar);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUi();
        showUserInformation();

        // Initialize Firebase if not already initialized
        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(binding.appBarActivityHome.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        mNavigationView = binding.navView; // Khởi tạo mNavigationView từ binding

        // Setup Navigation Controller và AppBarConfiguration
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_activity_home);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_account, R.id.nav_listFilm, R.id.nav_rap)
                .setOpenableLayout(drawer)
                .build();

        // Setup Toolbar và NavigationUI cho DrawerLayout
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, navController);

        // Setup BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_account) {
                navController.navigate(R.id.nav_account);
                mNavigationView.setCheckedItem(R.id.nav_account);
            } else if (id == R.id.action_listFilm) {
                navController.navigate(R.id.nav_listFilm);
                mNavigationView.setCheckedItem(R.id.nav_listFilm);
            } else if (id == R.id.action_rap) {
                navController.navigate(R.id.nav_rap);
                mNavigationView.setCheckedItem(R.id.nav_rap);
            }
            return true;
        });

        // Xử lý sự kiện khi chọn mục trong NavigationView
        mNavigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_account) {
                navController.navigate(R.id.nav_account);
                bottomNavigationView.getMenu().findItem(R.id.action_account).setChecked(true);
            } else if (id == R.id.nav_listFilm) {
                navController.navigate(R.id.nav_listFilm);
                bottomNavigationView.getMenu().findItem(R.id.action_listFilm).setChecked(true);
            } else if (id == R.id.nav_rap) {
                navController.navigate(R.id.nav_rap);
                bottomNavigationView.getMenu().findItem(R.id.action_rap).setChecked(true);
            } else if (id == R.id.nav_logout) {
                // Hiển thị hộp thoại xác nhận đăng xuất
                showLogoutConfirmationDialog();
            }

            // Đóng drawer khi đã chọn mục trong NavigationView
            drawer.closeDrawers();
            return true;
        });
    }

    private void initUi() {
        mNavigationView = binding.navView; // Khởi tạo mNavigationView từ binding
        imgAvatar = mNavigationView.getHeaderView(0).findViewById(R.id.image_avatar);
        textViewName = mNavigationView.getHeaderView(0).findViewById(R.id.textview_name);
        textViewEmail = mNavigationView.getHeaderView(0).findViewById(R.id.textview_email);
    }

    // Phương thức hiển thị hộp thoại xác nhận đăng xuất
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Thực hiện đăng xuất: chuyển đến form đăng nhập
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ActivityHome.this, MainActivity.class);
                startActivity(intent);
                finish(); // Kết thúc Activity hiện tại sau khi đăng xuất
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng hộp thoại và không làm gì
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            // Hiển thị hộp thoại xác nhận đăng xuất
            showLogoutConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void showUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if (name == null) {
            textViewName.setVisibility(View.GONE);
        } else {
            textViewName.setVisibility(View.VISIBLE);
            textViewName.setText(name);

        }

        textViewEmail.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.img).into(imgAvatar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultCaller.launch(Intent.createChooser(intent, "Select Picture"));
    }
}

