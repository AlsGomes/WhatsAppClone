package com.example.whatsappclone.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {

    public static boolean checkPermission(String[] necessaryPermissions, Activity activity, int requestCode) {
        System.out.println(Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT > 23) {
            List<String> notPermittedList = new ArrayList<>();
            for (String permission : necessaryPermissions) {
                boolean permitted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if (!permitted) {
                    notPermittedList.add(permission);
                }
            }

            if (!notPermittedList.isEmpty()) {
                String[] a = new String[notPermittedList.size()];
                ActivityCompat.requestPermissions(activity, notPermittedList.toArray(a), requestCode);
            }
        }

        return true;
    }
}
