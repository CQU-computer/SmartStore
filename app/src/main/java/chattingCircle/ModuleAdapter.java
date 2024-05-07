package chattingCircle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    public Context context;

    private List<ModuleItem> moduleItems;

    public ModuleAdapter(List<ModuleItem> moduleItems, Context context) {
        this.moduleItems = moduleItems;
        this.context = context;
    }

    public void updateData(List<ModuleItem> newData) {
        moduleItems.clear();
        moduleItems.addAll(newData);
        notifyDataSetChanged();
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
            case VIEW_TYPE_FIXED:{
                FixedViewHolder fx = new FixedViewHolder(inflater.inflate(R.layout.waterfall_fixed_item1, parent, false));
                return fx;
            }
            case VIEW_TYPE_CUSTOM:
                return new CustomViewHolder(inflater.inflate(R.layout.waterfall_list_item, parent, false));
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(moduleItems.size() - 1 - position == 0)
            return;
        ModuleItem moduleItem = moduleItems.get(moduleItems.size() - 1 - position);
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

    public interface ItemClickListener {
        void onFixedImageButtonClick(int position);
    }

    private static ItemClickListener mItemClickListener;

    public void setItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    private static class FixedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView fixedImage;
        private final Button buttonPublish;
        private final Button buttonMessage;
        private final Button buttonFollow;

        FixedViewHolder(View itemView) {
            super(itemView);
            fixedImage = itemView.findViewById(R.id.fixed_image);//!!!!
            buttonPublish = itemView.findViewById(R.id.button_publish);
            buttonMessage = itemView.findViewById(R.id.button_message);
            buttonFollow = itemView.findViewById(R.id.button_follow);
            buttonFollow.setOnClickListener(this);
        }
        public void onClick(View v) {
            if (v.getId() == R.id.button_follow) {
                Context context = v.getContext();

                if (mItemClickListener != null) {
                    mItemClickListener.onFixedImageButtonClick(getAdapterPosition());
                }
            }
        }
        void bind(ModuleItem item) {
        }
    }


    private class CustomViewHolder extends RecyclerView.ViewHolder {
        private final TextView customText;
        private final ImageView customImage;
        private final TextView userid;
        private final TextView likes;

        CustomViewHolder(View itemView) {
            super(itemView);
            customText = itemView.findViewById(R.id.tx);
            customImage = itemView.findViewById(R.id.custom_image);
            customText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            userid=itemView.findViewById(R.id.userID);
            likes=itemView.findViewById(R.id.postLike);
        }
        void bind(ModuleItem item) {

            customText.setText(item.Title);
            customImage.setImageResource(R.drawable.nullp);
            userid.setText(item.uname);
            likes.setText(item.like);

            userid.setOnClickListener(v->{
                Intent intent = new Intent(context, OtherZhuye.class);
                intent.putExtra("poster_id", item.userID);
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, Postings.class);
                intent.putExtra("trigger_single_items_click", false);
                intent.putExtra("post_Id", item.postid);
                intent.putExtra("post_name",item.Title);
                intent.putExtra("post_content",item.customText);
                intent.putExtra("post_img",item.imageUrl);
                intent.putExtra("post_time",item.time);
                intent.putExtra("user_ID",item.userID);
                intent.putExtra("post_like",item.like);
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }
    }


}