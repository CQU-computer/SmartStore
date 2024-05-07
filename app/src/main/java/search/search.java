package search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstore.R;
import com.example.smartstore.help_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import image_submit.attention_dialog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import self_edit_layout.self_edit_layout;
//import cn.student0.manager.RepeatLayoutManager;


public class search extends AppCompatActivity {
    public static Map<Integer,String> user_room = new HashMap<>();  //空间id -> name
    public static Map<Integer, String> user_stgs = new HashMap<>();  //储藏点id -> name
    public static Map< Integer, Map<Integer, ArrayList<Integer> > > room_stg_item = new HashMap<>();   //当前所在场景内的 所有空间 及其 对应收纳点 及其对应物品类别
    public static Map<Integer, itemClass> item_list = new HashMap<>();  //存储物品信息
    private RecyclerView recyclerView;
    private InfiniteScrollAdapter adapter;
    private Button search_return_btn;
    public  static Integer uid;
    private Integer Current_layout_id;
    private String Current_layout;
    private Boolean itemChange;
    private EditText editText;
    private Button search_btn;

    public static ArrayList<String> res = new ArrayList<>();
    private Button goto_change_layout;
    private Button hlp_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        recyclerView = findViewById(R.id.recyclerView);

        search_return_btn = findViewById(R.id.search_return_btn);
        SharedPreferences preference = getSharedPreferences("config", Context.MODE_PRIVATE);
        uid =  preference.getInt("uer_id",-1);
        Current_layout =  preference.getString("current_layout_name","");
        Current_layout_id =  preference.getInt("current_layout_id",-1);
        editText = findViewById(R.id.search_txt);
        search_btn = findViewById(R.id.search_btn);
        goto_change_layout = findViewById(R.id.goto_change_layout);
        hlp_search = findViewById(R.id.hlp_search);

        preference = getSharedPreferences("config",Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.layout_name)).setText(preference.getString("current_layout_name","还没有场景哦！"));


        {
            Thread t1 = new Thread(this::layout_get_room);
            Thread t2 = new Thread(this::room_get_stgs);
            Thread t3 = new Thread(this::stg_get_item);

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
            updateUI();
        }

        search_btn.setOnClickListener(v -> {
            res.clear();
            String content = editText.getText().toString();
            editText.clearFocus();
            editText.setText("");
            if(content.equals("")){
                Toast.makeText(this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
            }
            boolean exist = false;
            for(Integer key: item_list.keySet()){
                if(Objects.requireNonNull(item_list.get(key)).it_name.contains(content)){
                    exist = true;
                    res.add(" ▲ " + Current_layout + " - " + user_room.get(Objects.requireNonNull(item_list.get(key)).rom_id) + " - " + user_stgs.get(Objects.requireNonNull(item_list.get(key)).stg_id) +":" + item_list.get(key).it_name);
                }
            }
            if(!exist){
                res.add("没有找到" + content + "哦~");
            }
            search_dialog dd = new search_dialog( this,content);
            dd.onCreate_Attention_Dialog();
        });
        search_return_btn.setOnClickListener(v -> {
            user_room.clear();
            user_stgs.clear();
            room_stg_item.clear();
            item_list.clear();
            finish();
            overridePendingTransition(0,0);
        });
        goto_change_layout.setOnClickListener(v -> {
            attention_dialog dd = new attention_dialog("你确定要切换当前场景吗","场景切换" ,"确认切换", "不，我点错了",this, isAccept -> {
                if(isAccept){
                    user_room.clear();
                    user_stgs.clear();
                    room_stg_item.clear();
                    item_list.clear();
                    Intent intent = new Intent(this, self_edit_layout.class);
                    intent.putExtra("source", "search");
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    super.onBackPressed();
                }
            });
            dd.onCreate_Attention_Dialog();
        });
        hlp_search.setOnClickListener(v -> {
            Intent it = new Intent(this, help_activity.class);
            it.putExtra("where","search");
            startActivity(it);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String source = extras.getString("source");
            if (source != null) {
                String[] parts = source.split("/");
                editText.setText(parts[1]);
                search_btn.performClick();
            }
        }
    }


    public void updateUI() {
        recyclerView.invalidate();
        recyclerView.requestLayout();
        InfiniteScrollLayoutManager layoutManager = new InfiniteScrollLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InfiniteScrollAdapter(this);
        recyclerView.setAdapter(adapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }

            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }
    public void onBackPressed(){
        user_room.clear();
        user_stgs.clear();
        room_stg_item.clear();
        item_list.clear();
        finish();
        overridePendingTransition(0,0);
    }
    public void layout_get_room() {  //在每次场景切换执行,拉取当前场景所有room
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        StringBuilder queryParams = new StringBuilder();
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
                assert response.body() != null;
                String js = response.body().string();
                JSONArray jsonArray = new JSONArray(js);
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(k);

                    String rnm = jsonObject.getString("room_name");
                    String rid = jsonObject.getString("room_id");
                    user_room.put(Integer.parseInt(rid),rnm);
                    Map<Integer, ArrayList<Integer>>  tmp = new HashMap<>();
                    room_stg_item.put(Integer.parseInt(rid),tmp);
                }
            } else {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            response.body().close();
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public void room_get_stgs() {
        for (Integer key_room : user_room.keySet()) {  //遍历当前场景下所有的房间id

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            StringBuilder queryParams = new StringBuilder();
            queryParams.append(key_room);

            RequestBody body = RequestBody.create(JSON, "");
            String url = "http://120.26.248.74:8080/getStgId?room_id=" + queryParams;

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
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(k);

                        String snm = jsonObject.getString("stg_name");
                        String sid = jsonObject.getString("stg_id");

                        user_stgs.put(Integer.parseInt(sid),snm);
                        ArrayList<Integer> tmp = new ArrayList<>();
                        Objects.requireNonNull(room_stg_item.get(key_room)).put(Integer.parseInt(sid),tmp);

                    }
                } else {
                    System.out.println("响应码: " + response.code());
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    System.out.println("响应体: " + responseBody);
                }
                response.body().close();
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void stg_get_item() {

        for (Integer key_room : room_stg_item.keySet()) {  //遍历所有所需房间id
            for (Integer key_stg : Objects.requireNonNull(room_stg_item.get(key_room)).keySet()) {  //遍历某个房间的所有物品
                Integer stg_id = key_stg;

                OkHttpClient client = new OkHttpClient().newBuilder().build();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                StringBuilder queryParams = new StringBuilder();
                queryParams.append(stg_id);

                RequestBody body = RequestBody.create(JSON, "");
                String url = "http://120.26.248.74:8080/getItemId?stg_id=" + queryParams;

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
                             Integer it_id_ = Integer.parseInt(jsonObject.getString("it_id"));;
                             String best_before_ = jsonObject.getString("best_before");
                             Boolean it_fav_ = jsonObject.getString("it_fav").equals("1");
                             String it_name_ = jsonObject.getString("it_name");
                             String description_ = jsonObject.getString("remark");
                             Bitmap it_img_ = BitmapFactory.decodeFile(jsonObject.getString("it_img"));

                             item_list.put(it_id_,new itemClass(it_id_, best_before_, stg_id, it_fav_, it_name_, it_img_, description_, key_room));
                             Objects.requireNonNull(Objects.requireNonNull(room_stg_item.get(key_room)).get(key_stg)).add(it_id_);
                        }
                    } else {
                        System.out.println("响应码: " + response.code());
                        assert response.body() != null;
                        String responseBody = response.body().string();
                        System.out.println("响应体: " + responseBody);
                    }
                    response.body().close();
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}