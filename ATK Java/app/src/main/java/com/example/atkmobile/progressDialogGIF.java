package com.example.atkmobile;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class progressDialogGIF {
    Activity activity;
    Dialog dialog;
    //..we need the context else we can not create the dialog so get context in constructor
    public progressDialogGIF(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {
        /*progress dialog*/
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.progress_dialog);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);

        ImageView gifImageView = dialog.findViewById(R.id.custom_loading_imageView);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);

        Glide.with(activity)
                .load(R.drawable.progress_bar)
                .placeholder(R.drawable.progress_bar)
                .centerCrop()
                .crossFade()
                .into(imageViewTarget);

        dialog.show();
    }

    public void stopDialog() {
        dialog.dismiss();
    }
}
