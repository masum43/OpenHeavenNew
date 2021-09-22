package com.newandromo.dev18147.app821162.service;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import com.newandromo.dev18147.app821162.BuildConfig;
import timber.log.Timber;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 * <p>
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 * <p>
 * <intent-filter>
 * <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_LARGE_ICON = "largeIcon";
    public static final String KEY_BIG_PICTURE = "bigPicture";
    public static final String KEY_LAUNCH_URL = "launchURL";
    public static final String KEY_CTA = "CTA";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        if (BuildConfig.DEBUG)
            Timber.tag(TAG).d("From: %s", remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            if (BuildConfig.DEBUG) {
                Timber.tag(TAG).d("Message Notification Title: %s", remoteMessage.getNotification().getTitle());
                Timber.tag(TAG).d("Message Notification Body: %s", remoteMessage.getNotification().getBody());
            }

            String title = remoteMessage.getNotification().getTitle();
            String content = remoteMessage.getNotification().getBody();
            String largeIcon = "";
            String bigPicture = "";
            String launchUrl = "";
            String cta = "";

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                if (BuildConfig.DEBUG)
                    Timber.tag(TAG).d("Message data payload: %s", remoteMessage.getData());

                Map<String, String> _data = remoteMessage.getData();
                largeIcon = _data.get(KEY_LARGE_ICON);
                bigPicture = _data.get(KEY_BIG_PICTURE);
                launchUrl = _data.get(KEY_LAUNCH_URL);
                cta = _data.get(KEY_CTA);

                if (BuildConfig.DEBUG) {
                    Timber.d("Message Notification largeIcon= %s", largeIcon);
                    Timber.d("Message Notification bigPicture= %s", bigPicture);
                    Timber.d("Message Notification launchUrl= %s", launchUrl);
                    Timber.d("Message Notification CTA= %s", cta);
                }
            }

            Data data = new Data.Builder()
                    .putString(KEY_TITLE, title)
                    .putString(KEY_CONTENT, content)
                    .putString(KEY_LARGE_ICON, largeIcon)
                    .putString(KEY_BIG_PICTURE, bigPicture)
                    .putString(KEY_LAUNCH_URL, launchUrl)
                    .putString(KEY_CTA, cta)
                    .build();

            OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInputData(data)
                    .build();
            WorkManager.getInstance(getApplicationContext()).beginWith(work).enqueue();
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(@NotNull String token) {
        if (BuildConfig.DEBUG) Timber.tag(TAG).d("Refreshed token: %s", token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(token);
    }
}
