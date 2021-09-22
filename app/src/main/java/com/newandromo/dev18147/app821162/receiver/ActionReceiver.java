package com.newandromo.dev18147.app821162.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.utils.NotificationUtil;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_LAUNCH_URL;
import static com.newandromo.dev18147.app821162.utils.NotificationUtil.KEY_NOTIFICATION_ID;

public class ActionReceiver extends BroadcastReceiver {
    public static final String ACTION_CTA = "feed.reader.app.service.CTA";

    @Override
    public void onReceive(Context context, Intent intent) {
        // if (BuildConfig.DEBUG) Timber.d("onHandleIntent(): %s", intent);
        try {
            if (intent != null && intent.getExtras() != null) {
                String action = intent.getAction();
                if (ACTION_CTA.equals(action)) {
                    String launchUrl = intent.getStringExtra(KEY_LAUNCH_URL);
                    int notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, 0);

                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(launchUrl)));

                    new NotificationUtil(context).cancelNotification(notificationId);
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Timber.e("onReceive() error= %s", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
