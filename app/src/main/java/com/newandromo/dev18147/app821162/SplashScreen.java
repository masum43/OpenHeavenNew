package com.newandromo.dev18147.app821162;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.CategoryEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeTypeEntity;
import com.newandromo.dev18147.app821162.service.FeedPeriodicSyncWorker;
import com.newandromo.dev18147.app821162.ui.activities.MainActivity;
import com.newandromo.dev18147.app821162.utils.PrefUtils;

import static com.newandromo.dev18147.app821162.service.MyFirebaseMessagingService.KEY_LAUNCH_URL;

public class SplashScreen extends AppCompatActivity {

    public static  SplashScreen splashScreen ;
    public static String applovin_banner_ad_id = "";
    public static String appodeal_inters_id = "";
    public static String applovin_mrec_ad_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashScreen =  this;
        
        initApplovin();

        RelativeLayout progress =  findViewById(R.id.progressLayout);

        IronSource.init(this, getString(R.string.app_key), IronSource.AD_UNIT.BANNER);
        IronSource.init(this, getString(R.string.app_key), IronSource.AD_UNIT.INTERSTITIAL);
        IntegrationHelper.validateIntegration(this);


//        IronSource.setInterstitialListener(new InterstitialListener() {
//            /**
//             * Invoked when Interstitial Ad is ready to be shown after load function was called.
//             */
//            @Override
//            public void onInterstitialAdReady() {
//                progress.setVisibility(View.GONE);
//                IronSource.showInterstitial("DefaultInterstitial");
//            }
//            /**
//             * invoked when there is no Interstitial Ad available after calling load function.
//             */
//            @Override
//            public void onInterstitialAdLoadFailed(IronSourceError error) {
//                IronSource.loadInterstitial();
//                progress.setVisibility(View.GONE);
//                new LoadAppAsyncTask(SplashScreen.this).execute();
//               // Intent i = new Intent(SplashScreen.this, MainActivity.class);
//              //  startActivity(i);
//              //  finish();
//            }
//            /**
//             * Invoked when the Interstitial Ad Unit is opened
//             */
//            @Override
//            public void onInterstitialAdOpened() {
//            }
//            /*
//             * Invoked when the ad is closed and the user is about to return to the application.
//             */
//            @Override
//            public void onInterstitialAdClosed() {
//                progress.setVisibility(View.GONE);
//                new LoadAppAsyncTask(SplashScreen.this).execute();
//                //Intent i = new Intent(SplashScreen.this, MainActivity.class);
//              //  startActivity(i);
//              //  finish();
//            }
//            /**
//             * Invoked when Interstitial ad failed to show.
//             * @param error - An object which represents the reason of showInterstitial failure.
//             */
//            @Override
//            public void onInterstitialAdShowFailed(IronSourceError error) {
//                //IronSource.showInterstitial("DefaultInterstitial");
//               // new LoadAppAsyncTask(SplashScreen.this).execute();
//                  // Intent i = new Intent(SplashScreen.this, MainActivity.class);
//                 //  startActivity(i);
//                //   finish();
//            }
//            /*
//             * Invoked when the end user clicked on the interstitial ad, for supported networks only.
//             */
//            @Override
//            public void onInterstitialAdClicked() {
//            }
//            /** Invoked right before the Interstitial screen is about to open.
//             *  NOTE - This event is available only for some of the networks.
//             *  You should NOT treat this event as an interstitial impression, but rather use InterstitialAdOpenedEvent
//             */
//            @Override
//            public void onInterstitialAdShowSucceeded() {
//            }
//        });
//        IronSource.loadInterstitial();
//        IronSource.isInterstitialPlacementCapped("DefaultInterstitial");


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    }

    private void initApplovin() {
        AppLovinSdk.getInstance( this ).setMediationProvider("max");
        AppLovinSdk.initializeSdk( this, configuration -> {
            // AppLovin SDK is initialized, start loading ads
        });
        AppLovinSdk.getInstance(this).getSettings()
                .setTestDeviceAdvertisingIds(Arrays.asList("ccf4753b-1351-40f1-9094-6dd8d0ff817e",
                        "03f361a4-b613-4266-b214-e03b9c40b65c"));

        getMyIdsFromString();
        loadApplovinInters(this);

        new LoadAppAsyncTask(SplashScreen.this).execute();
    }

    private  void  getMyIdsFromString() {
        applovin_banner_ad_id = getString(R.string.applovin_banner_ad_id);
        appodeal_inters_id = getString(R.string.appodeal_inters_id);
        applovin_mrec_ad_id = getString(R.string.applovin_mrec_ad_id);
    }

    public static MaxInterstitialAd interstitialAd;
    public static void loadApplovinInters(Activity activity) {
        interstitialAd = new MaxInterstitialAd( appodeal_inters_id, activity );
        interstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {
                interstitialAd.loadAd();
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });
        // Load the first ad
        interstitialAd.loadAd();
    }
    public static void showApplovinIntersitial(){
        if (interstitialAd != null && interstitialAd.isReady())
            interstitialAd.showAd();
    }

    public static void showApplobinBanner(Activity context, FrameLayout adLayout) {
        MaxAdView adView;
        adView = new MaxAdView(applovin_banner_ad_id, context);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightPx = context.getResources().getDimensionPixelSize( R.dimen.banner_height );

        adView.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx ) );
        adLayout.addView(adView);

        adView.loadAd();
    }

    public static void showApplobinMrecAd(Activity activity, FrameLayout maxAdView) {
        MaxAdView adView;
        adView = new MaxAdView( applovin_mrec_ad_id, MaxAdFormat.MREC, activity );
//            adView.setListener( this );

        // MREC width and height are 300 and 250 respectively, on phones and tablets
        int widthPx = AppLovinSdkUtils.dpToPx( activity, 300 );
        int heightPx = AppLovinSdkUtils.dpToPx( activity, 250 );

        adView.setLayoutParams( new FrameLayout.LayoutParams( widthPx, heightPx ) );

        // Set background or background color for MRECs to be fully functional
        //adView.setBackgroundColor( R.color.background_color );

        //ViewGroup rootView = findViewById( android.R.id.content );
        maxAdView.addView( adView );

        // Load the ad
        adView.loadAd();
    }

    private static class LoadAppAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<SplashScreen> mActivity;

        LoadAppAsyncTask(SplashScreen activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                SplashScreen activity = mActivity.get();
                if (activity == null || activity.isFinishing()) return null;

                Context context = activity.getApplicationContext();
                DataRepository repo = ((MyApplication) context).getRepository();

                RemoteConfig.initiateRemoteConfig(context);

                PrefUtils.getLayoutType(context);
                PrefUtils.setIsSearch(context, false);
                PrefUtils.setSearchQuery(context, "");
                PrefUtils.setNavDrawerSelectedItem(context, R.id.nav_all_items);
                PrefUtils.setIsPopupAppRater(context, true);

                try {
                    List<CategoryEntity> categories = repo.getAllCategories();
                    if (categories != null && !categories.isEmpty()) {
                        for (CategoryEntity category : categories) {
                            int id = category.getId();
                            PrefUtils.setIsCategoryRefreshed(context, id, false);
                            PrefUtils.setCurrentListPosition(context, id, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    repo.resetAllRecentRead();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TODO Do less important stuff in another background thread to speedup startup
                // ---------------------------------------------------------------------------------
                new AppExecutors().networkIO().execute(() -> {
                    PrefUtils.setYoutubeTypesUpdated(context, false);

                    try {
                        List<YoutubeTypeEntity> types = repo.getAllYoutubeTypes();
                        if (types != null && !types.isEmpty()) {
                            for (YoutubeTypeEntity type : types) {
                                int id = type.getId();
                                PrefUtils.setYoutubeNextPageToken(context, id, "");
                                PrefUtils.setIsYoutubeSearching(context, id, false);
                                PrefUtils.setScrolledPosition(context, id, 0);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        repo.deleteAllYoutubeVideos();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        PrefUtils.setYoutubeNextPageToken(context, "");
                        PrefUtils.setIsYoutubeSearching(context, false);
                        repo.deleteSearchedVideos();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SplashScreen activity = mActivity.get();
            if (activity == null || activity.isFinishing()) return;

            FeedPeriodicSyncWorker.schedulePeriodicWork(activity.getApplicationContext()); // todo schedulePeriodicWork()

            try {
                if (activity.getIntent() != null && activity.getIntent().getExtras() != null) {
                    for (String key : activity.getIntent().getExtras().keySet()) {
                        Object value = activity.getIntent().getExtras().get(key);
                        if (!TextUtils.isEmpty(key) && key.equals(KEY_LAUNCH_URL)) {
                            if (value != null && !TextUtils.isEmpty(value.toString())) {
                                activity.startActivity(new
                                        Intent(Intent.ACTION_VIEW, Uri.parse(value.toString())));
                                activity.finish();
                                return;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            }

            try{

                Intent newIntent = new Intent();
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                newIntent.setClass(activity, MainActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(newIntent);
                activity.finish();

//                SplashScreen. mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                    @Override
//                    public void onAdDismissedFullScreenContent() {
//                        super.onAdDismissedFullScreenContent();
//                        Intent newIntent = new Intent();
//                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
//                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        newIntent.setClass(activity, MainActivity.class);
//                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        activity.startActivity(newIntent);
//                        activity.finish();
//                    }
//                });
            }catch (Exception e){
                Intent newIntent = new Intent();
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                newIntent.setClass(activity, MainActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(newIntent);
                activity.finish();
            }

        }
    }
}
