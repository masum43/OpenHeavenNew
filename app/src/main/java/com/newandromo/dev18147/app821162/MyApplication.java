package com.newandromo.dev18147.app821162;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import androidx.multidex.MultiDexApplication;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.facebook.ads.AudienceNetworkAds;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSNotificationPayload;
import com.onesignal.OneSignal;

import java.util.List;

import com.newandromo.dev18147.app821162.db.AppDatabase;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.service.FeedPeriodicSyncWorker;
import com.newandromo.dev18147.app821162.service.NotificationWorker;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_BIG_PICTURE;
import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_CONTENT;
import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_CTA;
import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_LARGE_ICON;
import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_LAUNCH_URL;
import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_TITLE;

/**
 * Android Application class. Used for accessing singletons.
 */
public class MyApplication extends MultiDexApplication {
    private AppExecutors mExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        AudienceNetworkAds.initialize(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

       // AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver();
        //ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);

        mExecutors = new AppExecutors();

        SharedPreferences preferences = getSharedPreferences("DIALOG", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(preferences.getInt("count",1) == 2){
            editor.putInt("count",1);
            editor.apply();
        }else{
            editor.putInt("count",preferences.getInt("count",0)+1);
            editor.apply();
        }


        // OneSignal Initialization
        // Logging set to help debug issues, remove before releasing your app.
        if (BuildConfig.DEBUG) {
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.WARN);
        }

        OneSignal.startInit(this)
                .autoPromptLocation(false)
                .setNotificationReceivedHandler(new MyNotificationReceivedHandler())
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        try {
            AnalyticsTrackers.initialize();
            AnalyticsTrackers.getInstance().get(this, AnalyticsTrackers.Target.APP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }

    private class MyNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            // JSONObject data = notification.payload.additionalData;
            String notificationID = notification.payload.notificationID;
            String title = notification.payload.title;
            String body = notification.payload.body;
            String smallIcon = notification.payload.smallIcon;
            String largeIcon = notification.payload.largeIcon;
            String bigPicture = notification.payload.bigPicture;
            String launchUrl = notification.payload.launchURL;
            // String smallIconAccentColor = notification.payload.smallIconAccentColor;
            // String sound = notification.payload.sound;
            // String ledColor = notification.payload.ledColor;
            // int lockScreenVisibility = notification.payload.lockScreenVisibility;
            // String groupKey = notification.payload.groupKey;
            // String groupMessage = notification.payload.groupMessage;
            // String fromProjectNumber = notification.payload.fromProjectNumber;
            // String rawPayload = notification.payload.rawPayload;

            if (BuildConfig.DEBUG) {
                Timber.i("OneSignal NotificationID received: %s", notificationID);
                Timber.i("OneSignal title: %s", title);
                Timber.i("OneSignal body: %s", body);
                Timber.i("OneSignal smallIcon: %s", smallIcon);
                Timber.i("OneSignal largeIcon: %s", largeIcon);
                Timber.i("OneSignal bigPicture: %s", bigPicture);
                Timber.i("OneSignal launchUrl: %s", launchUrl);
            }

            List<OSNotificationPayload.ActionButton> buttons = notification.payload.actionButtons;

            if (buttons != null && buttons.size() > 0) {
                String cta = buttons.get(0).text;

                Data workData = new Data.Builder()
                        .putString(KEY_TITLE, title)
                        .putString(KEY_CONTENT, body)
                        .putString(KEY_LARGE_ICON, largeIcon)
                        .putString(KEY_BIG_PICTURE, bigPicture)
                        .putString(KEY_LAUNCH_URL, launchUrl)
                        .putString(KEY_CTA, cta)
                        .build();

                OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                        .setInputData(workData)
                        .build();
                WorkManager.getInstance(getApplicationContext()).beginWith(work).enqueue();
            } else {
                FeedPeriodicSyncWorker.scheduleOneTimeWork(getApplicationContext());
            }
        }
    }

    private class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            String launchUrl = result.notification.payload.launchURL;

            try {
                if (!TextUtils.isEmpty(launchUrl))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(launchUrl)));
                else {
                    Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }

            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
            // if you are calling startActivity above.
        /*
           <application ...>
             <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
           </application>
        */
        }
    }
}
