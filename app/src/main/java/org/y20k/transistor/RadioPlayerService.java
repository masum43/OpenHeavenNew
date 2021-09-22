
package org.y20k.transistor;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import timber.log.Timber;

/**
 * PlayerService class
 */
public final class RadioPlayerService extends Service implements
        AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener {

    /* Main class variables */
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    private String mStreamUri;
    private boolean mPlayback;
    private int mPlayerInstanceCounter;
    private HeadphoneUnplugReceiver mHeadphoneUnplugReceiver;

    /* Constructor (default) */
    public RadioPlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // set up variables
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaPlayer = null;
        mPlayerInstanceCounter = 0;

        // Listen for headphone unplug
        IntentFilter headphoneUnplugIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        mHeadphoneUnplugReceiver = new HeadphoneUnplugReceiver();
        registerReceiver(mHeadphoneUnplugReceiver, headphoneUnplugIntentFilter);

        // TODO Listen for headphone button
        // Use MediaSession
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // checking for empty intent
        if (intent == null) {
            Timber.v("onStartCommand() Null-Intent received. Stopping self.");
            stopSelf();
        }

        // ACTION PLAY
        else if (intent.getAction().equals(TransistorKeys.ACTION_PLAY)) {
            Timber.v("onStartCommand() received command: PLAY");

            // set mPlayback true
            mPlayback = true;

            // get URL of station from intent
            mStreamUri = intent.getStringExtra(TransistorKeys.EXTRA_STREAM_URI);

            // start playback
            preparePlayback();

            // increase counter
            mPlayerInstanceCounter++;
        }

        // ACTION STOP
        else if (intent.getAction().equals(TransistorKeys.ACTION_STOP)) {
            Timber.v("onStartCommand() received command: STOP");

            // set mPlayback false
            mPlayback = false;

            // stop playback
            finishPlayback();

            // reset counter
            mPlayerInstanceCounter = 0;
        }

        // default return value for media playback
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        try {
            switch (focusChange) {
                // gain of audio focus of unknown duration
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mPlayback) {
                        if (mMediaPlayer == null) {
                            initializeMediaPlayer();
                        } else if (!mMediaPlayer.isPlaying()) {
                            mMediaPlayer.start();
                        }
                        mMediaPlayer.setVolume(1.0f, 1.0f);
                    }
                    break;
                // loss of audio focus of unknown duration
                case AudioManager.AUDIOFOCUS_LOSS:
                    finishPlayback();
                    break;
                // transient loss of audio focus
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (!mPlayback && mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        finishPlayback();
                    } else if (mPlayback && mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                    }
                    break;
                // temporary external request of audio focus
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        } catch (Exception e) {
            Timber.e("onAudioFocusChange() error= %s", e.getMessage());
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Timber.v("onCompletion() Resuming playback after completion / signal loss. Player instance count: %s",
                mPlayerInstanceCounter);
        mMediaPlayer.reset();
        mPlayerInstanceCounter++;
        initializeMediaPlayer();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mPlayerInstanceCounter == 1) {
            Timber.v("onPrepared() Preparation finished. Starting playback. Player instance count: %s +++",
                    mPlayerInstanceCounter);
            Timber.v("onPrepared() mStreamUri= %s +++", mStreamUri);

            // starting media player
            mp.start();

            // decrease counter
            mPlayerInstanceCounter--;

        } else {
            Timber.v("onPrepared() Stopping and re-initializing media player. Player instance count: %s",
                    mPlayerInstanceCounter);

            // release media player
            releaseMediaPlayer();

            // decrease counter
            mPlayerInstanceCounter--;

            // re-initializing media player
            if (mPlayerInstanceCounter >= 0) {
                initializeMediaPlayer();
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Timber.e("Unknown media playback error");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Timber.e("Connection to server lost");
                break;
            default:
                Timber.e("Generic audio playback error");
                break;
        }

        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                Timber.e("IO media error.");
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Timber.e("Malformed media.");
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Timber.e("Unsupported content type");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Timber.e("Media timeout");
                break;
            default:
                Timber.e("Other case of media playback error");
                break;
        }

        mp.reset();

        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {

        switch (what) {
            case MediaPlayer.MEDIA_INFO_UNKNOWN:
                Timber.i("Unknown media info");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                Timber.i("Buffering started");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                Timber.i("Buffering finished");
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE: // case never selected
                Timber.i("New metadata available");
                break;
            default:
                Timber.i("other case of media info");
                break;
        }

        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Timber.v("Buffering: %s", percent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Timber.v("onDestroy called.");

        // save state
        mPlayback = false;
        savePlaybackState();

        // unregister receivers
        this.unregisterReceiver(mHeadphoneUnplugReceiver);

        // retrieve notification system service and cancel notification
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(TransistorKeys.PLAYER_SERVICE_NOTIFICATION_ID);
    }

    /* Method to start the player */
    public void startActionPlay(Context context, int stationId, Station station) {
        Timber.v("starting playback service: %s", mStreamUri);

        mStreamUri = station.getStreamUri();

        // start player service using intent
        Intent intent = new Intent(context, RadioPlayerService.class);
        intent.setAction(TransistorKeys.ACTION_PLAY);
        intent.putExtra(TransistorKeys.EXTRA_STREAM_URI, mStreamUri);
        context.startService(intent);

        // put up notification
        RadioNotificationUtils radioNotificationUtils = new RadioNotificationUtils(context);
        radioNotificationUtils.setStationID(stationId);
        radioNotificationUtils.setStationName(station.getStationName());
        radioNotificationUtils.setStationIcon(station.getStationImage());
        radioNotificationUtils.createNotification();
    }

    /* Method to stop the player */
    public void startActionStop(Context context) {
        Timber.v("startActionStop() stopping playback service");

        // stop player service using intent
        Intent intent = new Intent(context, RadioPlayerService.class);
        intent.setAction(TransistorKeys.ACTION_STOP);
        context.startService(intent);
    }

    /* Set up the media player */
    private void initializeMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);

        try {
            mMediaPlayer.setDataSource(mStreamUri);
            mMediaPlayer.prepareAsync();
            Timber.v("initializeMediaPlayer() mStreamUri: %s", mStreamUri);
        } catch (Exception e) {
            Timber.e("initializeMediaPlayer() error= %s", e.getMessage());
        }
    }

    /* Release the media player */
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /* Prepare playback */
    private void preparePlayback() {
        // stop running player
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            releaseMediaPlayer();
        }

        // request focus and initialize media player
        if (mStreamUri != null && requestFocus()) {
            initializeMediaPlayer();
        }

        // save state
        mPlayback = true;
        savePlaybackState();

        // send local broadcast (needed by MainActivityFragment)
        Intent i = new Intent();
        i.setAction(TransistorKeys.ACTION_PLAYBACK_STARTED);
        LocalBroadcastManager.getInstance(this.getApplication()).sendBroadcast(i);
    }

    /* Finish playback */
    private void finishPlayback() {
        // release player
        releaseMediaPlayer();

        // save state
        mPlayback = false;
        savePlaybackState();

        // send local broadcast (needed by PlayerActivityFragment and MainActivityFragment)
        Intent i = new Intent();
        i.setAction(TransistorKeys.ACTION_PLAYBACK_STOPPED);
        LocalBroadcastManager.getInstance(this.getApplication()).sendBroadcast(i);

        // retrieve notification system service and cancel notification
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(TransistorKeys.PLAYER_SERVICE_NOTIFICATION_ID);
    }

    /* Request audio manager focus */
    private boolean requestFocus() {
        int result = mAudioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    /* Saves state of playback */
    private void savePlaybackState() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplication());
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(TransistorKeys.PLAYBACK, mPlayback);
        editor.apply();
    }

    /**
     * Inner class: Receiver for headphone unplug-signal
     */
    public class HeadphoneUnplugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPlayback && AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                Timber.v("Headphones unplugged. Stopping playback.");
                // stop playback
                finishPlayback();
            }
        }
    }
}
