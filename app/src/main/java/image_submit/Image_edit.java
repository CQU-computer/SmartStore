package image_submit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import Baidu.Baidu;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Image_edit extends AppCompatActivity {
    public Map<String, String> tmp = new HashMap<>();
    public ArrayList<String> room_name = new ArrayList<>();  //当前场景所有空间
    public Map<String, Integer> room_Id = new HashMap<>();  //空间及其对应id
    public Map<String, Integer> stg_Id = new HashMap<>();  //储藏点及其对应 id 键格式：房间 + 储藏点名称
    public String Current_layout;  //当前所在场景名称
    public Integer Current_layout_id; //当前所在场景id
    public Map<String, Map<String, ArrayList<String>>> room_stg_item = new HashMap<>();   //当前所在场景内的 所有空间 及其 对应收纳点 及其对应物品类别
    public ArrayList<String> item_space;  //具体物品所在空间
    private RecyclerView horizontalRecyclerView;
    private HorizontalAdapter adapter;
    private Button image_edit_return_btn;
    private TextView pages;
    public static List<item> items = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private ImageView sumbit_btn;

    public File file;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    public static boolean UPLOAD_IMAGE_REQUEST;
    private ArrayList<byte[]> image = new ArrayList<>();  //分割图片结果，每个物体一个byte[]
    private ArrayList<String> keyword = new ArrayList<>();  //检测结果keywords名单
    private ArrayList<String> item_type = new ArrayList<>();  //当前物品类别
    public ArrayList<ArrayList<Float>> attribute = new ArrayList<>();
    public ArrayList<String> recomment_reson = new ArrayList<>();
    private float[][] output;
    public Integer it = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_edit);
        SharedPreferences preference_id = getSharedPreferences("config", Context.MODE_PRIVATE);
        Current_layout_id = preference_id.getInt("current_layout_id", -1);

        SharedPreferences preference_name = getSharedPreferences("config", Context.MODE_PRIVATE);
        Current_layout = preference_name.getString("current_layout_name", "");

        item_type = Baidu.detect_type_1;
        keyword = Baidu.detect_res_1;
        item_space = Baidu.space_1;
        room_name = Baidu.layout_room;
        ;

        room_Id = Baidu.room_Id;
        ;
        image = Baidu.depart_res_1;
        attribute = Baidu.attributes_1;

        //*********************获得属性数据并传入神经网络得到归纳结果
        {
            output = new float[attribute.size()][4];
            int rows = attribute.size();
            int cols = 6;
            float[][] floatArray = new float[rows][cols];
            for (int i = 0; i < rows; i++) {
                ArrayList<Float> rowList = attribute.get(i);
                for (int j = 0; j < cols; j++) {
                    floatArray[i][j] = rowList.get(j);
                }
            }
            tensorflowLoader.newInstance(getApplicationContext()).get().run(floatArray, output);
        }
        //*********************获得属性数据并传入神经网络得到归纳结果


        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        if (!UPLOAD_IMAGE_REQUEST) {
                            Intent data = result.getData();
                            try {
                                adapter.setSelectedImageUri(data.getData());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });


        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            room_get_stg();
            try {
                handler.post(this::allDetectComplete);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        image_edit_return_btn = findViewById(R.id.image_edit_return_btn);
        sumbit_btn = findViewById(R.id.sumbit_btn);

        sumbit_btn.setOnClickListener(v -> {
            attention_dialog dd = new attention_dialog("请确保所有物品的编辑已完成!", "即将入库", "确认提交", "我再看看", this, isAccept -> {
                if (isAccept) {
                    final_upload();
                    SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("ItemChange", true);
                    editor.apply();
                    items.clear();
                    Baidu.depart_res_1.clear();
                    Baidu.detect_res_1.clear();
                    Baidu.attributes_1.clear();
                    Baidu.space_1.clear();
                    Baidu.layout_room.clear();
                    Baidu.detect_type_1.clear();
                    Baidu.activity.finish();
                    room_Id.clear();

                    finish();
                }
            });
            dd.onCreate_Attention_Dialog();
        });

        image_edit_return_btn.setOnClickListener(v -> {
            attention_dialog dd = new attention_dialog("你确定要放弃现有编辑，重新返回拍照界面吗？", "清空提醒", "重新开始", "不，我点错了", this, isAccept -> {
                if (isAccept) {
                    items.clear();
                    Baidu.depart_res_1.clear();
                    Baidu.detect_res_1.clear();
                    Baidu.attributes_1.clear();
                    Baidu.space_1.clear();
                    Baidu.layout_room.clear();
                    Baidu.detect_type_1.clear();
                    Baidu.activity.finish();
                    room_Id.clear();
                    finish();
                }
            });
            dd.onCreate_Attention_Dialog();
        });
    }

    public void allDetectComplete() {
        horizontalRecyclerView = findViewById(R.id.horizontal_recycler_view);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView.setLayoutManager(layoutManager);
        for (int i = 0; i < keyword.size(); i++) {
            item itm = new item(image.get(i), keyword.get(i), Current_layout + " -> " + item_space.get(i), recomment_reson.get(i), "3000-12-12", "", false, item_type.get(i));
            items.add(itm);
        }

        adapter = new HorizontalAdapter(this, imagePickerLauncher);
        horizontalRecyclerView.setAdapter(adapter);
        new PagerSnapHelper().attachToRecyclerView(horizontalRecyclerView);


        adapter.setOnItemDeletedListener(position -> {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            pages.setText(firstVisibleItemPosition + 1 + "/" + items.size());
        });

        horizontalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                pages = findViewById(R.id.pages);
                pages.setText(firstVisibleItemPosition + 1 + "/" + items.size());
            }
        });
    }

    @Override
    public void onBackPressed() {
        attention_dialog dd = new attention_dialog("你确定要放弃现有编辑，重新返回拍照界面吗？", "清空提醒", "重新开始", "不，我点错了", this, isAccept -> {
            if (isAccept) {
                items.clear();
                Baidu.depart_res_1.clear();
                Baidu.detect_res_1.clear();
                Baidu.attributes_1.clear();
                Baidu.space_1.clear();
                Baidu.layout_room.clear();
                Baidu.activity.finish();
                Baidu.detect_type_1.clear();
                room_Id.clear();
                super.onBackPressed();
            }
        });
        dd.onCreate_Attention_Dialog();
    }

    public void room_get_stg() {
        //获得所需要的空间 ; 并去重
        Set<String> set = new HashSet<>(item_space);
        List<String> item_space_only = new ArrayList<>(set);

        //遍历所有所需要的空间
        CountDownLatch latch = new CountDownLatch(item_space_only.size());
        for (String room : item_space_only) {
            Map<String, ArrayList<String>> tmp = new HashMap<>();
            room_stg_item.put(room, tmp);

            //获得当前空间id
            Integer room_id = room_Id.get(room);
            //获得当前空间内所有储藏点
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            StringBuilder queryParams = new StringBuilder();
            queryParams.append(room_id);

            RequestBody body = RequestBody.create(JSON, "");
            String url = "http://120.26.248.74:8080/getStgId?room_id=" + queryParams;
            try {
                new Thread(() -> {
                    try {
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            String js = response.body().string();
                            JSONArray jsonArray = new JSONArray(js);

                            it = jsonArray.length();
                            for (int k = 0; k < jsonArray.length(); k++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(k);
                                String snm = jsonObject.getString("stg_name");
                                Integer sid = jsonObject.getInt("stg_id");
                                //获得储藏点内所有物品类别
                                room_stg_item.get(room).put(snm, new ArrayList<>());
                                stg_Id.put(room + snm, sid);
                            }
                        } else {
                            System.out.println("响应码: " + response.code());
                            String responseBody = response.body().string();
                            System.out.println("响应体: " + responseBody);
                        }
                        response.body().close();
                    } catch (IOException e) {
                    } catch (JSONException e) {
                    } finally {
                        latch.countDown(); // 子线程执行完成，减少 latch 计数
                    }
                }).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            latch.await();
            stg_get_item();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void CompleteItemSpace() {
        for (int i = 0; i < output.length; i++) {
            int max_idx = findMaxIndex(output[i]);
            switch (max_idx) {
                case 0: {
                    get_best_stg(i, "抽屉");
                    continue;
                }
                case 1: {
                    get_best_stg(i, "柜");
                    continue;
                }
                case 2: {
                    get_best_stg(i, "架");
                    continue;
                }
                case 3: {
                    get_best_stg(i, "面");
                }
            }
        }
    }

    public String getFullName(String s) {
        switch (s) {
            case "抽屉": {
                return "抽屉";
            }
            case "柜": {
                return "柜子";
            }
            case "架": {
                return "置物架";
            }
            case "面": {
                return "桌柜面";
            }
        }
        return "";
    }

    public void stg_get_item() {

        int totalStorages = 0;
        for (Map<String, ArrayList<String>> storagesMap : room_stg_item.values())
            totalStorages += storagesMap.size();

        CountDownLatch latch = new CountDownLatch(totalStorages);

        for (String key_room : room_stg_item.keySet()) {  //遍历所有所需房间
            for (String key_stg : room_stg_item.get(key_room).keySet()) {  //遍历某个房间的所有物品
                Integer stg_id = stg_Id.get(key_room + key_stg);

                OkHttpClient client = new OkHttpClient().newBuilder().build();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                StringBuilder queryParams = new StringBuilder();
                queryParams.append(stg_id);

                RequestBody body = RequestBody.create(JSON, "");
                String url = "http://120.26.248.74:8080/getItemId?stg_id=" + queryParams;

                ArrayList<String> res = new ArrayList<>();
                try {
                        new Thread(() -> {
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
                                        String inm = jsonObject.getString("it_type");
                                        res.add(inm);
                                    }
                                } else {
                                    System.out.println("响应码: " + response.code());
                                    String responseBody = response.body().string();
                                    System.out.println("响应体: " + responseBody);
                                }
                                response.body().close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            } finally {
                                latch.countDown(); // 子线程执行完成，减少 latch 计数
                            }
                        }).start();

                    } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                room_stg_item.get(key_room).put(key_stg, res);
            }
        }
        try {
            latch.await();//所有物品已获取
            CompleteItemSpace();
//            allDetectComplete();  不能在子线程更新ui
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void get_best_stg(Integer i, String target_stg){

        String cur_room = item_space.get(i);   //获得当前物品所在room名称
        Map<String,ArrayList<String>> cur_stg = room_stg_item.get(cur_room);
        assert cur_stg != null;

        String Target_same_type_stg_name = "";
        String Target_stg_name = "";
        long max_same_type_cnt = 0;
        int min_cnt = 9999;
        String tmp = "";
        for (String key : cur_stg.keySet()) {  //遍历所有储藏点

            if(min_cnt > Objects.requireNonNull(cur_stg.get(key)).size()){
                min_cnt = Objects.requireNonNull(cur_stg.get(key)).size(); //兼顾寻找存放最少物品的储藏点
                Target_stg_name = key;
            }

            if(key.contains(target_stg)){  //有匹配储藏点
                tmp = key;
                int finalI = i;
                long count = Objects.requireNonNull(cur_stg.get(key)).stream()
                        .filter(Objects::nonNull) // 可以忽略null值
                        .filter(element -> element.equals(item_type.get(finalI)))
                        .count();

                if(max_same_type_cnt < count){
                    max_same_type_cnt = count; //兼顾寻找存放最少物品的储藏点
                    Target_same_type_stg_name = key;
                }
            }
        }
        if(!Target_same_type_stg_name.equals("")){
            String dummy = item_type.get(i);
            String[] substrings = dummy.split("-");
            dummy = substrings[substrings.length - 1];
            recomment_reson.add(Baidu.detect_res_1.get(i) + "可存放于" + getFullName(target_stg) + "，且" + cur_room + "中" + dummy + "类物品多放置于" + Target_same_type_stg_name);
            item_space.set(i,item_space.get(i) + " -> " + Target_same_type_stg_name);
        }else if(!tmp.equals("")){
            recomment_reson.add(Baidu.detect_res_1.get(i) + "很适合存放于" + cur_room + "中的" + Target_stg_name + "哦");
            item_space.set(i,item_space.get(i) + " -> " + tmp);
        } else{
            recomment_reson.add("由于"+item_space.get(i)+"中没有" + getFullName(target_stg)  + "，而"+Target_stg_name+"中物品相对较少，推荐将" + Baidu.detect_res_1.get(i) + "存放于" + Target_stg_name);
            item_space.set(i,item_space.get(i) + " -> " + Target_stg_name);
        }
    }

    public static int findMaxIndex(float[] floatArray) {
        if (floatArray == null || floatArray.length == 0) {
            throw new IllegalArgumentException("数组不能为空或长度为0");
        }

        int maxIndex = 0; // 假设第一个元素是最大值
        for (int i = 1; i < floatArray.length; i++) {
            if (floatArray[i] > floatArray[maxIndex]) {
                maxIndex = i; // 更新最大值的下标
            }
        }
        return maxIndex;
    }

    public void final_upload() {
        final Boolean[] subsuc = {true};
        CountDownLatch latch = new CountDownLatch(items.size()); // 创建CountDownLatch，计数为items的数量

        for (int i = 0; i < items.size(); i++) {
            SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
            String uid = String.valueOf(preferences.getInt("uer_id", 0));
            String it_type = items.get(i).item_type;
            String best_before = items.get(i).item_date;
            String stg_id = stg_Id.get(items.get(i).item_layout.split(" -> ")[1] + items.get(i).item_layout.split(" -> ")[2]).toString();
            String it_fav = items.get(i).item_star ? "1" : "0";
            String it_name = items.get(i).item_title;
            String remark = items.get(i).item_description;

            File it_img = convertByteArrayToFile(items.get(i).item_file);
            JSONObject json = new JSONObject();
            try {
                json.put("it_type", it_type);
                json.put("best_before", best_before);
                json.put("stg_id", stg_id);
                json.put("it_fav", it_fav);
                json.put("it_name", it_name);
                json.put("uid", uid);
                json.put("remark", remark);
                json.put("it_img", it_img);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
            Request request = new Request.Builder()
                    .url("http://120.26.248.74:8080/insertItem")
                    .post(body)
                    .build();

            OkHttpClient client1 = new OkHttpClient();
            client1.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(Image_edit.this, "网络问题", Toast.LENGTH_SHORT).show());
                    latch.countDown();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    assert response.body() != null;
                    if (!response.isSuccessful()) {
                        subsuc[0] = false;
                    } else {
                        try {
                            String responseBody = response.body().string();
                            System.out.println(it_name + responseBody);
                            tmp.put(it_name, responseBody);
                        } catch (Exception e) {
                        }
                    }
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (subsuc[0]) {
            runOnUiThread(() -> Toast.makeText(Image_edit.this, "提交成功！", Toast.LENGTH_SHORT).show());
        } else {
            runOnUiThread(() -> Toast.makeText(Image_edit.this, "提交失败！", Toast.LENGTH_SHORT).show());
        }

        Ruku();
    }

    public void Ruku(){
        for(int i = 0; i < items.size(); i++) {
            SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
            String uid =  String.valueOf(preferences.getInt("uer_id",0));
            String stg_name = items.get(i).item_layout.split(" -> ")[2];
            String room_name = items.get(i).item_layout.split(" -> ")[1];
            String layout_name = Current_layout;
            String  layout_id = Current_layout_id.toString();
            String it_name = items.get(i).item_title;
            String it_id = tmp.get(it_name);

            JSONObject json = new JSONObject();
            try {
                json.put("uid", uid);
                json.put("it_name", it_name);
                json.put("stg_name", stg_name);
                json.put("room_name", room_name);
                json.put("layout_name", layout_name);
                json.put("layout_id", layout_id);
                json.put("it_id",it_id);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
            Request request = new Request.Builder()
                    .url("http://120.26.248.74:8080/insertRuRecord")
                    .post(body)
                    .build();

            OkHttpClient client1 = new OkHttpClient();
            client1.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(Image_edit.this, "网络问题", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    assert response.body() != null;
                    if (!response.isSuccessful()) {  //响应成功，但返回失败值
                        Image_edit.this.runOnUiThread(() -> {
                            try {
                                String responseBody = response.body().string();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    else{
                    }
                }
            });
        }
    }


    public File convertByteArrayToFile(byte[] byteArray) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("image", ".png");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(byteArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

}

