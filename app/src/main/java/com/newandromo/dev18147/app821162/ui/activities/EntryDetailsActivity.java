package com.newandromo.dev18147.app821162.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;


import com.newandromo.dev18147.app821162.utils.AdsManager;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Locale;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.CategoryEntity;
import com.newandromo.dev18147.app821162.ui.fragments.EntryDetailFragment;
import com.newandromo.dev18147.app821162.ui.fragments.EntryDetailViewPagerFrag;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_ENTRY_DETAIL_SUBTITLE;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_ENTRY_DETAIL_TITLE;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_IS_CATEGORY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_POSITION;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_SEARCH_QUERY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_CATEGORY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_POSITION;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_SEARCH_QUERY;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_START_EXTERNAL_PLAYER;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_START_STANDALONE_PLAYER;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_WRITE_EXTERNAL_STORAGE;

public class EntryDetailsActivity extends BaseDetailActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String FRAG_VIEWPAGER = "viewpager";
    private String mTitle;
    private String mSubTitle;
    private int mPosition;
    private int mId;
    private boolean mIsCategory;
    private String mSearchQuery;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_entry_details;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppUtils.setAppTheme(this, false);
        super.onCreate(savedInstanceState);
        loadBannerAd();
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.getExtras() != null) {
                mPosition = intent.getIntExtra(INTENT_EXTRA_POSITION, 1);
                mId = intent.getIntExtra(INTENT_EXTRA_ID, 1);
                mIsCategory = intent.getBooleanExtra(INTENT_EXTRA_IS_CATEGORY, false);
                mSearchQuery = intent.getExtras().getString(INTENT_EXTRA_SEARCH_QUERY, "");

                if (!mIsCategory) {
                    if (mPosition == MainActivity.ALL_ITEMS)
                        mTitle = getString(R.string.nav_drawer_all_items);
                    else if (mPosition == MainActivity.BOOKMARKS)
                        mTitle = getString(R.string.nav_drawer_bookmarks);
                }
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in_fragment, R.anim.fade_out_fragment);
            ft.replace(R.id.entry_detail_viewpager_fragment, new EntryDetailViewPagerFrag(), FRAG_VIEWPAGER);
            ft.commit();

            new LoadCategoryAsyncTask(this).execute(mId);
        } else {
            mPosition = savedInstanceState.getInt(BUNDLE_POSITION, 1);
            mId = savedInstanceState.getInt(BUNDLE_ID, 0);
            mIsCategory = savedInstanceState.getBoolean(BUNDLE_IS_CATEGORY);
            mSearchQuery = savedInstanceState.getString(BUNDLE_SEARCH_QUERY, "");
            mTitle = savedInstanceState.getString(BUNDLE_ENTRY_DETAIL_TITLE);
            mSubTitle = savedInstanceState.getString(BUNDLE_ENTRY_DETAIL_SUBTITLE);
        }

        setActionBarTitles(mTitle, mSubTitle);
    }

    public void loadBannerAd() {
        final FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        AdsManager.bannerAd(EntryDetailsActivity.this,bannerContainer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AdsManager.bannerDismiss();
    }


    @Override
    protected void onStop() {
        super.onStop();
        AdsManager.bannerDismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdsManager.bannerDismiss();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_POSITION, mPosition);
        outState.putInt(BUNDLE_ID, mId);
        outState.putBoolean(BUNDLE_IS_CATEGORY, mIsCategory);
        outState.putString(BUNDLE_SEARCH_QUERY, mSearchQuery);
        outState.putString(BUNDLE_ENTRY_DETAIL_TITLE, mTitle);
        outState.putString(BUNDLE_ENTRY_DETAIL_SUBTITLE, mSubTitle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_START_STANDALONE_PLAYER || requestCode == REQ_START_EXTERNAL_PLAYER) {
            // Timber.d("onActivityResult() back from YoutubePlayerActivity");
            EntryDetailFragment edf = getCurrentEntryDetailFragmentPage();
            if (edf != null && edf.isAdded()) edf.reloadEntryDetail();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_WRITE_EXTERNAL_STORAGE) {
            EntryDetailFragment edf = getCurrentEntryDetailFragmentPage();
            // Request for write to external storage permission.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start downloading file
                // Timber.d("onRequestPermissionsResult() PERMISSION GRANTED");
                if (edf != null) edf.downloadFile(true);

            } else {
                // Permission request was denied.
                // Timber.d("onRequestPermissionsResult() PERMISSION DENIED");
                if (edf != null) edf.downloadFile(false);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (PrefUtils.useVolumeKeysToNavigate(this)) {
                EntryDetailViewPagerFrag frag = (EntryDetailViewPagerFrag)
                        getSupportFragmentManager().findFragmentByTag(FRAG_VIEWPAGER);
                if (frag != null && frag.isAdded() && !frag.isRemoving()) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_VOLUME_UP:
                            frag.selectEntry(false);
                            return true;
                        case KeyEvent.KEYCODE_VOLUME_DOWN:
                            frag.selectEntry(true);
                            return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onKeyDown(keyCode, event);
    }

    // Handle onKeyUp too to suppress beep
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (PrefUtils.useVolumeKeysToNavigate(this)) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please wait...");
        IronSource.setInterstitialListener(new InterstitialListener() {
            /**
             * Invoked when Interstitial Ad is ready to be shown after load function was called.
             */
            @Override
            public void onInterstitialAdReady() {
                dialog.dismiss();
                IronSource.showInterstitial("DefaultInterstitial");
            }

            /**
             * invoked when there is no Interstitial Ad available after calling load function.
             */
            @Override
            public void onInterstitialAdLoadFailed(IronSourceError error) {
                dialog.dismiss();
                finish();

                // Intent i = new Intent(SplashScreen.this, MainActivity.class);
                //  startActivity(i);
                //  finish();
            }

            /**
             * Invoked when the Interstitial Ad Unit is opened
             */
            @Override
            public void onInterstitialAdOpened() {
            }

            /*
             * Invoked when the ad is closed and the user is about to return to the application.
             */
            @Override
            public void onInterstitialAdClosed() {
                finish();
                //Intent i = new Intent(SplashScreen.this, MainActivity.class);
                //  startActivity(i);
                //  finish();
            }

            /**
             * Invoked when Interstitial ad failed to show.
             *
             * @param error - An object which represents the reason of showInterstitial failure.
             */
            @Override
            public void onInterstitialAdShowFailed(IronSourceError error) {
                //IronSource.showInterstitial("DefaultInterstitial");
                // new LoadAppAsyncTask(SplashScreen.this).execute();
                // Intent i = new Intent(SplashScreen.this, MainActivity.class);
                //  startActivity(i);
                //   finish();
            }

            /*
             * Invoked when the end user clicked on the interstitial ad, for supported networks only.
             */
            @Override
            public void onInterstitialAdClicked() {
            }

            /**
             * Invoked right before the Interstitial screen is about to open.
             * NOTE - This event is available only for some of the networks.
             * You should NOT treat this event as an interstitial impression, but rather use InterstitialAdOpenedEvent
             */
            @Override
            public void onInterstitialAdShowSucceeded() {
            }
        });
        IronSource.loadInterstitial();

    }

    public void onPageSelected(int position, int itemsCount) {
        try {
            if (mActionBar != null) {
                int pos = position + 1;
                if (pos > 0 && itemsCount > 0) {
                    try {
                        mSubTitle = String.format(Locale.getDefault(), "%d%s%d", pos, "/", itemsCount);
                    } catch (Exception ignore) {
                        mSubTitle = String.format(Locale.US, "%d%s%d", pos, "/", itemsCount);
                    }
                    mActionBar.setSubtitle(mSubTitle);
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    public void setActionBarTitles(String title, String subTitle) {
        if (mActionBar != null) {
            if (!TextUtils.isEmpty(mSearchQuery)) title = mSearchQuery;
            if (!TextUtils.isEmpty(title)) mActionBar.setTitle(title);
            if (!TextUtils.isEmpty(subTitle)) mActionBar.setSubtitle(subTitle);
        }
    }

    private EntryDetailFragment getCurrentEntryDetailFragmentPage() {
        try {
            ViewPager viewPager = findViewById(R.id.view_pager);
            if (viewPager != null && viewPager.getAdapter() != null) {
                int index = viewPager.getCurrentItem();
                return (EntryDetailFragment) viewPager.getAdapter().instantiateItem(viewPager, index);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
        return null;
    }

    private static class LoadCategoryAsyncTask extends AsyncTask<Integer, Void, CategoryEntity> {
        private WeakReference<EntryDetailsActivity> mActivity;

        LoadCategoryAsyncTask(EntryDetailsActivity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        protected CategoryEntity doInBackground(Integer... integers) {
            try {
                EntryDetailsActivity activity = mActivity.get();
                if (activity != null && !activity.isFinishing()) {
                    Context context = activity.getApplicationContext();
                    DataRepository repo = ((MyApplication) context).getRepository();
                    return repo.getCategoryById(integers[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CategoryEntity category) {
            try {
                EntryDetailsActivity a = mActivity.get();
                if (a != null && !a.isFinishing()) {
                    if (category == null) return;
                    if (a.mIsCategory) a.mTitle = category.getTitle();

                    if (a.mActionBar != null) {
                        if (!TextUtils.isEmpty(a.mSearchQuery)) a.mTitle = a.mSearchQuery;
                        if (!TextUtils.isEmpty(a.mTitle)) a.mActionBar.setTitle(a.mTitle);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
