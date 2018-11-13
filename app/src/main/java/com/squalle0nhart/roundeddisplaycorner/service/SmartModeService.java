package com.squalle0nhart.roundeddisplaycorner.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.squalle0nhart.roundeddisplaycorner.utils.AppPreference;
import com.squalle0nhart.roundeddisplaycorner.utils.AppUltis;

/**
 * Created by squalleonhart on 8/7/2017.
 */

public class SmartModeService extends AccessibilityService {
    public static boolean sIsInHome = false;
    private static String sLastPackageName = "";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getPackageName().equals(this.getPackageName())) return;
        if (!AppPreference.getInstance(this).isActiveSmartMode()) return;
        if (accessibilityEvent.getPackageName().equals(sLastPackageName)) return;
        sLastPackageName = accessibilityEvent.getPackageName().toString();
        Log.e("TAG","Event: "+sLastPackageName);

        if (accessibilityEvent.getPackageName().equals(AppUltis.getCurrentPackageName(this))) {
            if (!sIsInHome) {
                sIsInHome = true;
                reCreateView(this);
            }
        } else {
            if (sIsInHome) {
                sIsInHome = false;
                reCreateView(this);
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    private void reCreateView(Context context) {
        Intent intent = new Intent(context, ConerDisplayService.class);
        stopService(intent);
        startService(intent);
    }
}
