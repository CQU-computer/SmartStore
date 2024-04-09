package chattingcircle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.smartstore.R;

public class ChattingCircle extends AppCompatActivity {

    private Button button1;
    private Button button2;

    private FrameLayout subInterfaceContainer;


    ImageView icon1;
    ImageView icon2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_circle);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        icon2 = findViewById(R.id.icon2);
        icon1 = findViewById(R.id.icon1);
        Button commend = findViewById(R.id.commend);
        Button mine = findViewById(R.id.my_chatting_circle);
        ImageView commend_icon=findViewById(R.id.commend_icon);
        ImageView mine_icon=findViewById(R.id.my_circle_icon);

        EditText user_sg = findViewById(R.id.user_signature);
        EditText user_name = findViewById(R.id.user_name);

// 设置焦点改变监听，当用户点击EditText时弹出键盘
        user_sg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 弹出软键盘，通常在 AppCompatActivity 或 Activity 中使用
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

// 设置文本变化监听，实时获取用户输入的内容
        user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @SuppressLint("ResourceType")
            @Override
            public void afterTextChanged(Editable s) {
                String userInput = s.toString();
                // 对用户输入的文本进行处理或保存...
            }
        });
        user_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @SuppressLint("ResourceType")
            @Override
            public void afterTextChanged(Editable s) {
                String userInput = s.toString();
            }
        });


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置按钮1为选中状态
                button1.setTextColor(Color.BLACK);
                button2.setTextColor(Color.parseColor("#cccccc"));
                // 显示1图标
                icon1.setVisibility(View.VISIBLE);
                icon2.setVisibility(View.GONE);

                replaceSubInterface(new SubInterfaceAFragment());
            }

        });

        // 设置按钮2的点击事件
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置按钮2为选中状态
                button1.setTextColor(Color.parseColor("#cccccc"));
                button2.setTextColor(Color.BLACK);
                // 隐藏1图标
                icon1.setVisibility(View.GONE);
                icon2.setVisibility(View.VISIBLE);

                replaceSubInterface(new SubInterfaceBFragment());
            }
        });
        replaceSubInterface(new SubInterfaceAFragment());//初始状态显示我的分享界面


        mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mine.setTextColor(Color.parseColor("#204D36"));
                commend.setTextColor(Color.parseColor("#53856C"));
                mine_icon.setVisibility(View.VISIBLE);
                commend_icon.setVisibility(View.INVISIBLE);
            }
        });

        commend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mine.setTextColor(Color.parseColor("#53856C"));
                commend.setTextColor(Color.parseColor("#204D36"));
                mine_icon.setVisibility(View.INVISIBLE);
                commend_icon.setVisibility(View.VISIBLE);
                // 启动SecondActivity
                Intent intent = new Intent(ChattingCircle.this, Chattingcircle_recommend.class);
                startActivity(intent);
            }
        });
    }
    private void replaceSubInterface(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.sub_interface_container, fragment);
        transaction.commit();
    }
}