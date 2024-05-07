package com.example.smartstore;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class discription_dialog extends Dialog {
    private String original;
    private PriorityListener listener;
    private Context context;

    public interface PriorityListener {
        public void refreshPriorityUI(String string);
    }

    public discription_dialog(String original, Context context, int theme, PriorityListener listener) {
        super(context);
        this.original = original;
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    public void onCreateDialog() {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.discription_dialog, null);
        final Dialog dialog = new Dialog(context, R.style.style_dialog);
        dialog.setContentView(view);  // 完成对对话框样式的设计

        dialog.show();

        Button cancelButton = view.findViewById(R.id.discription_btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击取消按钮时关闭对话框
                dialog.cancel(); // 使用dialog对象来关闭对话框
            }
        });

        Button confirmButton = view.findViewById(R.id.discription_btn_confirm);
        EditText et = view.findViewById(R.id.discription);
        et.setText(original);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = et.getText().toString();  //必须放在监听事件内部
                s = s.trim();
                dialog.cancel(); // 使用dialog对象来关闭对话框
                listener.refreshPriorityUI(s);
            }
        });

        Window window = dialog.getWindow();
        if (window != null) { // 确保window不为null
            WindowManager.LayoutParams lp = window.getAttributes();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 设置宽度充满屏幕
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setAttributes(lp);
        }

    }
}
