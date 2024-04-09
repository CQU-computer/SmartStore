package chattingcircle;

public class ModuleItem {
    public enum Type { FIXED, CUSTOM }

    public final Type type;
    public final String imageUrl;
    public final String customText;

    public ModuleItem(Type type, String imageUrl, String customText) {
        this.type = type;
        this.imageUrl = imageUrl;
        this.customText = customText;
    }
}
