package chattingcircle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.smartstore.R;

import java.util.ArrayList;
import java.util.List;

public class Chattingcircle_recommend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chattingcircle_recommend);
        Button commend = findViewById(R.id.commend);
        Button mine = findViewById(R.id.my_chatting_circle);
        ImageView commend_icon=findViewById(R.id.commend_icon);
        ImageView mine_icon=findViewById(R.id.my_circle_icon);


        commend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置按钮1为选中状态
                commend.setTextColor(Color.parseColor("#204D36"));
                mine.setTextColor(Color.parseColor("#53856C"));
                // 显示1图标
                commend_icon.setVisibility(View.VISIBLE);
                mine_icon.setVisibility(View.INVISIBLE);
            }
        });

        mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commend.setTextColor(Color.parseColor("#53856C"));
                mine.setTextColor(Color.parseColor("#204D36"));
                // 显示1图标
                commend_icon.setVisibility(View.INVISIBLE);
                mine_icon.setVisibility(View.VISIBLE);

                // 启动SecondActivity
                Intent intent = new Intent(Chattingcircle_recommend.this, ChattingCircle.class);
                startActivity(intent);
            }
        });


        // 在Chattingcircle_recommend的onCreate方法中
        RecyclerView recyclerView = findViewById(R.id.list1); // 替换为实际的RecyclerView ID
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)); // 根据需求调整列数

        List<ModuleItem> moduleItems = getModuleItemsData(); // 获取数据列表
        recyclerView.setAdapter(new ModuleAdapter(moduleItems));
    }

        private List<ModuleItem> getModuleItemsData() {
            List<ModuleItem> moduleItems = new ArrayList<>();

            // 创建固定模块（发布 消息 关注模块）
            ModuleItem fixedModule = new ModuleItem(ModuleItem.Type.FIXED, " ", "");
            moduleItems.add(fixedModule);

            // 示例.可以根据实际需求生成或获取自定义模块数据
            for (int i = 0; i < 10; i++) {
                ModuleItem customModule = new ModuleItem(
                        ModuleItem.Type.CUSTOM,
                        "https://example.com/custom_image_" + (i + 1) +".png",
                        "自定义文本123123123 " + (i + 1)
                );
                moduleItems.add(customModule);
            }

            return moduleItems;
        }
}