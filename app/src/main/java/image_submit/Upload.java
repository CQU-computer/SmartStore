package image_submit;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartstore.MainActivity;
import com.example.smartstore.R;

import java.io.File;

import Baidu.Baidu;


public class Upload extends AppCompatActivity{
    public static final int STORAGE_PERMISSION = 1;
    private File file = null;
    private final Context context= this;
    int REQUEST_CODE_CAPTURE_IMAGE = 100;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_img);

        SharedPreferences preference_name = getSharedPreferences("config", Context.MODE_PRIVATE);
        String Current_layout =  preference_name.getString("current_layout_name","");

        TextView cur_l = findViewById(R.id.cur_l);
        cur_l.setText(Current_layout);

        Button scan_return = findViewById(R.id.scan_return);
        Button getImage = findViewById(R.id.load_by_photo);    //通过相册获取图片
        Button load_by_camera = findViewById(R.id.load_by_camera);    //通过拍照获取图片
        ImageView load_by_camera_2 = findViewById(R.id.load_by_camera_2);  //通过拍照获取图片

        load_by_camera_2.setOnClickListener(v -> {
            ImageSubmitUtil isu = new ImageSubmitUtil(context);
            uri = isu.getPic();
        });

        load_by_camera.setOnClickListener(v -> {
            ImageSubmitUtil isu = new ImageSubmitUtil(context);
            uri = isu.getPic();
        });

        getImage.setOnClickListener(v -> xzImage());
        scan_return.setOnClickListener(v -> rtn());
    }

    private void rtn() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }

    //处理活动结果
    private  ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String realPath = Utils.getRealPath(this, data);
                    file = new File(realPath);

                    //选取成功跳转
                    Intent intent2 = new Intent(context, Baidu.class);
                    intent2.putExtra("image",data.getData());
                    startActivity(intent2);
                }
            });

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }

    //获取图片
    private void xzImage() {
        if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    //监测权限是否开启
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已经被授予，不需要执行打开设置的操作
            }
            else {
                attention_dialog dd = new attention_dialog("开启读取照片权限，\n就可以上传自己宝贝的图片啦!" ,"获取照片权限未开启哦！","去开启", "下次再来",this, isAccept -> {
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
        if(requestCode == REQUEST_CODE_CAPTURE_IMAGE){
            attention_dialog dd = new attention_dialog("开启相机和照片权限，\n才能拍照识别哦~" ,"获取相机权限开启！","去开启", "下次再来",context, isAccept -> {
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE_CAPTURE_IMAGE && resultCode == RESULT_OK) {
                    Intent intent2 = new Intent(context,Baidu.class);
                    intent2.putExtra("image",uri);
                    startActivity(intent2);
            }
    }
}



