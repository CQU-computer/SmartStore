package audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;


public class audioTensorFlowLoader {
    private static Context mContext;
    Interpreter mInterpreter;
    private static audioTensorFlowLoader instance;

    public static audioTensorFlowLoader newInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new audioTensorFlowLoader();
        }
        return instance;
    }


    Interpreter get() {
        try {
            if (Objects.isNull(mInterpreter))
                mInterpreter = new Interpreter(loadModelFile(mContext));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mInterpreter;
    }


    // 获取文件
    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("audio_network.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

}
