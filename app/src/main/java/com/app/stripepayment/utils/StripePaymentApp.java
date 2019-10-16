package com.app.stripepayment.utils;

import android.app.Application;



public class StripePaymentApp extends Application {


    private static StripePaymentApp application;


    @Override
    public void onCreate() {
        super.onCreate();

        application = this;


        SharedPref.init(this);  // initialize Shared Pref


    }

    public static StripePaymentApp getInstance() {
        return application;
    }


}
