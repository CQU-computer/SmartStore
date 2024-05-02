package com.example.smartstore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import chattingCircle.Chattingcircle_recommend;
import image_submit.Upload;
import in_out_rcd.in_out_rcd;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import search.search;
import self_edit_layout.self_edit_layout;

public class  MainActivity extends AppCompatActivity {

        private TextView layout_name;
        private String layout_id;
        private Button search_btn;
        private SharedPreferences preferences;
        private Button camera_btn;
        private Button manage_btn;
        private Button record_btn;
        private String family_num;
        private String stg_num;
        private String item_num;
        private TextView dataF;
        private TextView dataS;
        private TextView dataI;
        private TextView help1_btn;
        private ImageView helpMain_btn;

    @Override
      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        layout_id = String.valueOf(preferences.getInt("current_layout_id",-1));

        layout_name = findViewById(R.id.textView11);
        search_btn = findViewById(R.id.search_btn);
        camera_btn = findViewById(R.id.camera_btn);
        manage_btn = findViewById(R.id.manage_btn);
        record_btn = findViewById(R.id.record_btn);
        dataF = findViewById(R.id.familyNum);
        dataS = findViewById(R.id.StgNum);
        dataI = findViewById(R.id.itemNum);
        help1_btn = findViewById(R.id.help1);
        helpMain_btn = findViewById(R.id.help_main);
        help1_btn.setOnClickListener(v -> {
            helpMain_btn.setVisibility(View.VISIBLE);
            ScaleAnimation anim = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RESTART, 0.5f, Animation.RESTART, 0.5f);
            anim.setDuration(500);
            helpMain_btn.startAnimation(anim);
        });
        helpMain_btn.setOnClickListener(v -> helpMain_btn.setVisibility(View.GONE));

        record_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, in_out_rcd.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        camera_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, Upload.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        search_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, search.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        manage_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, self_edit_layout.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        TextView goto2 = findViewById(R.id.oneTOtwo);
        TextView goto3 = findViewById(R.id.oneTOthree);
        TextView goto4 = findViewById(R.id.oneTOfour);

        goto2.setOnClickListener(v -> {
            Intent intent = new Intent(this, Family.family.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
        });

        goto3.setOnClickListener(v -> {
            Intent intent = new Intent(this, Chattingcircle_recommend.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
        });

        goto4.setOnClickListener(v -> {
            Intent intent = new Intent(this, mine_page.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
        });

        updateUI();
        System.out.println("+++++++++++++++++++++++++++++++++++");
    }

    @Override
    protected void onResume() {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!1111111111");
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String source = extras.getString("source");
            if (source != null && source.equals("self_edit+yes")) {
                updateUI();
            }
        }
    }

    private void updateUI(){

        Thread t1 = new Thread(this::getFamilyNum);
        Thread t2 = new Thread(this::getStgNum);
        Thread t3 = new Thread(this::getItemNum);

        {t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        t2.start();
        try {
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        t3.start();
        try {
            t3.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }}
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        layout_name.setText(preferences.getString("current_layout_name","还没有场景哦！"));
        layout_id = String.valueOf(preferences.getInt("current_layout_id",-1));
        dataF.setText(family_num);
        dataS.setText(stg_num);
        dataI.setText(item_num);
    }
    public void getFamilyNum(){
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(JSON, "");
            String url = "http://120.26.248.74:8080/main/getFamNum?layout_id=" + layout_id;
            System.out.println("************"+url);
                    try {
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            family_num = response.body().string();
                        }
                        else {
                            System.out.println("响应码: " + response.code());
                            String responseBody = response.body().string();
                            System.out.println("响应体: " + responseBody);
                        }
                        response.body().close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
        }
    public void getStgNum(){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/main/getStgNum?layout_id=" + layout_id;

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                stg_num = response.body().string();
            }
            else {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            response.body().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void getItemNum(){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/main/getItemNum?layout_id=" + layout_id;

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                item_num = response.body().string();
            }
            else {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            response.body().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    }

