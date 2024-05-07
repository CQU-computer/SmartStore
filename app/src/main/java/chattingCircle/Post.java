package chattingCircle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartstore.R;

import java.io.File;
import java.io.IOException;

import image_submit.Utils;
import image_submit.attention_dialog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Post extends AppCompatActivity {
    ScrollView postdetail;
    ImageView media;
    TextView vtext;

    EditText postName;
    EditText postText;

    Button save;
    Button cancel;

    File file=null;

    private static final int STORAGE_PERMISSION = 1;

    int REQUEST_CODE_CAPTURE_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postdetail=findViewById(R.id.postDetail);
        media=findViewById(R.id.media);
        vtext=findViewById(R.id.post_text_view_on_image);
        postText=findViewById(R.id.postText);
        postName=findViewById(R.id.postname);

        save=findViewById(R.id.save);
        cancel=findViewById(R.id.cancel);

        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        int serverId = preferences.getInt("uer_id", -1);
        final String[] servername = new String[1];
        final String[] servertext = new String[1];

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Post.this, Chattingcircle_recommend.class);
                intent.putExtra("trigger_cancel_click", true);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        postName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (!hasFocus) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    postName.setCursorVisible(false);
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    postName.requestFocus();
                    postName.setCursorVisible(true);
                }
            }
        });
        postName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @SuppressLint("ResourceType")
            @Override
            public void afterTextChanged(Editable s) {
                servername[0] = s.toString();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Post.this, Chattingcircle_recommend.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
        });

        postText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (!hasFocus) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    postText.setCursorVisible(false);
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    postText.requestFocus();
                    postText.setCursorVisible(true);
                }
            }
        });
        postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @SuppressLint("ResourceType")
            @Override
            public void afterTextChanged(Editable s) {
                servertext[0] = s.toString();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Post.this, Chattingcircle_recommend.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
        });
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                StringBuilder filtered = new StringBuilder();
                for (int i = start; i < end; i++) {
                    char character = source.charAt(i);
                    if (Character.isLetterOrDigit(character) || Character.isSpaceChar(character) ||
                            ".,?!@\"':;…+-*/=~()，。？！、～…：；.@（）“”'".indexOf(character) != -1 ||
                            Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
                            Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
                            Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
                            Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) {
                        filtered.append(character);
                    }
                    else {
                        continue;
                    }
                }

                return filtered.toString();
            }
        };
        postName.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(30)});
        postText.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(10000)});
        media.setOnClickListener(v -> xzImage());
        save.setOnClickListener(v -> {
            if(servertext[0]!=null&servername[0]!=null){
                File it_img = file;
                String filePath = "";
                if(file != null)
                    filePath = it_img.getPath();
                updateInfo(serverId,servername[0],servertext[0],filePath);
                Intent intent = new Intent(Post.this, Chattingcircle_recommend.class);
                intent.putExtra("trigger_save_click", true);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            else{
                Toast.makeText(this, "请输入标题和内容！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String realPath = Utils.getRealPath(this, data);
                    file = new File(realPath);
                    System.out.println("成功"+file );
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    media.setImageBitmap(bitmap);
                    if(file!=null){
                        vtext.setText("");
                    }
                }
            });

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
                attention_dialog dd = new attention_dialog("开启读取照片权限，\n就可以上传啦!" ,"获取照片权限未开启哦！","去开启", "下次再来",this, isAccept -> {
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
            attention_dialog dd = new attention_dialog("开启相机和照片权限，\n才能上传哦~" ,"获取相机权限开启！","去开启", "下次再来",this, isAccept -> {
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


    public void updateInfo(Integer uid, String post_name,String post_detail,String path) {
        OkHttpClient client1 = new OkHttpClient().newBuilder().build();
        MediaType JSON1 = MediaType.parse("application/json; charset=utf-8");

        StringBuilder queryParams1 = new StringBuilder();

        queryParams1.append("uid=").append(uid).append("&");
        queryParams1.append("post_detail=").append(post_detail).append("&");
        queryParams1.append("post_name=").append(post_name).append("&");
        queryParams1.append("post_media=").append(path);

        RequestBody body1 = RequestBody.create(JSON1, "");
        String url1 = "http://120.26.248.74:8080/addNewPost?" + queryParams1;
        try {
            new Thread(() -> {
                try {
                    Request request = new Request.Builder()
                            .url(url1)
                            .post(body1)
                            .build();
                    Response response = client1.newCall(request).execute();
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "发布成功", Toast.LENGTH_SHORT).show());
                    } else {
                        System.out.println("响应码: " + response.code());
                        String responseBody = response.body().string();
                        System.out.println("响应体: " + responseBody);
                    }
                    response.body().close();
                } catch (IOException e) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_SHORT).show());
                }
            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}