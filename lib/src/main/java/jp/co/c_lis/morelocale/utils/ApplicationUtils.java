package jp.co.c_lis.morelocale.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class ApplicationUtils {

    private static final String PERMISSION_CHANGE_CONFIGURATION = "android.permission.CHANGE_CONFIGURATION";

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    public static boolean checkPermission(Context context) {
        return (PackageManager.PERMISSION_GRANTED
                == context.getPackageManager().checkPermission(PERMISSION_CHANGE_CONFIGURATION,
                context.getPackageName()));
    }

}
