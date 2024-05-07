package chattingCircle;

public class ModuleItem {
    public enum Type { FIXED, CUSTOM }

    public final Type type;
    public final String imageUrl;
    public final String customText;
    public final String Title;
    public final String time;
    public String userID;
    public String like;
    public String postid;
    public String uname;

    public ModuleItem(Type type, String imageUrl, String customText, String title, String Time,String userID,String like,String postid,String uname) {
        this.type = type;
        this.imageUrl = imageUrl;
        this.customText = customText;
        this.Title = title;
        this.time = Time;
        this.userID = userID;
        this.like=like;
        this.postid=postid;
        this.uname=uname;
    }
}