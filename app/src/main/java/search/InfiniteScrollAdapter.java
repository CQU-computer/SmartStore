package search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartstore.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import image_submit.attention_dialog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfiniteScrollAdapter extends RecyclerView.Adapter<InfiniteScrollAdapter.ViewHolder> {
//    public List<info> dataList;
    public  Map<Integer,String> user_room = new HashMap<>();  //空间及对应id
    public  Map<Integer,String> user_stgs = new HashMap<>();  //储藏点及对应id
    public  Map< Integer, Map<Integer, ArrayList<Integer> > > room_stg_item = new HashMap<>();   //当前所在场景内的 所有空间 及其 对应收纳点 及其对应物品类别
    public  Map<Integer, itemClass> item_list = new HashMap<>();  //存储物品信息
    private ArrayList<Integer> room_id = new ArrayList<>();  //存储所有房间的id，顺序表，用于遍历
    private String Current_layout;
    private Context context;
    public InfiniteScrollAdapter(Context context) {
        this.user_room = search.user_room;
        this.user_stgs = search.user_stgs;
        this.room_stg_item = search.room_stg_item;
        this.item_list = search.item_list;
        this.context = context;
        room_id.addAll(user_room.keySet());
        SharedPreferences preference_name = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        Current_layout =  preference_name.getString("current_layout_name","");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cardLayout.removeAllViews();
        Integer r_id = room_id.get(position);

        holder.room_title.setText(user_room.get(r_id));
        holder.stgn.setText(String.valueOf(room_stg_item.get(r_id).size()));

        int cnt = 0;
        int cntstar = 0;
        for(Integer k1 : room_stg_item.get(r_id).keySet()){
            for(Integer k2: Objects.requireNonNull(room_stg_item.get(r_id).get(k1))){
                cnt++;
                if(Objects.requireNonNull(item_list.get(k2)).it_fav)
                    cntstar++;
            }
        }

        holder.itn.setText(String.valueOf(cnt));
        holder.strn.setText(String.valueOf(cntstar));

        if(position == room_id.size() - 1){
             holder.next_room_title.setText("暂时没有了");
             holder.search_arrow_blue.setVisibility(View.GONE);
             holder.search_arrow_green.setVisibility(View.GONE);
        }
        else{
            holder.next_room_title.setText(user_room.get(room_id.get(position+1)));
            if(position % 2 != 0){
                holder.search_space_blue.setVisibility(View.VISIBLE);
                holder.search_space_green.setVisibility(View.GONE);
                holder.search_arrow_blue.setVisibility(View.VISIBLE);
                holder.search_arrow_green.setVisibility(View.GONE);
            }
            else{
                holder.search_space_green.setVisibility(View.VISIBLE);
                holder.search_space_blue.setVisibility(View.GONE);
                holder.search_arrow_green.setVisibility(View.VISIBLE);
                holder.search_arrow_blue.setVisibility(View.GONE);
            }
        }

        int i = -1;
        for (Integer key_stg : room_stg_item.get(r_id).keySet()) {  //遍历当前房间下每一个储藏点id
            i ++;
            View itemCard = LayoutInflater.from(holder.cardLayout.getContext())
                    .inflate(R.layout.item_card_item, holder.cardLayout, false);

            if(position % 2 == 0) {
                (itemCard.findViewById(R.id.search_card_green)).setVisibility(View.VISIBLE);
                (itemCard.findViewById(R.id.search_card_blue)).setVisibility(View.GONE);
            }
            else{
                (itemCard.findViewById(R.id.search_card_green)).setVisibility(View.GONE);
                ( itemCard.findViewById(R.id.search_card_blue)).setVisibility(View.VISIBLE);
            }

            ((TextView) itemCard.findViewById(R.id.textViewCardName)).setText(user_stgs.get(key_stg));
            itemCard.setTag(String.valueOf(i));
            itemCard.setOnClickListener(v -> {
                holder.cardLayout.expandItem(Integer.parseInt((String) v.getTag()));
            });
            LinearLayout linearLayout = itemCard.findViewById(R.id.linear_layout);
            for (Integer key_it : Objects.requireNonNull(room_stg_item.get(r_id).get(key_stg))) {

                View single_items = LayoutInflater.from(linearLayout.getContext())
                        .inflate(R.layout.single_item, linearLayout, false);
                if (Objects.requireNonNull(item_list.get(key_it)).it_img != null) {
                    ((ImageView) single_items.findViewById(R.id.star_object_1)).setImageBitmap(Objects.requireNonNull(item_list.get(key_it)).it_img);
                }
                ((TextView) single_items.findViewById(R.id.itemtitle)).setText(Objects.requireNonNull(item_list.get(key_it)).it_name);
                ((TextView) single_items.findViewById(R.id.item_disciption)).setText(Current_layout + " -> " + user_room.get(room_id.get(position)) + " -> " + user_stgs.get(key_stg));
                ((TextView) single_items.findViewById(R.id.item_deadline)).setText(Objects.requireNonNull(item_list.get(key_it)).best_before);

                if (Objects.requireNonNull(item_list.get(key_it)).it_fav)
                    single_items.findViewById(R.id.it_fav).setVisibility(View.VISIBLE);
                else
                    single_items.findViewById(R.id.it_fav).setVisibility(View.GONE);

                linearLayout.addView(single_items);

                single_items.setOnLongClickListener(v -> {
                    attention_dialog dd = new attention_dialog("你确定要将"+item_list.get(key_it).it_name+"出库吗?","物品出库" ,"确认出库", "我点错了",context, isAccept -> {
                        if(isAccept){
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            single_items.getParent().requestDisallowInterceptTouchEvent(false);
                            Thread t1 = new Thread(() -> {
                                OkHttpClient client = new OkHttpClient().newBuilder().build();
                                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                                RequestBody body = RequestBody.create(JSON, "");
                                String url = "http://120.26.248.74:8080/ChuItem?uid=" + search.uid.toString() + "&it_id=" + key_it;
                                System.out.println(key_it);
                                try {
                                    Request request = new Request.Builder()
                                            .url(url)
                                            .post(body)
                                            .build();
                                    System.out.println();
                                    Response response = client.newCall(request).execute();
                                    if (!response.isSuccessful())
                                        System.out.println("响应码: " + response.code());
                                    String responseBody = response.body().string();
                                    System.out.println("响应体: " + responseBody);
                                    response.body().close();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            t1.start();
                            try {
                                t1.join();
                                Toast.makeText(context, "出库成功", Toast.LENGTH_SHORT).show();
                                single_items.setVisibility(View.GONE);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    dd.onCreate_Attention_Dialog();
                    return false;
                });
            }
            HorizontalScrollView sc = itemCard.findViewById(R.id.display);
            sc.setOnTouchListener((v, event) -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            });
            holder.cardLayout.addView(itemCard);

        }
        CardLinearLayout.mesured = false;
        System.out.println(holder.cardLayout.getChildCount());
    }

    @Override
    public int getItemCount() {
        return room_id.size();
    }
    static public class ViewHolder extends RecyclerView.ViewHolder {
        TextView room_title;
        ImageView search_space_green;
        ImageView search_space_blue;
        ImageView search_arrow_green;
        ImageView search_arrow_blue;
        TextView next_room_title;
        CardLinearLayout cardLayout;
        TextView stgn;
        TextView itn;
        TextView strn;
        View v;
        public ViewHolder(View itemView) {
                super(itemView);
                search_space_green  = itemView.findViewById(R.id.search_space_green);
                search_space_blue  = itemView.findViewById(R.id.search_space_blue);
                room_title = itemView.findViewById(R.id.room_title);
                search_arrow_blue = itemView.findViewById(R.id.search_arrow_blue);
                search_arrow_green = itemView.findViewById(R.id.search_arrow_green);
                next_room_title = itemView.findViewById(R.id.next_room_title);
                cardLayout = itemView.findViewById(R.id.cardLayout);
                v = itemView.findViewById(R.id.textView);
                stgn = itemView.findViewById(R.id.stg_n);
                itn = itemView.findViewById(R.id.it_n);
                strn = itemView.findViewById(R.id.str_n);
        }
    }
}