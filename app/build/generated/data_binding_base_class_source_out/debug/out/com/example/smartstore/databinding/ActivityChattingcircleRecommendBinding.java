// Generated by view binder compiler. Do not edit!
package com.example.smartstore.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.smartstore.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityChattingcircleRecommendBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView cBlack;

  @NonNull
  public final ImageView cCircleComment;

  @NonNull
  public final Button commend;

  @NonNull
  public final ImageView commendIcon;

  @NonNull
  public final ImageView imageView11;

  @NonNull
  public final RecyclerView list1;

  @NonNull
  public final Button myChattingCircle;

  @NonNull
  public final ImageView myCircleIcon;

  @NonNull
  public final TextView threeTOfour;

  @NonNull
  public final TextView threeTOone;

  @NonNull
  public final TextView threeTOtwo;

  private ActivityChattingcircleRecommendBinding(@NonNull ConstraintLayout rootView,
      @NonNull TextView cBlack, @NonNull ImageView cCircleComment, @NonNull Button commend,
      @NonNull ImageView commendIcon, @NonNull ImageView imageView11, @NonNull RecyclerView list1,
      @NonNull Button myChattingCircle, @NonNull ImageView myCircleIcon,
      @NonNull TextView threeTOfour, @NonNull TextView threeTOone, @NonNull TextView threeTOtwo) {
    this.rootView = rootView;
    this.cBlack = cBlack;
    this.cCircleComment = cCircleComment;
    this.commend = commend;
    this.commendIcon = commendIcon;
    this.imageView11 = imageView11;
    this.list1 = list1;
    this.myChattingCircle = myChattingCircle;
    this.myCircleIcon = myCircleIcon;
    this.threeTOfour = threeTOfour;
    this.threeTOone = threeTOone;
    this.threeTOtwo = threeTOtwo;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityChattingcircleRecommendBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityChattingcircleRecommendBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_chattingcircle_recommend, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityChattingcircleRecommendBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.c_black;
      TextView cBlack = ViewBindings.findChildViewById(rootView, id);
      if (cBlack == null) {
        break missingId;
      }

      id = R.id.c_circle_comment;
      ImageView cCircleComment = ViewBindings.findChildViewById(rootView, id);
      if (cCircleComment == null) {
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

      id = R.id.imageView11;
      ImageView imageView11 = ViewBindings.findChildViewById(rootView, id);
      if (imageView11 == null) {
        break missingId;
      }

      id = R.id.list1;
      RecyclerView list1 = ViewBindings.findChildViewById(rootView, id);
      if (list1 == null) {
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

      id = R.id.threeTOfour;
      TextView threeTOfour = ViewBindings.findChildViewById(rootView, id);
      if (threeTOfour == null) {
        break missingId;
      }

      id = R.id.threeTOone;
      TextView threeTOone = ViewBindings.findChildViewById(rootView, id);
      if (threeTOone == null) {
        break missingId;
      }

      id = R.id.threeTOtwo;
      TextView threeTOtwo = ViewBindings.findChildViewById(rootView, id);
      if (threeTOtwo == null) {
        break missingId;
      }

      return new ActivityChattingcircleRecommendBinding((ConstraintLayout) rootView, cBlack,
          cCircleComment, commend, commendIcon, imageView11, list1, myChattingCircle, myCircleIcon,
          threeTOfour, threeTOone, threeTOtwo);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}