package com.squalle0nhart.roundeddisplaycorner.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.squalle0nhart.roundeddisplaycorner.R;
import com.squalle0nhart.roundeddisplaycorner.utils.AppPreference;
import com.squalle0nhart.roundeddisplaycorner.utils.AppUltis;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by squalle0nhart on 12/5/2017.
 */

public class ConerDisplayService extends Service {
    Context mContext;
    WindowManager mWindowManager;
    View mOverLayTopLeft, mOverLayTopRight, mOverLayBotLeft, mOverLayBotRight;
    AppPreference mAppPreference;
    int screenWidth, screenHeight;
    public static ScheduledExecutorService sScheduledExecutor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mOverLayTopLeft = View.inflate(mContext, R.layout.top_left, null);
        mOverLayTopRight = View.inflate(mContext, R.layout.top_right, null);
        mOverLayBotLeft = View.inflate(mContext, R.layout.bot_left, null);
        mOverLayBotRight = View.inflate(mContext, R.layout.bot_right, null);
        mAppPreference = AppPreference.getInstance(mContext);

        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            if (sScheduledExecutor != null) {
                sScheduledExecutor.shutdown();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        sScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        sScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Log.e("SERVICE", "SERVICE RUNNING");
            }
        }, 0, 100000L, TimeUnit.MILLISECONDS);


        int size = mAppPreference.getConerSize();
        int flag = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        if (mAppPreference.isShowOverStatus()) {
            flag = flag | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            flag = flag | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
        }

        if (mAppPreference.isShowOverNavigation() || mAppPreference.isActiveSmartMode()) {
            flag = flag | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        }

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // Top Left
        if (mAppPreference.isShowTopLeft()) {
            WindowManager.LayoutParams topLeftParam =
                    new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                            flag, PixelFormat.TRANSLUCENT);
            topLeftParam.height = size;
            topLeftParam.width = size;
            topLeftParam.gravity = Gravity.TOP | Gravity.LEFT;
            mWindowManager.addView(mOverLayTopLeft, topLeftParam);
        } else {
            mOverLayTopLeft = null;
        }

        // Top Right
        if (mAppPreference.isShowTopRight()) {
            WindowManager.LayoutParams topLeftParam =
                    new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                            flag, PixelFormat.TRANSLUCENT);
            topLeftParam.height = size;
            topLeftParam.width = size;
            topLeftParam.gravity = Gravity.TOP | Gravity.RIGHT;
            mWindowManager.addView(mOverLayTopRight, topLeftParam);
        } else {
            mOverLayTopRight = null;
        }

        // Bot Left
        if (mAppPreference.isShowBotLeft()) {
            WindowManager.LayoutParams botLeftParam =
                    new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                            flag, PixelFormat.TRANSLUCENT);
            botLeftParam.height = size;
            botLeftParam.width = size;
            if (mAppPreference.isShowOverNavigation() && !mAppPreference.isActiveSmartMode()) {
                botLeftParam.gravity = Gravity.TOP | Gravity.LEFT;
                botLeftParam.y = screenHeight + AppUltis.navHeight(this) - size;
            } else {
                botLeftParam.gravity = Gravity.BOTTOM | Gravity.LEFT;
            }

            // Ưu tiên smartmode trước so với bật dưới navigation nên để code đoạn này sau
            if (mAppPreference.isActiveSmartMode() && SmartModeService.sIsInHome) {
                botLeftParam.gravity = Gravity.TOP | Gravity.LEFT;
                botLeftParam.y = screenHeight + AppUltis.navHeight(this) - size;
            } else if (mAppPreference.isActiveSmartMode()){
                botLeftParam.gravity = Gravity.BOTTOM | Gravity.LEFT;
            }
            mWindowManager.addView(mOverLayBotLeft, botLeftParam);
        } else if (mAppPreference.isActiveSmartMode() && !SmartModeService.sIsInHome) {
            mOverLayBotLeft = null;
        }

        // Bot Right
        if (mAppPreference.isShowBotRight()) {
            WindowManager.LayoutParams botRightParam =
                    new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                            flag, PixelFormat.TRANSLUCENT);
            botRightParam.height = size;
            botRightParam.width = size;

            if (mAppPreference.isShowOverNavigation() && !mAppPreference.isActiveSmartMode()) {
                botRightParam.gravity = Gravity.TOP | Gravity.RIGHT;
                botRightParam.y = screenHeight + AppUltis.navHeight(this) - size;
            } else {
                botRightParam.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            }

            // Ưu tiên smartmode trước so với bật dưới navigation nên để code đoạn này sau
            if (mAppPreference.isActiveSmartMode() && SmartModeService.sIsInHome) {
                botRightParam.gravity = Gravity.TOP | Gravity.RIGHT;
                botRightParam.y = screenHeight + AppUltis.navHeight(this) - size;
            } else if (mAppPreference.isActiveSmartMode() && !SmartModeService.sIsInHome) {
                botRightParam.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            }
            mWindowManager.addView(mOverLayBotRight, botRightParam);
        } else {
            mOverLayBotRight = null;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mWindowManager != null) {
                // Xóa view
                if (mOverLayTopLeft != null) mWindowManager.removeView(mOverLayTopLeft);
                if (mOverLayTopRight != null) mWindowManager.removeView(mOverLayTopRight);
                if (mOverLayBotLeft != null) mWindowManager.removeView(mOverLayBotLeft);
                if (mOverLayBotRight != null) mWindowManager.removeView(mOverLayBotRight);
                mWindowManager = null;
            }
        } catch (IllegalArgumentException e ){
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
