package audio;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.example.smartstore.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import Baidu.u1;
import image_submit.attention_dialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class openAudioDialog {
    private openAudioDialog.PriorityListener listener;
    private String token;
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private AudioRecord audioRecord;
    private String outputFile; // 录音文件的路径
    private boolean isRecording = false;
    private Context context;
    private myDictionary dic;
    private TextView audiotxt;
    private ImageView ing_btn;
    private ImageView stop_btn;
    private TextView return_btn;

    public interface PriorityListener {
        public void audioResult(Integer op, String searchTarget);
    }

    public openAudioDialog(Context context, PriorityListener priorityListener){
        this.context = context;
        this.listener = priorityListener;
        String SECRET_KEY = "HTfMD6RYSZtHVeS14JVgB8eP6hmc5JBo";
        String API_KEY = "byULjcF0TE5Z8UkCqbRdLXSh";
        AssetManager assetManager = context.getAssets();
        dic = new myDictionary(assetManager, context);
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioRecordings");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        outputFile = directory.getAbsolutePath() + "/audio_record.pcm";
        u1.getAccessToken(API_KEY, SECRET_KEY, new u1.OnAccessTokenReceivedListener() {
            @Override
            public void onSuccess(String accessToken) {
                token = accessToken;
            }
            public void onFailure(Exception e) {
            }
        });
    }

    public void onCreateDialog() {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.audiodialog, null);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(view);

        dialog.show();

        audiotxt = view.findViewById(R.id.audio_txt);
        ing_btn = view.findViewById(R.id.ing_audio_btn);
        stop_btn = view.findViewById(R.id.stop_audio_btn);
        return_btn = view.findViewById(R.id.audio_return_btn);

        return_btn.setOnClickListener(v -> {
            listener.audioResult(-1,"");  //表示取消操作
            dialog.cancel();
        });

        stop_btn.setOnClickListener(v -> {
            audiotxt.setText("聆听中");
            stop_btn.setVisibility(View.GONE);
            ing_btn.setVisibility(View.VISIBLE);

            startRecording();  //录音开始

        });

        ing_btn.setOnClickListener(v -> {
            stop_btn.setVisibility(View.VISIBLE);
            ing_btn.setVisibility(View.GONE);
            System.out.println("Begin:" + System.currentTimeMillis());
            stopRecording();  //录音结束
            check(dialog);
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

    private void startRecording() {
        int sampleRateInHz = 16000;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            attention_dialog dd = new attention_dialog("麦克风和读取存储权限，\n才能实现语音导航哦~", "权限开启提醒！", "去开启", "下次再来", context, isAccept -> {
                if (isAccept) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);  //打开设置
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            });
            dd.onCreate_Attention_Dialog();
            return;
        }

        new Thread(() -> {
            // 开始录音
            isRecording = true;
            byte[] audioBuffer = new byte[bufferSizeInBytes];
            audioRecord.startRecording();
            try {
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                while (isRecording) {
                    int readSize = audioRecord.read(audioBuffer, 0, bufferSizeInBytes);
                    if (readSize > 0) {
                        outputStream.write(audioBuffer, 0, readSize);
                    }
                }
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void stopRecording() {
        isRecording = false; // 停止录音
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }
    private void check(Dialog dialog){
        String base64Audio = encodeFileToBase64(outputFile);

        byte[] decodedBytes = Base64.getDecoder().decode(base64Audio);

        // 获取解码后字节数组的长度
        int lengthInBytes = decodedBytes.length;
        String json = "{" +
                "\"format\":\"pcm\"," +
                "\"rate\":16000," +
                "\"dev_pid\":1537," +
                "\"channel\":1," +
                "\"token\":\"" + token + "\"," +
                "\"cuid\":\"baidu_workshop\"," +
                "\"len\":" + lengthInBytes + "," +
                "\"speech\":\"" + base64Audio + "\"" +
                "}";


        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url("https://vop.baidu.com/server_api")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray resultArray = jsonObject.getJSONArray("result");
                        String resultString = resultArray.getString(0);
                        audiotxt.setText(resultString);

                        resultString = resultString.replace("。", "");
                        resultString = resultString.replace("？", "");
                        resultString = resultString.replace("！", "");
                        resultString = resultString.replace("，", "");

                        float[] inputData =  dic.getValidInput(resultString);
                        Integer op =  getUserIntent(inputData);
                        if(op != 3){
                            listener.audioResult(op,"");
                        }
                        else{
                            listener.audioResult(op,dic.search_target.get(0));
                        }

                        if(op != -2)
                            dialog.cancel();
                        else{
                            audiotxt.setText("请说出正确的语音指令哦~");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private Integer getUserIntent(float[] inputData){
        Interpreter tflite = audioTensorFlowLoader.newInstance(context).get();
        float[][] outputData = new float[1][12]; // 确保这个尺寸与您的模型输出尺寸匹配
        tflite.run(inputData, outputData);
        if(outputData[0][findMaxValueAndIndex(outputData[0])] < 0.85){
            return -2;  //无效语音
        }
        return findMaxValueAndIndex(outputData[0]);
//      {"切换场景","定制空间","拍照入库","物品查询","记录操作","出库操作","打开生态圈","查看某天任务","新建任务","编辑任务","邀请家人","加入邀请"};
    }

    public int findMaxValueAndIndex(float[] array) {
        float maxVal = array[0];
        int maxIndex = 0;

        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxVal) {
                maxVal = array[i];
                maxIndex = i;
            }
        }

        return maxIndex;
    }
    private String encodeFileToBase64(String filePath) {
        ByteArrayOutputStream bos = null;
        byte[] b = new byte[1024];
        int bytesRead;

        try {
            FileInputStream fis = new FileInputStream(filePath);
            bos = new ByteArrayOutputStream();
            while ((bytesRead = fis.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            byte[] bytes = bos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
