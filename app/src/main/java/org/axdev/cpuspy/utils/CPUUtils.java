//-----------------------------------------------------------------------------
//
// (C) Rob Beane, 2015 <robbeane@gmail.com>
//
//-----------------------------------------------------------------------------

package org.axdev.cpuspy.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

public class CPUUtils {

    public static final String CPU0 = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
    public static final String CPU1 = "/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq";
    public static final String CPU2 = "/sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq";
    public static final String CPU3 = "/sys/devices/system/cpu/cpu3/cpufreq/scaling_cur_freq";
    public static final String CPU4 = "/sys/devices/system/cpu/cpu4/cpufreq/scaling_cur_freq";
    public static final String CPU5 = "/sys/devices/system/cpu/cpu5/cpufreq/scaling_cur_freq";
    public static final String CPU6 = "/sys/devices/system/cpu/cpu6/cpufreq/scaling_cur_freq";
    public static final String CPU7 = "/sys/devices/system/cpu/cpu7/cpufreq/scaling_cur_freq";

    private static final String TAG_INFO = "CpuSpyInfo";
    private static String mCpuInfo;
    private static String mFreq;
    private static String mString;
    private static String mTemp;
    private static String mTempFile;

    private static String readFile(String PATH) {
        try {
            final File file = new File(PATH);
            if (file.canRead()) {
                final BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null) {
                    mString = line;
                }

                br.close();
            } else {
                Log.e(TAG_INFO, "Error reading file: " + PATH);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return mString;
    }

    /** Set the current min/max CPU frequency */
    private static String setFreq(String PATH) {
        try {
            final File file = new File(PATH);
            if (file.canRead()) {
                final BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null) {
                    mFreq = line;
                }

                br.close();
            } else {
                Log.e(TAG_INFO, "Error reading file: " + PATH);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        final int i = Integer.parseInt(mFreq) / 1000;
        mFreq = Integer.toString(i) + "MHz";

        return mFreq;
    }

    /** Retrieves information for ARM CPUs. */
    private static String readCpuInfo(String INFO) {
        try {
            final File file = new File("/proc/cpuinfo");
            if (file.canRead()) {
                final BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains(INFO)) {
                        mCpuInfo = parseLine(line);
                    }
                }
                br.close();
            } else {
                Log.e(TAG_INFO, "Error reading from /proc/cpuinfo");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return mCpuInfo;
    }

    // Basic function for parsing cpuinfo format strings.
    // cpuinfo format strings consist of [label:info] parts.
    // We only want to retrieve the info portion so we split
    // them using ':' as a delimeter.
    private static String parseLine(String line)  {
        String[] temp = line.split(":");
        if (temp.length != 2) return "N/A";
        return temp[1].trim();
    }

    /** Retrieves the current CPU temperature */
    private static String setTemp() {
        try {
            final File file = new File(mTempFile);
            if (file.canRead()) {
                final BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null) {
                    mTemp = line;
                }

                br.close();
            } else {
                Log.e(TAG_INFO, "Error reading file: " + mTempFile);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        long temp = Long.parseLong(mTemp);
        if (temp > 1000) temp /= 1000;
        else if (temp > 200) temp /= 10;

        return ((double) temp) + "°C";
    }

    private static String[] tempFiles = {
            "/sys/devices/platform/omap/omap_temp_sensor.0/temperature",
            "/sys/kernel/debug/tegra_thermal/temp_tj",
            "/sys/devices/system/cpu/cpu0/cpufreq/cpu_temp",
            "/sys/class/thermal/thermal_zone0/temp",
            "/sys/class/thermal/thermal_zone1/temp",
            "/sys/devices/virtual/thermal/thermal_zone0/temp",
            "/sys/devices/virtual/thermal/thermal_zone1/temp",
            "/sys/devices/system/cpu/cpufreq/cput_attributes/cur_temp",
            "/sys/devices/platform/s5p-tmu/curr_temp",
            "/sys/devices/platform/s5p-tmu/temperature",
    };

    public static boolean hasTemp() {
        for (final String s : tempFiles) {
            final File file = new File(s);
            if (file.canRead() && file.length() != 0) mTempFile = s;
        }

        return mTempFile != null;
    }

    /**
     * Returns a SystemProperty
     *
     * @param propName The Property to retrieve
     * @return The Property, or NULL if not found
     */
    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader br = null;
        try {
            final Process p = Runtime.getRuntime().exec("getprop " + propName);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = br.readLine();
            br.close();
        }
        catch (Exception ex) {
            Log.e(TAG_INFO, "Unable to read sysprop " + propName, ex);
            return null;
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (Exception e) {
                    Log.e(TAG_INFO, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    /** @return the CPU0 string */
    public static String getCpu0() {
        if (setFreq(CPU0) != null) return setFreq(CPU0);
        return null;
    }

    /** @return the CPU1 string */
    public static String getCpu1() {
        if (setFreq(CPU1) != null) return setFreq(CPU1);
        return null;
    }

    /** @return the CPU2 string */
    public static String getCpu2() {
        if (setFreq(CPU2) != null) return setFreq(CPU2);
        return null;
    }

    /** @return the CPU3 string */
    public static String getCpu3() {
        if (setFreq(CPU3) != null) return setFreq(CPU3);
        return null;
    }

    /** @return the CPU4 string */
    public static String getCpu4() {
        if (setFreq(CPU4) != null) return setFreq(CPU4);
        return null;
    }

    /** @return the CPU5 string */
    public static String getCpu5() {
        if (setFreq(CPU5) != null) return setFreq(CPU5);
        return null;
    }

    /** @return the CPU6 string */
    public static String getCpu6() {
        if (setFreq(CPU6) != null) return setFreq(CPU6);
        return null;
    }

    /** @return the CPU7 string */
    public static String getCpu7() {
        if (setFreq(CPU7) != null) return setFreq(CPU7);
        return null;
    }

    /** @return CPU governor string */
    public static String getGovernor() {
        final String governor = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
        if (readFile(governor) != null) return readFile(governor);
        return null;
    }

    /** @return CPU min/max frequency string */
    public static String getMinMax() {
        final String minFreq = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";
        final String maxFreq = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
        if (setFreq(minFreq) != null && setFreq(maxFreq) != null) return setFreq(minFreq) + " - " + setFreq(maxFreq);
        return null;
    }

    /** @return CPU features string */
    public static String getFeatures() {
        if (readCpuInfo("Features\t:") != null) return readCpuInfo("Features\t:");
        return null;
    }

    /** @return CPU architecture string */
    public static String getArch() {
        if (readCpuInfo("Processor\t:") !=null) return readCpuInfo("Processor\t:");
        return null;
    }

    /** @return the kernel version string */
    public static String getKernelVersion() {
        final String kernelVersion = "/proc/version";
        if (readFile(kernelVersion) != null) return readFile(kernelVersion);
        return null;
    }

    /** @return CPU temperature string */
    public static String getTemp() {
        if (setTemp() != null) return setTemp();
        return null;
    }

    /** @return Number of CPU cores */
    public static int getCoreCount() {
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        if (availableProcessors != 0) return availableProcessors;
        return 0;
    }
}