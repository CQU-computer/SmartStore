// Generated by view binder compiler. Do not edit!
package com.example.smartstore.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.smartstore.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DialogBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final EditText editText1;

  @NonNull
  public final EditText editText2;

  @NonNull
  public final EditText editText3;

  @NonNull
  public final ImageView imageView23;

  @NonNull
  public final LinearLayout linearLayout21;

  @NonNull
  public final LinearLayout linearLayout22;

  @NonNull
  public final LinearLayout linearLayout23;

  @NonNull
  public final LinearLayout linearLayout24;

  @NonNull
  public final Guideline part44;

  @NonNull
  public final TextView selectDate;

  @NonNull
  public final LinearLayout users;

  @NonNull
  public final Guideline x1;

  private DialogBinding(@NonNull ConstraintLayout rootView, @NonNull EditText editText1,
      @NonNull EditText editText2, @NonNull EditText editText3, @NonNull ImageView imageView23,
      @NonNull LinearLayout linearLayout21, @NonNull LinearLayout linearLayout22,
      @NonNull LinearLayout linearLayout23, @NonNull LinearLayout linearLayout24,
      @NonNull Guideline part44, @NonNull TextView selectDate, @NonNull LinearLayout users,
      @NonNull Guideline x1) {
    this.rootView = rootView;
    this.editText1 = editText1;
    this.editText2 = editText2;
    this.editText3 = editText3;
    this.imageView23 = imageView23;
    this.linearLayout21 = linearLayout21;
    this.linearLayout22 = linearLayout22;
    this.linearLayout23 = linearLayout23;
    this.linearLayout24 = linearLayout24;
    this.part44 = part44;
    this.selectDate = selectDate;
    this.users = users;
    this.x1 = x1;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DialogBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DialogBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.dialog, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DialogBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.editText1;
      EditText editText1 = ViewBindings.findChildViewById(rootView, id);
      if (editText1 == null) {
        break missingId;
      }

      id = R.id.editText2;
      EditText editText2 = ViewBindings.findChildViewById(rootView, id);
      if (editText2 == null) {
        break missingId;
      }

      id = R.id.editText3;
      EditText editText3 = ViewBindings.findChildViewById(rootView, id);
      if (editText3 == null) {
        break missingId;
      }

      id = R.id.imageView23;
      ImageView imageView23 = ViewBindings.findChildViewById(rootView, id);
      if (imageView23 == null) {
        break missingId;
      }

      id = R.id.linearLayout21;
      LinearLayout linearLayout21 = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout21 == null) {
        break missingId;
      }

      id = R.id.linearLayout22;
      LinearLayout linearLayout22 = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout22 == null) {
        break missingId;
      }

      id = R.id.linearLayout23;
      LinearLayout linearLayout23 = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout23 == null) {
        break missingId;
      }

      id = R.id.linearLayout24;
      LinearLayout linearLayout24 = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout24 == null) {
        break missingId;
      }

      id = R.id.part44;
      Guideline part44 = ViewBindings.findChildViewById(rootView, id);
      if (part44 == null) {
        break missingId;
      }

      id = R.id.select_date;
      TextView selectDate = ViewBindings.findChildViewById(rootView, id);
      if (selectDate == null) {
        break missingId;
      }

      id = R.id.users;
      LinearLayout users = ViewBindings.findChildViewById(rootView, id);
      if (users == null) {
        break missingId;
      }

      id = R.id.x1;
      Guideline x1 = ViewBindings.findChildViewById(rootView, id);
      if (x1 == null) {
        break missingId;
      }

      return new DialogBinding((ConstraintLayout) rootView, editText1, editText2, editText3,
          imageView23, linearLayout21, linearLayout22, linearLayout23, linearLayout24, part44,
          selectDate, users, x1);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
