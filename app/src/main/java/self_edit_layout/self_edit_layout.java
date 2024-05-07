package self_edit_layout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartstore.MainActivity;
import com.example.smartstore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class self_edit_layout extends AppCompatActivity {
    public static Map<String,ArrayList<String>> room_stgs = new HashMap<>();
    public static Map<String,Integer> user_layout = new HashMap<>();  //场景与对于id
    public static Map<String,Integer> user_room = new HashMap<>();  //空间及对应id
    public static Map<String,Integer> user_stgs = new HashMap<>();  //储藏点及对应id
    public static Integer uid;
    public static String Current_layout;
    public static Integer Current_layout_id;
    private LinearLayout rooms;
    private TextView cur_ly;
    private TextView cnt_room;
    private TextView cnt_stg;
    private Button change_layout_btn;
    private LinearLayout my_layouts;
    private TextView back_btn;
    private ImageView add_layout_btn;
    private Button add_room_btn;
    private View pre_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_edit_layout);

        SharedPreferences preference = getSharedPreferences("config", Context.MODE_PRIVATE);
        uid =  preference.getInt("uer_id",-1);
        Current_layout =  preference.getString("current_layout_name","");
        Current_layout_id =  preference.getInt("current_layout_id",-1);

        rooms = findViewById(R.id.rooms);
        cur_ly = findViewById(R.id.cur_layout);
        cnt_room = findViewById(R.id.cnt_room);
        cnt_stg = findViewById(R.id.cnt_stg);
        change_layout_btn = findViewById(R.id.change_layout_btn);
        my_layouts = findViewById(R.id.my_layouts);
        back_btn = findViewById(R.id.back_btn);
        add_layout_btn = findViewById(R.id.add_layout_btn);
        add_room_btn = findViewById(R.id.add_room);

        cur_ly.setText(Current_layout);

        {
            Thread t1 = new Thread(this::get_layout);
            Thread t2 = new Thread(this::layout_get_room);
            Thread t3 = new Thread(this::room_get_stgs);

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
            updateUi();
        }  //初始化ui界面

        back_btn.setOnClickListener(v -> {
            findViewById(R.id.PART2).animate().alpha(0).setDuration(500);
            findViewById(R.id.PART1).setClickable(true);
            findViewById(R.id.PART2).setVisibility(View.GONE);
        });

        add_layout_btn.setOnClickListener(v -> {
            add_lay_room_stg dd = new add_lay_room_stg(1,this, (isAccept, lay_title, lay_description1) -> {
                if (isAccept) {
                    Thread t1 = new Thread(() -> InsertLayout(lay_title));
                    t1.start();
                    try {
                        t1.join();         //云端插入完成
                    } catch (InterruptedException e) {
                    }
                }
            });
            dd.onCreate_Attention_Dialog();
        });

        add_room_btn.setOnClickListener(v -> {
            add_lay_room_stg dd = new add_lay_room_stg(2,this, (isAccept, room_title, room_description1) -> {
                if (isAccept) {
                    Thread t1 = new Thread(() -> InsertRoom(room_title));
                    t1.start();
                    try {
                        t1.join();         //云端插入完成
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            dd.onCreate_Attention_Dialog();
        });

        change_layout_btn.setOnClickListener(v -> {
            findViewById(R.id.PART2).animate().alpha(1).setDuration(500);
            findViewById(R.id.PART2).setVisibility(View.VISIBLE);
            findViewById(R.id.PART1).setClickable(false);

            if(my_layouts.getChildCount() != 0)  //不是第一次触发
                return;
            update_layout_ui();
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String source = extras.getString("source");
            if (source != null && source.equals("search")) {
                change_layout_btn.performClick();
            }
        }

    }

    public void update_layout_ui(){
        if(my_layouts.getChildCount() != 0)
            my_layouts.removeAllViews();
        for (String key_lay : Objects.requireNonNull(user_layout.keySet())) {  //遍历场景
            View myLayoutListItem = LayoutInflater.from(my_layouts.getContext())
                    .inflate(R.layout.mylayoutlistitem, my_layouts, false);
            if(!key_lay.equals(Current_layout))
                ((ImageView) myLayoutListItem.findViewById(R.id.chose_layout)).setImageResource(R.drawable.star_gray);
            else{
                ((ImageView) myLayoutListItem.findViewById(R.id.chose_layout)).setImageResource(R.drawable.star_green);
                pre_layout = myLayoutListItem;
            }
            ((TextView) myLayoutListItem.findViewById(R.id.my_layout_single)).setText(key_lay);

            myLayoutListItem.setOnClickListener(v -> {
                if(pre_layout == v)
                    return;
                else if(pre_layout != null){
                    ((ImageView) pre_layout.findViewById(R.id.chose_layout)).setImageResource(R.drawable.star_gray);
                }
                pre_layout = v;
                ((ImageView) v.findViewById(R.id.chose_layout)).setImageResource(R.drawable.star_green);

                SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("current_layout_name", key_lay);
                editor.apply();

                editor.putInt("current_layout_id", user_layout.get(key_lay));
                editor.apply();

                Current_layout_id = user_layout.get(key_lay);
                Current_layout = key_lay;

                room_stgs.clear();
                user_stgs.clear();
                user_room.clear();

                Thread t2 = new Thread(this::layout_get_room);
                Thread t3 = new Thread(this::room_get_stgs);

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
                rooms.removeAllViews();
                cur_ly.setText(Current_layout);
                updateUi();
            });

            my_layouts.addView(myLayoutListItem);
        }
    }
    public void update_room_ui(String newRoom){
        View single_rooms = LayoutInflater.from(rooms.getContext())
                .inflate(R.layout.room_item, rooms, false);
        ((TextView) single_rooms.findViewById(R.id.room_name)).setText(newRoom);

        single_rooms.setOnClickListener(v -> {
            add_lay_room_stg dd = new add_lay_room_stg(3,this, (isAccept, stg_title, room_description1) -> {
                if (isAccept) {
                    Thread t1 = new Thread(() -> InsertStg(user_room.get(newRoom),stg_title));
                    t1.start();
                    try {
                        t1.join();
                        update_stg_ui(v, stg_title);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            dd.onCreate_Attention_Dialog();
        });
        rooms.addView(single_rooms);
        cnt_room.setText(String.valueOf(user_room.size()));
    }
    public void update_stg_ui(View rom, String sName){
        LinearLayout stgs = rom.findViewById(R.id.stgs);

        View single_stg = LayoutInflater.from(stgs.getContext())
                .inflate(R.layout.stg_item, stgs, false);
        ((TextView) single_stg.findViewById(R.id.stg_name)).setText(sName);

        if(sName.contains("抽屉"))
            ((ImageView) single_stg.findViewById(R.id.stg_img)).setImageResource(R.drawable.chouti);
        else if(sName.contains("柜"))
            ((ImageView) single_stg.findViewById(R.id.stg_img)).setImageResource(R.drawable.guizi);
        else if(sName.contains("架"))
            ((ImageView) single_stg.findViewById(R.id.stg_img)).setImageResource(R.drawable.zhiwujia);
        else if(sName.contains("面"))
            ((ImageView) single_stg.findViewById(R.id.stg_img)).setImageResource(R.drawable.zhuomian);
        else
            ((ImageView) single_stg.findViewById(R.id.stg_img)).setImageResource(R.drawable.others);

        stgs.addView(single_stg);
    }
    public void updateUi(){
        cnt_room.setText(String.valueOf(room_stgs.size()));
        cnt_stg.setText(String.valueOf(user_stgs.size()));

        //根据当前所处场景画出结构
        for (String key_room : Objects.requireNonNull(room_stgs.keySet())) {  //遍历当前所需房间
            View single_rooms = LayoutInflater.from(rooms.getContext())
                    .inflate(R.layout.room_item, rooms, false);
            ((TextView) single_rooms.findViewById(R.id.room_name)).setText(key_room);

            for (String key_stgs : Objects.requireNonNull(room_stgs.get(key_room))) {  //遍历房间内所有的储藏点

                LinearLayout stgs = single_rooms.findViewById(R.id.stgs);

                View single_stg = LayoutInflater.from(stgs.getContext())
                        .inflate(R.layout.stg_item, stgs, false);
                ((TextView) single_stg.findViewById(R.id.stg_name)).setText(key_stgs);

                if(key_stgs.contains("抽屉"))
                    ((ImageView) single_stg.findViewById(R.id.stg_img)).setImageResource(R.drawable.chouti);
                else if(key_stgs.contains("柜"))
                    ((ImageView) single_stg.findViewById(R.id.stg_img)).setImageResource(R.drawable.guizi);
                else if(key_stgs.contains("架"))
                    ((ImageView) single_stg.findViewById(R.id.stg_img)).setImageResource(R.drawable.zhiwujia);
                else if(key_stgs.contains("面"))
                    ((ImageView) single_stg.findViewById(R.id.stg_img)).setImageResource(R.drawable.zhuomian);
                else
                    ((ImageView) single_stg.findViewById(R.id.stg_img)).setImageResource(R.drawable.others);
                stgs.addView(single_stg);
            }
            single_rooms.setOnClickListener(v -> {
                add_lay_room_stg dd = new add_lay_room_stg(3,this, (isAccept, stg_title, room_description1) -> {
                    if (isAccept) {
                        Thread t1 = new Thread(() -> InsertStg(user_room.get(key_room),stg_title));
                        t1.start();
                        try {
                            t1.join();
                            update_stg_ui(v, stg_title);
                            cnt_stg.setText(String.valueOf(user_stgs.size()));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                dd.onCreate_Attention_Dialog();
            });
            rooms.addView(single_rooms);
        }
    } //初始更新
    public void get_layout() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String queryParams = String.valueOf(uid);

        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/getLayoutId?uid=" + queryParams;

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String js = response.body().string();
                JSONArray jsonArray = new JSONArray(js);
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(k);

                    String lnm = jsonObject.getString("layout_name");
                    String lid = jsonObject.getString("layout_id");
                    user_layout.put(lnm,Integer.parseInt(lid));  //获取所有布局

                }
            } else {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            response.body().close();
        } catch (IOException | JSONException e) {
        }
    }  //只执行一次，用于获得所有layout id和name
    public void layout_get_room() {  //在每次场景切换执行,拉取当前场景所有room
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        StringBuilder queryParams = new StringBuilder();
//        queryParams.append(user_layout.get(Current_layout));
        queryParams.append(Current_layout_id);

        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/getRoomId?layout_id=" + queryParams;

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String js = response.body().string();
                JSONArray jsonArray = new JSONArray(js);
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(k);

                    String rnm = jsonObject.getString("room_name");
                    String rid = jsonObject.getString("room_id");
                    user_room.put(rnm,Integer.parseInt(rid));
                    ArrayList<String> tmp = new ArrayList<>();
                    room_stgs.put(rnm,tmp);
                }
            } else {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            response.body().close();
        } catch (IOException | JSONException e) {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_SHORT).show());
        }
    }
    public void room_get_stgs() {
        for (String key_room : room_stgs.keySet()) {  //遍历当前场景下所有的房间

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            StringBuilder queryParams = new StringBuilder();
            queryParams.append(user_room.get(key_room));  //获得房间id

            RequestBody body = RequestBody.create(JSON, "");
            String url = "http://120.26.248.74:8080/getStgId?room_id=" + queryParams;

            try {
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String js = response.body().string();
                    JSONArray jsonArray = new JSONArray(js);
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(k);

                        String snm = jsonObject.getString("stg_name");
                        String sid = jsonObject.getString("stg_id");

                        user_stgs.put(snm,Integer.parseInt(sid));
                        room_stgs.get(key_room).add(snm);
                    }
                } else {
                    System.out.println("响应码: " + response.code());
                    String responseBody = response.body().string();
                    System.out.println("响应体: " + responseBody);
                }
                response.body().close();
            } catch (IOException | JSONException e) {
            }
        }
    }
    public void InsertLayout(String lName){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, "");
        String tmp = "";
        String url = "http://120.26.248.74:8080/add/insertLayout?uid=" + uid + "&layout_name=" + lName + "&layout_size=0";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                user_layout.put(lName,Integer.parseInt(response.body().string()));
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "新建成功", Toast.LENGTH_SHORT).show();
                    update_layout_ui();
                });

            } else {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            assert response.body() != null;
            response.body().close();
        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_SHORT).show());
        }
    }
    public void InsertRoom(String rName){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/add/insertRoom?uid=" + uid + "&room_name=" + rName + "&layout_id=" + Current_layout_id;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                user_room.put(rName,Integer.parseInt(response.body().string()));
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "新建成功", Toast.LENGTH_SHORT).show();
                    update_room_ui(rName);
                });
            } else {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            assert response.body() != null;
            response.body().close();
        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_SHORT).show());
        }
    }
    public void InsertStg(Integer room_id,  String sName){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/add/insertStg?uid=" + uid + "&stg_name=" + sName + "&layout_id=" + Current_layout_id + "&room_id="+room_id;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                user_stgs.put(sName,Integer.parseInt(response.body().string()));
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "新建成功", Toast.LENGTH_SHORT).show();
                });
            } else {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            assert response.body() != null;
            response.body().close();
        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_SHORT).show());
        }
    }
    @Override
    public void onBackPressed() {
        allClear();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("source", "self_edit");
        startActivity(intent);
        overridePendingTransition(0,0);
        super.onBackPressed();
    }
    public void allClear(){
        room_stgs.clear();
        user_layout.clear();
        user_room.clear();
        user_stgs.clear();
        uid = null;
        Current_layout = null;
        Current_layout_id = null;
    }
}