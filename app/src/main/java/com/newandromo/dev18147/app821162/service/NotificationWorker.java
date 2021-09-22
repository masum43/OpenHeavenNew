package com.newandromo.dev18147.app821162.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.utils.NotificationUtil;
import timber.log.Timber;

public class NotificationWorker extends Worker {
    private Context mContext;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Data data = getInputData();
            NotificationUtil notificationUtil = new NotificationUtil(mContext);
            notificationUtil.createNewFCMNotification(data);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
                Timber.e("doWork() error= %s", e.getMessage());
            }
        }
        return Result.success();
    }
}
