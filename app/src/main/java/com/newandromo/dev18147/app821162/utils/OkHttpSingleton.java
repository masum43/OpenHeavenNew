package com.newandromo.dev18147.app821162.utils;

import okhttp3.OkHttpClient;

public class OkHttpSingleton {
    private static OkHttpSingleton mOkHttpSingleton;
    // No need to be static; OkHttpSingleton is unique so is this.
    private final OkHttpClient mOkHttpClient;

    // Private so that this cannot be instantiated.
    private OkHttpSingleton() {
        mOkHttpClient = new OkHttpClient();
    }

    public static synchronized OkHttpSingleton getInstance() {
        if (mOkHttpSingleton == null) {
            synchronized (OkHttpSingleton.class) {
                if (mOkHttpSingleton == null) {
                    mOkHttpSingleton = new OkHttpSingleton();
                }
            }
        }
        return mOkHttpSingleton;
    }

    // In case you just need the unique OkHttpClient instance.
    public OkHttpClient getClient() {
        return mOkHttpClient;
    }

    public void closeConnections() {
        try {
            mOkHttpClient.dispatcher().cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
