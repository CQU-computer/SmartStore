package audio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.smartstore.mine_page;

import Family.family;
import chattingCircle.Chattingcircle_recommend;
import image_submit.Upload;
import in_out_rcd.in_out_rcd;
import search.search;
import self_edit_layout.self_edit_layout;

public class audioTransform {
    private Integer op;
    private Context context;
    private Activity activity;
    private String target;

    public audioTransform(Integer op, Context context, String t){
        this.op = op;
        this.context = context;
        activity = (Activity) context;
        this.target = t;
    }

    public void audioStart(){
        System.out.println("End:" + System.currentTimeMillis());
        switch (op){
            case 0:{  //切换场景
                Intent intent = new Intent(context, self_edit_layout.class);
                intent.putExtra("source", "search");
                context.startActivity(intent);
                break;
            }
            case 1:{  //定制空间
                context.startActivity(new Intent(context, self_edit_layout.class));
                break;
            }
            case 2:{  //拍照入库
                context.startActivity(new Intent(context, Upload.class));
                break;
            }
            case 3:{  //物品查询
                Intent intent = new Intent(context,search.class);
                intent.putExtra("source", "audio_item_search/"+target);
                context.startActivity(intent);
                break;
            }
            case 4:{  //记录查询
                context.startActivity(new Intent(context, in_out_rcd.class));
                break;
            }
            case 5:{  //出库操作
                context.startActivity(new Intent(context, search.class));
                break;
            }
            case 6:{  //生态圈
                Intent intent = new Intent(context, Chattingcircle_recommend.class);
                context.startActivity(intent);
                ((Activity)context).finish();
                break;
            }
            case 7:{  //查看某天任务
                Intent intent = new Intent(context, family.class);
                intent.putExtra("source", "audio_check_task");
                context.startActivity(intent);
                ((Activity)context).finish();
                break;
            }
            case 8:{  //新建任务
                Intent intent = new Intent(context, family.class);
                intent.putExtra("source", "audio_addNew_task");
                context.startActivity(intent);
                ((Activity)context).finish();
                break;
            }
            case 9:{  //编辑任务
                Intent intent = new Intent(context, family.class);
                intent.putExtra("source", "audio_edit_task");
                context.startActivity(intent);
                ((Activity)context).finish();
                break;
            }
            case 10:{  //邀请家人
                Intent intent = new Intent(context, mine_page.class);
                intent.putExtra("source", "audio_invite");
                context.startActivity(intent);
                ((Activity)context).finish();
                break;
            }
            case 11:{  //加入邀请
                Intent intent = new Intent(context, mine_page.class);
                intent.putExtra("source", "audio_accept_invite");
                context.startActivity(intent);
                ((Activity)context).finish();
                break;
            }
            default:{
                break;
            }
        }
    }
}
