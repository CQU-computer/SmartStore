package com.example.smartstore;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class help_activity extends AppCompatActivity implements View.OnClickListener {

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private ImageView img2;
    private ImageView img3;
    private ImageView img4;
    private Button btn;
    private ImageView img1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help2);

        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        btn4 = findViewById(R.id.btn_4);
        btn = findViewById(R.id.help_return);

        img1 = findViewById(R.id.help_ai);
        img2 = findViewById(R.id.help_rcd);
        img3 = findViewById(R.id.help_addlay);
        img4 = findViewById(R.id.help_search);




        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String source = extras.getString("where");
            if (source != null && source.equals("search")) {
                btn4.performClick();
            }
            else if(source != null && source.equals("scan")) {
                btn1.performClick();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_1){
            img1.setVisibility(View.VISIBLE);
            img2.setVisibility(View.GONE);
            img3.setVisibility(View.GONE);
            img4.setVisibility(View.GONE);
        }

        else if(v.getId() == R.id.btn_2){
            img1.setVisibility(View.GONE);
            img2.setVisibility(View.VISIBLE);
            img3.setVisibility(View.GONE);
            img4.setVisibility(View.GONE);
        }

        else if(v.getId() == R.id.btn_3){
            img1.setVisibility(View.GONE);
            img2.setVisibility(View.GONE);
            img3.setVisibility(View.VISIBLE);
            img4.setVisibility(View.GONE);
        }

        else if(v.getId() == R.id.btn_4){
            img1.setVisibility(View.GONE);
            img2.setVisibility(View.GONE);
            img3.setVisibility(View.GONE);
            img4.setVisibility(View.VISIBLE);
        }

        else{
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }


    }
}