package in_out_rcd;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartstore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class in_out_rcd extends AppCompatActivity {
    public static List<String> test_information = new ArrayList<>(7);  //用于点击各个 “日” 显示的文本
    public static Map<Boolean, ArrayList<inoutClass>> info = new HashMap<>();
    public List<String> day = new ArrayList<>(7);  //用于记录日历中每一块要显示的“日”  左到右：现在到以前
    public List<String> month = new ArrayList<>(7);//用于记录日历中每一块要显示的“月”
    private String Current_layout_id;
    private LinearLayout page;

    public static Map<Integer,ArrayList<View>> OUT = new HashMap<>();
    public static Map<Integer,ArrayList<View>> IN = new HashMap<>();
    public static boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_out_rcd);

        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        Current_layout_id = String.valueOf(preferences.getInt("current_layout_id",-1));

        flag = true;  //入库

        page = findViewById(R.id.page);
        get_post_days();

        {
            Thread t1 = new Thread(this::updateDataTime);
            Thread t2 = new Thread(this::getInInfo);
            Thread t3 = new Thread(this::getOutInfo);

            t1.start();
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
            }
        }

        updateUI();
        add_time at = new add_time(this, findViewById(R.id.date_time_bar), page);
        at.add_date_item(0, day, month);

//****平滑切换****
        {
            //白到黑
            findViewById(R.id.rcd_change_btn_dark).animate().alpha(0).setDuration(1);
            findViewById(R.id.rcd_change_btn_dark).setEnabled(false);
            findViewById(R.id.rcd_change_btn_light).setOnClickListener(v -> {
                flag = false;
                findViewById(R.id.rcd_change_btn_light).setEnabled(false);
                findViewById(R.id.rcd_change_btn_dark).setEnabled(true);
                ValueAnimator colorAnimator = ValueAnimator.ofArgb(Color.parseColor("#EFF9F6"), Color.parseColor("#C7F2E3"));
                colorAnimator.setDuration(1000);
                colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        int currentValue = (int) animator.getAnimatedValue();
                        findViewById(R.id.backcolor).setBackgroundColor(currentValue);
                    }
                });
                colorAnimator.start();

                findViewById(R.id.rcd_light).animate().alpha(0).setDuration(1000);

                ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(findViewById(R.id.rcd_change_btn_light), "alpha", 1f, 0f);
                fadeOutAnimator.setDuration(800); // 设置动画持续时间

                ObjectAnimator translateXAnimator = ObjectAnimator.ofFloat(findViewById(R.id.rcd_change_btn_light), "translationX", 0f, 1.5f * findViewById(R.id.rcd_change_btn_light).getWidth());
                translateXAnimator.setDuration(800); // 设置动画持续时间

                ObjectAnimator fadeOutAnimator1 = ObjectAnimator.ofFloat(findViewById(R.id.rcd_change_btn_dark), "alpha", 0f, 1f);
                fadeOutAnimator1.setDuration(1000); // 设置动画持续时间

                ObjectAnimator translateXAnimator1 = ObjectAnimator.ofFloat(findViewById(R.id.rcd_change_btn_dark), "translationX", 0f, -(1.5f * findViewById(R.id.rcd_change_btn_light).getWidth()));
                translateXAnimator1.setDuration(1000); // 设置动画持续时间

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(fadeOutAnimator, translateXAnimator, fadeOutAnimator1, translateXAnimator1); // 同时播放两个动画
                animatorSet.start(); // 开始动画
            });
            //黑到白
            findViewById(R.id.rcd_change_btn_dark).setOnClickListener(v -> {
                flag = true;
                findViewById(R.id.rcd_change_btn_light).setEnabled(true);
                findViewById(R.id.rcd_change_btn_dark).setEnabled(false);
                ValueAnimator colorAnimator = ValueAnimator.ofArgb(Color.parseColor("#C7F2E3"), Color.parseColor("#EFF9F6"));
                colorAnimator.setDuration(1000);
                colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        int currentValue = (int) animator.getAnimatedValue();
                        findViewById(R.id.backcolor).setBackgroundColor(currentValue);
                    }
                });
                colorAnimator.start();

                findViewById(R.id.rcd_light).animate().alpha(1).setDuration(1300);

                ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(findViewById(R.id.rcd_change_btn_dark), "alpha", 1f, 0f);
                fadeOutAnimator.setDuration(800); // 设置动画持续时间

                ObjectAnimator translateXAnimator = ObjectAnimator.ofFloat(findViewById(R.id.rcd_change_btn_dark), "translationX", -1.5f * findViewById(R.id.rcd_change_btn_light).getWidth(), 0f);
                translateXAnimator.setDuration(800); // 设置动画持续时间

                ObjectAnimator fadeOutAnimator1 = ObjectAnimator.ofFloat(findViewById(R.id.rcd_change_btn_light), "alpha", 0f, 1f);
                fadeOutAnimator1.setDuration(1000); // 设置动画持续时间

                ObjectAnimator translateXAnimator1 = ObjectAnimator.ofFloat(findViewById(R.id.rcd_change_btn_light), "translationX", (1.5f * findViewById(R.id.rcd_change_btn_light).getWidth()), 0f);
                translateXAnimator1.setDuration(1000); // 设置动画持续时间

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(fadeOutAnimator, translateXAnimator, fadeOutAnimator1, translateXAnimator1); // 同时播放两个动画
                animatorSet.start(); // 开始动画
            });
        }


        {
            findViewById(R.id.rcd_dark_return_btn).setOnClickListener(v -> {
                test_information.clear();
                info.clear();
                IN.clear();
                OUT.clear();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }
    }

    public void updateUI() {
        if (info != null && info.get(true) != null) {
            for (inoutClass ic : Objects.requireNonNull(info.get(true))) {  //入库
                View rcd_item = LayoutInflater.from(this)
                        .inflate(R.layout.inrecd, page, false);
                ((TextView) rcd_item.findViewById(R.id.textView7)).setText(ic.it_name);
                ((TextView) rcd_item.findViewById(R.id.textView8)).setText(ic.where);
                ((TextView) rcd_item.findViewById(R.id.textView6)).setText(ic.user);
                ((TextView) rcd_item.findViewById(R.id.ttttt)).setText(ic.time);

                for (int i = 0; i < 7; i++)
                    if (getMONTH(Integer.parseInt(ic.month)).equals(month.get(i)) && ic.day.equals(day.get(i))) {
                        IN.computeIfAbsent(i, k -> new ArrayList<>());
                        Objects.requireNonNull(IN.get(i)).add(rcd_item);
                    }
            }
        }

        if (info != null && info.get(false) != null) {
            for (inoutClass ic : Objects.requireNonNull(info.get(false))) {  //出库

                View rcd_item = LayoutInflater.from(this)
                        .inflate(R.layout.inrecd, page, false);
                ((TextView) rcd_item.findViewById(R.id.textView7)).setText(ic.it_name);
                ((TextView) rcd_item.findViewById(R.id.textView8)).setText(ic.where);
                ((TextView) rcd_item.findViewById(R.id.textView6)).setText(ic.user);
                ((TextView) rcd_item.findViewById(R.id.ttttt)).setText(ic.time);

                for (int i = 0; i < 7; i++){
                    if (getMONTH(Integer.parseInt(ic.month)).equals(month.get(i)) && ic.day.equals(day.get(i))) {
                        OUT.computeIfAbsent(i, k -> new ArrayList<>());
                        Objects.requireNonNull(OUT.get(i)).add(rcd_item);
                    }
                }
            }
        }
    }
    public void getInInfo(){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/rukuRecord?layout_id=" + Current_layout_id;

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                String js = response.body().string();
                JSONArray jsonArray = new JSONArray(js);

                ArrayList<inoutClass> arr = new ArrayList<>();
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(k);

                    String layout_name = jsonObject.getString("layout_name");
                    String room_name = jsonObject.getString("room_name");
                    String it_name = jsonObject.getString("it_name") ;
                    String stg_name = jsonObject.getString("stg_name");
                    String dateTimeString = jsonObject.getString("ruchu_time");
                    String uname = jsonObject.getString("uname");
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTimeString, formatter);

                    String month = String.valueOf(offsetDateTime.getMonthValue());
                    String day = String.valueOf(offsetDateTime.getDayOfMonth());
                    String time = String.valueOf(offsetDateTime.toLocalTime()); // 获取本地时间部分

                    inoutClass ic = new inoutClass(day, month, time, it_name, uname, layout_name + " - " + room_name + " - " + stg_name);
                    arr.add(ic);
                }

                info.put(false,arr);
            } else {
                System.out.println("响应码: " + response.code());
                assert response.body() != null;
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            response.body().close();
        } catch (IOException | JSONException e) {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_SHORT).show());
        }
    }

    public void getOutInfo(){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/chukuRecord?layout_id=" + Current_layout_id;

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                String js = response.body().string();
                JSONArray jsonArray = new JSONArray(js);

                ArrayList<inoutClass> arr = new ArrayList<>();
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(k);

                    String layout_name = jsonObject.getString("layout_name");
                    String room_name = jsonObject.getString("room_name");
                    String it_name = jsonObject.getString("it_name") ;
                    String stg_name = jsonObject.getString("stg_name");
                    String dateTimeString = jsonObject.getString("ruchu_time");
                    String uname = jsonObject.getString("uname");
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTimeString, formatter);

                    String month = String.valueOf(offsetDateTime.getMonthValue());
                    String day = String.valueOf(offsetDateTime.getDayOfMonth());
                    String time = String.valueOf(offsetDateTime.toLocalTime()); // 获取本地时间部分

                    inoutClass ic = new inoutClass(day, month, time, it_name, uname, layout_name + " - " + room_name + " - " + stg_name);
                    arr.add(ic);
                }

                info.put(true,arr);
            } else {
                System.out.println("响应码: " + response.code());
                assert response.body() != null;
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            response.body().close();
        } catch (IOException | JSONException e) {
        }
    }

    public void updateDataTime(){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = "http://120.26.248.74:8080/delete7DAgo?";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }else{
                System.out.println("响应成功");
            }
            response.body().close();
        } catch (IOException e) {
        }
    }

    public void onBackPressed() {
        test_information.clear();
        info.clear();
        IN.clear();
        OUT.clear();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    //****获取今天以前七天的数据***
    public void get_post_days() {
        day.clear();
        month.clear();
        // 创建一个SimpleDateFormat对象用于格式化日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // 创建一个Calendar实例，并设置为当前日期和时间
        Calendar calendar = Calendar.getInstance();

        // 设置日历为当前日期的前一周
        calendar.add(Calendar.DATE, -6);

        // 遍历过去一周的每一天
        for (int i = 0; i < 7; i++) {
            Integer formatday = calendar.get(Calendar.DAY_OF_MONTH);
            day.add(formatday.toString());
            // 获取当天的月份（注意：月份是从0开始的）
            int formatmonth = calendar.get(Calendar.MONTH) + 1;
            month.add(getMONTH(formatmonth));
            // 将日历向前推一天，准备下一次循环
            calendar.add(Calendar.DATE, 1);
        }
    }

    //****将月份转化为英文显示***
    public String getMONTH(Integer m){
        switch(m){
            case (1): return "Jan.";
            case (2): return "Feb.";
            case (3): return "Mar.";
            case (4): return "Apr.";
            case (5): return "May.";
            case (6): return "Jun.";
            case (7): return "Jul.";
            case (8): return "Aug.";
            case (9): return "Sept.";
            case (10): return "Oct.";
            case (11): return "Nov.";
            case (12): return "Dec.";
        }
        return "";
    }

}

