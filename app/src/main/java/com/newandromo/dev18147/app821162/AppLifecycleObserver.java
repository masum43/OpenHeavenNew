package com.newandromo.dev18147.app821162;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import timber.log.Timber;

public class AppLifecycleObserver implements LifecycleObserver {
    public static boolean isAppOnForeground;

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onEnterForeground() {
        isAppOnForeground = true;

        if (BuildConfig.DEBUG) {
            Timber.d("isAppOnForeground= %s", AppLifecycleObserver.isAppOnForeground);
        }

        // FeedPeriodicSyncWorker.schedulePeriodicWork(); // todo schedulePeriodicWork()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onEnterBackground() {
        isAppOnForeground = false;

        if (BuildConfig.DEBUG) {
            Timber.d("isAppOnForeground= %s", AppLifecycleObserver.isAppOnForeground);
        }
    }
}
