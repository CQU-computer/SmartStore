// Generated by view binder compiler. Do not edit!
package com.example.smartstore.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.smartstore.Circle;
import com.example.smartstore.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityChattingCircleBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final Button button1;

  @NonNull
  public final Button button2;

  @NonNull
  public final ImageView chattingMyBgr;

  @NonNull
  public final Button commend;

  @NonNull
  public final ImageView commendIcon;

  @NonNull
  public final TextView concerns;

  @NonNull
  public final LinearLayout container;

  @NonNull
  public final ImageView icon1;

  @NonNull
  public final ImageView icon2;

  @NonNull
  public final ImageView imageView10;

  @NonNull
  public final LinearLayout linearLayout14;

  @NonNull
  public final LinearLayout linearLayout16;

  @NonNull
  public final LinearLayout linearLayout17;

  @NonNull
  public final Button myChattingCircle;

  @NonNull
  public final ImageView myCircleIcon;

  @NonNull
  public final ScrollView myCircleRoot;

  @NonNull
  public final LinearLayout none1;

  @NonNull
  public final FrameLayout subInterfaceContainer;

  @NonNull
  public final TextView thumbs;

  @NonNull
  public final Circle userImg1;

  @NonNull
  public final EditText userName;

  @NonNull
  public final EditText userSignature;

  private ActivityChattingCircleBinding(@NonNull ScrollView rootView, @NonNull Button button1,
      @NonNull Button button2, @NonNull ImageView chattingMyBgr, @NonNull Button commend,
      @NonNull ImageView commendIcon, @NonNull TextView concerns, @NonNull LinearLayout container,
      @NonNull ImageView icon1, @NonNull ImageView icon2, @NonNull ImageView imageView10,
      @NonNull LinearLayout linearLayout14, @NonNull LinearLayout linearLayout16,
      @NonNull LinearLayout linearLayout17, @NonNull Button myChattingCircle,
      @NonNull ImageView myCircleIcon, @NonNull ScrollView myCircleRoot,
      @NonNull LinearLayout none1, @NonNull FrameLayout subInterfaceContainer,
      @NonNull TextView thumbs, @NonNull Circle userImg1, @NonNull EditText userName,
      @NonNull EditText userSignature) {
    this.rootView = rootView;
    this.button1 = button1;
    this.button2 = button2;
    this.chattingMyBgr = chattingMyBgr;
    this.commend = commend;
    this.commendIcon = commendIcon;
    this.concerns = concerns;
    this.container = container;
    this.icon1 = icon1;
    this.icon2 = icon2;
    this.imageView10 = imageView10;
    this.linearLayout14 = linearLayout14;
    this.linearLayout16 = linearLayout16;
    this.linearLayout17 = linearLayout17;
    this.myChattingCircle = myChattingCircle;
    this.myCircleIcon = myCircleIcon;
    this.myCircleRoot = myCircleRoot;
    this.none1 = none1;
    this.subInterfaceContainer = subInterfaceContainer;
    this.thumbs = thumbs;
    this.userImg1 = userImg1;
    this.userName = userName;
    this.userSignature = userSignature;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityChattingCircleBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityChattingCircleBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_chatting_circle, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityChattingCircleBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button1;
      Button button1 = ViewBindings.findChildViewById(rootView, id);
      if (button1 == null) {
        break missingId;
      }

      id = R.id.button2;
      Button button2 = ViewBindings.findChildViewById(rootView, id);
      if (button2 == null) {
        break missingId;
      }

      id = R.id.chatting_my_bgr;
      ImageView chattingMyBgr = ViewBindings.findChildViewById(rootView, id);
      if (chattingMyBgr == null) {
        break missingId;
      }

      id = R.id.commend;
      Button commend = ViewBindings.findChildViewById(rootView, id);
      if (commend == null) {
        break missingId;
      }

      id = R.id.commend_icon;
      ImageView commendIcon = ViewBindings.findChildViewById(rootView, id);
      if (commendIcon == null) {
        break missingId;
      }

      id = R.id.concerns;
      TextView concerns = ViewBindings.findChildViewById(rootView, id);
      if (concerns == null) {
        break missingId;
      }

      id = R.id.container;
      LinearLayout container = ViewBindings.findChildViewById(rootView, id);
      if (container == null) {
        break missingId;
      }

      id = R.id.icon1;
      ImageView icon1 = ViewBindings.findChildViewById(rootView, id);
      if (icon1 == null) {
        break missingId;
      }

      id = R.id.icon2;
      ImageView icon2 = ViewBindings.findChildViewById(rootView, id);
      if (icon2 == null) {
        break missingId;
      }

      id = R.id.imageView10;
      ImageView imageView10 = ViewBindings.findChildViewById(rootView, id);
      if (imageView10 == null) {
        break missingId;
      }

      id = R.id.linearLayout14;
      LinearLayout linearLayout14 = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout14 == null) {
        break missingId;
      }

      id = R.id.linearLayout16;
      LinearLayout linearLayout16 = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout16 == null) {
        break missingId;
      }

      id = R.id.linearLayout17;
      LinearLayout linearLayout17 = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout17 == null) {
        break missingId;
      }

      id = R.id.my_chatting_circle;
      Button myChattingCircle = ViewBindings.findChildViewById(rootView, id);
      if (myChattingCircle == null) {
        break missingId;
      }

      id = R.id.my_circle_icon;
      ImageView myCircleIcon = ViewBindings.findChildViewById(rootView, id);
      if (myCircleIcon == null) {
        break missingId;
      }

      ScrollView myCircleRoot = (ScrollView) rootView;

      id = R.id.none1;
      LinearLayout none1 = ViewBindings.findChildViewById(rootView, id);
      if (none1 == null) {
        break missingId;
      }

      id = R.id.sub_interface_container;
      FrameLayout subInterfaceContainer = ViewBindings.findChildViewById(rootView, id);
      if (subInterfaceContainer == null) {
        break missingId;
      }

      id = R.id.thumbs;
      TextView thumbs = ViewBindings.findChildViewById(rootView, id);
      if (thumbs == null) {
        break missingId;
      }

      id = R.id.user_img1;
      Circle userImg1 = ViewBindings.findChildViewById(rootView, id);
      if (userImg1 == null) {
        break missingId;
      }

      id = R.id.user_name;
      EditText userName = ViewBindings.findChildViewById(rootView, id);
      if (userName == null) {
        break missingId;
      }

      id = R.id.user_signature;
      EditText userSignature = ViewBindings.findChildViewById(rootView, id);
      if (userSignature == null) {
        break missingId;
      }

      return new ActivityChattingCircleBinding((ScrollView) rootView, button1, button2,
          chattingMyBgr, commend, commendIcon, concerns, container, icon1, icon2, imageView10,
          linearLayout14, linearLayout16, linearLayout17, myChattingCircle, myCircleIcon,
          myCircleRoot, none1, subInterfaceContainer, thumbs, userImg1, userName, userSignature);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}