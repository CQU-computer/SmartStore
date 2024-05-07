package login;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartstore.MainActivity;
import com.example.smartstore.R;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public Integer uid;
    private EditText userTelEditText;
    private EditText passwordEditText;
    private ImageView loginButton;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private ImageView goto_sign_up;
    private View in1;
    private View in2;
    private View up;
    private ImageView sign_card;
    private ImageView goto_sign_in;
    private TextView title;
    private TextView description;
    private ImageView star;
    private boolean isJudgePass = false;
    private ImageView resigter_btn;
    private EditText userTelEditText_rgst;
    private EditText userpasswordEditText_rgst;
    private EditText judge_input;
    private TextView judge_btn;
    private static int i;
    private Handler handler;
    private TextView warning;
    private EditText confirm_pswd;
    private SharedPreferences preferences;
    private long currentTimeMillis;  //当前时间，毫秒
    private ImageView see1;
    private ImageView see2;
    private ImageView see3;
    private ImageView unsee1;
    private ImageView unsee2;
    private ImageView unsee3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 确保你的布局文件名为activity_login.xml

        MobSDK.submitPolicyGrantResult(true);
        MobSDK.init(this, "39792fe74ca6a", "69edb1dfeda3bed071eac4649b48579c");
        SMSSDK.registerEventHandler(eventHandler);

        userTelEditText = findViewById(R.id.login_userTel_edit_text); // 你的用户名输入框ID
        passwordEditText = findViewById(R.id.login_password_edit_text); // 你的密码输入框ID
        userTelEditText_rgst = findViewById(R.id.register_userTel_edit_text);
        userpasswordEditText_rgst = findViewById(R.id.register_password_edit_text);
        confirm_pswd = findViewById(R.id.register_confirm_password_edit_text);
        loginButton = findViewById(R.id.Login_button); // 你的登录按钮ID
        goto_sign_up = findViewById(R.id.goto_sign_up);
        in1 = findViewById(R.id.view_sign_in1);
        in2 = findViewById(R.id.view_sign_in2);
        up = findViewById(R.id.view_sign_up);
        sign_card = findViewById(R.id.sign_card);
        goto_sign_in = findViewById(R.id.goto_sign_in);
        title = findViewById(R.id.sign_title);
        description = findViewById(R.id.sign_description);
        star = findViewById(R.id.login_in_judge_star);
        resigter_btn = findViewById(R.id.Register_button);
        isJudgePass = false;
        judge_input = findViewById(R.id.judge_input);
        judge_btn = findViewById(R.id.judge_btn);
        warning = findViewById(R.id.warning);
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        currentTimeMillis = System.currentTimeMillis();
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeInMillis(currentTimeMillis);
        see1 = findViewById(R.id.see_1);
        see2 = findViewById(R.id.see_2);
        see3 = findViewById(R.id.see_3);
        unsee1 = findViewById(R.id.unsee_1);
        unsee2 = findViewById(R.id.unsee_2);
        unsee3 = findViewById(R.id.unsee_3);
        see1.setOnClickListener(this);
        see2.setOnClickListener(this);
        see3.setOnClickListener(this);
        unsee1.setOnClickListener(this);
        unsee2.setOnClickListener(this);
        unsee3.setOnClickListener(this);

        //第一次进入app，需要更新物品内容
        SharedPreferences.Editor editor = preferences.edit();

        if(directly_login_in(currentTimeMillis)){  //是否直接登录
            refresh_login_time(currentTimeMillis);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userTel = userTelEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (userTel.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
                    star.setX((float) findViewById(R.id.part_27).getWidth() / 7);
                    isJudgePass = false;
                    star.setImageResource(R.drawable.star_gray);
                    star.setEnabled(true);
                } else if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    star.setX((float) findViewById(R.id.part_27).getWidth() / 7);
                    isJudgePass = false;
                    star.setImageResource(R.drawable.star_gray);
                    star.setEnabled(true);
                } else if (!isJudgePass) {
                    Toast.makeText(LoginActivity.this, "未通过验证！", Toast.LENGTH_SHORT).show();
                } else {
                    login(userTel, password);
                }
            }
        });
        goto_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 1150); // 从(0,0)移动到(100,100)
                animation.setDuration(1000); // 设置动画持续时间为1秒
                animation.setFillAfter(true); // 设置动画结束后保持状态
                sign_card.startAnimation(animation);

                ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(findViewById(R.id.yoyo), "alpha", 1f, 0f);
                fadeOutAnimator.setDuration(500);
                fadeOutAnimator.setInterpolator(new AccelerateInterpolator());

                ObjectAnimator fadeOutAnimator1 = ObjectAnimator.ofFloat(goto_sign_up, "alpha", 1f, 0f);
                fadeOutAnimator1.setDuration(500);
                fadeOutAnimator1.setInterpolator(new AccelerateInterpolator());

                ObjectAnimator fadeOutAnimator2 = ObjectAnimator.ofFloat(in1, "alpha", 1f, 0f);
                fadeOutAnimator2.setDuration(500);
                fadeOutAnimator2.setInterpolator(new AccelerateInterpolator());

                ObjectAnimator fadeOutAnimator3 = ObjectAnimator.ofFloat(in2, "alpha", 1f, 0f);
                fadeOutAnimator3.setDuration(500);
                fadeOutAnimator3.setInterpolator(new AccelerateInterpolator());

                ObjectAnimator fadeOutAnimator4 = ObjectAnimator.ofFloat(goto_sign_in, "alpha", 0f, 1f);
                fadeOutAnimator4.setDuration(1000);
                fadeOutAnimator4.setInterpolator(new AccelerateInterpolator());

                ObjectAnimator fadeOutAnimator5 = ObjectAnimator.ofFloat(up, "alpha", 0f, 1f);
                fadeOutAnimator5.setDuration(1000);
                fadeOutAnimator5.setInterpolator(new AccelerateInterpolator());

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(fadeOutAnimator, fadeOutAnimator1, fadeOutAnimator2, fadeOutAnimator3, fadeOutAnimator4, fadeOutAnimator5);
                animatorSet.start();

                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        goto_sign_up.setVisibility(View.GONE);
                        in1.setVisibility(View.GONE);
                        in2.setVisibility(View.GONE);
                        findViewById(R.id.yoyo).setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {
                    }
                });

                up.setVisibility(View.VISIBLE);
                goto_sign_in.setVisibility(View.VISIBLE);
                title.setText("Welcome");
                description.setText("欢迎注册，一起共建智慧小屋！");
            }
        });
        goto_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warning.setText(" ");
                star.setX((float) findViewById(R.id.part_27).getWidth() / 7);
                isJudgePass = false;
                star.setImageResource(R.drawable.star_gray);
                star.setEnabled(true);

                TranslateAnimation animation = new TranslateAnimation(0, 0, 1150, 0);
                animation.setDuration(1000);
                animation.setFillAfter(true);
                sign_card.startAnimation(animation);

                ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(findViewById(R.id.yoyo), "alpha", 0f, 1f);
                fadeOutAnimator.setDuration(1000);
                fadeOutAnimator.setInterpolator(new AccelerateInterpolator());

                ObjectAnimator fadeOutAnimator1 = ObjectAnimator.ofFloat(goto_sign_up, "alpha", 0f, 1f);
                fadeOutAnimator1.setDuration(1000);
                fadeOutAnimator1.setInterpolator(new AccelerateInterpolator());

                // 创建第二个视图的渐变动画
                ObjectAnimator fadeOutAnimator2 = ObjectAnimator.ofFloat(in1, "alpha", 0f, 1f);
                fadeOutAnimator2.setDuration(1000);
                fadeOutAnimator2.setInterpolator(new AccelerateInterpolator());

                // 创建第三个视图的渐变动画
                ObjectAnimator fadeOutAnimator3 = ObjectAnimator.ofFloat(in2, "alpha", 0f, 1f);
                fadeOutAnimator3.setDuration(1000);
                fadeOutAnimator3.setInterpolator(new AccelerateInterpolator());

                ObjectAnimator fadeOutAnimator4 = ObjectAnimator.ofFloat(goto_sign_in, "alpha", 1f, 0f);
                fadeOutAnimator4.setDuration(500);
                fadeOutAnimator4.setInterpolator(new AccelerateInterpolator());

                ObjectAnimator fadeOutAnimator5 = ObjectAnimator.ofFloat(up, "alpha", 1f, 0f);
                fadeOutAnimator5.setDuration(500);
                fadeOutAnimator5.setInterpolator(new AccelerateInterpolator());

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(fadeOutAnimator, fadeOutAnimator1, fadeOutAnimator2, fadeOutAnimator3, fadeOutAnimator4, fadeOutAnimator5);
                animatorSet.start();
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        goto_sign_in.setVisibility(View.GONE);
                        up.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {
                    }
                });
                in1.setVisibility(View.VISIBLE);
                in2.setVisibility(View.VISIBLE);
                goto_sign_up.setVisibility(View.VISIBLE);
                findViewById(R.id.yoyo).setVisibility(View.VISIBLE);
                title.setText("Hello");
                description.setText("欢迎登录，开启全新一天！");
            }
        });

        SlideVerificationView svv = new SlideVerificationView(this, findViewById(R.id.login_in_judge_star), findViewById(R.id.part_27));
        SliderVerificationCallback callback = new SliderVerificationCallback() {

            @Override
            public void onSliderVerified(boolean isVerified) {
                if (isVerified) {
                    ScaleAnimation scaleAnimation = new ScaleAnimation(
                            1f, 0.9f, // X轴起始和结束缩放比例
                            1f, 0.9f // Y轴起始和结束缩放比例
                    );

                    scaleAnimation.setDuration(300);
                    scaleAnimation.setRepeatCount(1);
                    scaleAnimation.setRepeatMode(Animation.REVERSE);
                    star.startAnimation(scaleAnimation);

                    star.setImageResource(R.drawable.star_green);
                    star.setEnabled(false);
                    isJudgePass = true;
                } else {
                    Toast.makeText(LoginActivity.this, "验证失败！", Toast.LENGTH_SHORT).show();
                    star.setX((float) findViewById(R.id.part_27).getWidth() / 7);
                    isJudgePass = false;
                }
            }
        };
        svv.init(callback);

        judge_btn.setOnClickListener(v -> {
            String phoneNum = userTelEditText_rgst.getText().toString().trim();
            String confime = confirm_pswd.getText().toString().trim();
            String pswd = userpasswordEditText_rgst.getText().toString().trim();
            warning.setTextColor(Color.RED);

            if (TextUtils.isEmpty(phoneNum)) {
                warning.setText("手机号不能为空！");
                return;
            }
            else if (TextUtils.isEmpty(pswd)) {
                warning.setText("请输入密码！");
            }
            else if (TextUtils.isEmpty(confime)) {
                warning.setText("请确认密码！");
            }
            else if(!confime.equals(pswd)){
                warning.setText("两次输入密码不一致！");
            }
            else{
                SMSSDK.getVerificationCode("86", phoneNum);  //发送验证码
                warning.setText(" ");

                judge_btn.setClickable(false);
                new Thread(() -> {

                    for (i = 60; i > 0; i--) {
                        handler.sendEmptyMessage(-1);
                        if (i <= 0) {
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendEmptyMessage(-2);
                }).start();
            }
        });
        resigter_btn.setOnClickListener(v -> {
            String phoneNum = userTelEditText_rgst.getText().toString().trim();
            String confime = confirm_pswd.getText().toString().trim();
            String pswd = userpasswordEditText_rgst.getText().toString().trim();
            String code = judge_input.getText().toString().trim();

            if (TextUtils.isEmpty(phoneNum)) {
                warning.setText("手机号不能为空！");
            }
            else if (TextUtils.isEmpty(pswd)) {
                warning.setText("请输入密码！");
            }
            else if (TextUtils.isEmpty(confime)) {
                warning.setText("请确认密码！");
            }
            else if(!confime.equals(pswd)){
                warning.setText("两次输入密码不一致！");
            }
            else if (TextUtils.isEmpty(code)) {
                warning.setText("请输入验证码！");
            }
            else{
                warning.setText(" ");
                SMSSDK.submitVerificationCode("86", phoneNum, code);
            }
        });

        class MyHandler extends Handler {
            final WeakReference<LoginActivity> loginActivityWeakReference;
            MyHandler(LoginActivity loginActivity) {
                loginActivityWeakReference = new WeakReference<>(loginActivity);
            }
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == -1) {
                    judge_btn.setText(i + "s");
                }

                if (msg.what == -2) {
                    judge_btn.setText("重新发送");
                    judge_btn.setClickable(true);
                    i = 60;
                }

                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if (result == SMSSDK.RESULT_COMPLETE) {
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {  //验证成功
                        String userTel = userTelEditText_rgst.getText().toString();
                        String password = userpasswordEditText_rgst.getText().toString();
                        signup(userTel, password);
                    }
                    else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        warning.setText("验证码已发送");
                        warning.setTextColor(Color.parseColor("#6B6B6B"));
                    }
                    else {
                        ((Throwable) data).printStackTrace();
                    }
                }
                else if (result == SMSSDK.RESULT_ERROR) {
                    try {
                        Throwable throwable = (Throwable) data;
                        throwable.printStackTrace();
                        JSONObject object = new JSONObject(throwable.getMessage());
                        String des = object.optString("detail");//错误描述
                        int status = object.optInt("status");//错误代码
                        if (status > 0 && !TextUtils.isEmpty(des)) {
                            if(des.equals("需要校验的验证码错误"))
                                 Toast.makeText(LoginActivity.this, "验证码错误！", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(LoginActivity.this, des, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e) {
                    }
                }
            }
        }
        handler = new MyHandler(this);
    }

    EventHandler eventHandler = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            handler.sendMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    private void login(String userTel, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("u_tel", userTel);
            json.put("upwd", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url("http://120.26.248.74:8080/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "登录失败: 网络问题", Toast.LENGTH_SHORT).show();
                    star.setX((float) findViewById(R.id.part_27).getWidth() / 7);
                    isJudgePass = false;
                    star.setImageResource(R.drawable.star_gray);
                    star.setEnabled(true);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (!response.isSuccessful()) {
                    LoginActivity.this.runOnUiThread(() -> {
                        if(responseBody.equals("failure")){
                            Toast.makeText(LoginActivity.this, "登录失败: 用户名或密码错误", Toast.LENGTH_SHORT).show();
                            star.setX((float) findViewById(R.id.part_27).getWidth() / 7);
                            isJudgePass = false;
                            star.setImageResource(R.drawable.star_gray);
                            star.setEnabled(true);
                        }
                        else{
                            star.setX((float) findViewById(R.id.part_27).getWidth() / 7);
                            isJudgePass = false;
                            star.setImageResource(R.drawable.star_gray);
                            star.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "用户不存在，请注册新用户", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if (responseBody.equals("success")){
                    runOnUiThread(() -> {
                        Thread t = new Thread(() -> uid = usePhoneGetUid(userTel));
                        t.start();
                        try {
                            t.join();
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("uer_id", uid);
                            editor.putInt("current_layout_id",-1);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            refresh_login_time(currentTimeMillis);  //更新登录时间
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });
    }

    private Integer usePhoneGetUid(String utel) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "");
        String url = "http://120.26.248.74:8080/usePhoneGetUid?u_tel=" + utel;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                return Integer.parseInt(response.body().string());

            } else {
                System.out.println("响应码: " + response.code());
                String responseBody = response.body().string();
                System.out.println("响应体: " + responseBody);
            }
            response.body().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
    private void signup(String userTel, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("u_tel", userTel);
            json.put("upwd", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url("http://120.26.248.74:8080/register")
//                .url("http://10.237.54.151:8080/register")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "注册失败: 网络问题", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String responseBody = response.body().string();
                if (!response.isSuccessful()) {  //响应成功，但返回失败值
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(responseBody.equals("repeat_sign_up")){
                                Toast.makeText(LoginActivity.this, "该手机号已被注册！", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, responseBody, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else if (responseBody.equals("sign_up_success")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功，快去登录吧！", Toast.LENGTH_SHORT).show();
                            userTelEditText_rgst.setText("");
                            userpasswordEditText_rgst.setText("");
                            goto_sign_in.performClick();
                        }
                    });
                }
            }
        });
    }

    //更新最新登录时间
    private void refresh_login_time(long currentTimeMillis){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("last_login_time", currentTimeMillis);
        editor.apply();
    }

    //是否直接登录
    private boolean directly_login_in(long currentTimeMillis){
        long previous_timeMillis = preferences.getLong("last_login_time",0);
        long timeDifference = currentTimeMillis - previous_timeMillis;
        long timeDifferenceDays = timeDifference / (1000 * 60 * 60 * 24);
        return timeDifferenceDays <= 7;
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.unsee_1){
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            unsee1.setVisibility(View.GONE);
            see1.setVisibility(View.VISIBLE);
        }
        else if(v.getId() == R.id.see_1){
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            unsee1.setVisibility(View.VISIBLE);
            see1.setVisibility(View.GONE);
        }
        else if(v.getId() == R.id.unsee_2){
            confirm_pswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            unsee2.setVisibility(View.GONE);
            see2.setVisibility(View.VISIBLE);
        }
        else if(v.getId() == R.id.see_2){
            confirm_pswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            unsee2.setVisibility(View.VISIBLE);
            see2.setVisibility(View.GONE);
        }
        else if(v.getId() == R.id.unsee_3){
            userpasswordEditText_rgst.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            unsee3.setVisibility(View.GONE);
            see3.setVisibility(View.VISIBLE);
        }
        else if(v.getId() == R.id.see_3){
            userpasswordEditText_rgst.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            unsee3.setVisibility(View.VISIBLE);
            see3.setVisibility(View.GONE);
        }
    }
}



