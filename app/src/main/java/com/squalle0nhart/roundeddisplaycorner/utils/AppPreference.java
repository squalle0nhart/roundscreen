package com.squalle0nhart.roundeddisplaycorner.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squalle0nhart.roundeddisplaycorner.Constants;


public class AppPreference {
    private static AppPreference instance;
    private SharedPreferences preferences;


    public int getConerSize() {
        return getInt(Constants.CONER_SIZE, 50);
    }

    public void setConerSize(int size) {
        putInt(Constants.CONER_SIZE, size);
    }

    public boolean isShowTopLeft() {
        return getBoolean(Constants.SHOW_TOP_LEFT, true);
    }

    public void setShowTopLeft(boolean isShow) {
        putBoolean(Constants.SHOW_TOP_LEFT, isShow);
    }

    public boolean isShowTopRight() {
        return getBoolean(Constants.SHOW_TOP_RIGHT, true);
    }

    public void setShowTopRight(boolean isShow) {
        putBoolean(Constants.SHOW_TOP_RIGHT, isShow);
    }


    public boolean isShowBotRight() {
        return getBoolean(Constants.SHOW_BOT_RIGHT, true);
    }

    public void setShowBotRight(boolean isShow) {
        putBoolean(Constants.SHOW_BOT_RIGHT, isShow);
    }


    public boolean isShowBotLeft() {
        return getBoolean(Constants.SHOW_BOT_LEFT, true);
    }

    public void setShowBotLeft(boolean isShow) {
        putBoolean(Constants.SHOW_BOT_LEFT, isShow);
    }

    public void setShowOverStatus(boolean isShow) {
        putBoolean(Constants.SHOW_OVER_STATUS, isShow);
    }

    public boolean isShowOverStatus() {
        return getBoolean(Constants.SHOW_OVER_STATUS, true);
    }

    public void setShowOverNavigation(boolean isShow) {
        putBoolean(Constants.SHOW_OVER_NAVIGATION, isShow);
    }

    public boolean isShowOverNavigation() {
        return getBoolean(Constants.SHOW_OVER_NAVIGATION, false);
    }

    public boolean isActiveService() {
        return getBoolean(Constants.ACTIVE_SERVICE, true);
    }

    public void setActiveService(boolean isActiveService) {
        putBoolean(Constants.ACTIVE_SERVICE, isActiveService);
    }

    public boolean isActiveSmartMode() {
        return getBoolean(Constants.SMART_MODE, false);
    }

    public void setActiveSmartMode(boolean isActiveMode) {
        putBoolean(Constants.SMART_MODE, isActiveMode);
    }


    /***********************************************************************************************
     * Code xử lý dữ liệu preference
     ***********************************************************************************************/

    /**
     * Contractor
     *
     * @param context
     */
    public AppPreference(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    /**
     * Use to initialization and get object AppPreference
     *
     * @param context
     */
    public static AppPreference getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreference(context);
        }
        return instance;
    }

    public boolean contain(String key) {
        return preferences.contains(key);
    }

    /**
     * Set an int value in the preferences editor
     *
     * @param key   : name of preference to modify
     * @param value : the new value of preference
     * @return : AppPreference to continue edit
     */
    public AppPreference putInt(String key, int value) {
        preferences.edit().putInt(key, value).commit();
        return instance;
    }

    /**
     * Retrieve an int value from the preferences.
     *
     * @param key          : the name of preference to retrieve
     * @param defaultValue : value to return if this preference does not exist
     * @return : return the preference value if it exist, else return default
     * value
     */
    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    /**
     * Set a long value in preference editor
     *
     * @param key   : name of preference to modify
     * @param value : the new value of preference
     * @return : AppPreference to continue edit
     */
    public AppPreference putLong(String key, long value) {

        preferences.edit().putLong(key, value).commit();
        return instance;
    }

    /**
     * Retrieve an long value from the preferences
     *
     * @param key          : the name of preference to retrieve
     * @param defaultValue : value to return if this preference does not exist
     * @return : return the preference value if exist, else return default value
     */
    public long getLong(String key, long defaultValue) {

        return preferences.getLong(key, defaultValue);
    }

    /**
     * Set a String value in preference editor
     *
     * @param key   : name of preference to modify
     * @param value : the new value of preference
     * @return : return AppPreference to continue edit
     */
    public AppPreference putString(String key, String value) {

        preferences.edit().putString(key, value).commit();
        return instance;
    }

    /**
     * Retrieve a String value from the preference
     *
     * @param key          : the name of preference to retrieve
     * @param defaultValue : value to return if preference does not exist
     * @return : return the preference value if exist, else return default value
     */
    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    /**
     * Set a boolean value in preference editor
     *
     * @param key   : name of preference to modify
     * @param value : the new value of preference
     * @return : return AppPreference to continue edit
     */
    public AppPreference putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).commit();
        return instance;
    }

    /**
     * Retrieve a boolean value from the preferences
     *
     * @param key          : the name of preference to retrieve
     * @param defaultValue : value to return if preference does not exist
     * @return : return the preference value if exist, else return default value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * Set a float value in preference editor
     *
     * @param key   : name of preference to modify
     * @param value : the new value of preference
     * @return : return AppPreference to continue edit
     */
    public AppPreference putFloat(String key, float value) {
        preferences.edit().putFloat(key, value).commit();
        return instance;
    }

    /**
     * Retrieve a float value from the preferences
     *
     * @param key          : the name of preference to retrieve
     * @param defaultValue : value to return if preference dose not exist
     * @return : return the preference value if exist, else return default value
     */
    public float getFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    public void removeString(String key) {
        preferences.edit().remove(key).apply();
    }


    public AppPreference putArray(String arrayName, byte[] array) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(arrayName + "_size", array.length);
        for (int i = 0; i < array.length; i++)
            editor.putInt(arrayName + "_" + i, array[i]);
        editor.commit();
        return instance;
    }

    public byte[] getByteArray(String arrayName) {
        int size = preferences.getInt(arrayName + "_size", 0);
        byte[] array = new byte[size];
        for (int i = 0; i < size; i++)
            array[i] = (byte) preferences.getInt(arrayName + "_" + i, 0);
        return array;
    }
}