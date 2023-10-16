package com.example.cosplay_suit_app.Activity;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cosplay_suit_app.API;
import com.example.cosplay_suit_app.Adapter.Adapter_SanPham;
import com.example.cosplay_suit_app.DTO.DTO_SanPham;
import com.example.cosplay_suit_app.DTO.Favorite;
import com.example.cosplay_suit_app.DTO.SanPhamInterface;
import com.example.cosplay_suit_app.MainActivity;
import com.example.cosplay_suit_app.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Chitietsanpham extends AppCompatActivity {
    static String url = API.URL;
    static final String BASE_URL = url + "/product/";
    static final String BASE_URL_FAVoRITE = url + "/user/api/";
    String TAG = "chitietsp";
    ImageView img_backsp, img_pro, img_favorite;
    TextView tv_price, tv_name;
    ArrayList<DTO_SanPham> mlist;
    Adapter_SanPham adapter;
    RecyclerView rcv_5, rcv_bl;

    String idproduct, nameproduct, imageproduct;
    BottomSheetDialog bottomSheetDialog;
    Dialog fullScreenDialog;
    int priceproduct;
    boolean isMyFavorite = false;
    String id;
    static int slkho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chitietsanpham);
        Log.d(TAG, "onCreate: Đã vào chi tiết sp");
        SharedPreferences sharedPreferences = this.getSharedPreferences("User", this.MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");
        showDialog();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                //cong viec background viet o day
                Anhxa();
                Intent intent = getIntent();
                idproduct = intent.getStringExtra("id_product");
                nameproduct = intent.getStringExtra("name");
                priceproduct = intent.getIntExtra("price", 0);
                slkho = intent.getIntExtra("slkho", 0);
                imageproduct = intent.getStringExtra("image");
                loadFavorite();
                TextView tv_product = findViewById(R.id.tv_product);
                try {

                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                //tuong tac voi giao dien
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fullScreenDialog.dismiss();

                        Glide.with(Chitietsanpham.this).load(imageproduct).centerCrop().into(img_pro);
                        tv_name.setText(" " + nameproduct + " ");
                        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                        tv_price.setText("" + decimalFormat.format(priceproduct));
                        if (isMyFavorite) {
                            img_favorite.setImageResource(R.drawable.favorite_24);
                        } else {
                            img_favorite.setImageResource(R.drawable.ic_no_favorite_24);
                        }
                        img_backsp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onBackPressed();
                            }
                        });
                        tv_product.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Gọi hàm showDialog() và truyền các giá trị cần thiết
                                Intent intent = getIntent();
                                String idproduct = intent.getStringExtra("id_product");
                                String nameproduct = intent.getStringExtra("name");
                                int priceproduct = intent.getIntExtra("price", 0);
                                String imageproduct = intent.getStringExtra("image");
                                String about = intent.getStringExtra("about");

                                showDialog(Chitietsanpham.this, idproduct, nameproduct, priceproduct, imageproduct, about);
                            }
                        });
                        img_favorite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!id.equalsIgnoreCase("")) {
//                    if (isMyFavorite) {
//                        removeToFavorite(context, sanPham.getId());
//                    } else {
//                        addToFavorite(context, sanPham.getId());
//                    }
                                } else {
                                    new AlertDialog.Builder(Chitietsanpham.this).setTitle("Notification!!")
                                            .setMessage("You need to log in to add favorites,Do you want to log in??")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    startActivity(new Intent(Chitietsanpham.this, LoginActivity.class));
                                                    dialogInterface.dismiss();
                                                }
                                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();
                                }
                            }
                        });
                    }
                });
            }
        });










//        rcv_5 =findViewById(R.id.rcv_5);
//        mlist = new ArrayList<DTO_SanPham>();
//        adapter = new Adapter_SanPham(mlist, Chitietsanpham.this);
//        rcv_5.setAdapter(adapter);
//        GetListSanPham();







    }

    public void loadFavorite() {
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_FAVoRITE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        SanPhamInterface truyenInterface = retrofit.create(SanPhamInterface.class);
        Call<Favorite> objT = truyenInterface.list_favorite(id,idproduct);

        objT.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                if (response.isSuccessful()){
                    isMyFavorite = response.isSuccessful();
                    Log.e("bl", "BL: " + isMyFavorite );
                }else{
                    isMyFavorite = false;
                }
            }
            @Override
            public void onFailure(Call<Favorite> call, Throwable t) {
                Log.e("bl", "onFailure: " + t.getLocalizedMessage());
                isMyFavorite = false;
            }
        });

    }

    public void Anhxa() {
        img_pro = findViewById(R.id.img_pro);
        tv_price = findViewById(R.id.tv_price);
        tv_name = findViewById(R.id.tv_name);
        img_favorite = findViewById(R.id.img_favorite);
        img_backsp = findViewById(R.id.img_backsp);
    }

    void GetListSanPham() {
        // tạo gson
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // sử dụng interface
        SanPhamInterface sanPhamInterface = retrofit.create(SanPhamInterface.class);

        // tạo đối tượng
        Call<List<DTO_SanPham>> objCall = sanPhamInterface.lay_danh_sach();
        objCall.enqueue(new Callback<List<DTO_SanPham>>() {
            @Override
            public void onResponse(Call<List<DTO_SanPham>> call, Response<List<DTO_SanPham>> response) {
                if (response.isSuccessful()) {

                    mlist.clear();
                    mlist.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d("list", "onResponse: " + mlist.size());

                } else {
                    Toast.makeText(Chitietsanpham.this,
                            "Không lấy được dữ liệu" + response.message(), Toast.LENGTH_SHORT).show();
                }

//                GetListSanPham();
            }

            @Override
            public void onFailure(Call<List<DTO_SanPham>> call, Throwable t) {

            }
        });

    }

    public void showDialog(Context context, String idproduct, String nameproduct, int priceproduct, String imageproduct, String about) {
        bottomSheetDialog = new BottomSheetDialog(Chitietsanpham.this);

        View view = getLayoutInflater().inflate(R.layout.dialog_productdetail, null);
        bottomSheetDialog.setContentView(view);


        float screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        float halfScreenHeight = screenHeight / 1.5f;
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheetInternal != null) {
            ViewGroup.LayoutParams layoutParams = bottomSheetInternal.getLayoutParams();

            if (layoutParams != null) {
                layoutParams.height = (int) halfScreenHeight;
                bottomSheetInternal.setLayoutParams(layoutParams);

            }
        }

        // Hiển thị tên sản phẩm
        TextView tv_name = bottomSheetDialog.findViewById(R.id.tv_name);
        tv_name.setText("" + nameproduct);

        // Hiển thị giá sản phẩm
        TextView tv_price = bottomSheetDialog.findViewById(R.id.tv_price);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tv_price.setText("" + decimalFormat.format(priceproduct));

        // Hiển thị thông tin sản phẩm
        TextView tv_about = bottomSheetDialog.findViewById(R.id.tv_about);
        tv_about.setText(about);
        tv_about.setMaxLines(1);
        tv_about.setEllipsize(TextUtils.TruncateAt.END);
        TextView tv_read = bottomSheetDialog.findViewById(R.id.tv_read);
        final int[] count = {0};
        tv_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count[0] == 0) {
                    tv_about.setMaxLines(1000);
                    tv_read.setText("Return");
                    count[0] = 1;
                } else {
                    tv_about.setMaxLines(1);
                    tv_read.setText("Read more");
                    count[0] = 0;
                }
            }
        });

        //Thêm sản phẩm vào giỏ hàng
        Button btnaddcart = bottomSheetDialog.findViewById(R.id.btn_addcart);
        btnaddcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddcart(context, priceproduct, slkho, imageproduct);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    public static void dialogAddcart(Context context, int priceproduct, int slkho, String imageproduct) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_addcart);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        // Hiển thị giá sản phẩm
        TextView tvgiasp = dialog.findViewById(R.id.tv_giasp);
        tvgiasp.setText(+priceproduct + " VNĐ");

        // Hiển thị số lượng sản phẩm
        TextView tvslkho = dialog.findViewById(R.id.tv_slkho);
        tvslkho.setText("WareHouse: " + slkho);

        // Hiển thị image sản phẩm
        ImageView imgsp = dialog.findViewById(R.id.imgproduct);
        Glide.with(context).load(imageproduct).centerCrop().into(imgsp);

        // Chiều rộng full màn hình
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        // Chiều cao theo dialog màn hình
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // Đặt vị trí của dialog ở phía dưới cùng của màn hình
        layoutParams.gravity = Gravity.BOTTOM;

        window.setAttributes(layoutParams);
        window.setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();
    }


    void addFavorite(Context context, Favorite favorite) {
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        SanPhamInterface truyenInterface = retrofit.create(SanPhamInterface.class);
        Call<Favorite> objT = truyenInterface.add_favorite(favorite);

        objT.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Added to your favorites list...", Toast.LENGTH_SHORT).show();
////                    context.startActivity(new Intent(context, MainActivity.class));
////                    GetListSanPham();
//                    updateData(mlist);
                }
            }

            @Override
            public void onFailure(Call<Favorite> call, Throwable t) {
                Log.e("bl", "onFailure: " + t.getLocalizedMessage());
            }
        });
    }




    public void showDialog() {



        fullScreenDialog = new Dialog(Chitietsanpham.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        fullScreenDialog.setContentView(R.layout.dialog_load_chi_tiet_product);
        ImageView imageView = fullScreenDialog.findViewById(R.id.img_pro);
        ImageView imageView1 = fullScreenDialog.findViewById(R.id.img_bl);
        TextView tv_name = fullScreenDialog.findViewById(R.id.tv_name);
        TextView id_comment = fullScreenDialog.findViewById(R.id.id_comment);
        TextView id_daban = fullScreenDialog.findViewById(R.id.id_daban);
        LinearLayout id_li = fullScreenDialog.findViewById(R.id.id_li);
        TextView tv_product = fullScreenDialog.findViewById(R.id.tv_product);
        ImageView img_backsp = fullScreenDialog.findViewById(R.id.img_backsp);
        Animation mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(900);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);
        imageView.startAnimation(mAnimation);
        imageView1.startAnimation(mAnimation);
        tv_name.startAnimation(mAnimation);
        id_comment.startAnimation(mAnimation);
        id_daban.startAnimation(mAnimation);
        id_li.startAnimation(mAnimation);
        tv_product.startAnimation(mAnimation);
        img_backsp.startAnimation(mAnimation);
        Window window = fullScreenDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }

        fullScreenDialog.show();


    }
}