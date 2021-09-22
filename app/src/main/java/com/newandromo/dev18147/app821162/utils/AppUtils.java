package com.newandromo.dev18147.app821162.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;


import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.ui.activities.SettingsActivity;
import com.newandromo.dev18147.app821162.ui.activities.YoutubePlayerActivity;
import com.newandromo.dev18147.app821162.ui.activities.YoutubeSearchActivity;
import com.newandromo.dev18147.app821162.ui.enums.Theme;
import com.newandromo.dev18147.app821162.ui.fragments.AppInviteFragment;
import com.newandromo.dev18147.app821162.ui.fragments.ContentShareFragment;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_SHARE_BODY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_SHARE_IMAGE;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_SHARE_SUBJECT;
import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.YOUTUBE_WATCH_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.UTF8;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.VENDOR_YOUTUBE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.YOUTUBE_PACKAGE_NAME;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_START_EXTERNAL_PLAYER;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_START_STANDALONE_PLAYER;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_WRITE_EXTERNAL_STORAGE;
import static com.newandromo.dev18147.app821162.utils.NotificationUtil.NEW_POSTS_BUNDLE_NOTIFICATION_ID;
import static com.newandromo.dev18147.app821162.utils.NotificationUtil.NEW_POSTS_NOTIFICATION_ID;

public class AppUtils {
    // private static final String CHARSET = "charset=";
    private static final long[] NUMBERS = {1000L, 1000000L, 1000000000L, 1000000000000L};
    private static final String YOUTUBE_THUMBNAIL = "http://img.youtube.com/vi/%s/0.jpg";
    private static final String TAG_THUMBNAILS = "thumbnails";
    private static final String TAG_MAXRES = "maxres";
    private static final String TAG_STANDARD = "standard";
    private static final String TAG_HIGH = "high";
    private static final int MAX_YOUTUBE_RESULTS = 20;
    private static final String BUNDLE_SHARE_CUSTOM_URI = "shareCustomUri";
    private static final String BUNDLE_SHARE_CUSTOM_SCHEME = "shareCustomScheme";

    private AppUtils() {
    } // No instantiation

    public static void setAppTheme(Context context, boolean isDrawer) {
        try {
            int navigationTheme = Theme.valueOf(PrefUtils.getAppTheme(context)).getNavigationThemeId();
            int theme = Theme.valueOf(PrefUtils.getAppTheme(context)).getThemeId();
            context.setTheme(isDrawer ? navigationTheme : theme);
        } catch (Exception e) {
            // context.setTheme(isDrawer ? R.style.Nav_AppTheme_Light : R.style.AppTheme_Light);
            e.printStackTrace();
        }
    }

    public static boolean isDarkTheme(Context context) {
        return PrefUtils.getAppTheme(context).equals(Theme.DARK.name())
                || PrefUtils.getAppTheme(context).equals(Theme.NIGHT.name());
    }

    public static boolean isLightTheme(Context context) {
        return PrefUtils.getAppTheme(context).equals(Theme.LIGHT.name())
                || PrefUtils.getAppTheme(context).equals(Theme.FLAMINGO.name())
                || PrefUtils.getAppTheme(context).equals(Theme.LIME.name())
                || PrefUtils.getAppTheme(context).equals(Theme.BANANA.name())
                || PrefUtils.getAppTheme(context).equals(Theme.ORANGE.name());
    }

    public static int getPlaceholderColor(Context context) {
        String theme = PrefUtils.getAppTheme(context);
        try {
            if (theme.equals(Theme.NIGHT.name()))
                return R.color.image_placeholder_night;
            else if (theme.equals(Theme.DARK.name()))
                return R.color.image_placeholder_dark;
            else
                return R.color.image_placeholder;
        } catch (Exception e) {
            e.printStackTrace();
            return R.color.image_placeholder;
        }
    }

    public static int getSwipeRefreshColor(Context context, boolean isProgress) {
        String theme = PrefUtils.getAppTheme(context);
        if (isProgress) {
            try {
                if (theme.equals(Theme.LIGHT.name()))
                    return android.R.color.white;
                else if (isLightTheme(context)
                        || isDarkTheme(context)
                        || theme.equals(Theme.GREEN.name())
                        || theme.equals(Theme.LIGHT_GREEN.name()))
                    return R.color.grey_deepen;
                else
                    return android.R.color.white;
            } catch (Exception e) {
                e.printStackTrace();
                return android.R.color.white;
            }
        } else {
            try {
                return Theme.valueOf(theme).getAccentColorId();
            } catch (Exception e) {
                e.printStackTrace();
                return android.R.color.black;
            }
        }
    }

    private static boolean canResolveIntent(Intent intent, Activity activity) {
        List<ResolveInfo> resolveInfo = activity.getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    private static boolean isYoutubeAppInstalled(Context context) {
        try {
            return YouTubeIntents.getInstalledYouTubeVersionName(context) != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void openYouTube(Activity activity, @NonNull String videoId, int playerOption) {
        try {
            if (RemoteConfig.isOverrideYoutubePlayer()) {
                externalYouTube(activity, videoId);
            } else {
                // int playerOption = Integer.parseInt(PrefUtils.getYouTubePlayerOption(activity));
                switch (playerOption) {
                    case 1:
                        Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                                activity, RemoteConfig.getAndroidDeveloperApiKey(), videoId, 0, true, true);
                        if (intent != null
                                && canResolveIntent(intent, activity)
                                && isYoutubeAppInstalled(activity)) {
                            activity.startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
                        } else {
                            externalYouTube(activity, videoId);
                        }
                        break;
                    case 2:
                        externalYouTube(activity, videoId);
                        break;
                    default:
                        if (isYoutubeAppInstalled(activity)) {
                            inAppYouTube(activity, videoId);
                        } else {
                            externalYouTube(activity, videoId);
                        }

                }

            }
        } catch (Exception e) {
            try {
                Uri videoUri = Uri.parse(YOUTUBE_WATCH_URL + videoId);
                activity.startActivityForResult(new Intent(Intent.ACTION_VIEW, videoUri),
                        REQ_START_EXTERNAL_PLAYER);
            } catch (Exception ignore) {

            }
            e.printStackTrace();
        }
    }

    private static void inAppYouTube(Activity activity, @NonNull String videoId) {
        Intent intent = new Intent(activity.getApplicationContext(), YoutubePlayerActivity.class);
        intent.putExtra(YoutubePlayerActivity.EXTRA_VIDEO_ID, videoId);
        activity.startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
        onActivityEnterExit(activity);
    }

    private static void externalYouTube(Activity activity, @NonNull String videoId) {
        Uri videoUri = Uri.parse(YOUTUBE_WATCH_URL + videoId);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(VENDOR_YOUTUBE + videoId));
        List<ResolveInfo> list = activity.getPackageManager().queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY);

        if (list.size() == 0) {
            intent = new Intent(Intent.ACTION_VIEW, videoUri);
            AppUtils.preferPackageForIntent(activity, intent, YOUTUBE_PACKAGE_NAME);
        }

        activity.startActivityForResult(intent, REQ_START_EXTERNAL_PLAYER);
    }

    public static void inAppYouTubeSearch(Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setClass(activity.getApplicationContext(), YoutubeSearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void preferPackageForIntent(Context context, Intent intent, String packageName) {
        PackageManager pm = context.getPackageManager();
        for (ResolveInfo resolveInfo : pm.queryIntentActivities(intent, 0)) {
            if (resolveInfo.activityInfo.packageName.equals(packageName)) {
                intent.setPackage(packageName);
                break;
            }
        }
    }

    public static void chromeCustomTabs(Activity activity, String url) {
        try {
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
            // Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_arrow_back_black_24dp);
            // intentBuilder.setCloseButtonIcon(icon);
            intentBuilder.setShowTitle(true);
            intentBuilder.enableUrlBarHiding();
            prepareShareActionButton(activity, url, intentBuilder);
            openCustomTab(activity, intentBuilder.build(), Uri.parse(url));
        } catch (Exception e) {
            e.printStackTrace();
            openCustomIntent(activity, url);
        }
    }

    private static void prepareShareActionButton(Activity activity, String url, CustomTabsIntent.Builder builder) {
        try {
            String shareBody = url;
            if (!shareBody.contains(RemoteConfig.getAppDynamicLinkDomain())) {
                shareBody = activity.getString(R.string.share_via, shareBody, RemoteConfig.getAppStoreUrl());
            }

            Bundle shareBundle = createShareBundle("", shareBody, "", "", "");
            Intent shareIntent = getShareIntent(shareBundle);
            PendingIntent pi = PendingIntent.getActivity(activity, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_share_black_24dp);
            builder.setActionButton(icon, activity.getString(R.string.share), pi, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the URL on a Custom Tab if possible. Otherwise fallsback to opening it on a WebView.
     *
     * @param activity         The host activity.
     * @param customTabsIntent a CustomTabsIntent to be used if Custom Tabs is available.
     * @param uri              the Uri to be opened.
     */
    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri) {
        String packageName = CustomTabsHelper.getPackageNameToUse(activity);
        // If we cant find a package name, it means there's no browser that supports
        // Chrome Custom Tabs installed. So, we fallback to other external browsers.
        if (packageName == null) {
            openCustomIntent(activity, uri.toString());
        } else {
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.intent.setData(uri);
            customTabsIntent.launchUrl(activity, uri);
        }
    }

    public static void openCustomIntent(Activity activity, String url) {
        try {
            // intent://tracks:274964006#Intent;scheme=soundcloud;package=com.soundcloud.android;end
            if (url.startsWith("intent://") && url.endsWith("end")) {
                String[] urlSplit = TextUtils.split(url, "#");
                String data = urlSplit[0];

                int s = url.indexOf("scheme=") + "scheme=".length();
                String scheme = url.substring(s, url.indexOf(";", s));

                int p = url.indexOf("package=") + "package=".length();
                String _package = url.substring(p, url.indexOf(";", p));

                Uri uri = Uri.parse(data.replace("intent", scheme));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                AppUtils.preferPackageForIntent(activity, intent, _package);

                activity.startActivity(intent);
            } else {
                AppUtils.openExternalBrowser(activity, url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.openExternalBrowser(activity, url);
        }
    }

    public static void openSettingsActivity(Activity activity) {
        try {
            activity.startActivity(new Intent(activity, SettingsActivity.class));
            // onActivityEnterExit(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openExternalBrowser(Activity activity, String url) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onActivityEnterExit(Activity activity) {
        try {
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tintMenuItemIcon(Context context, MenuItem menuItem) {
        try {
            int greyDeepen = ContextCompat.getColor(context, R.color.grey_deepen);
            int white = ContextCompat.getColor(context, android.R.color.white);

            Drawable itemIcon = DrawableCompat.wrap(menuItem.getIcon());
            DrawableCompat.setTint(itemIcon, isLightTheme(context) ? greyDeepen : white);
            menuItem.setIcon(itemIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /*public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }*/

    public static void cancelNotification(Context context) {
        try {
            NotificationUtil notificationUtil = new NotificationUtil(context);
            notificationUtil.cancelNotification(NEW_POSTS_NOTIFICATION_ID);
            notificationUtil.cancelNotification(NEW_POSTS_BUNDLE_NOTIFICATION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isTablet(Context context) {
        try {
            return context.getResources().getConfiguration().smallestScreenWidthDp >= 600
                    || isDeviceTablet(context);
        } catch (Exception e) {
            e.printStackTrace();
            return isDeviceTablet(context);
        }
    }

    private static boolean isDeviceTablet(Context context) {
        try {
            return context.getResources().getBoolean(R.bool.is_tablet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isPortrait(Context context) {
        try {
            return context.getResources().getBoolean(R.bool.is_portrait);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static int convertDpToPixels(Context context, float dp) {
        try {
            Resources r = context.getResources();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void showToast(Activity activity, String message, boolean isLongDuration) {
        try {
            Toast.makeText(activity, message, isLongDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        } catch (Exception ignore) {
        }
    }

    public static void copyToClipboard(Context context, String text) {
        try {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText("TEXT", text);
                clipboard.setPrimaryClip(clip);
            }
            if (BuildConfig.DEBUG) Timber.d("copyToClipboard() %s", text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void appShare(Activity activity, FragmentManager fragmentManager, Bundle shareBundle) {
        try {
            AppInviteFragment appInviteFragment = AppInviteFragment.newInstance(shareBundle);
            appInviteFragment.show(fragmentManager, "appInviteFragment");
        } catch (Exception e) {
            share(activity, shareBundle);
            e.printStackTrace();
        }
    }

    public static void contentShare(Activity a, FragmentManager frag, Bundle shareBundle) {
        try {
            ContentShareFragment contentShareFragment = ContentShareFragment.newInstance(shareBundle);
            contentShareFragment.show(frag, "contentShareFragment");
        } catch (Exception e) {
            share(a, shareBundle);
            e.printStackTrace();
        }
    }


    public static void smsShare(Activity activity, Bundle shareBundle) {
        try {
            String subject = shareBundle.getString(BUNDLE_SHARE_SUBJECT, "");
            String shareBody = shareBundle.getString(BUNDLE_SHARE_BODY, "");
            String extraText = subject + " " + shareBody;

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:"));  // This ensures only SMS apps respond
            intent.putExtra("sms_body", extraText);
            activity.startActivity(intent);
        } catch (Exception e) {
            share(activity, shareBundle);
            e.printStackTrace();
        }
    }

    public static void specificPackageShare(Activity activity, Bundle shareBundle, String packageName) {
        try {
            //String title = shareBundle.getString(BUNDLE_SHARE_SUBJECT, "");

            Intent intent = getShareIntent(shareBundle);
            intent.setPackage(packageName);
            activity.startActivity(intent);
        } catch (Exception e) {
            share(activity, shareBundle);
            e.printStackTrace();
        }
    }

    public static void share(Activity activity, Bundle shareBundle) {
        try {
            Intent intent = getShareIntent(shareBundle);
            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Intent getShareIntent(Bundle shareBundle) {
        String subject = shareBundle.getString(BUNDLE_SHARE_SUBJECT, "");
        String shareBody = shareBundle.getString(BUNDLE_SHARE_BODY, "");
        String extraText = subject + " " + shareBody;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, extraText.trim());
        return intent;
    }

    public static Bundle createShareBundle(String subject, @NonNull String body, String image,
                                           String customUri, String customScheme) {
        Bundle shareBundle = new Bundle();
        shareBundle.putString(BUNDLE_SHARE_SUBJECT, subject);
        shareBundle.putString(BUNDLE_SHARE_BODY, body);
        shareBundle.putString(BUNDLE_SHARE_IMAGE, image);
        shareBundle.putString(BUNDLE_SHARE_CUSTOM_URI, customUri);
        shareBundle.putString(BUNDLE_SHARE_CUSTOM_SCHEME, customScheme);
        return shareBundle;
    }

    @SuppressLint("ObsoleteSdkInt")
    public static void hideStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT < 16) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String getAssetJsonData(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            if (hasKitKat())
                json = new String(buffer, StandardCharsets.UTF_8);
            else
                json = new String(buffer, UTF8);

            if (TextUtils.isEmpty(json))
                json = new String(buffer, Charset.forName(UTF8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getStringFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        if (hasKitKat()) {
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }
        } else {
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, UTF8))) {
                int c;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }
        }
        return textBuilder.toString();
    }

    public static void downloadFile(final Activity activity, final String url, final String fileName) {
        try {
            if (isWriteExternalStorageGranted(activity)) {
                // Permission is already available, start downloading file
                boolean isDownloadHandled = handleDownload(activity, url, fileName);
                if (!isDownloadHandled) openExternalBrowser(activity, url);
            } else {
                // Permission has not been granted and must be requested.
                requestWriteExternalStoragePermission(activity);
            }
        } catch (Exception e) {
            openExternalBrowser(activity, url);
            e.printStackTrace();
        }
    }

    private static boolean isWriteExternalStorageGranted(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static void requestWriteExternalStoragePermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Timber.d("requestWriteExternalStoragePermission() show dialog WITH option to disable");
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_WRITE_EXTERNAL_STORAGE);
        } else {
            // Timber.d("requestWriteExternalStoragePermission() show dialog WITHOUT option to disable");
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_WRITE_EXTERNAL_STORAGE);
        }
    }

    /**
     * Handles a download by loading the file from `fromUrl` and saving it to `toFilename` on the external storage
     * <p>
     * This requires the two permissions `android.permission.INTERNET` and `android.permission.WRITE_EXTERNAL_STORAGE`
     * <p>
     * Only supported on API level 9 (Android 2.3) and above
     *
     * @param context    a valid `Context` reference
     * @param fromUrl    the URL of the file to download, e.g. the one from `WebView.onDownloadRequested(...)`
     * @param toFilename the name of the destination file where the download should be saved, e.g. `myImage.jpg`
     * @return whether the download has been successfully handled or not
     */
    @SuppressLint("ObsoleteSdkInt")
    private static boolean handleDownload(final Context context, final String fromUrl, final String toFilename) {
        if (Build.VERSION.SDK_INT < 9) {
            // throw new RuntimeException("Method requires API level 9 or above");
            return false;
        }

        final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fromUrl));
        if (Build.VERSION.SDK_INT >= 11) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, toFilename);

        final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            if (dm != null)
                try {
                    dm.enqueue(request);
                } catch (Exception e) {
                    if (Build.VERSION.SDK_INT >= 11) {
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                    }
                    dm.enqueue(request);

                    e.printStackTrace();
                }

            return true;
        }
        // if the download manager app has been disabled on the device
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean stringContainsItemFromList(String inputString, String[] items) {
        try {
            for (String item : items) {
                try {
                    if (inputString.contains(item)) {
                        return true;
                    }
                } catch (Exception ignore) {
                }
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    public static boolean stringEndsWithItemFromList(String inputString, String[] items) {
        try {
            for (String item : items) {
                try {
                    if (inputString.endsWith(item)) {
                        return true;
                    }
                } catch (Exception ignore) {
                }
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    public static String getVideoThumb(JSONObject snippet, String videoId) {
        String thumb = "";
        String[] thumbTypes = {TAG_MAXRES, TAG_STANDARD, TAG_HIGH};

        for (String thumbType : thumbTypes) {
            try {
                thumb = snippet.getJSONObject(TAG_THUMBNAILS)
                        .getJSONObject(thumbType).getString(TAG_URL).trim();

                if (!TextUtils.isEmpty(thumb)) {
                    //if (BuildConfig.DEBUG) Timber.d("THUMB_URL= %s", thumb);
                    return thumb;
                }
            } catch (Exception ignored) {
            }
        }

        if (TextUtils.isEmpty(thumb)) {
            try {
                thumb = String.format(YOUTUBE_THUMBNAIL, videoId);
                //if (BuildConfig.DEBUG) Timber.d("THUMB_URL= %s", thumb);
            } catch (Exception ignored) {
            }
        }
        return thumb;
    }

    public static String formatViewCount(DecimalFormat formatter, String value) {
        try {
            if (getLanguageCode().equalsIgnoreCase("en")) {
                long count = Long.parseLong(value);
                String viewCount = formatViews(count);
                if (!TextUtils.isEmpty(viewCount)) {
                    return viewCount;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatter.format(Long.valueOf(value));
    }

    private static String formatViews(long val) {
        if (val < NUMBERS[0]) return Long.toString(val);
        if (val < NUMBERS[1]) return makeDecimal(val, NUMBERS[0], "K");
        if (val < NUMBERS[2]) return makeDecimal(val, NUMBERS[1], "M");
        if (val < NUMBERS[3]) return makeDecimal(val, NUMBERS[2], "B");
        return makeDecimal(val, NUMBERS[3], "T");
    }

    private static String makeDecimal(long val, long div, String sfx) {
        val = val / (div / 10);
        long whole = val / 10;
        long tenths = val % 10;
        if ((tenths == 0) || (whole >= 10))
            return String.format(Locale.US, "%d%s", whole, sfx);
        return String.format(Locale.US, "%d.%d%s", whole, tenths, sfx);
    }

    private static String getLanguageCode() {
        try {
            String langCode = Locale.getDefault().getLanguage().trim();
            if (!TextUtils.isEmpty(langCode))
                return langCode;
        } catch (Exception ignored) {
        }
        return "en";
    }

    /*private static boolean isEqual(String strA, String strB) {
        try {
            Collator usCollator = Collator.getInstance(Locale.US);
            usCollator.setStrength(Collator.PRIMARY);
            String stringA = strA.replaceAll("\"", "");
            String stringB = strB.replaceAll("\"", "");

            return usCollator.compare(stringA, stringB) == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }*/

    /**
     * Builds URI for deep linking.
     */
    public static Uri buildDeepLink(Bundle bundle) {
        // Build the link with all required parameters
        String title = bundle.getString(BUNDLE_SHARE_SUBJECT, "");
        String link = bundle.getString(BUNDLE_SHARE_BODY, "");
        String image = bundle.getString(BUNDLE_SHARE_IMAGE, "");
        String customUri = bundle.getString(BUNDLE_SHARE_CUSTOM_URI, "");
        String customScheme = bundle.getString(BUNDLE_SHARE_CUSTOM_SCHEME, "");

        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority(RemoteConfig.getAppDynamicLinkDomain())
                .path("/")
                .appendQueryParameter("link", link);

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(image)) {
            builder.appendQueryParameter("st", title);
            builder.appendQueryParameter("si", image);
        }

        if (!TextUtils.isEmpty(customUri) && !TextUtils.isEmpty(customScheme)) {
            if (customScheme.equals("channel") || customScheme.equals("playlist")) {
                customScheme += "://";
            } else {
                customScheme += ":";
            }
            Uri myCustomUri = Uri.parse(customUri);
            builder.appendQueryParameter("al", customScheme + myCustomUri.getEncodedSchemeSpecificPart());
        }

        builder.appendQueryParameter("apn", BuildConfig.APPLICATION_ID);
        builder.appendQueryParameter("amv", Integer.toString(BuildConfig.VERSION_CODE));
        // Return the completed deep link.
        return builder.build();
    }

    public static String buildYoutubePlaylistUrl(String playlistId, String nextPageToken, boolean isNextPage) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.googleapis.com")
                .appendPath("youtube")
                .appendPath(RemoteConfig.youtubeDataAPIVersion())
                .appendPath("playlistItems")
                .appendQueryParameter("part", "contentDetails")
                .appendQueryParameter("maxResults", String.valueOf(MAX_YOUTUBE_RESULTS))
                .appendQueryParameter("playlistId", playlistId)
                .appendQueryParameter("fields", "items/contentDetails,nextPageToken")
                .appendQueryParameter("key", RemoteConfig.getAndroidDeveloperApiKey());

        if (isNextPage) {
            builder.appendQueryParameter("pageToken", nextPageToken);
        }

        return builder.build().toString();
    }

    public static String buildYoutubeChannelUrl(String channelId, String nextPageToken, boolean isNextPage) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.googleapis.com")
                .appendPath("youtube")
                .appendPath(RemoteConfig.youtubeDataAPIVersion())
                .appendPath("search")
                .appendQueryParameter("part", "snippet")
                .appendQueryParameter("channelId", channelId)
                .appendQueryParameter("maxResults", String.valueOf(MAX_YOUTUBE_RESULTS))
                .appendQueryParameter("order", "date")
                .appendQueryParameter("type", "video")
                .appendQueryParameter("fields", "items/id,nextPageToken")
                .appendQueryParameter("key", RemoteConfig.getAndroidDeveloperApiKey());

        if (isNextPage) {
            builder.appendQueryParameter("pageToken", nextPageToken);
        }

        return builder.build().toString();
    }

    public static String buildYoutubeVideoSearchUrl(String query, String nextPageToken, boolean isNextPage) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.googleapis.com")
                .appendPath("youtube")
                .appendPath(RemoteConfig.youtubeDataAPIVersion())
                .appendPath("search")
                .appendQueryParameter("part", "snippet")
                .appendQueryParameter("maxResults", String.valueOf(MAX_YOUTUBE_RESULTS))
                .appendQueryParameter("q", query)
                .appendQueryParameter("type", "video")
                .appendQueryParameter("fields", "items/id,nextPageToken")
                .appendQueryParameter("key", RemoteConfig.getAndroidDeveloperApiKey());

        if (isNextPage) {
            builder.appendQueryParameter("pageToken", nextPageToken);
        }

        return builder.build().toString();
    }

    public static String buildYoutubeVideoDetailUrl(String videoIds) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.googleapis.com")
                .appendPath("youtube")
                .appendPath(RemoteConfig.youtubeDataAPIVersion())
                .appendPath("videos")
                .appendQueryParameter("part", "snippet,contentDetails,statistics")
                .appendQueryParameter("id", videoIds)
                .appendQueryParameter("key", RemoteConfig.getAndroidDeveloperApiKey());

        return builder.build().toString();
    }

    public static String buildChannelDetailUrl(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.googleapis.com")
                .appendPath("youtube")
                .appendPath(RemoteConfig.youtubeDataAPIVersion())
                .appendPath("channels")
                .appendQueryParameter("id", id)
                .appendQueryParameter("key", RemoteConfig.getAndroidDeveloperApiKey());

        builder.appendQueryParameter("part", "snippet");
        builder.appendQueryParameter("fields", "items(id,kind,snippet/title)");

        /*if (isFullDetail) {
            builder.appendQueryParameter("part", "snippet,statistics");
            builder.appendQueryParameter("fields",
                    "items(id,kind,snippet(description,thumbnails,title),statistics(subscriberCount,videoCount))");
        } else {
            builder.appendQueryParameter("part", "snippet");
            builder.appendQueryParameter("fields", "items(id,kind,snippet/title)");
        }*/

        return builder.build().toString();
    }

    public static String buildPlaylistDetailUrl(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.googleapis.com")
                .appendPath("youtube")
                .appendPath(RemoteConfig.youtubeDataAPIVersion())
                .appendPath("playlists")
                .appendQueryParameter("id", id)
                .appendQueryParameter("key", RemoteConfig.getAndroidDeveloperApiKey());

        builder.appendQueryParameter("part", "snippet");
        builder.appendQueryParameter("fields", "items(id,kind,snippet/title)");

        /*if (isFullDetail) {
            builder.appendQueryParameter("part", "contentDetails,snippet");
            builder.appendQueryParameter("fields",
                    "items(contentDetails,id,kind,snippet(channelTitle,description,thumbnails,title))");
        } else {
            builder.appendQueryParameter("part", "snippet");
            builder.appendQueryParameter("fields", "items(id,kind,snippet/title)");
        }*/

        return builder.build().toString();
    }

    public static String buildChannelVideosPageUrl(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.youtube.com")
                .appendPath("channel")
                .appendPath(id)
                .appendPath("videos");
        return builder.build().toString();
    }

    public static String buildPlaylistPageUrl(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.youtube.com")
                .appendPath("playlist")
                .appendQueryParameter("list", id);
        return builder.build().toString();
    }

    public static String buildGoogleBlogPostSearchUrl(String query, String blogId) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.googleapis.com")
                .appendPath("blogger")
                .appendPath("v3")
                .appendPath("blogs")
                .appendPath(blogId)
                .appendPath("posts")
                .appendPath("search")
                .appendQueryParameter("q", query)
                .appendQueryParameter("key", RemoteConfig.getAndroidDeveloperApiKey());
        return builder.build().toString();
    }

    public static String buildWordPressPostSearchUrl(String query, String siteUrl) {
        String scheme = siteUrl.startsWith("https://") ? "https" : "http";
        Uri.Builder builder = new Uri.Builder();
        String hostName = siteUrl;
        try {
            URL mUrl = new URL(hostName);
            hostName = mUrl.getHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.scheme(scheme)
                .authority(hostName)
                .appendPath("wp-json")
                .appendPath("wp")
                .appendPath("v2")
                .appendPath("posts")
                .appendQueryParameter("_embed", "1")
                .appendQueryParameter("search", query)
                .appendQueryParameter("per_page", String.valueOf(RemoteConfig.getWordPressSearchResults()));
        return builder.build().toString();
    }
}
