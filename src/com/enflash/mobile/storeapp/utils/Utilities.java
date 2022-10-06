package com.enflash.mobile.storeapp.utils;

import android.view.Gravity;
import android.widget.Toast;

import com.enflash.mobile.storeapp.application.App;

import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

public class Utilities {

    public void showToast(String msg) {
        Toast toast = Toast.makeText(App.getAppInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public boolean hasInternet() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String formatter(Double amount){
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.getDefault());

        return formatter.format("$%,.2f", amount).toString();

    }

}
