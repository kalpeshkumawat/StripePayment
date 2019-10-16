package com.app.stripepayment.utils;

import android.content.Context;
import android.content.SharedPreferences;



public class SharedPref {


    // SharedPreferences name
    private final static String PREF_NAME = "my_application_payment";


    // default values
    private final static String DEFAULT_STRING_VALUE = "";
    private final static boolean DEFAULT_BOOLEAN_VALUE = false;
    private final static int DEFAULT_INTEGER_VALUE = 0;


    // SharedPreferences key







    //SharedPreferences reference
    private SharedPreferences sharedPreferences;

    //SharedPref reference
    private static SharedPref sharedPref;

    // default constructor
    private SharedPref() {
    }


    /**
     * Method to initialize SharedPreferences
     *
     * @param context - represent application context
     */

    public static void init(Context context) {
        if (sharedPref == null) {
            sharedPref = new SharedPref();
            sharedPref.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    /**
     * Method to get Instance of SharedPref
     *
     * @return - return SharedPref instance
     */

    public static SharedPref getInstance() {
        if (sharedPref == null) {
            init(StripePaymentApp.getInstance());
        }
        return sharedPref;
    }


    /**
     * Method to save a string value in SharedPreferences
     *
     * @param key   - represent key string
     * @param value - represent value ( string )
     *              <p>
     *              Here data stored in key value pair in SharedPreferences
     */

    public void writeString(String key, String value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, value);
        editor.commit();
        editor.apply();
    }

    /**
     * Method to read a string value from sharedPreferences using key
     *
     * @param key - represent key string
     * @return - return string value corresponding to key
     */

    public String readString(String key) {
        return sharedPreferences.getString(key, DEFAULT_STRING_VALUE);
    }

    /**
     * Method to save a boolean value in SharedPreferences
     *
     * @param key   - represent key string
     * @param value - represent value ( boolean )
     *              <p>
     *              Here data stored in key value pair in SharedPreferences
     */

    public void writeBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(key, value);
        editor.commit();
        editor.apply();
    }

    /**
     * Method to read a boolean value from sharedPreferences using key
     *
     * @param key - represent key string
     * @return - return  boolean value  corresponding to key
     */

    public boolean readBoolean(String key) {
        return sharedPreferences.getBoolean(key, DEFAULT_BOOLEAN_VALUE);
    }

    /**
     * Method to save a Integer value in SharedPreferences
     *
     * @param key   - represent key string
     * @param value - represent value ( Integer )
     *              <p>
     *              Here data stored in key value pair in SharedPreferences
     */

    public void writeInteger(String key, int value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Method to read a Integer value from sharedPreferences using key
     *
     * @param key - represent key string
     * @return - return Integer value  corresponding to key
     */

    public int readInteger(String key) {
        return sharedPreferences.getInt(key, DEFAULT_INTEGER_VALUE);
    }

    /**
     * Method to remove any value from sharedPreferences using key
     *
     * @param key - represent key string
     */

    public void removeValue(String key) {
        SharedPreferences.Editor editor = getEditor();
        editor.remove(key);
        editor.commit();
    }

    /**
     * Method to get editor
     *
     * @return - return SharedPreferences.Editor
     */

    private SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

    /**
     * Method to clear all sharedPreferences Data
     */

    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
