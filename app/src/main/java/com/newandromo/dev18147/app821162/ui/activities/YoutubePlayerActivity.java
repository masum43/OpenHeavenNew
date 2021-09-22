package com.newandromo.dev18147.app821162.ui.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.AudioUtils;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.YOUTUBE_WATCH_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.VENDOR_YOUTUBE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.YOUTUBE_PACKAGE_NAME;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_RECOVERY_DIALOG;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_START_EXTERNAL_PLAYER;

public class YoutubePlayerActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener,
        YouTubePlayer.OnFullscreenListener,
        YouTubePlayer.PlayerStateChangeListener {
    public static final String EXTRA_VIDEO_ID = "video_id";
    private static final int PORTRAIT_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
    private static final int LANDSCAPE_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;

    private String videoId;
    private YouTubePlayer.PlayerStyle playerStyle;
    private Orientation orientation;
    private YouTubePlayerView playerView;
    private YouTubePlayer player;
    private boolean handleError = true;



    public enum Orientation {
        AUTO, AUTO_START_WITH_LANDSCAPE, ONLY_LANDSCAPE, ONLY_PORTRAIT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initialize();

        playerView = new YouTubePlayerView(this);
        playerView.initialize(RemoteConfig.getAndroidDeveloperApiKey(), this);

        addContentView(playerView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        playerView.setBackgroundResource(android.R.color.black);

        AppUtils.hideStatusBar(this); // StatusBarUtil.hide(this);
    }

    private void initialize() {

        videoId = getIntent().getStringExtra(EXTRA_VIDEO_ID);

        if (playerStyle == null)
            playerStyle = YouTubePlayer.PlayerStyle.DEFAULT;

        if (orientation == null)
            orientation = Orientation.AUTO;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player,
                                        boolean wasRestored) {
        this.player = player;
        player.setOnFullscreenListener(this);
        player.setPlayerStateChangeListener(this);

        switch (orientation) {
            case AUTO:
                player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                        | YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                        | YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE
                        | YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                break;
            case AUTO_START_WITH_LANDSCAPE:
                player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                        | YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                        | YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE
                        | YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                player.setFullscreen(true);
                break;
            case ONLY_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                        | YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                player.setFullscreen(true);
                break;
            case ONLY_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                        | YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                player.setFullscreen(true);
                break;
        }

        switch (playerStyle) {
            case CHROMELESS:
                player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                break;
            case MINIMAL:
                player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                break;
            case DEFAULT:
            default:
                player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                break;
        }

        if (!wasRestored)
            player.loadVideo(videoId);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, REQ_RECOVERY_DIALOG).show();
        } else {
            String errorMessage = String.format("There was an error initializing the YouTubePlayer (%s)",
                    errorReason.toString());
            AppUtils.showToast(this, errorMessage, true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_RECOVERY_DIALOG) {
            // Retry initialization if user performed a recovery action
            playerView.initialize(RemoteConfig.getAndroidDeveloperApiKey(), this);
        } else if (requestCode == REQ_START_EXTERNAL_PLAYER) {
            handleError = false;
        }
    }

    // YouTubePlayer.OnFullscreenListener
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (orientation) {
            case AUTO:
            case AUTO_START_WITH_LANDSCAPE:
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (player != null)
                        player.setFullscreen(true);
                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (player != null)
                        player.setFullscreen(false);
                }
                break;
            case ONLY_LANDSCAPE:
            case ONLY_PORTRAIT:
                break;
        }
    }

    @Override
    public void onFullscreen(boolean fullScreen) {
        switch (orientation) {
            case AUTO:
            case AUTO_START_WITH_LANDSCAPE:
                if (fullScreen)
                    setRequestedOrientation(LANDSCAPE_ORIENTATION);
                else
                    setRequestedOrientation(PORTRAIT_ORIENTATION);
                break;
            case ONLY_LANDSCAPE:
            case ONLY_PORTRAIT:
                break;
        }
    }

    // YouTubePlayer.PlayerStateChangeListener
    @Override
    public void onError(YouTubePlayer.ErrorReason reason) {
        if (BuildConfig.DEBUG) {
            Timber.e("onError() videoId= %s, reason= %s", videoId, reason.name());
        }

        if (handleError && !YouTubePlayer.ErrorReason.NETWORK_ERROR.equals(reason)) {
            startVideo(videoId);
        }
    }

    @Override
    public void onAdStarted() {
    }

    @Override
    public void onLoaded(String videoId) {
    }

    @Override
    public void onLoading() {
    }

    @Override
    public void onVideoEnded() {
    }

    @Override
    public void onVideoStarted() {
        AppUtils.hideStatusBar(this); // StatusBarUtil.hide(this);
    }

    // Audio Managing
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            AudioUtils.adjustMusicVolume(this, true, true);
            AppUtils.hideStatusBar(this); // StatusBarUtil.hide(this);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            AudioUtils.adjustMusicVolume(this, false, true);
            AppUtils.hideStatusBar(this); // StatusBarUtil.hide(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void startVideo(@NonNull String videoId) {
        try {
            Uri videoUri = Uri.parse(getVideoUrl(videoId));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(VENDOR_YOUTUBE + videoId));
            List<ResolveInfo> list = getPackageManager().queryIntentActivities(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY);

            if (list.size() == 0) {
                intent = new Intent(Intent.ACTION_VIEW, videoUri);
                AppUtils.preferPackageForIntent(this, intent, YOUTUBE_PACKAGE_NAME);
            }

            startActivityForResult(intent, REQ_START_EXTERNAL_PLAYER);
        } catch (Exception e) {
            try {
                Uri videoUri = Uri.parse(getVideoUrl(videoId));
                startActivityForResult(new Intent(Intent.ACTION_VIEW, videoUri), REQ_START_EXTERNAL_PLAYER);
            } catch (Exception ignore) {

            }
            Timber.e("startVideo() error= %s", e.getMessage());
        }
    }

    private String getVideoUrl(@NonNull String videoId) {
        return YOUTUBE_WATCH_URL + videoId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.cancelNotification(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // AppUtils.applicationPaused();
    }

    // Animation
    @Override
    public void onBackPressed() {


        try {
            super.onBackPressed();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        AppUtils.onActivityEnterExit(this);
    }
}
