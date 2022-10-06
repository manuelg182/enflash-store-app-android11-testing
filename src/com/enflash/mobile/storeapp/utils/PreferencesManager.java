package com.enflash.mobile.storeapp.utils;

import android.app.Application;
import android.content.SharedPreferences;

import com.enflash.mobile.storeapp.application.App;


public class PreferencesManager {

    private static final String CONFIGURATION_STORE = "configurationStore";
    private static final String CONFIG_STORE_OPERATION_STATUS = "configStoreOperationStatus";
    private static final String CONFIG_STORE_AUTO_ACCEPT = "configStoreAutoAccept";
    private static final String CONFIG_STORE_SATURATED = "configStoreSaturated";
    private static final String PATH_FILE = "pathFile";
    private static final String LOGO_BANNER = "logoBanner";
    private static final String COMPANY_ID = "companyId";
    private static final String TIME_TO_REJECT = "timeToReject";


    public static void setCompanyId(Long companyId) {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = generalPrefs.edit();
        prefsEditor.putLong(COMPANY_ID, companyId);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public static Long getCompanyId() {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        Long companyId = generalPrefs.getLong(COMPANY_ID, 0L);
        return companyId;
    }

    public static void setLogoBanner(String logoBanner) {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = generalPrefs.edit();
        prefsEditor.putString(LOGO_BANNER, logoBanner);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public static String getLogoBanner() {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        String companyId = generalPrefs.getString(LOGO_BANNER, "");
        return companyId;
    }

    public static void setConfigStoreOperationStatus(Boolean status) {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = generalPrefs.edit();
        prefsEditor.putBoolean(CONFIG_STORE_OPERATION_STATUS, status);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public static Boolean getConfigStoreOperationStatus() {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        Boolean status = generalPrefs.getBoolean(CONFIG_STORE_OPERATION_STATUS, false);
        return status;
    }

    public static void setPath(String ip) {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = generalPrefs.edit();
        prefsEditor.putString(PATH_FILE, ip);
        prefsEditor.apply();
    }

    public static String getPath() {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        return generalPrefs.getString(PATH_FILE, "");
    }

    public static void setConfigStoreAutoAccept(Boolean status) {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = generalPrefs.edit();
        prefsEditor.putBoolean(CONFIG_STORE_AUTO_ACCEPT, status);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public static Boolean getConfigStoreAutoAccept() {
//        SharedPreferences generalPrefs = App.getAppInstance()
//                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
//        Boolean status = generalPrefs.getBoolean(CONFIG_STORE_AUTO_ACCEPT, false);
        return false;
    }

    public static void setConfigStoreSaturated(Boolean status) {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = generalPrefs.edit();
        prefsEditor.putBoolean(CONFIG_STORE_SATURATED, status);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public static Boolean getConfigStoreSaturated() {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        Boolean status = generalPrefs.getBoolean(CONFIG_STORE_SATURATED, false);
        return status;
    }

    public static void setTimeToReject(Long time) {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = generalPrefs.edit();
        prefsEditor.putLong(TIME_TO_REJECT, time);
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public static Long getTimeToReject() {
        SharedPreferences generalPrefs = App.getAppInstance()
                .getSharedPreferences(CONFIGURATION_STORE, Application.MODE_PRIVATE);
        Long time = generalPrefs.getLong(TIME_TO_REJECT, 12L);
        return time;
    }
}
