package id.ismail.dokterapps.lib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.NotificationManagerCompat;

public class NotificationPermissionHandler {
    public static void checkNotificationPermission(Context context) {
        if (!isNotificationEnabled(context)) {
            showNotificationPermissionDialog(context);
        }
    }

    private static boolean isNotificationEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    private static void showNotificationPermissionDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Enable Notifications")
                .setMessage("To receive important updates, enable notifications for this app.")
                .setPositiveButton("Enable", (dialog, which) -> openAppSystemSettings(context))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static void openAppSystemSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        context.startActivity(intent);
    }
}
