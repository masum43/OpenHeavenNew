package com.newandromo.dev18147.app821162.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

//import com.mopub.mobileads.MoPubView;

import org.jetbrains.annotations.NotNull;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;

import static com.newandromo.dev18147.app821162.utils.Constants.Const.IS_ENABLE_OPEN_IN_BROWSER;

public abstract class BaseDetailActivity extends BaseActivity {
    public ActionBar mActionBar;
   // private MoPubView mMoPubViewBanner;

    private RelativeLayout mAdViewBannerContainer;

    protected abstract int getLayoutResource();

    @Override
    public void setSupportActionBar(Toolbar toolbar) {
        try {
            super.setSupportActionBar(toolbar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutResource() > 0) {
            setContentView(getLayoutResource());

            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) setSupportActionBar(toolbar);

            mActionBar = this.getSupportActionBar();
            if (mActionBar != null) {
                mActionBar.setDisplayHomeAsUpEnabled(true);
                mActionBar.setHomeButtonEnabled(true);
            }

           /* try {
                if (RemoteConfig.isShowBannerAdsEntryDetail()) {
                    if (RemoteConfig.isShowMoPubBannerAdEntryDetail()) {
                        mMoPubViewBanner = new MoPubView(this);
                        mMoPubViewBanner.setAdUnitId(RemoteConfig.getMoPubBannerIdEntryDetail());
                        mMoPubViewBanner.setKeywords(null);
                        mMoPubViewBanner.setUserDataKeywords(null);
                        mMoPubViewBanner.loadAd();
                        mAdViewBannerContainer = findViewById(R.id.bannerLayoutBottom);
                        if (null != mAdViewBannerContainer)
                            mAdViewBannerContainer.addView(mMoPubViewBanner);
                    } else if (RemoteConfig.isShowFacebookBannerAdEntryDetail()) {
                        mFacebookAdViewBanner = new com.facebook.ads.AdView(this,
                                RemoteConfig.getFacebookBannerIdEntryDetail(),
                                AppUtils.isTablet(this) ? AdSize.BANNER_HEIGHT_90 : AdSize.BANNER_HEIGHT_50);

                        mFacebookAdViewBanner.loadAd();
                        mAdViewBannerContainer = findViewById(R.id.bannerLayoutBottom);
                        if (null != mAdViewBannerContainer)
                            mAdViewBannerContainer.addView(mFacebookAdViewBanner);
                    } else {
                        mAdMobAdViewBanner = new AdView(this);
                        AdUtils.adMobBannerAd(this, mAdMobAdViewBanner, RemoteConfig.getAdMobBannerIdEntryDetail());
                    }
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            }*/

            /*if (canToggleFullscreen()) {
                getWindow().getDecorView()
                        .setOnSystemUiVisibilityChangeListener(i -> toggleToolbar(!isImmersiveMode()));
            }*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.menu_entry_detail, menu);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem fontSize = menu.findItem(R.id.menu_font_size);
        if (fontSize != null) AppUtils.tintMenuItemIcon(this, fontSize);

        MenuItem share = menu.findItem(R.id.menu_share);
        if (share != null) AppUtils.tintMenuItemIcon(this, share);

        MenuItem openInBrowser = menu.findItem(R.id.menu_open_in_browser);
        if (openInBrowser != null) {
            AppUtils.tintMenuItemIcon(this, openInBrowser);
            openInBrowser.setVisible(IS_ENABLE_OPEN_IN_BROWSER);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == R.id.menu_font_size) {
            int checkedItem = Integer.parseInt(PrefUtils.getEntryDetailFontSize(this));
            AlertDialog.Builder _builder = new AlertDialog.Builder(this);
            _builder.setTitle(R.string.pref_title_item_font_size);
            _builder.setSingleChoiceItems(getResources().getStringArray(R.array.pref_font_size_titles),
                    checkedItem,
                    (dialog, which) -> {
                        if (checkedItem == which) {
                            dialog.cancel();
                            return;
                        }
                        PrefUtils.setEntryDetailFontSize(this, String.valueOf(which));
                        dialog.dismiss();
                    });
            _builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();

       /* try {
            if (mMoPubViewBanner != null) {
                mMoPubViewBanner.destroy();
                mMoPubViewBanner = null;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }*/
    }

    /*private void toggleToolbar(boolean isShow) {
        try {
            if (mActionBar != null) {
                if (isShow) mActionBar.show();
                else mActionBar.hide();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    private boolean canToggleFullscreen() {
        return !AppUtils.isTablet(this)
                && !RemoteConfig.isShowSingleEntryDetailLayout()
                && AppUtils.hasKitKat();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void toggleImmersiveMode() {
        if (canToggleFullscreen()) {
            try {
                View decorView = getWindow().getDecorView();
                int uiOptions = decorView.getSystemUiVisibility();
                uiOptions &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
                uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
                uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
                uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

                decorView.setSystemUiVisibility(isImmersiveMode() ? 0 : uiOptions);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isImmersiveMode() {
        try {
            int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
            return ((uiOptions & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0)
                    && ((uiOptions & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0)
                    && ((uiOptions & View.SYSTEM_UI_FLAG_IMMERSIVE) != 0)
                    && ((uiOptions & View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) != 0);
        } catch (Exception ignore) {
        }
        return false;
    }*/
}
