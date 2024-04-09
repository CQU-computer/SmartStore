package chattingcircle;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstore.R;

import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_FIXED = 0;
    private static final int VIEW_TYPE_CUSTOM = 1;

    private List<ModuleItem> moduleItems;

    public ModuleAdapter(List<ModuleItem> moduleItems) {
        this.moduleItems = moduleItems;
    }

    @Override
    public int getItemViewType(int position) {
        return moduleItems.get(position).type == ModuleItem.Type.FIXED ? VIEW_TYPE_FIXED : VIEW_TYPE_CUSTOM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_FIXED:
                return new FixedViewHolder(inflater.inflate(R.layout.waterfall_fixed_item1, parent, false));
            case VIEW_TYPE_CUSTOM:
                return new CustomViewHolder(inflater.inflate(R.layout.waterfall_list_item, parent, false));
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModuleItem moduleItem = moduleItems.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_FIXED:
                ((FixedViewHolder) holder).bind(moduleItem);
                break;
            case VIEW_TYPE_CUSTOM:
                ((CustomViewHolder) holder).bind(moduleItem);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return moduleItems.size();
    }

    private static class FixedViewHolder extends RecyclerView.ViewHolder {
        private final ImageView fixedImage;
        private final Button buttonPublish;
        private final Button buttonMessage;
        private final Button buttonFollow;

        FixedViewHolder(View itemView) {
            super(itemView);
            fixedImage = itemView.findViewById(R.id.fixed_image);
            buttonPublish = itemView.findViewById(R.id.button_publish);
            buttonMessage = itemView.findViewById(R.id.button_message);
            buttonFollow = itemView.findViewById(R.id.button_follow);
        }

        void bind(ModuleItem item) {
            // 设置固定模块的图片和其他属性
            //Glide.with(itemView.getContext()).load(item.imageUrl).into(fixedImage);
            // 添加按钮点击事件等
        }
    }

    private static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final TextView customText;
        private final ImageView customImage;

        CustomViewHolder(View itemView) {
            super(itemView);
            customText = itemView.findViewById(R.id.tx);
            customImage = itemView.findViewById(R.id.custom_image);
            customText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        }

        void bind(ModuleItem item) {
            // 设置自定义模块的内容
            customText.setText(item.customText);
            //Glide.with(itemView.getContext()).load(item.imageUrl).into(customImage);
        }
    }
}
