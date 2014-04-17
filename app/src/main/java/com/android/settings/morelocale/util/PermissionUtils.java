package com.android.settings.morelocale.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class PermissionUtils {

    private static final String PERMISSION_CHANGE_CONFIGURATION = "android.permission.CHANGE_CONFIGURATION";

    public static boolean checkPermission(Context context) {
        return (PackageManager.PERMISSION_GRANTED
                == context.getPackageManager().checkPermission(PERMISSION_CHANGE_CONFIGURATION,
                context.getPackageName()));
    }

}
