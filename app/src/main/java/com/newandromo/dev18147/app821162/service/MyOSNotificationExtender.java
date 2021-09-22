package com.newandromo.dev18147.app821162.service;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.OneSignal;

import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.utils.NotificationUtil;

public class MyOSNotificationExtender extends NotificationExtenderService {
    // private static final String TAG = MyOSNotificationExtender.class.getSimpleName();

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        // Read Properties from result
        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = builder -> {
            NotificationUtil notificationUtil = new NotificationUtil(getApplicationContext());
            // Sets the background notification color to Red on Android 5.0+ devices.
            return builder
                    .setChannelId(getString(R.string.notification_channel_one_signal_silent))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(notificationUtil.getThemeColor());
        };

        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
        OneSignal.cancelNotification(displayedResult.androidNotificationId);

        // Return true to stop the notification from displaying
        return true;
    }
}
