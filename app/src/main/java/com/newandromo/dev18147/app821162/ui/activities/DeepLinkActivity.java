package com.newandromo.dev18147.app821162.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.SplashScreen;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.utils.Constants.Const.HTTP;

public class DeepLinkActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppUtils.setAppTheme(this, false);
        super.onCreate(savedInstanceState);

        /*Intent intent = getIntent();
        if (intent != null) {
            if (!TextUtils.isEmpty(intent.getAction())) {
                String action = intent.getAction();
                if (action.equalsIgnoreCase(Intent.ACTION_VIEW)) {
                    Uri deepLink = intent.getData();

                    if (deepLink != null && !TextUtils.isEmpty(deepLink.toString())) {
                        mDeepLink = deepLink.toString();

                        if (BuildConfig.DEBUG)
                            Timber.d("onCreate() deepLink= %s", deepLink.toString());
                    }
                }
            }
        }

        if (!TextUtils.isEmpty(mDeepLink) && !mGoHome) {
            mGoHome = true;
            AppUtils.chromeCustomTabs(DeepLinkActivity.this, mDeepLink);
        } else {
            goHome();
        }*/

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, data -> {
                    // Get deep link from result (may be null if no link is found)
                    Uri deepLink = null;
                    if (data != null) {
                        deepLink = data.getLink();
                    }

                    if (deepLink != null && !TextUtils.isEmpty(deepLink.toString())) {
                        String deepUrl = deepLink.toString();

                        String youtubeScheme = getString(R.string.scheme_youtube) + "://";
                        if (deepUrl.startsWith(youtubeScheme)) {
                            deepUrl = deepUrl.replace(youtubeScheme, HTTP);
                        }

                        String appScheme = getString(R.string.scheme_my_app) + "://";
                        if (deepUrl.startsWith(appScheme)) {
                            deepUrl = deepUrl.replace(appScheme, HTTP);
                        }

                        if (BuildConfig.DEBUG) Timber.d("onSuccess() deepUrl= %s", deepUrl);

                        AppUtils.chromeCustomTabs(DeepLinkActivity.this, deepUrl);

                    } else {

                        String mDeepLink = getDeepLink();

                        if (!TextUtils.isEmpty(mDeepLink)) {
                            AppUtils.chromeCustomTabs(DeepLinkActivity.this, mDeepLink);
                        } else {
                            if (BuildConfig.DEBUG) Timber.e("onSuccess() no link found");
                            goHome();
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    String mDeepLink = getDeepLink();

                    if (!TextUtils.isEmpty(mDeepLink)) {
                        AppUtils.chromeCustomTabs(DeepLinkActivity.this, mDeepLink);
                    } else {
                        if (BuildConfig.DEBUG) Timber.e("onFailure() error= %s", e.getMessage());
                        goHome();
                    }
                });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        goHome();
    }

    private String getDeepLink() {
        String deepLink = "";
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getAction() != null) {
                String action = intent.getAction();
                if (action.equalsIgnoreCase(Intent.ACTION_VIEW)) {
                    Uri intentData = intent.getData();

                    if (intentData != null && !TextUtils.isEmpty(intentData.toString())) {
                        deepLink = intentData.toString();

                        if (BuildConfig.DEBUG)
                            Timber.d("getDeepLink() intentData= %s", intentData.toString());
                    }
                }
            }
        }
        return deepLink;
    }

    private void goHome() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(this, SplashScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
