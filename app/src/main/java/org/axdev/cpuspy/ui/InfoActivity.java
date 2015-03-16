package org.axdev.cpuspy.ui;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.axdev.cpuspy.CpuSpyApp;
import org.axdev.cpuspy.R;

public class InfoActivity extends ActionBarActivity implements OnClickListener {

    private final String API_LEVEL = "ro.build.version.sdk";
    private final String BOARD_PLATFORM = "ro.board.platform";
    private final String GOVERNOR = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
    private final String MIN_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";
    private final String MAX_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";

    private String mArch;
    private String mFeatures;
    private String mGovernor;
    private String mMinFreq;
    private String mMaxFreq;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);
        setTextViews();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView mKernelCardView = (CardView)findViewById(R.id.card_view_kernel);
        mKernelCardView.setOnClickListener(this);
    }

    /** Set text and fontface for TextViews */
    private void setTextViews() {

        // Loading Font Face
        final Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Medium.ttf");

        final TextView mKernelHeader = (TextView)findViewById(R.id.kernel_header);
        final TextView mKernelGovernorHeader = (TextView)findViewById(R.id.kernel_governor_header);
        final TextView mKernelGovernor = (TextView)findViewById(R.id.kernel_governor);
        final TextView mKernelVersionHeader = (TextView)findViewById(R.id.kernel_version_header);
        final TextView mKernelVersion = (TextView)findViewById(R.id.kernel_version);
        final TextView mCpuHeader = (TextView)findViewById(R.id.cpu_header);
        final TextView mCpuAbiHeader = (TextView)findViewById(R.id.cpu_abi_header);
        final TextView mCpuAbi = (TextView)findViewById(R.id.cpu_abi);
        final TextView mCpuArchHeader = (TextView)findViewById(R.id.cpu_arch_header);
        final TextView mCpuArch = (TextView)findViewById(R.id.cpu_arch);
        final TextView mCpuCoreHeader = (TextView)findViewById(R.id.cpu_core_header);
        final TextView mCpuCore = (TextView)findViewById(R.id.cpu_core);
        final TextView mCpuFreqHeader = (TextView)findViewById(R.id.cpu_freq_header);
        final TextView mCpuFreq = (TextView)findViewById(R.id.cpu_freq);
        final TextView mCpuFeaturesHeader = (TextView)findViewById(R.id.cpu_features_header);
        final TextView mCpuFeatures = (TextView)findViewById(R.id.cpu_features);
        final TextView mDeviceInfo = (TextView)findViewById(R.id.device_header);
        final TextView mDeviceBuildHeader = (TextView)findViewById(R.id.device_build_header);
        final TextView mDeviceBuild = (TextView)findViewById(R.id.device_build);
        final TextView mDeviceApiHeader = (TextView)findViewById(R.id.device_api_header);
        final TextView mDeviceApi = (TextView)findViewById(R.id.device_api);
        final TextView mDeviceManufHeader = (TextView)findViewById(R.id.device_manuf_header);
        final TextView mDeviceManuf = (TextView)findViewById(R.id.device_manuf);
        final TextView mDeviceModelHeader = (TextView)findViewById(R.id.device_model_header);
        final TextView mDeviceModel = (TextView)findViewById(R.id.device_model);
        final TextView mDeviceBoardHeader = (TextView)findViewById(R.id.device_board_header);
        final TextView mDeviceBoard = (TextView)findViewById(R.id.device_board);
        final TextView mDevicePlatformHeader = (TextView)findViewById(R.id.device_platform_header);
        final TextView mDevicePlatform = (TextView)findViewById(R.id.device_platform);

        final String api = getSystemProperty(API_LEVEL);
        final String platform = getSystemProperty(BOARD_PLATFORM);
        final String frequency = getMinFreq() + " - " + getMaxFreq();

        final int getNumCores = Runtime.getRuntime().availableProcessors();

        mKernelVersion.setText(System.getProperty("os.version"));
        mKernelGovernor.setText(getGovernor());
        mCpuAbi.setText(Build.CPU_ABI);
        mCpuArch.setText(getArch());
        mCpuCore.setText(Integer.toString(getNumCores));
        mCpuFreq.setText(frequency);
        mCpuFeatures.setText(getFeatures());
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
    /**
     * Returns a SystemProperty
     *
     * @param propName The Property to retrieve
     * @return The Property, or NULL if not found
     */
    private String getSystemProperty(String propName) {
        String TAG = "CPUSpyInfo";
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        }
        catch (IOException ex) {
            Log.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    /** Get the current cpu governor */
    private String getGovernor() {
        try {
            InputStream is = new FileInputStream(GOVERNOR);
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);

            String line;
            while ((line = br.readLine())!= null ) {
                mGovernor = line;
            }

            is.close();
        } catch (IOException ignored) {}

        // made it
        return mGovernor;
    }

    /** Get the current minimum frequency */
    private String getMinFreq() {
        try {
            InputStream is = new FileInputStream(MIN_FREQ);
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);

            String line;
            while ((line = br.readLine())!= null ) {
                mMinFreq = line;
            }

            is.close();
        } catch (IOException ignored) {}

        int i = Integer.parseInt(mMinFreq) / 1000;
        mMinFreq = Integer.toString(i) + "MHz";

        // made it
        return mMinFreq;
    }

    /** Get the current maximum frequency */
    private String getMaxFreq() {
        try {
            InputStream is = new FileInputStream(MAX_FREQ);
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);

            String line;
            while ((line = br.readLine())!= null ) {
                mMaxFreq = line;
            }

            is.close();
        } catch (IOException ignored) {}

        int i = Integer.parseInt(mMaxFreq) / 1000;
        mMaxFreq = Integer.toString(i) + "MHz";

        // made it
        return mMaxFreq;
    }

    /** Retrieves information for ARM CPUs. */
    private String getFeatures() {
        try {
            File info = new File("/proc/cpuinfo");
            if (info.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(info));

                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("Features\t:")) {
                        mFeatures = parseLine(line);
                    }
                }
                br.close();
            }
        } catch (IOException ignored) {}
        return mFeatures;
    }

    private String getArch() {
        try {
            File info = new File("/proc/cpuinfo");
            if (info.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(info));

                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("Processor\t:")) {
                        mArch = parseLine(line);
                    }
                }
                br.close();
            }
        } catch (IOException ignored) {}
        return mArch;
    }

    // Basic function for parsing cpuinfo format strings.
    // cpuinfo format strings consist of [label:info] parts.
    // We only want to retrieve the info portion so we split
    // them using ':' as a delimeter.
    private String parseLine(String line)  {
        String[] temp = line.split(":");
        if (temp.length != 2)
            return "N/A";

        return temp[1].trim();
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
