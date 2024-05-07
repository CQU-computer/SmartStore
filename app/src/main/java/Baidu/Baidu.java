package Baidu;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartstore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import image_submit.Image_edit;
import image_submit.attention_dialog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Baidu extends AppCompatActivity {

    public static ArrayList<String> layout_room = new ArrayList<>();  //当前场景的所有空间
    public static Map<String, Integer> room_Id = new HashMap<>();  //空间及其对应id
    public String Current_layout;  //当前所在场景名称
    public Integer Current_layout_id; //当前所在场景id
    private String api_key = "Rzsp1FGglma8MO073jb7fw9u";
    private String secret_key = "HdgncXUDOsH3WZ8av2fAjKT88fwPlGex";
    private String token;
    private boolean op;
    public ArrayList<byte[]> depart_res = new ArrayList<>();  //分割图片结果，每个物体一个byte[]
    public ArrayList<String> detect_res = new ArrayList<>();  //检测结果keywords名单
    public ArrayList<String> detect_type = new ArrayList<>();  //检测类别
    public ArrayList<ArrayList<Float>> attributes = new ArrayList<>();
    public ArrayList<String> space = new ArrayList<>();
    public static ArrayList<String> detect_type_1 = new ArrayList<>();
    public ArrayList<String> detect_type_2 = new ArrayList<>();
    public static ArrayList<byte[]> depart_res_1 = new ArrayList<>();
    public ArrayList<byte[]> depart_res_2 = new ArrayList<>();
    public static ArrayList<String> detect_res_1 = new ArrayList<>();
    public ArrayList<String> detect_res_2 = new ArrayList<>();
    public static ArrayList<ArrayList<Float>> attributes_1 = new ArrayList<>();
    public ArrayList<ArrayList<Float>> attributes_2 = new ArrayList<>();
    public static ArrayList<String>  space_1 = new ArrayList<>();
    public ArrayList<String>  space_2 = new ArrayList<>();
    private Uri uri;
    public static Activity activity;
    private Context context;
    private ImageView add_new_item_btn;
    private Button image_edit_return_btn;
    private ImageView  complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu);

        SharedPreferences preference_id = getSharedPreferences("config", Context.MODE_PRIVATE);
        Current_layout_id =  preference_id.getInt("current_layout_id",-1);

        SharedPreferences preference_name = getSharedPreferences("config", Context.MODE_PRIVATE);
        Current_layout =  preference_name.getString("current_layout_name","");
        layout_get_room(Current_layout_id);  //获取当前场景下所有房间及其对应id

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        context = this;
        activity = (Activity)this;
        uri = getIntent().getParcelableExtra("image");
        add_new_item_btn = findViewById(R.id.add_new_item_btn);
        image_edit_return_btn = findViewById(R.id.image_edit_return_btn);
        complete = findViewById(R.id.edit_btn);

        complete.setOnClickListener(v -> {
            attention_dialog dd = new attention_dialog("请确保所需物品与属性已设置完成","物品确认完成提醒" ,"确认完成", "再检查一下",this, isAccept -> {
                if(isAccept){
                    attributes_1.addAll(attributes_2); //属性
                    depart_res_1.addAll(depart_res_2); //图片
                    detect_res_1.addAll(detect_res_2); //名称
                    space_1.addAll(space_2);           //空间
                    detect_type_1.addAll(detect_type_2); //类别
                    //属性、名称与空间、图片、类别
                    Intent intent = new Intent(context, Image_edit.class);
                    System.out.println("dddd" + detect_res_1 + "  " + detect_type_1);
                    startActivity(intent);
                }
            });
            dd.onCreate_Attention_Dialog();
        });
        //获取token
        u1.getAccessToken(api_key, secret_key, new u1.OnAccessTokenReceivedListener() {
            @Override
            public void onSuccess(String accessToken) {
                token = accessToken;
                op = false;
                new ImageProcessingTask().execute(uri);
            }
            public void onFailure(Exception e) {
                System.out.println("获取token失败");
            }
        });

        add_new_item_btn.setOnClickListener(new View.OnClickListener() {
            private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            op = false;
                            new ImageProcessingTask().execute(data.getData());
                        }
                    });
            @Override
            public void onClick(View v) {  //再次选取照片
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            }
        });

        image_edit_return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attention_dialog dd = new attention_dialog("你确定要放弃所有的编辑吗？","清空提醒" ,"确认返回", "我点错了",context, isAccept -> {
                    if(isAccept){
                        depart_res_1.clear();
                        detect_res_1.clear();
                        attributes_1.clear();
                        space_1.clear();
                        layout_room.clear();
                        room_Id.clear();
                        detect_type_1.clear();
                        finish();
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    }
                });
                dd.onCreate_Attention_Dialog();
            }
        });
    }

    private class ImageProcessingTask extends AsyncTask<Uri, Void, String> {
        @Override
        protected String doInBackground(Uri... uris) {
            try {
                if(op) //处理识别
                    detect(Baidu.this, token);
                else {   //处理分割
                    Uri uri = uris[0];
                    depart(Baidu.this, token, uri);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {  //接受doInBackground返回值
            if(op) {  //识别完成
                LinearLayout container_1 = activity.findViewById(R.id.container_1);
                LinearLayout container_2 = activity.findViewById(R.id.container_2);

                OkHttpClient client = new OkHttpClient().newBuilder().build();

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                StringBuilder queryParams = new StringBuilder();
                for (int j = 0; j < detect_res.size(); j++) {
                    if (queryParams.length() > 0) {
                        queryParams.append("," + detect_res.get(j).toString());
                    }
                    else{
                        queryParams.append("itemName=").append(detect_res.get(j).toString());
                    }
                }
                RequestBody body = RequestBody.create(JSON, "");
                String url = "http://120.26.248.74:8080/getItemAttribute?" + queryParams;
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
                                for (int k = 0; k < detect_res.size(); k++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(k);

                                    float sizeFloat = Float.parseFloat(jsonObject.getString("size"));
                                    float urgnFloat = Float.parseFloat(jsonObject.getString("urgn"));
                                    float butyFloat = Float.parseFloat(jsonObject.getString("buty"));
                                    float freqFloat = Float.parseFloat(jsonObject.getString("freq"));
                                    float ligtFloat = Float.parseFloat(jsonObject.getString("ligt"));
                                    float wterFloat = Float.parseFloat(jsonObject.getString("wter"));
                                    String sp = jsonObject.getString("space");
                                    space.add(sp);
                                    attributes.add(new ArrayList<Float>() {{
                                        add(sizeFloat);
                                        add(urgnFloat);
                                        add(butyFloat);
                                        add(freqFloat);
                                        add(ligtFloat);
                                        add(wterFloat);
                                    }});
                                }
                                activity.runOnUiThread(() -> {

                                    for (int k = 0; k < detect_res.size(); k++) {

                                        if (k % 2 == 0) {
                                            View single_items = LayoutInflater.from(container_1.getContext())
                                                    .inflate(R.layout.edit_item_card, container_1, false);

                                            ((TextView) single_items.findViewById(R.id.detect_res)).setText(detect_res.get(k));
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(depart_res.get(k), 0, depart_res.get(k).length);
                                            ((ImageView) single_items.findViewById(R.id.detect_img)).setImageBitmap(bitmap);
                                            ((TextView) single_items.findViewById(R.id.size)).setText(getAttributeName("size",attributes.get(k).get(0)));
                                            ((TextView) single_items.findViewById(R.id.urgn)).setText(getAttributeName("urgn",attributes.get(k).get(1)));
                                            ((TextView) single_items.findViewById(R.id.buty)).setText(getAttributeName("buty",attributes.get(k).get(2)));
                                            ((TextView) single_items.findViewById(R.id.freq)).setText(getAttributeName("freq",attributes.get(k).get(3)));
                                            ((TextView) single_items.findViewById(R.id.ligt)).setText(getAttributeName("ligt",attributes.get(k).get(4)));
                                            ((TextView) single_items.findViewById(R.id.wter)).setText(getAttributeName("wter",attributes.get(k).get(5)));

                                            boolean flag = false;
                                            //如果没有此房间，则随机选择
                                            String first_room = "";
                                            for(String lr: layout_room) {  //遍历所有房间
                                                first_room = lr;
                                                if (lr.contains(space.get(k))) {  //针对每一个房间，判断是否包含房间的关键字段
                                                    ((TextView) single_items.findViewById(R.id.space)).setText("· " + lr);
                                                    space.set(k,lr);
                                                    flag = true;
                                                    break;
                                                }
                                            }
                                            if(!flag) {  //未找到匹配房间
                                                ((TextView) single_items.findViewById(R.id.space)).setText("· 无匹配空间");
                                                space.set(k, first_room);
                                            }

                                            container_1.addView(single_items);
                                            depart_res_1.add(depart_res.get(k));
                                            detect_res_1.add(detect_res.get(k));
                                            attributes_1.add(attributes.get(k));
                                            space_1.add(space.get(k));
                                            detect_type_1.add(detect_type.get(k));

                                            if(k == detect_res.size() - 1){
                                                depart_res.clear();
                                                detect_res.clear();
                                                attributes.clear();
                                                space.clear();
                                                detect_type.clear();
                                            }

                                            single_items.setOnLongClickListener(v -> {
                                                attention_dialog dd = new attention_dialog("你确认要删除"+ detect_res_1.get(container_1.indexOfChild(v)).toString()+"吗？","删除物品" ,"确认删除", "我点错了",context, isAccept -> {
                                                    if(isAccept){
                                                        detect_res_1.remove(container_1.indexOfChild(v));
                                                        depart_res_1.remove(container_1.indexOfChild(v));
                                                        attributes_1.remove(container_1.indexOfChild(v));
                                                        space_1.remove(container_1.indexOfChild(v));
                                                        detect_type_1.remove(container_1.indexOfChild(v));
                                                        container_1.removeView(v);

                                                    }
                                                });
                                                dd.onCreate_Attention_Dialog();
                                                return true;
                                            });

                                            single_items.setOnClickListener(v -> {
                                                attribute_dialog dd = new attribute_dialog(detect_res_1.get(container_1.indexOfChild(v)), space_1.get(container_1.indexOfChild(v)), attributes_1.get(container_1.indexOfChild(v)), context, (isAccept, single_attribute, spaces, keywords)-> {
                                                    if(isAccept){
                                                        Integer kk = container_1.indexOfChild(v);
                                                        attributes_1.set(kk,single_attribute);
                                                        space_1.set(kk, spaces);
                                                        detect_res_1.set(kk, keywords);
                                                        ((TextView) v.findViewById(R.id.size)).setText(getAttributeName("size",attributes_1.get(kk).get(0)));
                                                        ((TextView) v.findViewById(R.id.urgn)).setText(getAttributeName("urgn",attributes_1.get(kk).get(1)));
                                                        ((TextView) v.findViewById(R.id.buty)).setText(getAttributeName("buty",attributes_1.get(kk).get(2)));
                                                        ((TextView) v.findViewById(R.id.freq)).setText(getAttributeName("freq",attributes_1.get(kk).get(3)));
                                                        ((TextView) v.findViewById(R.id.ligt)).setText(getAttributeName("ligt",attributes_1.get(kk).get(4)));
                                                        ((TextView) v.findViewById(R.id.wter)).setText(getAttributeName("wter",attributes_1.get(kk).get(5)));
                                                        ((TextView) v.findViewById(R.id.space)).setText("· " + spaces);
                                                        ((TextView) v.findViewById(R.id.detect_res)).setText(keywords);
                                                    }
                                                });
                                                dd.onCreate_Attention_Dialog();
                                            });
                                        }

                                        else {
                                            View single_items = LayoutInflater.from(container_2.getContext())
                                                    .inflate(R.layout.edit_item_card, container_2, false);
                                            ((TextView) single_items.findViewById(R.id.detect_res)).setText(detect_res.get(k));
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(depart_res.get(k), 0, depart_res.get(k).length);
                                            ((ImageView) single_items.findViewById(R.id.detect_img)).setImageBitmap(bitmap);
                                            ((TextView) single_items.findViewById(R.id.size)).setText(getAttributeName("size",attributes.get(k).get(0)));
                                            ((TextView) single_items.findViewById(R.id.urgn)).setText(getAttributeName("urgn",attributes.get(k).get(1)));
                                            ((TextView) single_items.findViewById(R.id.buty)).setText(getAttributeName("buty",attributes.get(k).get(2)));
                                            ((TextView) single_items.findViewById(R.id.freq)).setText(getAttributeName("freq",attributes.get(k).get(3)));
                                            ((TextView) single_items.findViewById(R.id.ligt)).setText(getAttributeName("ligt",attributes.get(k).get(4)));
                                            ((TextView) single_items.findViewById(R.id.wter)).setText(getAttributeName("wter",attributes.get(k).get(5)));

                                            Boolean flag = false;
                                            String first_room = "";
                                            //如果没有此房间，则随机选择
                                            for(String lr: layout_room) {  //遍历所有房间
                                                first_room = lr;
                                                if (lr.contains(space.get(k))) {  //针对每一个房间，判断是否包含房间的关键字段
                                                    ((TextView) single_items.findViewById(R.id.space)).setText("· " + lr);
                                                    flag = true;
                                                    space.set(k,lr);
                                                    break;
                                                }
                                            }
                                            if(!flag) {  //未找到匹配房间
                                                ((TextView) single_items.findViewById(R.id.space)).setText("· 无匹配空间");
                                                space.set(k, first_room);
                                            }

                                            depart_res_2.add(depart_res.get(k));
                                            detect_res_2.add(detect_res.get(k));
                                            attributes_2.add(attributes.get(k));
                                            space_2.add(space.get(k));
                                            detect_type_2.add(detect_type.get(k));
                                            container_2.addView(single_items);

                                            if(k == detect_res.size() - 1){
                                                depart_res.clear();
                                                detect_res.clear();
                                                attributes.clear();
                                                space.clear();
                                                detect_type.clear();
                                            }

                                            single_items.setOnLongClickListener(v -> {
                                                attention_dialog dd = new attention_dialog("你确认要删除"+ detect_res_2.get(container_2.indexOfChild(v)).toString()+"吗？","删除物品" ,"确认删除", "我点错了",context, isAccept -> {
                                                    if(isAccept){
                                                        detect_res_2.remove(container_2.indexOfChild(v));
                                                        depart_res_2.remove(container_2.indexOfChild(v));
                                                        attributes_2.remove(container_2.indexOfChild(v));
                                                        space_2.remove(container_2.indexOfChild(v));
                                                        detect_type_2.remove(container_2.indexOfChild(v));
                                                        container_2.removeView(v);
                                                    }
                                                });
                                                dd.onCreate_Attention_Dialog();
                                                return true;
                                            });

                                            single_items.setOnClickListener(v -> {
                                                attribute_dialog dd = new attribute_dialog(detect_res_2.get(container_2.indexOfChild(v)), space_2.get(container_2.indexOfChild(v)), attributes_2.get(container_2.indexOfChild(v)), context, (isAccept, single_attribute, spaces, keywords)-> {
                                                    if(isAccept){
                                                        Integer kk = container_2.indexOfChild(v);
                                                        attributes_2.set(kk,single_attribute);
                                                        space_2.set(kk, spaces);
                                                        detect_res_2.set(kk,keywords);
                                                        ((TextView) v.findViewById(R.id.size)).setText(getAttributeName("size",attributes_2.get(kk).get(0)));
                                                        ((TextView) v.findViewById(R.id.urgn)).setText(getAttributeName("urgn",attributes_2.get(kk).get(1)));
                                                        ((TextView) v.findViewById(R.id.buty)).setText(getAttributeName("buty",attributes_2.get(kk).get(2)));
                                                        ((TextView) v.findViewById(R.id.freq)).setText(getAttributeName("freq",attributes_2.get(kk).get(3)));
                                                        ((TextView) v.findViewById(R.id.ligt)).setText(getAttributeName("ligt",attributes_2.get(kk).get(4)));
                                                        ((TextView) v.findViewById(R.id.wter)).setText(getAttributeName("wter",attributes_2.get(kk).get(5)));
                                                        ((TextView) v.findViewById(R.id.space)).setText("· " + spaces);
                                                        ((TextView) v.findViewById(R.id.detect_res)).setText(keywords);
                                                    }
                                                });
                                                dd.onCreate_Attention_Dialog();
                                            });
                                        }
                                    }

                                });
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
                        }
                    }).start();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else{  //分割完成，执行识别操作
                    op = true;
                    new ImageProcessingTask().execute();
            }
        }


    //物品分割
    private void depart(Context context, String token, Uri uri) throws IOException {
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/multi_object_detect";
        try {
            byte[] imgData = getBytesFromUri(context.getContentResolver(),uri);
            Bitmap originalBitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam;
            String result = HttpUtil.post(url, token, param);
            JSONObject jsonObject = new JSONObject(result);
            JSONArray resultArray = jsonObject.getJSONArray("result");


            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject item = resultArray.getJSONObject(i);
                JSONObject location = item.getJSONObject("location");
                int left = location.getInt("left");
                int top = location.getInt("top");
                int width = location.getInt("width");
                int height = location.getInt("height");

                Bitmap croppedBitmap = Bitmap.createBitmap(originalBitmap, left, top, width, height);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                depart_res.add(stream.toByteArray());  //分割后的图片存起来
            }

        } catch (IOException e) {
            Log.e("AdvancedGeneral", "IOException occurred", e);
        } catch (Exception e) {
            Log.e("AdvancedGeneral", "General exception occurred", e);
        }
    }
    //uri获得检测结果
    private void detect(Context context, String token) throws IOException {
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general";
        try {

            for(int k = 0; k < Math.min(depart_res.size(),10); k++){
                byte[] imgData = depart_res.get(k);
                String imgStr = Base64Util.encode(imgData);
                String imgParam = URLEncoder.encode(imgStr, "UTF-8");
                String param = "image=" + imgParam;
                String res = HttpUtil.post(url, token, param);
                JSONObject jsonObject = new JSONObject(res);
                JSONArray resultArray = jsonObject.getJSONArray("result");

                double maxScore = Double.MIN_VALUE;
                String maxScoreKeyword = null;
                String maxScoreType = null;

                for (int i = 0; i < resultArray.length(); i++) {
                    JSONObject obj = resultArray.getJSONObject(i);
                    String root = obj.getString("root");
                    if(root.contains("非自然图像") || root.contains("建筑") || root.contains("活动") || root.contains("人物") || root.contains("场景") || root.contains("二维码") || root.contains("风景") || root.contains("动物") || root.contains("动漫") || root.contains("交通工具"))
                        continue;
                    double score = obj.getDouble("score");
                    String keyword = obj.getString("keyword");

                    if (score > maxScore) {
                        maxScore = score;
                        maxScoreKeyword = keyword;
                        maxScoreType = root;
                    }
                }
                detect_res.add(maxScoreKeyword);
                detect_type.add(maxScoreType);
        }

        } catch (IOException e) {
            Log.e("AdvancedGeneral", "IOException occurred", e);
        } catch (Exception e) {
            Log.e("AdvancedGeneral", "General exception occurred", e);
        }
    }
    //将uri转化为二进制形式图片
    private byte[] getBytesFromUri(ContentResolver contentResolver, Uri uri) throws IOException {
        InputStream inputStream = contentResolver.openInputStream(uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}

    private String getAttributeName(String atri, Float value){
        switch (atri){
            case "size":{
                if(value <= 1) return "· 小物件";
                else if(value > 1 && value <= 2) return "· 中物件";
                else return "· 大物件";
            }
            case "urgn":{
                if(value <= 1) return "  · 非紧急";
                else if(value > 1 && value <= 2) return "  · 稍紧急";
                else return "  · 紧急";
            }
            case "buty":{
                if(value <= 1) return "· 不美观";
                else if(value > 1 && value <= 2) return "· 较美观";
                else return "· 美观";
            }
            case "freq":{
                if(value <= 1) return "  · 少用";
                else if(value > 1 && value <= 2) return "  · 偶尔用";
                else return "  · 常用";
            }
            case "ligt":{
                if(value <= 1) return "· 不避光";
                else if(value > 1 && value <= 2) return "· 防暴晒";
                else return "· 避光";
            }
            case "wter":{
                if(value <= 1) return "  · 不避湿";
                else if(value > 1 && value <= 2) return "  · 防潮";
                else return "  · 干燥";
            }
            default: return" ";
        }
    }
    @Override
    public void onBackPressed() {
        attention_dialog dd = new attention_dialog("你确定要放弃现有编辑，重新返回拍照界面吗？","清空提醒" ,"重新开始", "不，我点错了",this, isAccept -> {
            if(isAccept){
                depart_res_1.clear();
                detect_res_1.clear();
                attributes_1.clear();
                detect_type_1.clear();
                space_1.clear();
                layout_room.clear();
                room_Id.clear();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                super.onBackPressed();
            }
        });
        dd.onCreate_Attention_Dialog();
    }
    public void layout_get_room(Integer layout_id){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        StringBuilder queryParams = new StringBuilder();
        queryParams.append(layout_id);

        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/getRoomId?layout_id=" + queryParams;
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

                            String rnm = jsonObject.getString("room_name");
                            Integer rid = jsonObject.getInt("room_id");

                            layout_room.add(rnm);
                            room_Id.put(rnm,rid);
                        }
                    }
                    else {
                        System.out.println("响应码: " + response.code());
                        String responseBody = response.body().string();
                        System.out.println("响应体: " + responseBody);
                    }
                    response.body().close();
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }).start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    }
