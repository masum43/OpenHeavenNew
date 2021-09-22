package com.newandromo.dev18147.app821162.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.newandromo.dev18147.app821162.AppRater;
import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.SplashScreen;
import com.newandromo.dev18147.app821162.ui.fragments.EntryListFragment;
import com.newandromo.dev18147.app821162.ui.fragments.EntryListViewPagerFragment;
import com.newandromo.dev18147.app821162.ui.fragments.ThemeDialogFragment;
import com.newandromo.dev18147.app821162.ui.fragments.YoutubeTypeFragment;
import com.newandromo.dev18147.app821162.ui.fragments.YoutubeTypeViewPagerFrag;
import com.newandromo.dev18147.app821162.ui.listener.EntryListEventListener;
import com.newandromo.dev18147.app821162.ui.listener.TabsEventListener;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.youtube.player.YouTubeIntents;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.APP_MARKET_URL_GOOGLE;
import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.MAIL_TO;
import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.TEL;
import static com.newandromo.dev18147.app821162.utils.PrefUtils.PREF_APP_THEME;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabsEventListener, EntryListEventListener {
    public static final int ALL_ITEMS = 1, BOOKMARKS = 2 /*, VIDEOS = 3*/;
    private static MainActivity mainActivity;
    private final SharedPreferences.OnSharedPreferenceChangeListener
            mPrefListener = (sharedPreferences, key) -> {
        String[] filter = new String[]{PREF_APP_THEME};
        if (Arrays.asList(filter).indexOf(key) != -1) {
            if (PREF_APP_THEME.equals(key)) {
                recreate();
            }
        }
    };
    boolean doubleBackToExitPressedOnce = false;
    private ActionBar mActionBar;
    private AppBarLayout mAppBarLayout;
    private boolean mViewIsAtHome;
    // @Nullable PersonalInfoManager mPersonalInfoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppUtils.setAppTheme(this, true);
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_main);
        PrefUtils.registerOnSharedPreferenceChangeListener(this, mPrefListener);

        AppRater.app_launched(this);

        loadBannerAd();
        /*try {
            HashMap<String, String> adMobConfig = new HashMap<>();
            adMobConfig.put("npa", "1");

            SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(RemoteConfig.getMoPubNativeIdEntryList())
                    .withMediatedNetworkConfiguration(GooglePlayServicesAdapterConfiguration.class.getName(), adMobConfig)
                    .build();
            MoPub.initializeSdk(this, sdkConfiguration, initSdkListener());

            mPersonalInfoManager = MoPub.getPersonalInformationManager();
            if (mPersonalInfoManager != null) {
                mPersonalInfoManager.subscribeConsentStatusChangeListener(initConsentChangeListener());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            mActionBar = this.getSupportActionBar();
            toolbar.setOnClickListener(v -> PrefUtils.setToolbarClicked(
                    MainActivity.this, !PrefUtils.isToolbarClicked(MainActivity.this)));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        mAppBarLayout = findViewById(R.id.appbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.nav_extra_web)
                .setVisible(!TextUtils.isEmpty(RemoteConfig.getPromoteMusicUrl()));
        navigationView.getMenu().findItem(R.id.nav_web)
                .setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_social_facebook)
                .setVisible(!TextUtils.isEmpty(RemoteConfig.getFacebookUrl()));
        navigationView.getMenu().findItem(R.id.nav_social_instagram)
                .setVisible(!TextUtils.isEmpty(RemoteConfig.getInstagramUrl()));
        navigationView.getMenu().findItem(R.id.nav_social_twitter)
                .setVisible(!TextUtils.isEmpty(RemoteConfig.getTwitterUrl()));
        navigationView.getMenu().findItem(R.id.nav_social_youtube)
                .setVisible(!TextUtils.isEmpty(RemoteConfig.getYoutubeUrl()));
        navigationView.getMenu().findItem(R.id.nav_contact_phone)
                .setVisible(!TextUtils.isEmpty(RemoteConfig.getClientPhone()));
        navigationView.getMenu().findItem(R.id.nav_contact_email)
                .setVisible(!TextUtils.isEmpty(RemoteConfig.getClientEmail()));

        try {
            View header = navigationView.getHeaderView(0);
            if (header != null) {
               /* ImageView appIcon = header.findViewById(R.id.appIcon);
                int appIconVisibility = RemoteConfig.isShowAppIcon() ? View.VISIBLE : View.GONE;
                appIcon.setVisibility(appIconVisibility);

                TextView headerTitle = header.findViewById(R.id.header_title);
                *//*String appName = !TextUtils.isEmpty(RemoteConfig.getAppName()) ?
                        RemoteConfig.getAppName() : getString(R.string.app_name);
                headerTitle.setText(appName);*//*
                int hTitleVisibility = RemoteConfig.isShowHeaderTitle() ? View.VISIBLE : View.GONE;
                headerTitle.setVisibility(hTitleVisibility);*/

                /*try {
                    ImageView headerImage = header.findViewById(R.id.header_image);
                    if (!TextUtils.isEmpty(RemoteConfig.getMenuHeaderImageUrl())) {
                        Picasso.get()
                                .load(RemoteConfig.getMenuHeaderImageUrl())//R.drawable.header
                                .fit().centerCrop()
                                .placeholder(R.color.drawer_header_image_placeholder)
                                .error(R.drawable.header)
                                .noFade()
                                .into(headerImage);
                    } else {
                        Picasso.get()
                                .load(R.drawable.header)//R.drawable.header
                                .fit().centerCrop()
                                .placeholder(R.color.drawer_header_image_placeholder)
                                .error(R.drawable.header)
                                .noFade()
                                .into(headerImage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        displayView(PrefUtils.getNavDrawerSelectedItem(this));
    }

    public void loadBannerAd() {
        final FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        SplashScreen.showApplobinBanner(this, bannerContainer);

//        IronSourceBannerLayout banner = IronSource.createBanner(this, ISBannerSize.BANNER);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT);
//        bannerContainer.addView(banner, 0, layoutParams);
//
//        banner.setBannerListener(new BannerListener() {
//            @Override
//            public void onBannerAdLoaded() {
//                // Called after a banner ad has been successfully loaded
//            }
//
//            @Override
//            public void onBannerAdLoadFailed(IronSourceError error) {
//                // Called after a banner has attempted to load an ad but failed.
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        bannerContainer.removeAllViews();
//                    }
//                });
//            }
//
//            @Override
//            public void onBannerAdClicked() {
//                // Called after a banner has been clicked.
//            }
//
//            @Override
//            public void onBannerAdScreenPresented() {
//                // Called when a banner is about to present a full screen content.
//            }
//
//            @Override
//            public void onBannerAdScreenDismissed() {
//                // Called after a full screen content has been dismissed
//            }
//
//            @Override
//            public void onBannerAdLeftApplication() {
//                // Called when a user would be taken out of the application context.
//            }
//        });
//
//        IronSource.loadBanner(banner, "DefaultBanner");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        android.view.MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) AppUtils.tintMenuItemIcon(this, searchItem);

        android.view.MenuItem toggleLayout = menu.findItem(R.id.action_toggle_layout);
        if (toggleLayout != null) AppUtils.tintMenuItemIcon(this, toggleLayout);

        android.view.MenuItem languageItem = menu.findItem(R.id.action_language);
        if (languageItem != null) AppUtils.tintMenuItemIcon(this, languageItem);

        android.view.MenuItem markAllAsRead = menu.findItem(R.id.action_mark_all_as_read);
        if (markAllAsRead != null) AppUtils.tintMenuItemIcon(this, markAllAsRead);

        android.view.MenuItem toggleUnread = menu.findItem(R.id.action_toggle_unread);
        if (toggleUnread != null) AppUtils.tintMenuItemIcon(this, toggleUnread);

        android.view.MenuItem sortByDate = menu.findItem(R.id.action_sort_by_date);
        if (sortByDate != null) AppUtils.tintMenuItemIcon(this, sortByDate);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*try {
            if (PrefUtils.isPopupAppRater(this)) {
                PrefUtils.setIsPopupAppRater(this, false);

                if (!PrefUtils.isDisableAppRater(this)) {
                    AppRater appRater = new AppRater(this);
                    appRater.initializeAppRater(false); // normal use
                    // appRater.showTestRateDialog(); // test mode
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!mViewIsAtHome) { // if the current view is not the Home fragment
                displayView(R.id.nav_all_items); // display the Home fragment.
            } else {
                EntryListFragment elf = getCurrentEntryListFragmentPage();
                if (elf != null) {
                    if (!TextUtils.isEmpty(elf.getSearchQuery())) {
                        elf.resetSearchQuery();
                        int itemId = PrefUtils.getNavDrawerSelectedItem(this);
                        Fragment frag = getSupportFragmentManager().findFragmentByTag("frag" + itemId);
                        if (frag instanceof EntryListViewPagerFragment) {
                            EntryListViewPagerFragment el = (EntryListViewPagerFragment) frag;
                            el.loadCategories();
                            return;
                        }
                    }
                }
                // super.onBackPressed();  // If view is in Home fragment, exit application.
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;

                if (BuildConfig.DEBUG)
                    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 3000);
            }
        }
    }

    public void getSearchResult() {
        new Handler().postDelayed(() -> {
            EntryListFragment elf = getCurrentEntryListFragmentPage();
            if (elf != null) {
                if (!TextUtils.isEmpty(elf.getSearchQuery())) {
                    int itemId = PrefUtils.getNavDrawerSelectedItem(this);
                    Fragment frag = getSupportFragmentManager().findFragmentByTag("frag" + itemId);
                    if (frag instanceof EntryListViewPagerFragment) {
                        EntryListViewPagerFragment el = (EntryListViewPagerFragment) frag;
                        el.loadCategories();
                    }
                }
            }
        }, TimeUnit.MILLISECONDS.toMillis(500));
    }

    @Override
    public void onEntryListItemSelected() {
        String tag = "frag" + PrefUtils.getNavDrawerSelectedItem(this);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof EntryListViewPagerFragment) {
            EntryListViewPagerFragment frag = (EntryListViewPagerFragment) fragment;
            frag.showInterstitialAd();
        }
    }

    @Override
    public void onTabSelected(int position, boolean isVideoList) {
        if (isVideoList) {
            YoutubeTypeFragment ylf = getCurrentYoutubeTypeFragmentPage();
            if (ylf != null && ylf.isAdded()) ylf.scrollToTop(false);
        } else {
            EntryListFragment elf = getCurrentEntryListFragmentPage();
            if (elf != null && elf.isAdded()) elf.scrollToTop(false);
        }
    }

    public void setActionBarTitles(String title, String subTitle) {
        if (mActionBar != null) {
            if (!TextUtils.isEmpty(title)) mActionBar.setTitle(title);
            mActionBar.setSubtitle(subTitle);
        }
    }

    private void resetAppBarLayoutScroll() {
        new Handler().postDelayed(() -> {
            try {
                if (null != mAppBarLayout) mAppBarLayout.setExpanded(true, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, TimeUnit.SECONDS.toMillis(1));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displayView(item.getItemId());
        return true;
    }

    private void displayView(int itemId) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        NavigationView navigationView = findViewById(R.id.nav_view);
        MenuItem menuItem = navigationView.getMenu().findItem(itemId);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("frag" + itemId);
        switch (itemId) {
            case R.id.nav_all_items:
                if (fragment == null)
                    fragment = EntryListViewPagerFragment.newInstance(ALL_ITEMS);
                PrefUtils.setNavDrawerSelectedItem(this, R.id.nav_all_items);
                mViewIsAtHome = true;
                break;
            case R.id.nav_bookmarks:
                if (fragment == null)
                    fragment = EntryListViewPagerFragment.newInstance(BOOKMARKS);
                PrefUtils.setNavDrawerSelectedItem(this, R.id.nav_bookmarks);
                mViewIsAtHome = false;
                break;
            case R.id.nav_videos:
                if (fragment == null)
                    fragment = YoutubeTypeViewPagerFrag.newInstance();
                PrefUtils.setNavDrawerSelectedItem(this, R.id.nav_videos);
                mViewIsAtHome = false;
                break;

            case R.id.nav_extra_web:
                AppUtils.chromeCustomTabs(this, RemoteConfig.getPromoteMusicUrl());
                break;
            case R.id.nav_web:
                AppUtils.chromeCustomTabs(this, "https://www.ambydennis.com");
                break;
            case R.id.nav_social_facebook:
                AppUtils.openExternalBrowser(this, RemoteConfig.getFacebookUrl());
                break;
            case R.id.nav_social_instagram:
                AppUtils.openExternalBrowser(this, RemoteConfig.getInstagramUrl());
                break;
            case R.id.nav_social_twitter:
                AppUtils.openExternalBrowser(this, RemoteConfig.getTwitterUrl());
                break;
            case R.id.nav_social_youtube:
                try {
                    String uniqueId = RemoteConfig.getYoutubeUrl()
                            .replace("http://www.youtube.com/channel/", "")
                            .replace("https://www.youtube.com/channel/", "");

                    Intent ytIntent = YouTubeIntents.createChannelIntent(this, uniqueId);
                    startActivity(ytIntent);
                } catch (Exception e) {
                    AppUtils.openExternalBrowser(this, RemoteConfig.getYoutubeUrl());
                }
                break;
            case R.id.nav_contact_phone:
                String[] CLIENT_PHONES = TextUtils.split(RemoteConfig.getClientPhone(), ";");
                AlertDialog.Builder builderPhone = new AlertDialog.Builder(MainActivity.this);
                builderPhone.setTitle(getString(R.string.nav_drawer_mobile));
                builderPhone.setItems(CLIENT_PHONES, (dialog, which) -> {
                    String uriString = String.format(TEL, CLIENT_PHONES[which]
                            .replace(" - TTCL", "")
                            .replace(" - Airtel", "")
                            .replace(" - Halotel", "")
                            .replace(" - Tigo", "")
                            .replace(" - WhatsApp", ""));
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(uriString)));
                });
                builderPhone.show();
                break;
            case R.id.nav_contact_email:
                String[] CLIENT_EMAILS = TextUtils.split(RemoteConfig.getClientEmail(), ";");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.nav_drawer_email));
                builder.setItems(CLIENT_EMAILS, (dialog, which) -> {
                    String uriString = String.format(MAIL_TO, CLIENT_EMAILS[which]);
                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(uriString)));
                });
                builder.show();
                break;
            case R.id.nav_style:
                ThemeDialogFragment themeDialog = new ThemeDialogFragment();
                themeDialog.show(getSupportFragmentManager(), "theme");
                break;
            case R.id.nav_rate_app:
                try {
                    String uriString = String.format(APP_MARKET_URL_GOOGLE, getPackageName());
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
                } catch (Exception ignore) {
                }
                break;
            case R.id.nav_share_app:
                Bundle shareBundle = AppUtils.createShareBundle("",
                        RemoteConfig.getAppStoreUrl(), "", "", "");
                AppUtils.appShare(this, getSupportFragmentManager(), shareBundle);
                break;
            case R.id.nav_settings:
                AppUtils.openSettingsActivity(this);
                break;
            case R.id.nav_info:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in_fragment, R.anim.fade_out_fragment);
            ft.replace(R.id.content_frame, fragment, "frag" + itemId);
            ft.commit();

            String title = !TextUtils.isEmpty(RemoteConfig.getAppName()) ?
                    RemoteConfig.getAppName() : getString(R.string.app_name);
            if (menuItem != null) {
                if (itemId == R.id.nav_bookmarks || itemId == R.id.nav_videos) {
                    title = String.valueOf(menuItem.getTitle());
                }
                menuItem.setChecked(true);
            }

            // set the toolbar title
            setActionBarTitles(title, "");
            resetAppBarLayoutScroll();
        }

        // DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // drawer.closeDrawer(GravityCompat.START);
    }

    public EntryListFragment getCurrentEntryListFragmentPage() {
        try {
            ViewPager viewPager = findViewById(R.id.view_pager);
            if (null != viewPager && viewPager.getAdapter() != null) {
                int index = viewPager.getCurrentItem();
                Fragment fragment = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, index);
                if (fragment instanceof EntryListFragment)
                    return (EntryListFragment) fragment;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public YoutubeTypeFragment getCurrentYoutubeTypeFragmentPage() {
        try {
            ViewPager viewPager = findViewById(R.id.view_pager);
            if (null != viewPager && viewPager.getAdapter() != null) {
                int index = viewPager.getCurrentItem();
                Fragment fragment = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, index);
                if (fragment instanceof YoutubeTypeFragment)
                    return (YoutubeTypeFragment) fragment;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*private SdkInitializationListener initSdkListener() {
        return () -> {
            if (BuildConfig.DEBUG) Timber.d("MoPub SDK initialized.");
            if (mPersonalInfoManager != null && mPersonalInfoManager.shouldShowConsentDialog()) {
                mPersonalInfoManager.loadConsentDialog(initDialogLoadListener());
            }
        };
    }

    private ConsentStatusChangeListener initConsentChangeListener() {
        return (oldConsentStatus, newConsentStatus, canCollectPersonalInformation) -> {
            if (BuildConfig.DEBUG) {
                Timber.d("oldConsentStatus: %s \nnewConsentStatus: %s",
                        oldConsentStatus.name(), newConsentStatus.name());
            }
            if (mPersonalInfoManager != null && mPersonalInfoManager.shouldShowConsentDialog()) {
                mPersonalInfoManager.loadConsentDialog(initDialogLoadListener());
            }
        };
    }

    private ConsentDialogListener initDialogLoadListener() {
        return new ConsentDialogListener() {

            @Override
            public void onConsentDialogLoaded() {
                if (mPersonalInfoManager != null) {
                    mPersonalInfoManager.showConsentDialog();
                }
            }

            @Override
            public void onConsentDialogLoadFailed(@NonNull MoPubErrorCode moPubErrorCode) {
                if (BuildConfig.DEBUG) {
                    Timber.e("onConsentDialogLoadFailed() Consent dialog failed to load. errorCode=%s"
                            , moPubErrorCode);
                }
            }
        };
    }*/
}
