package com.newandromo.dev18147.app821162.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;


import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;

import org.jetbrains.annotations.NotNull;

import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.ui.fragments.LegalDocsFragment;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;

public class YoutubeSearchActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppUtils.setAppTheme(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_search);
        loadBannerAd();

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) AppUtils.tintMenuItemIcon(this, searchItem);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_player_options:
                final int checkedItem = Integer.parseInt(PrefUtils.getYouTubePlayerOption(YoutubeSearchActivity.this));
                AlertDialog.Builder _builder = new AlertDialog.Builder(this);
                _builder.setTitle(R.string.youtube_player_options);
                _builder.setSingleChoiceItems(getResources().getStringArray(R.array.pref_youtube_player_modes_entries),
                        checkedItem,
                        (dialog, which) -> {
                            if (checkedItem == which) {
                                dialog.cancel();
                                return;
                            }
                            PrefUtils.setYouTubePlayerOption(YoutubeSearchActivity.this, String.valueOf(which));
                            dialog.cancel();
                        });
                _builder.show();
                return true;
            case R.id.menu_youtube_terms:
                LegalDocsFragment legalDocsFragment = LegalDocsFragment.newInstance(false);
                legalDocsFragment.show(getSupportFragmentManager(), "legalDocsFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    }

    @Override
    public void onBackPressed() {

            super.onBackPressed();
            AppUtils.onActivityEnterExit(this);

    }
}
