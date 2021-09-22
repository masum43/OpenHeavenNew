package com.newandromo.dev18147.app821162.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import java.util.ArrayList;
import java.util.List;

import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.SplashScreen;
import com.newandromo.dev18147.app821162.db.entity.EntryEntity;
import com.newandromo.dev18147.app821162.receiver.ActionReceiver;
import com.newandromo.dev18147.app821162.ui.activities.EntryDetailActivity;
import com.newandromo.dev18147.app821162.ui.enums.Theme;

import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_BIG_PICTURE;
import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_CONTENT;
import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_CTA;
import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_LARGE_ICON;
import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_LAUNCH_URL;
import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_TITLE;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ENTRY_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ENTRY_IMAGE_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ENTRY_TITLE;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ENTRY_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_GO_HOME;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_SINGLE_LAYOUT;

public class NotificationUtil extends ContextWrapper {
    public static final int NEW_FCM_NOTIFICATION_ID = 777;
    public static final int REQ_NEW_FCM_NOTIFICATION_ID = 888;
    public static final int NEW_POSTS_NOTIFICATION_ID = 1001;
    public static final int NEW_POSTS_BUNDLE_NOTIFICATION_ID = 1002;
    public static final int REQ_NEW_POSTS_NOTIFICATION = 1003;
    public static final int REQ_NEW_POSTS_BUNDLE_NOTIFICATION = 1004;
    public static final String NEW_POSTS_CHANNEL_DEFAULT = "new_posts_channel_default";
    public static final String NEW_POSTS_CHANNEL_HIGH = "new_posts_channel_high";
    public static final String KEY_NOTIFICATION_ID = "notificationId";
    private Context mContext;
    private NotificationManager manager;
    private NotificationManagerCompat managerCompat;

    public NotificationUtil(Context context) {
        super(context);
        mContext = context;
        if (AppUtils.hasOreo()) {
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build();

            NotificationChannel channel1 = new NotificationChannel(NEW_POSTS_CHANNEL_DEFAULT,
                    getString(R.string.notification_channel_new_posts_default), NotificationManager.IMPORTANCE_DEFAULT);
            channel1.enableLights(true);
            channel1.setLightColor(getThemeColor());
            channel1.enableVibration(RemoteConfig.isEnableNotificationVibration());
            if (RemoteConfig.isEnableNotificationSound()) {
                channel1.setSound(sound, audioAttributes);
            } else channel1.setSound(null, null);
            // channel1.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(channel1);

            NotificationChannel channel2 = new NotificationChannel(NEW_POSTS_CHANNEL_HIGH,
                    getString(R.string.notification_channel_new_posts_high), NotificationManager.IMPORTANCE_HIGH);
            channel2.enableLights(true);
            channel2.setLightColor(getThemeColor());
            channel2.enableVibration(RemoteConfig.isEnableNotificationVibration());
            if (RemoteConfig.isEnableNotificationSound()) {
                channel2.setSound(sound, audioAttributes);
            } else channel2.setSound(null, null);
            // channel2.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(channel2);

            NotificationChannel channel3 = new NotificationChannel(
                    getString(R.string.notification_channel_fcm_default),
                    getString(R.string.notification_channel_push_name),
                    NotificationManager.IMPORTANCE_HIGH);
            channel3.enableLights(true);
            channel3.setLightColor(getThemeColor());
            channel3.enableVibration(true);
            channel3.setSound(sound, audioAttributes);
            channel3.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(channel3);

            NotificationChannel channel4 = new NotificationChannel(
                    getString(R.string.notification_channel_one_signal_silent),
                    getString(R.string.notification_channel_push_name),
                    NotificationManager.IMPORTANCE_NONE);
            channel4.enableLights(false);
            channel4.setSound(null, null);
            channel4.enableVibration(false);
            getManager().createNotificationChannel(channel4);
        }
    }

    public void createNewFCMNotification(Data data) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext,
                getString(R.string.notification_channel_fcm_default));
        defaultNotificationValues(builder);
        createNewFCMNotification(builder, data);
    }

    public void createNewFCMNotification(NotificationCompat.Builder builder, Data data) {
        String title = data.getString(KEY_TITLE);
        String content = data.getString(KEY_CONTENT);
        String largeIcon = data.getString(KEY_LARGE_ICON);
        String bigPicture = data.getString(KEY_BIG_PICTURE);
        String launchUrl = data.getString(KEY_LAUNCH_URL);
        String cta = data.getString(KEY_CTA);

        Bitmap bitMapLargeIcon = null;
        Bitmap bitMapPicture = null;

        try {
            bitMapLargeIcon = Picasso.get().load(largeIcon).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            bitMapPicture = Picasso.get().load(bigPicture).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.setContentTitle(title);

        if (!TextUtils.isEmpty(content))
            builder.setContentText(content);

        if (bitMapLargeIcon != null)
            builder.setLargeIcon(bitMapLargeIcon);

        if (bitMapPicture != null) {
            builder.setLargeIcon(bitMapLargeIcon != null ? bitMapLargeIcon : bitMapPicture);
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigPicture(bitMapPicture);
            bigPictureStyle.bigLargeIcon(null);
            if (!TextUtils.isEmpty(content))
                bigPictureStyle.setSummaryText(content);

            builder.setStyle(bigPictureStyle);
        } else {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(title);
            String styledText = String.format("<b>%s</b><br>%s", title, content);

            if (AppUtils.hasNougat()) {
                styledText = String.valueOf(Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY));
            } else {
                styledText = String.valueOf(Html.fromHtml(styledText));
            }

            if (!TextUtils.isEmpty(styledText)) {
                bigTextStyle.bigText(styledText);
                bigTextStyle.setSummaryText(styledText);
            }
            builder.setStyle(bigTextStyle);
        }

        if (!TextUtils.isEmpty(launchUrl)) {
            Intent intent = new Intent(mContext, ActionReceiver.class);
            intent.setAction(ActionReceiver.ACTION_CTA);
            intent.putExtra(KEY_LAUNCH_URL, launchUrl);
            intent.putExtra(KEY_NOTIFICATION_ID, NEW_FCM_NOTIFICATION_ID);
            PendingIntent pendingIntent = PendingIntent
                    .getBroadcast(mContext, REQ_NEW_FCM_NOTIFICATION_ID, intent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

            if (!TextUtils.isEmpty(cta)) {
                builder.addAction(0, cta, pendingIntent);
            }

            builder.setContentIntent(pendingIntent);
            notify(NEW_FCM_NOTIFICATION_ID, builder.build());
        }
    }

    public void createNewPostsNotification(List<EntryEntity> entries) {
        if (entries == null || entries.isEmpty()) return;

        NotificationCompat.Builder builder;
        int newCount = entries.size();

        if (AppUtils.hasOreo()) {
            if (newCount == 1) {
                builder = new NotificationCompat.Builder(mContext,
                        RemoteConfig.isShowHeadsUpNotification() ?
                                NEW_POSTS_CHANNEL_HIGH : NEW_POSTS_CHANNEL_DEFAULT);
            } else {
                builder = new NotificationCompat.Builder(mContext,
                        RemoteConfig.isShowHeadsUpNotificationBundle() ?
                                NEW_POSTS_CHANNEL_HIGH : NEW_POSTS_CHANNEL_DEFAULT);
            }
        } else {
            builder = new NotificationCompat.Builder(mContext, NEW_POSTS_CHANNEL_DEFAULT);

            if (newCount == 1) {
                builder.setPriority(RemoteConfig.isShowHeadsUpNotification() ?
                        NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT);
            } else {
                builder.setPriority(RemoteConfig.isShowHeadsUpNotificationBundle() ?
                        NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT);
            }
        }

        defaultNotificationValues(builder);
        createNewPostsNotification(builder, entries);
    }

    private void createNewPostsNotification(NotificationCompat.Builder builder, List<EntryEntity> entries) {
        int newCount = entries.size();
        if (newCount == 1) { // todo exactly 1 post
            EntryEntity entry = entries.get(0);

            String contentTitle = getResources()
                    .getQuantityString(R.plurals.notification_new_posts, newCount, newCount);

            String title = Jsoup.parse(entry.getTitle(), "", Parser.htmlParser()).text();

            String excerpt = Jsoup.parse(entry.getExcerpt(), "", Parser.htmlParser()).text();
            String thumbnail = entry.getThumbUrl();

            if (AppUtils.hasNougat()) {
                excerpt = String.valueOf(Html.fromHtml(excerpt, Html.FROM_HTML_MODE_LEGACY));
            } else {
                excerpt = String.valueOf(Html.fromHtml(excerpt));
            }

            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thumbnail)) {
                Bitmap bitmap = null;
                try {
                    bitmap = Picasso.get().load(thumbnail).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (bitmap != null) {
                    if (RemoteConfig.isShowCustomNotification()) {
                        showCustomNotification(builder, bitmap, title);
                    } else {
                        showBigPictureStyle(builder, bitmap, title, excerpt);
                    }

                    notifyToEntryDetail(builder, entry);
                } else {
                    // builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                    builder.setContentTitle(contentTitle);
                    notifyToHome(builder);
                }
            } else {
                // builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                builder.setContentTitle(contentTitle);
                notifyToHome(builder);
            }

        } else { // todo more than 1 post

            String contentTitle = getResources()
                    .getQuantityString(R.plurals.notification_new_posts, newCount, newCount);
            builder.setContentTitle(contentTitle);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            List<String> entryTitles = new ArrayList<>();
            for (int i = 0; i < newCount; i++) {
                String entryTitle = entries.get(i).getTitle();
                if (!TextUtils.isEmpty(entryTitle)) {
                    try {
                        entryTitle = Jsoup.parse(entryTitle, "", Parser.htmlParser()).text();
                    } catch (Exception ignore) {
                    }
                    inboxStyle.addLine(entryTitle);
                    entryTitles.add(entryTitle);
                }
            }

            if (newCount > 7) {
                int reminder = newCount - 7;
                inboxStyle.setSummaryText(getString(R.string.more_plus, reminder));
            }

            if (!entryTitles.isEmpty())
                builder.setContentText(TextUtils.join(", ", entryTitles));

            builder.setStyle(inboxStyle);  // set inbox style

            notifyToHome(builder);
        }
    }

    private void showBigPictureStyle(NotificationCompat.Builder builder, Bitmap bitmap, String title, String excerpt) {
        builder.setLargeIcon(bitmap);
        builder.setContentTitle(title);
        if (!TextUtils.isEmpty(excerpt))
            builder.setContentText(excerpt);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigPicture(bitmap);
        bigPictureStyle.bigLargeIcon(null);
        if (!TextUtils.isEmpty(excerpt))
            bigPictureStyle.setSummaryText(excerpt);

        builder.setStyle(bigPictureStyle); // set big picture style
    }

    private void showCustomNotification(NotificationCompat.Builder builder, Bitmap bitmap, String title) {
        RemoteViews notificationSmall = new RemoteViews(getPackageName(), R.layout.notification_small);
        RemoteViews notificationLarge = new RemoteViews(getPackageName(), R.layout.notification_large);

        notificationSmall.setImageViewBitmap(R.id.thumb, bitmap);
        notificationLarge.setImageViewBitmap(R.id.thumb, bitmap);

        notificationSmall.setTextViewText(R.id.title, title);
        notificationLarge.setTextViewText(R.id.title, title);

        builder.setCustomContentView(notificationSmall);
        builder.setCustomBigContentView(notificationLarge);
    }

    private void notifyToHome(NotificationCompat.Builder builder) {
        Intent intent = new Intent(mContext, SplashScreen.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                REQ_NEW_POSTS_BUNDLE_NOTIFICATION, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);

        notify(NEW_POSTS_BUNDLE_NOTIFICATION_ID, builder.build());
    }

    private void notifyToEntryDetail(NotificationCompat.Builder builder, EntryEntity entry) {
        Intent intent = new Intent(mContext, EntryDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INTENT_EXTRA_ENTRY_ID, entry.getId());
        intent.putExtra(INTENT_EXTRA_ENTRY_TITLE, entry.getTitle());
        intent.putExtra(INTENT_EXTRA_ENTRY_IMAGE_URL, entry.getThumbUrl());
        intent.putExtra(INTENT_EXTRA_ENTRY_URL, entry.getUrl());
        intent.putExtra(INTENT_EXTRA_IS_SINGLE_LAYOUT, true);
        intent.putExtra(INTENT_EXTRA_IS_GO_HOME, true);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                REQ_NEW_POSTS_NOTIFICATION, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(contentIntent);

        notify(NEW_POSTS_NOTIFICATION_ID, builder.build());
    }

    private void defaultNotificationValues(NotificationCompat.Builder builder) {
        // builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setWhen(System.currentTimeMillis());
        builder.setColor(getThemeColor());
        builder.setAutoCancel(true);
    }

    public int getThemeColor() {
        int notificationColor = Color.RED;
        try {
            int accentColorId = Theme.valueOf(PrefUtils.getAppTheme(mContext)).getAccentColorId();
            notificationColor = ContextCompat.getColor(mContext, accentColorId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notificationColor;
    }

    private void notify(int id, Notification notification) {
        getManagerCompat().notify(id, notification);
    }

    public void cancelNotification(int id) {
        getManagerCompat().cancel(id);
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    private NotificationManagerCompat getManagerCompat() {
        if (managerCompat == null) {
            managerCompat = NotificationManagerCompat.from(this);
        }
        return managerCompat;
    }
}
