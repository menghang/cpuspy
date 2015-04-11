//-----------------------------------------------------------------------------
//
// (C) Rob Beane, 2015 <robbeane@gmail.com>
//
//-----------------------------------------------------------------------------

package org.axdev.cpuspy.ui;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import com.balysv.materialripple.MaterialRippleLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.axdev.cpuspy.CpuSpyApp;
import org.axdev.cpuspy.R;
import org.axdev.cpuspy.utils.CPUUtils;
import org.axdev.cpuspy.utils.TypefaceSpan;
import org.axdev.cpuspy.utils.ThemeUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class InfoActivity extends ActionBarActivity implements OnClickListener {

    @InjectView(R.id.card_view_kernel) CardView mKernelCardView;
    @InjectView(R.id.card_view_device) CardView mDeviceCardView;
    @InjectView(R.id.card_view_cpu) CardView mCpuCardView;
    @InjectView(R.id.kernel_header) TextView mKernelHeader;
    @InjectView(R.id.kernel_governor_header) TextView mKernelGovernorHeader;
    @InjectView(R.id.kernel_governor) TextView mKernelGovernor;
    @InjectView(R.id.kernel_version_header) TextView mKernelVersionHeader;
    @InjectView(R.id.kernel_version) TextView mKernelVersion;
    @InjectView(R.id.cpu_header) TextView mCpuHeader;
    @InjectView(R.id.cpu_abi_header) TextView mCpuAbiHeader;
    @InjectView(R.id.cpu_abi) TextView mCpuAbi;
    @InjectView(R.id.cpu_arch_header) TextView mCpuArchHeader;
    @InjectView(R.id.cpu_arch) TextView mCpuArch;
    @InjectView(R.id.cpu_core_header) TextView mCpuCoreHeader;
    @InjectView(R.id.cpu_core) TextView mCpuCore;
    @InjectView(R.id.cpu_freq_header) TextView mCpuFreqHeader;
    @InjectView(R.id.cpu_freq) TextView mCpuFreq;
    @InjectView(R.id.cpu_features_header) TextView mCpuFeaturesHeader;
    @InjectView(R.id.cpu_features) TextView mCpuFeatures;
    @InjectView(R.id.device_header) TextView mDeviceInfo;
    @InjectView(R.id.device_build_header) TextView mDeviceBuildHeader;
    @InjectView(R.id.device_build) TextView mDeviceBuild;
    @InjectView(R.id.device_api_header) TextView mDeviceApiHeader;
    @InjectView(R.id.device_api) TextView mDeviceApi;
    @InjectView(R.id.device_manuf_header) TextView mDeviceManufHeader;
    @InjectView(R.id.device_manuf) TextView mDeviceManuf;
    @InjectView(R.id.device_model_header) TextView mDeviceModelHeader;
    @InjectView(R.id.device_model) TextView mDeviceModel;
    @InjectView(R.id.device_board_header) TextView mDeviceBoardHeader;
    @InjectView(R.id.device_board) TextView mDeviceBoard;
    @InjectView(R.id.device_platform_header) TextView mDevicePlatformHeader;
    @InjectView(R.id.device_platform) TextView mDevicePlatform;

    @Optional @InjectView(R.id.ripple_info) MaterialRippleLayout mMaterialRippleLayout;

    private final String API_LEVEL = "ro.build.version.sdk";
    private final String BOARD_PLATFORM = "ro.board.platform";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 21) {
            ThemeUtils.onActivityCreateSetNavBar(this);
        }
        ThemeUtils.onActivityCreateSetTheme(this);

        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.info_layout);

        ButterKnife.inject(this);
        setTextViews();

        // Use custom Typeface for action bar title on KitKat devices
        if (Build.VERSION.SDK_INT == 19) {
            SpannableString s = new SpannableString(getResources().getString(R.string.information));
            s.setSpan(new TypefaceSpan(this, "Roboto-Medium.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Update the action bar title with the TypefaceSpan instance
            getSupportActionBar().setTitle(s);
        } else {
            getSupportActionBar().setTitle(R.string.information);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (sp.getBoolean("darkTheme", true)) {
            mKernelCardView.setCardBackgroundColor(getResources().getColor(R.color.card_dark_background));
            mDeviceCardView.setCardBackgroundColor(getResources().getColor(R.color.card_dark_background));
            mCpuCardView.setCardBackgroundColor(getResources().getColor(R.color.card_dark_background));
            if (Build.VERSION.SDK_INT == 19) {
                mMaterialRippleLayout.setRippleColor(getResources().getColor(R.color.ripple_material_dark));
            }
        } else {
            mKernelCardView.setCardBackgroundColor(getResources().getColor(R.color.card_light_background));
            mDeviceCardView.setCardBackgroundColor(getResources().getColor(R.color.card_light_background));
            mCpuCardView.setCardBackgroundColor(getResources().getColor(R.color.card_light_background));
            if (Build.VERSION.SDK_INT == 19) {
                mMaterialRippleLayout.setRippleColor(getResources().getColor(R.color.ripple_material_light));
            }
        }

        mKernelCardView = (CardView)findViewById(R.id.card_view_kernel);
        mKernelCardView.setOnClickListener(this);
    }

    /** Set text and fontface for TextViews */
    private void setTextViews() {

        // Loading Font Face
        final Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Medium.ttf");

        final String api = CPUUtils.getSystemProperty(API_LEVEL);
        final String platform = CPUUtils.getSystemProperty(BOARD_PLATFORM);
        final String frequency = CPUUtils.getMinFreq() + " - " + CPUUtils.getMaxFreq();

        final int getNumCores = Runtime.getRuntime().availableProcessors();

        mKernelVersion.setText(System.getProperty("os.version"));
        mKernelGovernor.setText(CPUUtils.getGovernor());
        mCpuAbi.setText(Build.CPU_ABI);
        mCpuArch.setText(CPUUtils.getArch());
        mCpuCore.setText(Integer.toString(getNumCores));
        mCpuFreq.setText(frequency);
        mCpuFeatures.setText(CPUUtils.getFeatures());
        mDeviceBuild.setText(Build.ID);
        mDeviceApi.setText(api);
        mDeviceManuf.setText(Build.MANUFACTURER);
        mDeviceModel.setText(Build.MODEL);
        mDeviceBoard.setText(Build.BOARD);
        mDevicePlatform.setText(platform);

        mKernelHeader.setTypeface(tf);
        mKernelGovernorHeader.setTypeface(tf);
        mKernelVersionHeader.setTypeface(tf);
        mCpuHeader.setTypeface(tf);
        mCpuAbiHeader.setTypeface(tf);
        mCpuArchHeader.setTypeface(tf);
        mCpuCoreHeader.setTypeface(tf);
        mCpuFreqHeader.setTypeface(tf);
        mCpuFeaturesHeader.setTypeface(tf);
        mDeviceInfo.setTypeface(tf);
        mDeviceBuildHeader.setTypeface(tf);
        mDeviceApiHeader.setTypeface(tf);
        mDeviceManufHeader.setTypeface(tf);
        mDeviceModelHeader.setTypeface(tf);
        mDeviceBoardHeader.setTypeface(tf);
        mDevicePlatformHeader.setTypeface(tf);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_view_kernel:
                CpuSpyApp _app = (CpuSpyApp) getApplicationContext();
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .content(_app.getKernelVersion())
                        .build();

                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogInfoAnimation;
                dialog.show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
