package audio;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import jackmego.com.jieba_android.JiebaSegmenter;

public class myDictionary {

    private JSONObject jsonObject = null;
    private int maxLength = 15;
    private Context context;
    public ArrayList<String> search_target = new ArrayList<>();
    public myDictionary(AssetManager assetManager, Context context) {
        this.context = context;
        // 异步初始化
        JiebaSegmenter.init(context);

        try {
            InputStream inputStream = assetManager.open("word_index.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            inputStream.close();
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (IOException | JSONException e) {
            System.out.println("Error");
        }
    }

    public float getValueByKey(String key) {
        float value = 0;
        if (jsonObject != null && jsonObject.has(key)) {
            try {
                value = (float)jsonObject.getInt(key);
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("Error");
            }
        }
        return value;
    }

    //输入一串文本 -> 返回15大小的词向量
    float[] getValidInput(String resultString) {
        ArrayList<String> terms = JiebaSegmenter.getJiebaSegmenterSingleton().getDividedString(resultString);
        ArrayList<Float> tmp = new ArrayList<>();

        for (int i = 0; i < Math.min(maxLength, terms.size()); i++) {
            String myWord = terms.get(i);
            float index = getValueByKey(myWord);
            if (index == 0) {
                search_target.add(myWord);
                continue;
            };
            tmp.add(index);
        }

        while (tmp.size() < maxLength) {
            tmp.add(0, 0f);
        }

        float[] result = new float[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            result[i] = tmp.get(i);
        }
        return result;
    }
}

