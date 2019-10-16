package com.app.stripepayment.stripe;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.app.stripepayment.R;

public class Utils {


    private static Dialog mProgressDialog = null;


    public static void showProgress(Context mContext) {


        if (mContext != null) {


            mProgressDialog = new Dialog(mContext);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setContentView(R.layout.progress_bar);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);

            Activity activity = (Activity) mContext;

            if (mContext == null || activity.isFinishing()) {
                return;
            }



            mProgressDialog.show();


        }

    }

    public static void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();


        }
    }


}
