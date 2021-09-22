package com.newandromo.dev18147.app821162.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.SplashScreen;
import com.newandromo.dev18147.app821162.ui.fragments.EntryDetailFragment;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;

import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_ENTRY_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_ENTRY_IMAGE_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_IS_CATEGORY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_IS_GO_HOME;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ENTRY_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ENTRY_IMAGE_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_CATEGORY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_GO_HOME;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_START_EXTERNAL_PLAYER;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_START_STANDALONE_PLAYER;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_WRITE_EXTERNAL_STORAGE;
import static com.newandromo.dev18147.app821162.utils.PrefUtils.PREF_ENTRY_DETAIL_FONT_SIZE;

public class EntryDetailActivity extends BaseDetailActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String FRAG_ENTRY_DETAIL = "frag_entry_detail";
    private int mCatId;
    private boolean mIsCategory;
    private int mEntryId;
    private String mEntryImageUrl;

    private boolean mIsGoHome;
    private final SharedPreferences.OnSharedPreferenceChangeListener
            mPrefListener = (sharedPreferences, key) -> {
        String[] fontChange = new String[]{PREF_ENTRY_DETAIL_FONT_SIZE};
        if (!this.isFinishing()) {
            if (Arrays.asList(fontChange).indexOf(key) != -1) {
                if (PREF_ENTRY_DETAIL_FONT_SIZE.equals(key)) {
                    recreate();
                }
            }
        }
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_entry_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppUtils.setAppTheme(this, true);
        super.onCreate(savedInstanceState);
        PrefUtils.registerOnSharedPreferenceChangeListener(this, mPrefListener);

        loadBannerAd();

        if (savedInstanceState != null) {
            mCatId = savedInstanceState.getInt(BUNDLE_ID);
            mIsCategory = savedInstanceState.getBoolean(BUNDLE_IS_CATEGORY);
            mEntryId = savedInstanceState.getInt(BUNDLE_ENTRY_ID);
            mEntryImageUrl = savedInstanceState.getString(BUNDLE_ENTRY_IMAGE_URL);
            mIsGoHome = savedInstanceState.getBoolean(BUNDLE_IS_GO_HOME, false);
        } else {

            Intent intent = getIntent();
            if (intent != null && intent.getExtras() != null) {
                mCatId = intent.getIntExtra(INTENT_EXTRA_ID, 0);
                mIsCategory = intent.getBooleanExtra(INTENT_EXTRA_IS_CATEGORY, false);
                mEntryId = intent.getIntExtra(INTENT_EXTRA_ENTRY_ID, 0);
                mEntryImageUrl = intent.getStringExtra(INTENT_EXTRA_ENTRY_IMAGE_URL);
                mIsGoHome = intent.getBooleanExtra(INTENT_EXTRA_IS_GO_HOME, false);
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in_fragment, R.anim.fade_out_fragment);
            ft.replace(R.id.entry_detail_fragment,
                    EntryDetailFragment.newInstance(mCatId, mEntryId), FRAG_ENTRY_DETAIL);
            ft.commit();
        }

        ImageView imageView = findViewById(R.id.backdrop);
        if (imageView != null) {
            try {
                if (!TextUtils.isEmpty(mEntryImageUrl)) {
                    Picasso.get()
                            .load(mEntryImageUrl)
                            .noPlaceholder()
                            .into(imageView);
                } else imageView.setVisibility(View.GONE);
            } catch (Exception ignore) {
                imageView.setVisibility(View.GONE);
            }
        }


    }

    public void loadBannerAd() {
        final FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        IronSourceBannerLayout banner = IronSource.createBanner(this, ISBannerSize.BANNER);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        bannerContainer.addView(banner, 0, layoutParams);

        banner.setBannerListener(new BannerListener() {
            @Override
            public void onBannerAdLoaded() {
                // Called after a banner ad has been successfully loaded
            }

            @Override
            public void onBannerAdLoadFailed(IronSourceError error) {
                // Called after a banner has attempted to load an ad but failed.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bannerContainer.removeAllViews();
                    }
                });
            }

            @Override
            public void onBannerAdClicked() {
                // Called after a banner has been clicked.
            }

            @Override
            public void onBannerAdScreenPresented() {
                // Called when a banner is about to present a full screen content.
            }

            @Override
            public void onBannerAdScreenDismissed() {
                // Called after a full screen content has been dismissed
            }

            @Override
            public void onBannerAdLeftApplication() {
                // Called when a user would be taken out of the application context.
            }
        });

        IronSource.loadBanner(banner, "DefaultBanner");
    }


    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_ID, mCatId);
        outState.putBoolean(BUNDLE_IS_CATEGORY, mIsCategory);
        outState.putInt(BUNDLE_ENTRY_ID, mEntryId);
        outState.putString(BUNDLE_ENTRY_IMAGE_URL, mEntryImageUrl);
        outState.putBoolean(BUNDLE_IS_GO_HOME, mIsGoHome);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_START_STANDALONE_PLAYER || requestCode == REQ_START_EXTERNAL_PLAYER) {
            // Timber.d("onActivityResult() back from YoutubePlayerActivity");
            EntryDetailFragment edf = (EntryDetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(FRAG_ENTRY_DETAIL);
            if (edf != null && edf.isAdded()) edf.reloadEntryDetail();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_WRITE_EXTERNAL_STORAGE) {
            EntryDetailFragment edf = (EntryDetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(FRAG_ENTRY_DETAIL);
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
    public void onBackPressed() {
        if (!mIsGoHome) {

          super.onBackPressed();
        } else goHome();
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
