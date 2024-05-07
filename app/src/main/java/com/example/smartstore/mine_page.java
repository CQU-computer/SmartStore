package com.example.smartstore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import audio.audioTransform;
import audio.openAudioDialog;
import chattingCircle.ChattingCircle;
import chattingCircle.Chattingcircle_recommend;
import image_submit.Utils;
import image_submit.attention_dialog;
import in_out_rcd.in_out_rcd;
import login.LoginActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class mine_page extends AppCompatActivity  {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    EditText user_name1;
    EditText user_sg1;
    private Activity activity;
    Button setting;
    ImageView rank;
    ImageView HonorLevel;
    public Circle user_img;
    TextView user_id;
    private int u_id;
    private String invitecode; //用于接受邀请码
    private int u_id2;//用户接受邀请者的id
    private  int layoutid;    //用于接受返回的场景
    private  int layout_id;  //当前场景id
    ImageView my_like_btn;
    Button checkinButton;
    TextView use_rank;
    TextView HonorText;
    TextView HonorNextLevel;
    TextView use_exp;
    TextView max_exp;
    ProgressBar progressBar;
    Context context;
    public static final String[] LEVEL_NAMES = {"下一级：收纳新手", "下一级：收纳玩家", "下一级：收纳专手", "下一级：收纳达人", "下一级：收纳大师", "下一级：齐物王者"};
    public static final String[] LEVEL_rank = {" 收纳新手", " 收纳玩家", " 收纳专手", " 收纳达人", " 收纳大师", " 齐物王者"};
    private static final int STORAGE_PERMISSION = 1;
    int REQUEST_CODE_CAPTURE_IMAGE = 100;

    private File file;
    ImageView my_feedback_btn;
    private ImageView my_record_btn;
    private ImageView people;
    private Button contact;
    private ArrayList<String> user_list=new ArrayList<String>();//用于存放当前场景下的所有用户
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_page);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        context = this;
        setting=findViewById(R.id.setting_btn);
        activity = (Activity)this;
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = preferences.edit();
        user_sg1 = findViewById(R.id.user_signature1);
        user_name1 = findViewById(R.id.user_name1);
        user_id=findViewById(R.id.user_id);
        user_img=findViewById(R.id.user_img);
        my_like_btn=findViewById(R.id.my_like_btn);
        my_feedback_btn=findViewById(R.id.my_feedback_btn);
        my_record_btn = findViewById(R.id.my_record_btn);
        checkinButton = findViewById(R.id.checkin_button);
        HonorLevel=findViewById(R.id.HonorLevel);
        rank=findViewById(R.id.rank);
        use_rank=findViewById(R.id.user_rank);
        HonorText=findViewById(R.id.HonorLevelText);
        HonorNextLevel=findViewById(R.id.HonorNextLevel);
        use_exp=findViewById(R.id.user_exp);
        max_exp=findViewById(R.id.max_exp);
        progressBar = findViewById(R.id.exp_progress);
        contact = findViewById(R.id.contact_btn);
        people=findViewById(R.id.my_peo_btn);

        TextView audio = findViewById(R.id.audio);
        TextView goto1 = findViewById(R.id.fourTOine);
        TextView goto2 = findViewById(R.id.fourTOtwo);
        TextView goto3 = findViewById(R.id.fourTOthree);


        resetCheckinStatusIfNewDay();

        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        int serverId = preferences.getInt("uer_id", -1);
        layout_id = preferences.getInt("current_layout_id", -1);
        u_id = serverId;
        editor.putInt("uer_id", serverId);
        editor.apply();
        String UID=""+serverId;
        user_id.setText(UID);
        getinfoAll(serverId);

        audio.setOnLongClickListener(v -> {
            openAudioDialog ad = new openAudioDialog(this, (op,tmp) -> {
                if(op != -1){
                    audioTransform at = new audioTransform(op, this, tmp);
                    at.audioStart();
                }
            });
            ad.onCreateDialog();
            return false;
        });
        contact.setOnClickListener(v -> showInviteDialog());

        user_name1.setOnFocusChangeListener((v, hasFocus) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!hasFocus) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                user_name1.setCursorVisible(false);
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                user_name1.requestFocus();
                user_name1.setCursorVisible(true);
            }
        });
        user_name1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @SuppressLint("ResourceType")
            @Override
            public void afterTextChanged(Editable s) {
                String userName = s.toString();
                int updateId = serverId;
//                editor.putString("username", userName);
//                editor.apply();
//                String updatename = preferences.getString("username", null);
                updateInfoname(updateId,userName);
            }
        });
        user_sg1.setOnFocusChangeListener((v, hasFocus) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!hasFocus) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                user_sg1.setCursorVisible(false);
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                user_sg1.requestFocus();
                user_sg1.setCursorVisible(true);
            }
        });
        user_sg1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @SuppressLint("ResourceType")
            @Override
            public void afterTextChanged(Editable s) {
                String userSg = s.toString();
                editor.putString("usersg", userSg);
                editor.apply();
                int updateId = serverId;
                String updatesg = preferences.getString("usersg", null);
                updateInfosg(updateId,updatesg);
            }
        });
        ViewGroup rootView = (ViewGroup) findViewById(R.id.root_layout);
        if (rootView != null) {
            rootView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
                @Override
                public void onChildViewAdded(View parent, View child) {}
                @Override
                public void onChildViewRemoved(View parent, View child) {}
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user_name1.clearFocus();
                        user_sg1.clearFocus();
                    }
                };
                private void addClickListenerRecursively(ViewGroup parent) {
                    for (int i = 0; i < parent.getChildCount(); i++) {
                        View child = parent.getChildAt(i);
                        child.setOnClickListener(onClickListener);

                        if (child instanceof ViewGroup) {
                            addClickListenerRecursively((ViewGroup) child);
                        }
                    }
                }
                {
                    addClickListenerRecursively(rootView);
                }
            });
        }
        checkinButton.setOnClickListener(v -> {
            if (isAlreadyCheckedInToday()) {
                Toast.makeText(mine_page.this, "今天已经签到过了", Toast.LENGTH_SHORT).show();
                checkinButton.setText("已签到");
                return;
            }
            updateCheckinStatus(true);
            int bef_experience = preferences.getInt("user_experience", 0);
            int updatedExperience = bef_experience + 50;
            editor.putInt("user_experience", updatedExperience);
            editor.apply();
            int updateId = serverId;
            String updatexp = String.valueOf(preferences.getInt("user_experience", 0));

            updateInfoxp(updateId,updatexp);

            int uer_experience = preferences.getInt("user_experience", 0);
            String user_expStr= "" + uer_experience;
            use_exp.setText(user_expStr);
            use_rank.setText(""+calculateLevel(uer_experience));
            String userLevelName = calculateNextLevel(uer_experience);
            HonorNextLevel.setText(userLevelName);
            String userRankText=calculateLevelText(uer_experience);
            HonorText.setText(userRankText);
            imagelevel(uer_experience);
            String maxExp=" / "+calculateMAXEXP(uer_experience);
            max_exp.setText(maxExp);
            int progress = (int) (((double) uer_experience /calculateMAXEXP(uer_experience) ) * 100);
            progressBar.setProgress(progress);

            showRandomMotivationalQuote();
            checkinButton.setText("签到成功");
        });

        boolean isCheckedIn = preferences.getBoolean("is_checked_in", false);
        if (isCheckedIn) {
            checkinButton.setText("已签到");
        } else {
            checkinButton.setText("· 今天还未签到哦");
        }
        my_like_btn.setOnClickListener(v -> {
            Intent intent2 = new Intent(mine_page.this, ChattingCircle.class);
            intent2.putExtra("trigger_button2_click", true);
            startActivity(intent2);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        setting.setOnClickListener(v -> {
            attention_dialog dd = new attention_dialog("你确定要退出登录吗？" ,"退出登录","确定退出", "取消",mine_page.this, isAccept -> {
                if(isAccept){
                    long TimeMillis = Instant.ofEpochSecond(946684800).toEpochMilli();
                    editor.putLong("last_login_time", TimeMillis);
                    editor.apply();

                    Intent intent = new Intent(mine_page.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            dd.onCreate_Attention_Dialog();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        user_img.setOnClickListener(v -> xzImage());

        my_record_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, in_out_rcd.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        my_feedback_btn.setOnClickListener(v -> {
            discription_dialog dd = new discription_dialog("在这里输入反馈哦~",mine_page.this, R.style.style_dialog, new discription_dialog.PriorityListener() {
                @Override
                public void refreshPriorityUI(String string) {
                    updateInfoFeedback(serverId,string);
                }
            });
            dd.onCreateDialog();
        });

        people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建一个自定义对话框
                Dialog dialog = new Dialog(mine_page.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_user_list);
                LinearLayout userContainer = dialog.findViewById(R.id.user_container);
                TextView title=dialog.findViewById(R.id.user_title);
                userContainer.removeAllViews();

                SharedPreferences preference_name = getSharedPreferences("config", Context.MODE_PRIVATE);
                String Current_layout =  preference_name.getString("current_layout_name","");

                title.setText(Current_layout + "的成员: ");
                title.setTextSize(15);
                for (String userName : user_list) {
                    TextView textView = new TextView(mine_page.this);
                    textView.setText("· " + userName);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    int marginInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
                    layoutParams.setMargins(0, marginInPixels, 0, 0); // left, top, right, bottom

                    textView.setLayoutParams(layoutParams);
                    userContainer.addView(textView);
                }
                dialog.show();
            }
        });
        //****拉取用户名称
        Thread t3= new Thread(() -> getUsers(layout_id));
        t3.start();
        try {
            t3.join();
        }catch (InterruptedException e){
        }

        goto1.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
        });

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String source = extras.getString("source");
            if (source != null && (source.equals("audio_invite") || source.equals("audio_accept_invite"))) {
                contact.performClick();
            }
        }
    }

    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        int serverId = preferences.getInt("uer_id", -1);
        getInfo(serverId);
    }

    private  ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String realPath = Utils.getRealPath(this, data);
                    file = new File(realPath);

                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    user_img.setImageBitmap(bitmap);
                    SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                    int serverId = preferences.getInt("uer_id", -1);
                    final_upload(file,serverId);

                }
            });

    public void final_upload(File file,int id){
        String uid =  String.valueOf(id);
        File it_img = file;
        JSONObject json = new JSONObject();
        try {
            json.put("u_img",it_img);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url("http://120.26.248.74:8080/updateUserInfo?uid="+uid)
                .post(body)
                .build();

        OkHttpClient client1 = new OkHttpClient();
        client1.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) {
                assert response.body() != null;
                if (!response.isSuccessful()) {
                    mine_page.this.runOnUiThread(() -> {
                        try {
                            String responseBody = response.body().string();
                        } catch (IOException e) {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_SHORT).show());
                        }
                    });
                }
                else{
                    runOnUiThread(() -> Toast.makeText(mine_page.this, "提交成功！", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
    private void xzImage() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, STORAGE_PERMISSION);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            else {
                attention_dialog dd = new attention_dialog("开启读取照片权限，\n就可以上传头像啦!" ,"获取照片权限未开启哦！","去开启", "下次再来",this, isAccept -> {
                    if(isAccept){
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
                dd.onCreate_Attention_Dialog();
            }
        }
        if(requestCode == REQUEST_CODE_CAPTURE_IMAGE){
            attention_dialog dd = new attention_dialog("开启相机和照片权限，\n才能上传头像哦~" ,"获取相机权限开启！","去开启", "下次再来",this, isAccept -> {
                if(isAccept){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);  //打开设置
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
            dd.onCreate_Attention_Dialog();
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
                @SuppressLint("StaticFieldLeak")
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

                                    user_name1.setText(serverUname);
                                    user_sg1.setText(serverUsignature);

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
                                                return null;
                                            }
                                        }

                                        @Override
                                        protected void onPostExecute(Bitmap bitmap) {
                                            if (bitmap != null) {
                                                user_img.setImageBitmap(bitmap);
                                            } else {
                                                user_img.setImageResource(R.drawable.yoyo);
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
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_SHORT).show());
                    } catch (JSONException e) {
                    }
                }
            }).start();

        } catch (Exception e) {
        }
    }

    private void resetCheckinStatusIfNewDay() {
        Date today = Calendar.getInstance().getTime();
        String todayString = new SimpleDateFormat("yyyyMMdd").format(today);
        String storedDateString = preferences.getString("last_day_string", "");

        if (!storedDateString.equals(todayString)) {
            resetCheckinStatus();
            editor.putString("last_day_string", todayString);
            editor.apply();
        }
    }

    private void resetCheckinStatus() {
        editor.putBoolean("is_checked_in", false);
    }
    private boolean isAlreadyCheckedInToday() {
        long lastCheckinTimestamp = preferences.getLong("last_checkin_date", 0L);
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);

        Calendar todayEnd = (Calendar) todayStart.clone();
        todayEnd.add(Calendar.DAY_OF_MONTH, 1);

        Calendar lastCheckinDate = Calendar.getInstance();
        lastCheckinDate.setTimeInMillis(lastCheckinTimestamp);

        return lastCheckinDate.after(todayStart) && lastCheckinDate.before(todayEnd);
    }

    private void updateCheckinStatus(boolean isCheckedIn) {
        editor.putBoolean("is_checked_in", isCheckedIn);
        editor.putLong("last_checkin_date", System.currentTimeMillis());
        editor.apply();
    }

    private static final String[] MOTIVATIONAL_QUOTES = {
            "每一次努力都不会被辜负，继续加油！",
            "积累点滴经验，构筑坚实未来！",
            "积跬步以致千里，坚持每日签到，累积宝贵经验，成就卓越自我。",
            "签到不只是打卡，更是对进步的见证!",
            "相信自己，未来就在你的手中，勇往直前！"
    };

    private void showRandomMotivationalQuote() {
        int randomIndex = new Random().nextInt(MOTIVATIONAL_QUOTES.length);
        String quote = MOTIVATIONAL_QUOTES[randomIndex];

        new AlertDialog.Builder(mine_page.this)
                .setTitle("签到成功")
                .setMessage(quote)
                .setPositiveButton("好的", null)
                .show();
    }

    public String calculateNextLevel(int userExperience) {
        if (userExperience >= 5000) {
            return "已是最大等级";
        } else if (userExperience >= 4000) {
            return LEVEL_NAMES[5];
        } else if (userExperience >= 2000) {
            return LEVEL_NAMES[4];
        } else if (userExperience >= 1000) {
            return LEVEL_NAMES[3];
        } else if (userExperience >= 500) {
            return LEVEL_NAMES[2];
        } else {
            return LEVEL_NAMES[1];
        }
    }
    public String calculateLevelText(int userExperience) {
        if (userExperience >= 5000) {
            return LEVEL_rank[5];
        } else if (userExperience >= 4000) {
            return LEVEL_rank[4];
        } else if (userExperience >= 2000) {
            return LEVEL_rank[3];
        } else if (userExperience >= 1000) {
            return LEVEL_rank[2];
        } else if (userExperience >= 500) {
            return LEVEL_rank[1];
        } else {
            return LEVEL_rank[0];
        }
    }
    public void imagelevel(int userExperience){
        int resourceId;
        Drawable drawable;
        if (userExperience >= 5000) {
            resourceId = getResources().getIdentifier("rank6", "drawable", getPackageName());
            drawable = getResources().getDrawable(resourceId);
        } else if (userExperience >= 4000) {
            resourceId = getResources().getIdentifier("rank5", "drawable", getPackageName());
            drawable = getResources().getDrawable(resourceId);
        } else if (userExperience >= 2000) {
            resourceId = getResources().getIdentifier("rank4", "drawable", getPackageName());
            drawable = getResources().getDrawable(resourceId);
        } else if (userExperience >= 1000) {
            resourceId = getResources().getIdentifier("rank3", "drawable", getPackageName());
            drawable = getResources().getDrawable(resourceId);
        } else if (userExperience >= 500) {
            resourceId = getResources().getIdentifier("rank2", "drawable", getPackageName());
            drawable = getResources().getDrawable(resourceId);
        } else {
            resourceId = getResources().getIdentifier("rank1", "drawable", getPackageName());
            drawable = getResources().getDrawable(resourceId);
        }
        rank.setImageDrawable(drawable);
        HonorLevel.setImageDrawable(drawable);
    }
    public int calculateLevel(int userExperience) {
        if (userExperience >= 5000) {
            return 6;
        } else if (userExperience >= 4000) {
            return 5;
        } else if (userExperience >= 2000) {
            return 4;
        } else if (userExperience >= 1000) {
            return 3;
        } else if (userExperience >= 500) {
            return 2;
        } else {
            return 1;
        }
    }
    public int calculateMAXEXP(int userExperience) {
        if (userExperience >= 5000) {
            return 10000;
        } else if (userExperience >= 4000) {
            return 5000;
        } else if (userExperience >= 2000) {
            return 4000;
        } else if (userExperience >= 1000) {
            return 2000;
        } else if (userExperience >= 500) {
            return 1000;
        } else {
            return 500;
        }
    }

    public void updateInfoxp(Integer uid, String u_xp) {
        OkHttpClient client1 = new OkHttpClient().newBuilder().build();
        MediaType JSON1 = MediaType.parse("application/json; charset=utf-8");

        StringBuilder queryParams1 = new StringBuilder();

        queryParams1.append("uid=").append(uid).append("&");
        queryParams1.append("u_xp=").append(u_xp);

        RequestBody body1 = RequestBody.create(JSON1, "");
        String url1 = "http://120.26.248.74:8080/updateUserInfo?" + queryParams1;
        try {
            new Thread(() -> {
                try {
                    Request request = new Request.Builder()
                            .url(url1)
                            .post(body1)
                            .build();

                    Response response = client1.newCall(request).execute();
                    if (response.isSuccessful()) {
                    } else {
                        System.out.println("响应码: " + response.code());
                        String responseBody = response.body().string();
                        System.out.println("响应体: " + responseBody);
                    }
                    response.body().close();
                } catch (IOException e) {
                }
            }).start();

        } catch (Exception e) {
        }
    }

    public void updateInfoname(Integer uid, String uname) {
        OkHttpClient client1 = new OkHttpClient().newBuilder().build();
        MediaType JSON1 = MediaType.parse("application/json; charset=utf-8");

        StringBuilder queryParams1 = new StringBuilder();

        queryParams1.append("uid=").append(uid).append("&");
        queryParams1.append("uname=").append(uname);

        RequestBody body1 = RequestBody.create(JSON1, "");
        String url1 = "http://120.26.248.74:8080/updateUserInfo?" + queryParams1;
        try {
            new Thread(() -> {
                try {
                    Request request = new Request.Builder()
                            .url(url1)
                            .post(body1)
                            .build();

                    Response response = client1.newCall(request).execute();
                    if (response.isSuccessful()) {
                        editor.putString("username", uname);
                        editor.apply();
                    } else {
                        System.out.println("响应码: " + response.code());
                        String responseBody = response.body().string();
                        System.out.println("响应体: " + responseBody);
                    }
                    response.body().close();
                } catch (IOException e) {
                }
            }).start();

        } catch (Exception e) {
        }
    }
    public void updateInfosg(Integer uid, String u_sg) {
        OkHttpClient client1 = new OkHttpClient().newBuilder().build();
        MediaType JSON1 = MediaType.parse("application/json; charset=utf-8");

        StringBuilder queryParams1 = new StringBuilder();

        queryParams1.append("uid=").append(uid).append("&");
        queryParams1.append("u_signature=").append(u_sg);

        RequestBody body1 = RequestBody.create(JSON1, "");
        String url1 = "http://120.26.248.74:8080/updateUserInfo?" + queryParams1;
        try {
            new Thread(() -> {
                try {
                    Request request = new Request.Builder()
                            .url(url1)
                            .post(body1)
                            .build();

                    Response response = client1.newCall(request).execute();
                    if (response.isSuccessful()) {
                    } else {
                        System.out.println("响应码: " + response.code());
                        String responseBody = response.body().string();
                        System.out.println("响应体: " + responseBody);
                    }
                    response.body().close();
                } catch (IOException e) {
                }
            }).start();

        } catch (Exception e) {
        }
    }
    public void updateInfoFeedback(Integer uid, String string){
        OkHttpClient client1 = new OkHttpClient().newBuilder().build();
        MediaType JSON1 = MediaType.parse("application/json; charset=utf-8");

        StringBuilder queryParams1 = new StringBuilder();

        queryParams1.append("uid=").append(uid).append("&");
        queryParams1.append("u_feedback=").append(string);

        RequestBody body1 = RequestBody.create(JSON1, "");
        String url1 = "http://120.26.248.74:8080/updateUserInfo?" + queryParams1;
        try {
            new Thread(() -> {
                try {
                    Request request = new Request.Builder()
                            .url(url1)
                            .post(body1)
                            .build();

                    Response response = client1.newCall(request).execute();
                    if (response.isSuccessful()) {

                    } else {

                    }
                    response.body().close();
                } catch (IOException e) {
                }
            });

        } catch (Exception e) {
        }
    }
    public void getinfoAll(int id){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        StringBuilder queryParams = new StringBuilder();
        queryParams.append("uid=").append(id); // 直接拼接整数值
        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/getUserInfo?" + queryParams;
        try {
            new Thread(new Runnable() {
                @SuppressLint("StaticFieldLeak")
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
                                int u_xp = jsonObject.getInt("u_xp");
                                String u_img=jsonObject.getString("u_img");

                                activity.runOnUiThread(() -> {
                                    String serverUname = uname;
                                    String serverUsignature = u_signature;
                                    int serverUxp = u_xp;

                                    editor.putString("username",serverUname);
                                    editor.putString("usersg",serverUsignature);
                                    editor.apply();

                                    user_name1.setText(serverUname);
                                    user_sg1.setText(serverUsignature);
                                    new AsyncTask<Void, Void, Bitmap>() {
                                        protected Bitmap doInBackground(Void... voids) {
                                            try {
                                                URL imageUrl = new URL(u_img);
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
                                                user_img.setImageBitmap(bitmap);
                                            } else {
                                            }
                                        }
                                    }.execute();
                                    String user_expStr = "" + serverUxp;
                                    use_exp.setText(user_expStr);
                                    String u_rank=""+calculateLevel(serverUxp);
                                    use_rank.setText(u_rank);
                                    String usernextLevelName = calculateNextLevel(serverUxp);
                                    HonorNextLevel.setText(usernextLevelName);
                                    String userRankText=calculateLevelText(serverUxp);
                                    HonorText.setText(userRankText);
                                    imagelevel(serverUxp);
                                    String maxExp = " / " + calculateMAXEXP(serverUxp);
                                    max_exp.setText(maxExp);
                                    int progress = (int) (((double) serverUxp / calculateMAXEXP(serverUxp)) * 100);
                                    progressBar.setProgress(progress);
                                    editor.putInt("user_experience",serverUxp );
                                    editor.apply();
                                });

                            }
                            else {
                                // 如果JSONArray为空，处理这种情况
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
            }).start();
        } catch (Exception e) {
        }

    }
    public void getUsers(int layout_id) {
        OkHttpClient client1 = new OkHttpClient().newBuilder().build();
        MediaType JSON1 = MediaType.parse("application/json; charset=utf-8");

        StringBuilder queryParams1 = new StringBuilder();

        queryParams1.append("layout_id=").append(layout_id);
        System.out.println("你好世界 获取用户参数" + queryParams1);
        RequestBody body1 = RequestBody.create(JSON1, "");
        String url1 = "http://120.26.248.74:8080/useLayoutGetUid?" + queryParams1;
        try {
            Request request = new Request.Builder()
                    .url(url1)
                    .post(body1)
                    .build();

            Response response = client1.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println("你好世界 成功拉取用户名称");
                String js = response.body().string();
                JSONArray jsonArray = new JSONArray(js);
                //拉取的时候清空taskList,避免重复拉取
                user_list.clear();
                for (int k = 0; k < jsonArray.length(); k++){
                    JSONObject jsonObject = jsonArray.getJSONObject(k);

                    String user_name=jsonObject.getString("uname");
                    user_list.add(user_name);
                }} else {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            response.body().close();
        } catch (IOException | JSONException e) {
        }
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    private void showInviteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("邀请你的家庭成员");
        String[] items = {"邀请成员", "接受邀请"};
        builder.setItems(items, (dialog, which) -> {
            if (which == 0) {
                showInviteCodeDialog();
            } else if (which == 1) {
                showEnterInviteCodeDialog();
            }
            dialog.dismiss();
        }).setNegativeButton("返回", null);

        builder.show();
    }
    //*****生成邀请码
    private void showInviteCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Thread t1= new Thread(() -> invite_family(u_id,layout_id));
        t1.start();
        try {
            t1.join();
        }catch (InterruptedException e){
        }
        System.out.println("你好世界 邀请码 " + invitecode);
        builder.setMessage("你的邀请码是："+invitecode); // 假设的邀请码
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // 用户点击了确认按钮，可以在这里执行相关操作
                dialog.dismiss();
            }
        });
        builder.show();
    }
    //*****生成邀请码
    //*****接受邀请
    private void showEnterInviteCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入你的邀请码");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String inviteCode1 = input.getText().toString();
                Thread t1= new Thread(() -> accept_invite(inviteCode1,u_id));
                t1.start();
                try {
                    t1.join();
                }catch (InterruptedException e){
                }
                System.out.println("你好世界 邀请人 " + layoutid);
                if (layoutid==-1) { Toast.makeText(context, "您的验证码无效", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "您成功加入家庭: "+layoutid, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
    //*****接受邀请
    //****api接口
    public void invite_family(int uid,int layout_id){
        OkHttpClient client1 = new OkHttpClient().newBuilder().build();
        MediaType JSON1 = MediaType.parse("application/json; charset=utf-8");

        StringBuilder queryParams1 = new StringBuilder();

        queryParams1.append("uid=").append(uid).append("&");
        queryParams1.append("layout_id=").append(layout_id);


        System.out.println("你好世界 邀请家庭成员参数 " + queryParams1);
        RequestBody body1 = RequestBody.create(JSON1, "");
        String url1 = "http://120.26.248.74:8080/inviteFamily?" + queryParams1;
        try {
            Request request = new Request.Builder()
                    .url(url1)
                    .post(body1)
                    .build();

            Response response = client1.newCall(request).execute();
            if (response.isSuccessful()) {
                invitecode = response.body().string();
                System.out.println("你好世界 成功获取邀请码： "+invitecode);
            } else {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            response.body().close();
        } catch (IOException e) {
        }
    }
    public void accept_invite(String invite_code, int u_id){
        OkHttpClient client1 = new OkHttpClient().newBuilder().build();
        MediaType JSON1 = MediaType.parse("application/json; charset=utf-8");

        StringBuilder queryParams1 = new StringBuilder();

        queryParams1.append("invite_code=").append(invite_code).append("&");
        queryParams1.append("uid=").append(u_id);

        System.out.println("你好世界 接受邀请参数 " + queryParams1);
        RequestBody body1 = RequestBody.create(JSON1, "");
        String url1 = "http://120.26.248.74:8080/acceptInvite?" + queryParams1;
        try {
            Request request = new Request.Builder()
                    .url(url1)
                    .post(body1)
                    .build();

            Response response = client1.newCall(request).execute();
            if (response.isSuccessful()) {
                String s=response.body().string();
                layoutid = Integer.parseInt(s);
                System.out.println("你好世界 成功接受邀请，你加入的场景为 "+layoutid);
            } else {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            response.body().close();
        } catch (IOException e) {
        }
    }
}