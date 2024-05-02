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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.smartstore.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FamilyBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button addTask;

  @NonNull
  public final Button clearTask;

  @NonNull
  public final LinearLayout containerLayout;

  @NonNull
  public final TextView date;

  @NonNull
  public final LinearLayout dateTimeBar;

  @NonNull
  public final TextView day;

  @NonNull
  public final Button help;

  @NonNull
  public final ImageView imageView13;

  @NonNull
  public final LinearLayout linearLayout15;

  @NonNull
  public final TextView month;

  @NonNull
  public final ScrollView scrollView5;

  @NonNull
  public final TextView test;

  @NonNull
  public final TextView twoTOfour;

  @NonNull
  public final TextView twoTOone;

  @NonNull
  public final TextView twoTOthree;

  @NonNull
  public final Button urgentTask;

  @NonNull
  public final ImageView warning;

  private FamilyBinding(@NonNull ConstraintLayout rootView, @NonNull Button addTask,
      @NonNull Button clearTask, @NonNull LinearLayout containerLayout, @NonNull TextView date,
      @NonNull LinearLayout dateTimeBar, @NonNull TextView day, @NonNull Button help,
      @NonNull ImageView imageView13, @NonNull LinearLayout linearLayout15, @NonNull TextView month,
      @NonNull ScrollView scrollView5, @NonNull TextView test, @NonNull TextView twoTOfour,
      @NonNull TextView twoTOone, @NonNull TextView twoTOthree, @NonNull Button urgentTask,
      @NonNull ImageView warning) {
    this.rootView = rootView;
    this.addTask = addTask;
    this.clearTask = clearTask;
    this.containerLayout = containerLayout;
    this.date = date;
    this.dateTimeBar = dateTimeBar;
    this.day = day;
    this.help = help;
    this.imageView13 = imageView13;
    this.linearLayout15 = linearLayout15;
    this.month = month;
    this.scrollView5 = scrollView5;
    this.test = test;
    this.twoTOfour = twoTOfour;
    this.twoTOone = twoTOone;
    this.twoTOthree = twoTOthree;
    this.urgentTask = urgentTask;
    this.warning = warning;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FamilyBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FamilyBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.family, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FamilyBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.add_task;
      Button addTask = ViewBindings.findChildViewById(rootView, id);
      if (addTask == null) {
        break missingId;
      }

      id = R.id.clear_task;
      Button clearTask = ViewBindings.findChildViewById(rootView, id);
      if (clearTask == null) {
        break missingId;
      }

      id = R.id.containerLayout;
      LinearLayout containerLayout = ViewBindings.findChildViewById(rootView, id);
      if (containerLayout == null) {
        break missingId;
      }

      id = R.id.date;
      TextView date = ViewBindings.findChildViewById(rootView, id);
      if (date == null) {
        break missingId;
      }

      id = R.id.date_time_bar;
      LinearLayout dateTimeBar = ViewBindings.findChildViewById(rootView, id);
      if (dateTimeBar == null) {
        break missingId;
      }

      id = R.id.day;
      TextView day = ViewBindings.findChildViewById(rootView, id);
      if (day == null) {
        break missingId;
      }

      id = R.id.help;
      Button help = ViewBindings.findChildViewById(rootView, id);
      if (help == null) {
        break missingId;
      }

      id = R.id.imageView13;
      ImageView imageView13 = ViewBindings.findChildViewById(rootView, id);
      if (imageView13 == null) {
        break missingId;
      }

      id = R.id.linearLayout15;
      LinearLayout linearLayout15 = ViewBindings.findChildViewById(rootView, id);
      if (linearLayout15 == null) {
        break missingId;
      }

      id = R.id.month;
      TextView month = ViewBindings.findChildViewById(rootView, id);
      if (month == null) {
        break missingId;
      }

      id = R.id.scrollView5;
      ScrollView scrollView5 = ViewBindings.findChildViewById(rootView, id);
      if (scrollView5 == null) {
        break missingId;
      }

      id = R.id.test;
      TextView test = ViewBindings.findChildViewById(rootView, id);
      if (test == null) {
        break missingId;
      }

      id = R.id.twoTOfour;
      TextView twoTOfour = ViewBindings.findChildViewById(rootView, id);
      if (twoTOfour == null) {
        break missingId;
      }

      id = R.id.twoTOone;
      TextView twoTOone = ViewBindings.findChildViewById(rootView, id);
      if (twoTOone == null) {
        break missingId;
      }

      id = R.id.twoTOthree;
      TextView twoTOthree = ViewBindings.findChildViewById(rootView, id);
      if (twoTOthree == null) {
        break missingId;
      }

      id = R.id.urgent_task;
      Button urgentTask = ViewBindings.findChildViewById(rootView, id);
      if (urgentTask == null) {
        break missingId;
      }

      id = R.id.warning;
      ImageView warning = ViewBindings.findChildViewById(rootView, id);
      if (warning == null) {
        break missingId;
      }

      return new FamilyBinding((ConstraintLayout) rootView, addTask, clearTask, containerLayout,
          date, dateTimeBar, day, help, imageView13, linearLayout15, month, scrollView5, test,
          twoTOfour, twoTOone, twoTOthree, urgentTask, warning);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}