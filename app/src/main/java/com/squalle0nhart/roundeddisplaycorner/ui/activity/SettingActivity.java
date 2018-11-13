package com.squalle0nhart.roundeddisplaycorner.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squalle0nhart.roundeddisplaycorner.R;
import com.squalle0nhart.roundeddisplaycorner.service.ConerDisplayService;
import com.squalle0nhart.roundeddisplaycorner.utils.AppPreference;
import com.squalle0nhart.roundeddisplaycorner.utils.AppUltis;

import java.util.ArrayList;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;
import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class SettingActivity extends AppCompatActivity {
    private Context mContext;
    SeekBar sbSize;
    Switch swShowOverStatus, swShowOverNavigation, swEnableService, swSmartMode;
    AppPreference mAppPreference;
    RelativeLayout rlChooseConer, rlEnableApp, rlOpenSourceLicense, rlMoreApp, rlContactMe;
    Integer[] mConerChoosed;
    ArrayList<Integer> mConerChoosedList = new ArrayList<>();
    public static final int REQUEST_CODE = 1993;
    public static final int REQUEST_CODE2 = 1992;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        reCreateConverView();
        showRateDialog(mContext);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= 23 &&
                    !Settings.canDrawOverlays(mContext)) {
                mAppPreference.setActiveService(false);
                if (swEnableService != null) swEnableService.setChecked(false);
            } else {
                // Check xem nếu có quyền thì tạo lại góc màn hình
                if (swEnableService != null) swEnableService.setChecked(true);
                if (swShowOverNavigation.isChecked()) mAppPreference.setShowOverNavigation(true);
                if (swShowOverStatus.isChecked()) mAppPreference.setShowOverStatus(true);
                reCreateConverView();
            }
        }

        if (requestCode == REQUEST_CODE2) {
            if (AppUltis.isAccessibilityServiceEnable(mContext)) {
                mAppPreference.setActiveSmartMode(true);
                swSmartMode.setEnabled(true);
            } else {
                mAppPreference.setActiveSmartMode(false);
                swSmartMode.setEnabled(false);
            }
        }
    }

    private void initView() {
        mAppPreference = AppPreference.getInstance(mContext);
        sbSize = (SeekBar) findViewById(R.id.sb_size);
        initConerChooser();


        MobileAds.initialize(mContext, getString(R.string.ads_id));
        final AdView mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        if (!AppUltis.isAccessibilityServiceEnable(mContext)) {
            mAppPreference.setActiveSmartMode(false);
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                !Settings.canDrawOverlays(mContext)) {
            mAppPreference.setActiveService(false);
        }

        swShowOverStatus = (Switch) findViewById(R.id.sw_show_notification);
        swShowOverNavigation = (Switch) findViewById(R.id.sw_show_navigation_bar);
        swSmartMode = (Switch) findViewById(R.id.sw_smartmode);
        swEnableService = (Switch) findViewById(R.id.sw_enable_service);

        swShowOverStatus.setChecked(mAppPreference.isShowOverStatus());
        swShowOverNavigation.setChecked(mAppPreference.isShowOverNavigation());
        swSmartMode.setChecked(mAppPreference.isActiveSmartMode());
        swEnableService.setChecked(mAppPreference.isActiveService());

        // Set lại trạng thái view
        sbSize.setProgress(mAppPreference.getConerSize());

        // Điều chỉnh size coner
        sbSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mAppPreference.setConerSize(seekBar.getProgress());
                reCreateConverView();
            }
        });


        swShowOverNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppUltis.hasNavBar(mContext)
                        && swShowOverNavigation.isChecked()) {
                    mAppPreference.setShowOverNavigation(false);
                    swShowOverNavigation.setChecked(false);
                    Toast.makeText(mContext, R.string.device_dont_have_navigation, Toast.LENGTH_LONG).show();
                    return;
                }

                mAppPreference.setShowOverNavigation(swShowOverNavigation.isChecked());
                reCreateConverView();
            }
        });

        swShowOverStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppPreference.setShowOverNavigation(swShowOverNavigation.isChecked());
                if (Build.VERSION.SDK_INT >= 23 &&
                        !Settings.canDrawOverlays(mContext)) {
                    if (swShowOverStatus.isChecked()) showRequestPermissionDialog(mContext);
                    mAppPreference.setShowOverStatus(false);
                } else {
                    mAppPreference.setShowOverStatus(swShowOverStatus.isChecked());
                    reCreateConverView();
                }
            }
        });

        swSmartMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppUltis.hasNavBar(mContext)
                        && swSmartMode.isChecked()) {
                    mAppPreference.setActiveSmartMode(false);
                    swSmartMode.setChecked(false);
                    Toast.makeText(mContext, R.string.device_dont_have_navigation, Toast.LENGTH_LONG).show();
                    return;
                }

                if (!AppUltis.isAccessibilityServiceEnable(mContext) && swSmartMode.isChecked()) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                            .title(R.string.app_name)
                            .content(R.string.request_accessibility_service)
                            .positiveText(R.string.ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent mintent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                    startActivityForResult(mintent, REQUEST_CODE2);
                                }
                            });

                    MaterialDialog dialog = builder.build();
                    dialog.show();
                    return;
                }

                if (AppUltis.isAccessibilityServiceEnable(mContext) && !swSmartMode.isChecked()) {
                    mAppPreference.setActiveSmartMode(true);
                }

                mAppPreference.setActiveSmartMode(swSmartMode.isChecked());
            }
        });


        rlChooseConer = (RelativeLayout) findViewById(R.id.ll_custom_coner);
        rlChooseConer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initConerChooser();
                MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                        .title(R.string.choose_coner)
                        .items(R.array.array_choose_coner)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                mAppPreference.setShowTopLeft(false);
                                mAppPreference.setShowTopRight(false);
                                mAppPreference.setShowBotLeft(false);
                                mAppPreference.setShowBotRight(false);
                                for (int i = 0; i < which.length; i++) {
                                    if (which[i] == 0) {
                                        mAppPreference.setShowTopLeft(true);
                                    }

                                    if (which[i] == 1) {
                                        mAppPreference.setShowTopRight(true);
                                    }

                                    if (which[i] == 2) {
                                        mAppPreference.setShowBotLeft(true);
                                    }

                                    if (which[i] == 3) {
                                        mAppPreference.setShowBotRight(true);
                                    }
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.ok);
                MaterialDialog materialDialog = builder.build();
                materialDialog.setSelectedIndices(mConerChoosed);
                materialDialog.show();
            }
        });

        /*rlEnableApp = (RelativeLayout) findViewById(R.id.rl_enable_app);
        rlEnableApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swEnableService.isChecked()) {
                    swEnableService.setChecked(false);
                    mAppPreference.setActiveService(false);
                } else {
                    if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(mContext)) {
                        swEnableService.setChecked(false);
                        showRequestPermissionDialog(mContext);
                        mAppPreference.setActiveService(false);
                    }
                    swEnableService.setChecked(true);
                    mAppPreference.setActiveService(true);
                    reCreateConverView();
                }
            }
        });*/

        swEnableService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAppPreference.setActiveService(b);
                if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(mContext) && b) {
                    swEnableService.setChecked(false);
                    showRequestPermissionDialog(mContext);
                    mAppPreference.setActiveService(false);
                }
                reCreateConverView();
            }
        });


        rlOpenSourceLicense = (RelativeLayout) findViewById(R.id.rl_open_source_license);
        rlOpenSourceLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Notices notices = new Notices();
                notices.addNotice(new Notice("LicensesDialog", "https://github.com/PSDev/LicensesDialog", "Copyright 2013-2017 Philip Schiffer", new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("Android Rate", "https://github.com/hotchemi/Android-Rate", "Copyright (c) 2015 Shintaro Katafuchi", new MITLicense()));
                notices.addNotice(new Notice("Material Dialogs", "https://github.com/afollestad/material-dialogs", "Copyright (c) 2014-2016 Aidan Michael Follestad", new MITLicense()));

                new LicensesDialog.Builder(mContext)
                        .setNotices(notices)
                        .setIncludeOwnLicense(true)
                        .build()
                        .show();
            }
        });

        //https://play.google.com/store/apps/dev?id=7252196085307713045
        rlMoreApp = (RelativeLayout) findViewById(R.id.rl_more_app);
        rlMoreApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/dev?id=7252196085307713045"));
                startActivity(browserIntent);
            }
        });

        rlContactMe = (RelativeLayout) findViewById(R.id.rl_contact_me);
        rlContactMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("20115754.bka@gmail.com");
                Uri uri = Uri.parse(uriText);
                send.setData(uri);
                send.putExtra(Intent.EXTRA_SUBJECT, "Rounded Display");
                startActivity(Intent.createChooser(send, "Send mail..."));
            }
        });
    }

    // Tạo lại view
    private void reCreateConverView() {
        if (mAppPreference.isActiveService()) {
            if (Build.VERSION.SDK_INT >= 23 &&
                    !Settings.canDrawOverlays(mContext)) {
                showRequestPermissionDialog(mContext);
            } else {
                Intent intent = new Intent(mContext, ConerDisplayService.class);
                stopService(intent);
                startService(intent);
            }
        } else {
            Intent intent = new Intent(mContext, ConerDisplayService.class);
            stopService(intent);
        }
    }

    /**
     * Lưu trữ việc hiển thị 4 góc vào preference
     */
    private void initConerChooser() {
        mConerChoosedList = new ArrayList<>();
        if (mAppPreference.isShowTopLeft()) {
            mConerChoosedList.add(0);
        }

        if (mAppPreference.isShowTopRight()) {
            mConerChoosedList.add(1);
        }

        if (mAppPreference.isShowBotLeft()) {
            mConerChoosedList.add(2);
        }

        if (mAppPreference.isShowBotRight()) {
            mConerChoosedList.add(3);
        }

        mConerChoosed = new Integer[mConerChoosedList.size()];
        mConerChoosed = mConerChoosedList.toArray(new Integer[mConerChoosedList.size()]);
    }


    private void showRequestPermissionDialog(Context context) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(R.string.app_name)
                .content(R.string.request_draw_overtop_permission)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent mintent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startActivityForResult(mintent, REQUEST_CODE);
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    private void showRateDialog(Context context) {
        AppRate.with(context)
                .setInstallDays(0)
                .setLaunchTimes(2)
                .setRemindInterval(2)
                .setShowLaterButton(true)
                .setDebug(false)
                .setOnClickButtonListener(new OnClickButtonListener() {
                    @Override
                    public void onClickButton(int which) {
                    }
                })
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);
    }
}
