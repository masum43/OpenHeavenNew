package com.newandromo.dev18147.app821162;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MyAnalytics {
    public static final String CT_ENTRY = "entry";
    public static final String CT_VIDEO = "video";

    private MyAnalytics() {
    }

    public static void shareContent(Context ctx, String title, String url) {
        try {
            boolean isVideo = url.startsWith("https://youtu");
            String type = isVideo ? CT_VIDEO : CT_ENTRY;
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, url);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, title);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void selectContent(Context ctx, String title, String url, String type) {
        try {
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, url);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, title);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchItem(Context ctx, String term) {
        try {
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, term);
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setFeedsLanguage(Context ctx, String feedsLanguage) {
        try {
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
            firebaseAnalytics.setUserProperty("feeds_language", feedsLanguage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
