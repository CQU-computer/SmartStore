package Family;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartstore.R;

import java.util.List;
import java.util.Objects;

public class add_time1 extends LinearLayout {
    private Context context;
    private LinearLayout dateTimeBar;
    private TextView test_info;
    public add_time1(Context context, LinearLayout dateTimeBar, TextView test_info){
        super(context);
        this.context=context;
        this.dateTimeBar=dateTimeBar;
        this.test_info=test_info;
    }

    public  void add_date_item(Integer start_idx, List<String> day, List<String> week,List<String> month,List<Task> tasks){

        for(int i=start_idx;i<day.size();i=i+1){
            View date_item= LayoutInflater.from(context)
                    .inflate(R.layout.data_item1,dateTimeBar,false);

            TextView rcd_day=date_item.findViewById(R.id.rcd_day);
            TextView rcd_wee=date_item.findViewById(R.id.rcd_week);
            View date_index=date_item.findViewById(R.id.date_index);
            ImageView rest=date_item.findViewById(R.id.rest);
            ImageView bell=date_item.findViewById(R.id.bell);
            if(i==start_idx){rcd_day.setText("Now");
            }
            else{ rcd_day.setText(day.get(i));}
            rcd_wee.setText(week.get(i));
            //将日期的格式调整成和任务的日期一致，x月xx日
            String month_ =month.get(i);
            int intday=Integer.parseInt(day.get(i));
            String day_ = String.format("%d", intday);
            String format_date = month_ + "-" + day_+"-";

            boolean hasTask = false;
            for (Task task : tasks) {
                String[] parts = task.getDate().split("-");
                if (Objects.equals(parts[1], month_) && Objects.equals(parts[2], day_)) {
                    hasTask = true;
                    break;
                }
            }
            if (hasTask) {
                bell.setVisibility(VISIBLE); // 如果有任务，显示bell图标
                rest.setVisibility(INVISIBLE); // 隐藏rest图标
            } else {
                bell.setVisibility(INVISIBLE); // 如果没有任务，隐藏bell图标
                rest.setVisibility(VISIBLE); // 显示rest图标
            }
            date_item.setTag(i);  //循环地设置每一天的数据，并为每一天设下一个tag，方便监听点击事件时知道是哪一天被点击
            dateTimeBar.addView(date_item);//把date_item放入日历中
        }
    };}