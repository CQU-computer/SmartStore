// Generated by view binder compiler. Do not edit!
package com.example.smartstore.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Guideline;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.smartstore.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class UploadImgBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final TextView curL;

  @NonNull
  public final ImageView imageView3;

  @NonNull
  public final ImageView imageView4;

  @NonNull
  public final ImageView imageView5;

  @NonNull
  public final LinearLayout linearLayout;

  @NonNull
  public final Button loadByCamera;

  @NonNull
  public final ImageView loadByCamera2;

  @NonNull
  public final Button loadByPhoto;

  @NonNull
  public final Guideline part28;

  @NonNull
  public final Button scanHelp;

  @NonNull
  public final Button scanReturn;

  private UploadImgBinding(@NonNull ScrollView rootView, @NonNull TextView curL,
      @NonNull ImageView imageView3, @NonNull ImageView imageView4, @NonNull ImageView imageView5,
      @NonNull LinearLayout linearLayout, @NonNull Button loadByCamera,
      @NonNull ImageView loadByCamera2, @NonNull Button loadByPhoto, @NonNull Guideline part28,
      @NonNull Button scanHelp, @NonNull Button scanReturn) {
    this.rootView = rootView;
    this.curL = curL;
    this.imageView3 = imageView3;
    this.imageView4 = imageView4;
    this.imageView5 = imageView5;
    this.linearLayout = linearLayout;
    this.loadByCamera = loadByCamera;
    this.loadByCamera2 = loadByCamera2;
    this.loadByPhoto = loadByPhoto;
    this.part28 = part28;
    this.scanHelp = scanHelp;
    this.scanReturn = scanReturn;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static UploadImgBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static UploadImgBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.upload_img, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static UploadImgBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cur_l;
      TextView curL = ViewBindings.findChildViewById(rootView, id);
      if (curL == null) {
        break missingId;
      }

      id = R.id.imageView3;
      ImageView imageView3 = ViewBindings.findChildViewById(rootView, id);
      if (imageView3 == null) {
        break missingId;
      }

      id = R.id.imageView4;
      ImageView imageView4 = ViewBindings.findChildViewById(rootView, id);
      if (imageView4 == null) {
        break missingId;
      }

      id = R.id.imageView5;
      ImageView imageView5 = ViewBindings.findChildViewById(rootView, id);
      if (imageView5 == null) {
        break missingId;
      }

      id = R.id.linearLayout;
      LinearLayout linearLayout = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout == null) {
        break missingId;
      }

      id = R.id.load_by_camera;
      Button loadByCamera = ViewBindings.findChildViewById(rootView, id);
      if (loadByCamera == null) {
        break missingId;
      }

      id = R.id.load_by_camera_2;
      ImageView loadByCamera2 = ViewBindings.findChildViewById(rootView, id);
      if (loadByCamera2 == null) {
        break missingId;
      }

      id = R.id.load_by_photo;
      Button loadByPhoto = ViewBindings.findChildViewById(rootView, id);
      if (loadByPhoto == null) {
        break missingId;
      }

      id = R.id.part28;
      Guideline part28 = ViewBindings.findChildViewById(rootView, id);
      if (part28 == null) {
        break missingId;
      }

      id = R.id.scan_help;
      Button scanHelp = ViewBindings.findChildViewById(rootView, id);
      if (scanHelp == null) {
        break missingId;
      }

      id = R.id.scan_return;
      Button scanReturn = ViewBindings.findChildViewById(rootView, id);
      if (scanReturn == null) {
        break missingId;
      }

      return new UploadImgBinding((ScrollView) rootView, curL, imageView3, imageView4, imageView5,
          linearLayout, loadByCamera, loadByCamera2, loadByPhoto, part28, scanHelp, scanReturn);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}