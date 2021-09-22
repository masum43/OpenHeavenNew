package com.newandromo.dev18147.app821162.utils;

import android.content.Context;
import android.media.AudioManager;

public class AudioUtils {

    private static final Object mSingletonLock = new Object();
    private static AudioManager audioManager;

    private static AudioManager getInstance(Context context) {
        synchronized (mSingletonLock) {
            if (audioManager != null)
                return audioManager;
            if (context != null)
                audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            return audioManager;
        }
    }

    public static void adjustMusicVolume(Context context, boolean up, boolean showInterface) {
        int direction = up ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER;
        int flag = AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE | (showInterface ? AudioManager.FLAG_SHOW_UI : 0);
        getInstance(context).adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, flag);
    }
}
