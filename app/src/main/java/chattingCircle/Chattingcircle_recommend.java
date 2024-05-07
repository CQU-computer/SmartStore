package chattingCircle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.smartstore.MainActivity;
import com.example.smartstore.R;
import com.example.smartstore.mine_page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import audio.audioTransform;
import audio.openAudioDialog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Chattingcircle_recommend extends AppCompatActivity implements ModuleAdapter.ItemClickListener{

    private ArrayList<String> post_name_ = new ArrayList<>();
    private ArrayList<String> bp_ = new ArrayList<>();
    private ArrayList<String>  post_detail_ = new ArrayList<>();
    private ArrayList<String> post_rls_time_ = new ArrayList<>();
    private ArrayList<String> user_ID_ = new ArrayList<>();
    private ArrayList<String> post_like_ = new ArrayList<>();
    private ArrayList<String> post_id_ = new ArrayList<>();
    private ArrayList<String> post_uname_ = new ArrayList<>();
    private Context context;
    private RecyclerView recyclerView;
    int serverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chattingcircle_recommend);
        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        serverId = preferences.getInt("uer_id",-1);
        context = this;
        TextView audio = findViewById(R.id.audio);
        Button commend = findViewById(R.id.commend);
        Button mine = findViewById(R.id.my_chatting_circle);
        ImageView commend_icon=findViewById(R.id.commend_icon);
        ImageView mine_icon=findViewById(R.id.my_circle_icon);
        TextView goto1 = findViewById(R.id.threeTOone);
        TextView goto2 = findViewById(R.id.threeTOtwo);
        TextView goto4 = findViewById(R.id.threeTOfour);
        recyclerView = findViewById(R.id.list1);

        audio.setOnLongClickListener(v -> {
            openAudioDialog ad = new openAudioDialog(this,  (op,tmp) -> {
                if(op != -1){
                    audioTransform at = new audioTransform(op, this,tmp);
                    at.audioStart();
                }
            });
            ad.onCreateDialog();
            return false;
        });

        boolean shouldTriggerPostingBackClick = getIntent().getBooleanExtra("trigger_postingback_click", false);
        if(shouldTriggerPostingBackClick){
            finish();
        }
        boolean shouldSaveClick = getIntent().getBooleanExtra("trigger_save_click", false);
        if(shouldSaveClick){
            finish();
        }
        boolean shouldCancelClick = getIntent().getBooleanExtra("trigger_cancel_click", false);
        if(shouldCancelClick){
            finish();
        }



        goto1.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
        });

        goto2.setOnClickListener(v -> {
            Intent intent = new Intent(context, Family.family.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
        });
        goto4.setOnClickListener(v -> {
            Intent intent = new Intent(context, mine_page.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
        });

        commend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commend.setTextColor(Color.parseColor("#204D36"));
                mine.setTextColor(Color.parseColor("#53856C"));
                commend_icon.setVisibility(View.VISIBLE);
                mine_icon.setVisibility(View.INVISIBLE);
            }
        });

        mine.setOnClickListener(v -> {
            commend.setTextColor(Color.parseColor("#53856C"));
            mine.setTextColor(Color.parseColor("#204D36"));
            commend_icon.setVisibility(View.INVISIBLE);
            mine_icon.setVisibility(View.VISIBLE);

            Intent intent = new Intent(Chattingcircle_recommend.this, ChattingCircle.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        Thread t1 = new Thread(this::getPost);
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        RecyclerView recyclerView = findViewById(R.id.list1);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        List<ModuleItem> moduleItems = getModuleItemsData();
        ModuleAdapter adapter = new ModuleAdapter(moduleItems,this);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(this);
    }

    public void onFixedImageButtonClick(int position) {
        Intent intent = new Intent(this, Post.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private List<ModuleItem> getModuleItemsData() {
        List<ModuleItem> moduleItems = new ArrayList<>();

        ModuleItem fixedModule = new ModuleItem(ModuleItem.Type.FIXED, null, "","","","","","","");
        moduleItems.add(fixedModule);

        for (int i = 0; i < bp_.size(); i++) {
            ModuleItem customModule = new ModuleItem(
                    ModuleItem.Type.CUSTOM,
                    bp_.get(i),
                    post_detail_.get(i),
                    post_name_.get(i),
                    post_rls_time_.get(i),
                    user_ID_.get(i),
                    post_like_.get(i),
                    post_id_.get(i),
                    post_uname_.get(i)

            );

            moduleItems.add(customModule);
        }
        return moduleItems;
    }
    public void getPost() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String url = "http://120.26.248.74:8080/scrollPost";

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String js = response.body().string();
                JSONArray jsonArray = new JSONArray(js);
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(k);

                    String post_name = jsonObject.getString("post_name");
                    String bp = jsonObject.getString("post_media");
                    String post_detail = jsonObject.getString("post_detail");
                    String post_rls_time = jsonObject.getString("post_rls_time");
                    String post_likes = jsonObject.getString("post_likes");
                    int commaIndex = post_rls_time.indexOf("T");
                    String partofptime = post_rls_time.substring(0, commaIndex);
                    int userid=jsonObject.getInt("uid");
                    String postid=jsonObject.getString("post_id");
                    String uname=jsonObject.getString("uname");

                    post_name_.add(post_name);
                    bp_.add(bp);
                    post_detail_.add(post_detail);
                    post_rls_time_.add(partofptime);
                    user_ID_.add(userid+"");
                    post_like_.add(post_likes);
                    post_id_.add(postid);
                    post_uname_.add(uname);
                }
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

    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        post_name_.clear();
        bp_.clear();
        post_detail_.clear();
        post_rls_time_.clear();
        user_ID_.clear();
        post_like_.clear();
        post_id_.clear();
        post_uname_.clear();

        Thread t1 = new Thread(this::getPost);
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (recyclerView != null) {
            ModuleAdapter adapter = (ModuleAdapter) recyclerView.getAdapter();
            if (adapter != null) {
                List<ModuleItem> updatedModuleItems = getModuleItemsData();
                adapter.updateData(updatedModuleItems);
            } else {
                List<ModuleItem> moduleItems = getModuleItemsData();
                ModuleAdapter newAdapter = new ModuleAdapter(moduleItems, this);
                recyclerView.setAdapter(newAdapter);
                newAdapter.setItemClickListener(this);
            }
        }
    }

}