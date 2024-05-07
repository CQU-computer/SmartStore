package chattingCircle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartstore.Circle;
import com.example.smartstore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OtherZhuye extends AppCompatActivity {

    private Button button1;
    private Button button2;
    Context context;
    ImageView postimage;
    TextView postname;
    TextView postrlstime;
    TextView postlikes;
    public Circle user_img1;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private Activity activity;
    ImageView icon1;
    ImageView icon2;
    TextView user_sg;
    TextView user_name;
    TextView thumbs;
    TextView work;
    ImageView commend_icon;
    ImageView mine_icon;
    int works=0;
    int like=0;
    int serverId;
    String id;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_zhuye);
        activity = (Activity)this;
        context=(Context)this;

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = preferences.edit();

        Intent intent = getIntent();
        id=intent.getStringExtra("poster_id");
        serverId=Integer.parseInt(id);

        //serverId = preferences.getInt("uer_id",-1);
        thumbs=findViewById(R.id.thumbs);
        work=findViewById(R.id.concerns);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        icon2 = findViewById(R.id.icon2);
        icon1 = findViewById(R.id.icon1);
        user_sg = findViewById(R.id.user_signature);
        user_name = findViewById(R.id.user_name);
        postimage=findViewById(R.id.postmedia);
        postname=findViewById(R.id.postname);
        postrlstime=findViewById(R.id.postrlstime);
        postlikes=findViewById(R.id.postlikes);
        user_img1=findViewById(R.id.user_img1);

        getInfo(serverId);
        getworks(serverId);
        like=0;
        getlikesnum(serverId);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setTextColor(Color.BLACK);
                button2.setTextColor(Color.parseColor("#cccccc"));
                icon1.setVisibility(View.VISIBLE);
                icon2.setVisibility(View.GONE);
                LinearLayout linearLayout = findViewById(R.id.container);
                linearLayout.removeAllViews();
                getshare(serverId);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setTextColor(Color.parseColor("#cccccc"));
                button2.setTextColor(Color.BLACK);
                icon1.setVisibility(View.GONE);
                icon2.setVisibility(View.VISIBLE);
                LinearLayout linearLayout = findViewById(R.id.container);
                linearLayout.removeAllViews();
                getlike(serverId);
            }
        });
        boolean shouldTriggerButton2Click = getIntent().getBooleanExtra("trigger_button2_click", false);
        if (shouldTriggerButton2Click) {
            button2.performClick();
        }
        else{
            button1.performClick();
        }

    }
    public void getInfo(int id){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        StringBuilder queryParams = new StringBuilder();
        queryParams.append("uid=").append(id);

        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/getUserInfo?" + queryParams;
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            String js = response.body().string();
                            JSONArray jsonArray = new JSONArray(js);
                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);

                                String uname = jsonObject.getString("uname");
                                String u_signature = jsonObject.getString("u_signature");
                                String u_img=jsonObject.getString("u_img");

                                activity.runOnUiThread(() -> {
                                    String serverUname = uname;
                                    String serverUsignature = u_signature;
                                    String serverimg=u_img;

                                    editor.putString("username",serverUname);
                                    editor.putString("usersg",serverUsignature);
                                    editor.apply();

                                    user_name.setText(serverUname);
                                    user_sg.setText(serverUsignature);
                                    new AsyncTask<Void, Void, Bitmap>() {
                                        @Override
                                        protected Bitmap doInBackground(Void... voids) {
                                            try {
                                                URL imageUrl = new URL(serverimg);
                                                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                                                connection.setDoInput(true);
                                                connection.connect();
                                                InputStream input = connection.getInputStream();
                                                return BitmapFactory.decodeStream(input);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                return null;
                                            }
                                        }
                                        @Override
                                        protected void onPostExecute(Bitmap bitmap) {
                                            if (bitmap != null) {
                                                user_img1.setImageBitmap(bitmap);
                                            } else {
                                                //Toast.makeText(activity, "加载头像失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }.execute();
                                });
                            }
                            else {
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
                    }
                }
            }).start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void getshare(int id){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        StringBuilder queryParams = new StringBuilder();
        queryParams.append("uid=").append(id);
        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/getUserShared?" + queryParams;
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            String js = response.body().string();
                            JSONArray jsonArray = new JSONArray(js);
                            String[] names=new String [jsonArray.length()];
                            Bitmap[] medias=new Bitmap[jsonArray.length()];

                            String[] times=new String [jsonArray.length()];
                            int[] likes=new int [jsonArray.length()];
                            int[] post_ids=new int [jsonArray.length()];
                            for (int k = 0; k < jsonArray.length(); k++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(k);

                                medias[k]=BitmapFactory.decodeFile(jsonObject.getString("post_media"));
                                String pname = jsonObject.getString("post_name");
                                names[k]=pname;
                                String ptime = jsonObject.getString("post_rls_time");
                                int commaIndex = ptime.indexOf("T");
                                String partOfPtime = ptime.substring(0, commaIndex);
                                times[k]=partOfPtime;
                                int plikes = jsonObject.getInt("post_likes");
                                likes[k]=plikes;
                                int pId=jsonObject.getInt("post_id");
                                post_ids[k]=pId;
                                System.out.println("我帖子呢" + jsonObject);
                            }
                            activity.runOnUiThread(() -> {
                                for (int k = 0; k < jsonArray.length(); k++) {
                                    int finalK = k;
                                    LinearLayout sub_interface_container = activity.findViewById(R.id.container);

                                    View single_items = LayoutInflater.from(sub_interface_container.getContext())
                                            .inflate(R.layout.chatting_my_content, sub_interface_container, false);

                                    ((TextView) single_items.findViewById(R.id.postname)).setText(names[finalK]);
                                    ((TextView) single_items.findViewById(R.id.postrlstime)).setText(times[finalK]);
                                    ((TextView) single_items.findViewById(R.id.postlikes)).setText(""+likes[finalK]);
                                    ((ImageView) single_items.findViewById(R.id.postmedia)).setImageBitmap(medias[finalK]);

                                    single_items.setOnClickListener(v -> {
                                        Intent intent = new Intent(context, Postings.class);
                                        intent.putExtra("trigger_single_items_click", true);
                                        intent.putExtra("post_Id", post_ids[finalK]);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    });
                                    sub_interface_container.addView(single_items);
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
                }
            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void getworks(int id){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        StringBuilder queryParams = new StringBuilder();
        queryParams.append("uid=").append(id);

        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/getUserShared?" + queryParams;
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            String js = response.body().string();
                            JSONArray jsonArray = new JSONArray(js);
                            works=jsonArray.length();
                            activity.runOnUiThread(() -> {
                                work.setText(""+works);
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
                }
            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void getlikesnum(int id){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        StringBuilder queryParams = new StringBuilder();
        queryParams.append("uid=").append(id);
        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/getUserShared?" + queryParams;
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            String js = response.body().string();
                            JSONArray jsonArray = new JSONArray(js);
                            like=0;
                            for (int k = 0; k < jsonArray.length(); k++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(k);
                                int plikes = jsonObject.getInt("post_likes");
                                like=like+plikes;
                            }
                            activity.runOnUiThread(() -> {
                                thumbs.setText(like+"");
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
                }
            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void getlike(int id){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        StringBuilder queryParams = new StringBuilder();
        queryParams.append("uid=").append(id);

        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/getUserLiked?" + queryParams;
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            String js = response.body().string();
                            JSONArray jsonArray = new JSONArray(js);
                            String[] names=new String [jsonArray.length()];
                            Bitmap[] medias=new Bitmap[jsonArray.length()];

                            String[] times=new String [jsonArray.length()];
                            int[] likes=new int [jsonArray.length()];
                            int[] post_ids=new int [jsonArray.length()];
                            for (int k = 0; k < jsonArray.length(); k++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(k);

                                medias[k]=BitmapFactory.decodeFile(jsonObject.getString("post_media"));
                                String pname = jsonObject.getString("post_name");
                                names[k]=pname;
                                String ptime = jsonObject.getString("post_rls_time");
                                int commaIndex = ptime.indexOf("T");
                                String partOfPtime = ptime.substring(0, commaIndex);
                                times[k]=partOfPtime;
                                int plikes = jsonObject.getInt("post_likes");
                                likes[k]=plikes;
                                int pId=jsonObject.getInt("post_id");
                                post_ids[k]=pId;
                                System.out.println("我帖子呢" + jsonObject);
                            }
                            activity.runOnUiThread(() -> {
                                for (int k = 0; k < jsonArray.length(); k++) {
                                    int finalK = k;
                                    LinearLayout sub_interface_container = activity.findViewById(R.id.container);

                                    View single_items = LayoutInflater.from(sub_interface_container.getContext())
                                            .inflate(R.layout.chatting_my_content, sub_interface_container, false);

                                    ((TextView) single_items.findViewById(R.id.postname)).setText(names[finalK]);
                                    ((TextView) single_items.findViewById(R.id.postrlstime)).setText(times[finalK]);
                                    ((TextView) single_items.findViewById(R.id.postlikes)).setText(""+likes[finalK]);
                                    System.out.println("-------+"+medias[finalK]);
                                    ((ImageView) single_items.findViewById(R.id.postmedia)).setImageBitmap(medias[finalK]);

                                    single_items.setOnClickListener(v -> {
                                        Intent intent = new Intent(context, Postings.class);
                                        intent.putExtra("trigger_single_items_click", true);
                                        intent.putExtra("post_Id", post_ids[finalK]);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    });
                                    sub_interface_container.addView(single_items);
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
                }
            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}