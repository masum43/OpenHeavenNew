package com.newandromo.dev18147.app821162.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.tabs.TabLayout;
//import com.mopub.mobileads.MoPubInterstitial;
//import com.mopub.mobileads.MoPubView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.MyAnalytics;
import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.db.DataGenerator;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.CategoryEntity;
import com.newandromo.dev18147.app821162.db.entity.FeedEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeTypeEntity;
import com.newandromo.dev18147.app821162.ui.activities.MainActivity;
import com.newandromo.dev18147.app821162.ui.listener.TabsEventListener;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_POSITION;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.ALL_ENTRIES;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.LAYOUT_TYPE_DOUBLE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.LAYOUT_TYPE_LARGE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.LAYOUT_TYPE_LIST;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.UNREAD;
import static com.newandromo.dev18147.app821162.utils.PrefUtils.PREF_FEEDS_LANGUAGE_CODE;
import static com.newandromo.dev18147.app821162.utils.PrefUtils.PREF_GLOBAL_REFRESH;
import static com.newandromo.dev18147.app821162.utils.PrefUtils.PREF_LAYOUT_TYPE;
import static com.newandromo.dev18147.app821162.utils.PrefUtils.PREF_SORT_BY_DATE;
import static com.newandromo.dev18147.app821162.utils.PrefUtils.PREF_VIEW_MODE;

public class EntryListViewPagerFragment extends Fragment implements
        TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {
    private final SharedPreferences.OnSharedPreferenceChangeListener
            mPrefListener = (sharedPreferences, key) -> {
        String[] filter = new
                String[]{PREF_FEEDS_LANGUAGE_CODE, PREF_LAYOUT_TYPE, PREF_VIEW_MODE,
                PREF_SORT_BY_DATE, PREF_GLOBAL_REFRESH};
        if (Arrays.asList(filter).indexOf(key) != -1) {
            switch (key) {
                case PREF_FEEDS_LANGUAGE_CODE:
                    switchFeeds();
                    break;
                case PREF_LAYOUT_TYPE:
                case PREF_VIEW_MODE:
                case PREF_SORT_BY_DATE:
                case PREF_GLOBAL_REFRESH:
                    loadCategories();
                    break;
            }
        }
    };
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabsEventListener mTabsEventListener;
    private EntryListPagerAdapter mAdapter;
    private int mPosition;

  //  private AdView mAdMobAdViewBanner;
    private RelativeLayout mAdViewBannerContainer;
    private int mCurrentItem;
  //  private MoPubInterstitial mMoPubInterstitial;

 //   private InterstitialAd mAdMobInterstitialAd;

    public EntryListViewPagerFragment() {
        // Required empty public constructor
    }

    public static EntryListViewPagerFragment newInstance(int position) {
        EntryListViewPagerFragment frag = new EntryListViewPagerFragment();
        Bundle b = new Bundle();
        b.putInt(BUNDLE_POSITION, position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            mTabsEventListener = (TabsEventListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrefUtils.registerOnSharedPreferenceChangeListener(getActivity(), mPrefListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getActivity() != null) {
            RelativeLayout layoutBanner = this.getActivity().findViewById(R.id.bannerLayoutBottom);
            if (layoutBanner != null) layoutBanner.removeAllViews();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager, container, false);

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(BUNDLE_POSITION);
            mCurrentItem = savedInstanceState.getInt("currentItem");
        } else if (getArguments() != null) {
            mPosition = getArguments().getInt(BUNDLE_POSITION);
        }

        mViewPager = view.findViewById(R.id.view_pager);

        mAdapter = new EntryListPagerAdapter(getChildFragmentManager());
        mAdapter.addFragment(new DummyFragment(), "");

        mViewPager.setAdapter(mAdapter);

        if (getActivity() != null) {
            mTabLayout = getActivity().findViewById(R.id.tabs);
            if (mTabLayout != null) {
                mTabLayout.setupWithViewPager(mViewPager, true);
                mTabLayout.addOnTabSelectedListener(this);

                if (MainActivity.ALL_ITEMS == mPosition) {
                    if (mTabLayout.getTabCount() > 1) {
                        mTabLayout.setVisibility(View.VISIBLE);
                    } else {
                        mTabLayout.setVisibility(View.GONE);
                    }
                } else {
                    mTabLayout.setVisibility(View.GONE);
                }
            }
        }

        // Work-around a bug that sometimes happens with the tabs
        mViewPager.setCurrentItem(0);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // initBannerAd();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_POSITION, mPosition);
        outState.putInt("currentItem", mViewPager.getCurrentItem());
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        try {
            inflater.inflate(R.menu.menu_entry_list, menu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NotNull Menu menu) {
        MenuItem languageItem = menu.findItem(R.id.action_language);
        if (languageItem != null)
            languageItem.setVisible(RemoteConfig.isEnableFeedLanguageOption());

        MenuItem toggleLayout = menu.findItem(R.id.action_toggle_layout);
        if (toggleLayout != null) {
            String layoutType = PrefUtils.getLayoutType(getActivity());
            if (LAYOUT_TYPE_LIST.equals(layoutType)) {
                toggleLayout.setIcon(R.drawable.ic_layout_double);
            } else if (LAYOUT_TYPE_DOUBLE.equals(layoutType)) {
                toggleLayout.setIcon(R.drawable.ic_layout_large);
            } else if (LAYOUT_TYPE_LARGE.equals(layoutType)) {
                toggleLayout.setIcon(R.drawable.ic_layout_list);
            }
            AppUtils.tintMenuItemIcon(getActivity(), toggleLayout);
            toggleLayout.setVisible(RemoteConfig.isEnableLayoutToggle());
        }

        MenuItem refresh = menu.findItem(R.id.menu_refresh);
        if (refresh != null) refresh.setVisible(false);

        MenuItem markAllAsRead = menu.findItem(R.id.action_mark_all_as_read);
        if (markAllAsRead != null) markAllAsRead.setVisible(false);

        MenuItem toggleUnread = menu.findItem(R.id.action_toggle_unread);
        if (toggleUnread != null) {
            if (UNREAD.equals(PrefUtils.getViewMode(getActivity()))) {
                toggleUnread.setTitle(R.string.action_view_all)
                        .setIcon(R.drawable.ic_visibility_white_24dp);
                AppUtils.tintMenuItemIcon(getActivity(), toggleUnread);
            } else {
                toggleUnread.setTitle(R.string.action_view_only_unread)
                        .setIcon(R.drawable.ic_visibility_off_white_24dp);
            }

            toggleUnread.setVisible(MainActivity.BOOKMARKS != mPosition);
            toggleUnread.setVisible(false);
        }

        MenuItem sortItem = menu.findItem(R.id.action_sort_by_date);
        if (sortItem != null) sortItem.setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_language:
                if (getActivity() == null) return false;
                List<String> languageCodes = new ArrayList<>();
                String[] languageNamesArray;

                try {
                    String jsonString = RemoteConfig.getDefaultFeedsJson();

                    if (TextUtils.isEmpty(jsonString)) {
                        jsonString = AppUtils.getAssetJsonData(getActivity(), "default_feeds_new.json");
                    }

                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray defaultsArray = jsonObject.getJSONArray("defaults");

                    languageNamesArray = new String[defaultsArray.length()];

                    for (int i = 0; i < defaultsArray.length(); i++) {
                        languageCodes.add(defaultsArray.getJSONObject(i).getString("id"));
                        languageNamesArray[i] = defaultsArray.getJSONObject(i).getString("name");
                    }

                    String languageCode = PrefUtils.getFeedsLanguageCode(getActivity());
                    int index = languageCodes.contains(languageCode) ?
                            languageCodes.indexOf(languageCode) : 0;

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.pref_title_post_language));
                    builder.setSingleChoiceItems(languageNamesArray,
                            index,
                            (dialog, which) -> {
                                if (index == which) {
                                    dialog.cancel();
                                    return;
                                }
                                PrefUtils.setFeedsLanguageCode(getActivity(), languageCodes.get(which));
                                MyAnalytics.setFeedsLanguage(getActivity(), languageCodes.get(which));
                                dialog.dismiss();
                            });
                    builder.show();
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        Timber.e("onOptionsItemSelected(action_language) error= %s", e.getMessage());
                        e.printStackTrace();
                    }
                }
                return true;
            case R.id.action_toggle_unread:
                new Thread(() -> {
                    if (getActivity() != null) {
                        try {
                            Context context = getActivity().getApplicationContext();
                            DataRepository repo = ((MyApplication) context).getRepository();
                            repo.resetAllRecentRead();

                            getActivity().runOnUiThread(() -> {
                                String viewMode1 = PrefUtils.getViewMode(getActivity());
                                PrefUtils.setViewMode(getActivity(), UNREAD.equals(viewMode1) ? ALL_ENTRIES : UNREAD);
                                if (getActivity() != null) getActivity().invalidateOptionsMenu();
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return true;
            case R.id.action_sort_by_date:
                PrefUtils.setIsOldestFirst(getActivity(), !PrefUtils.isOldestFirst(getActivity()));
                // getActivity().invalidateOptionsMenu();
                return true;
            case R.id.action_toggle_layout:
                String layoutType = PrefUtils.getLayoutType(getActivity());
                if (LAYOUT_TYPE_LIST.equals(layoutType)) {
                    PrefUtils.setLayoutType(getActivity(), LAYOUT_TYPE_DOUBLE);
                } else if (LAYOUT_TYPE_DOUBLE.equals(layoutType)) {
                    PrefUtils.setLayoutType(getActivity(), LAYOUT_TYPE_LARGE);
                } else if (LAYOUT_TYPE_LARGE.equals(layoutType)) {
                    PrefUtils.setLayoutType(getActivity(), LAYOUT_TYPE_LIST);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadCategories();
    }

   /* private void initBannerAd() {
        try {
            if (getActivity() != null) {
                if (RemoteConfig.isShowBannerAdsEntryList()) {
                    if (RemoteConfig.isShowMoPubBannerAdEntryList()) {
                        mMoPubViewBanner = new MoPubView(getActivity());
                        mMoPubViewBanner.setAdUnitId(RemoteConfig.getMoPubBannerIdEntryList());
                        mMoPubViewBanner.setKeywords(null);
                        mMoPubViewBanner.setUserDataKeywords(null);
                        mMoPubViewBanner.loadAd();
                        mAdViewBannerContainer = getActivity().findViewById(R.id.bannerLayoutBottom);
                        mAdViewBannerContainer.addView(mMoPubViewBanner);
                    } else if (RemoteConfig.isShowFacebookBannerAdEntryList()) {
                        mFacebookAdViewBanner = new com.facebook.ads.AdView(getActivity(),
                                RemoteConfig.getFacebookBannerIdEntryList(),
                                AppUtils.isTablet(getActivity()) ? AdSize.BANNER_HEIGHT_90 : AdSize.BANNER_HEIGHT_50);
                        mFacebookAdViewBanner.loadAd();
                        mAdViewBannerContainer = getActivity().findViewById(R.id.bannerLayoutBottom);
                        mAdViewBannerContainer.addView(mFacebookAdViewBanner);
                    } else {
                        mAdMobAdViewBanner = new AdView(getActivity());
                        AdUtils.adMobBannerAd(getActivity(), mAdMobAdViewBanner, RemoteConfig.getAdMobBannerIdEntryList());
                    }
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Timber.d("initBannerAd() error= %s", e.getMessage());
                e.printStackTrace();
            }
        }
    }*/

  /*  private void initInterstitialAd() {
        try {
            if (getActivity() != null) {
                if (RemoteConfig.isShowMoPubInterstitialEntryList()) {
                    if (mMoPubInterstitial == null) {
                        mMoPubInterstitial = new MoPubInterstitial(getActivity(),
                                RemoteConfig.getMoPubInterstitialIdEntryList());
                    }
                    AdUtils.moPubInterstitialAd(mMoPubInterstitial);
                } else if (RemoteConfig.isShowFBInterstitialEntryList()) {
                    if (mFacebookInterstitialAd != null) {
                        mFacebookInterstitialAd.destroy();
                        mFacebookInterstitialAd = null;
                    }
                    mFacebookInterstitialAd =
                            new com.facebook.ads.InterstitialAd(getActivity(),
                                    RemoteConfig.getFbInterstitialIdEntryList());
                    AdUtils.facebookInterstitialAd(mFacebookInterstitialAd);
                } else {
                    mAdMobInterstitialAd = new InterstitialAd(getActivity());
                    AdUtils.adMobInterstitialAd(getActivity(),
                            mAdMobInterstitialAd, RemoteConfig.getAdMobInterstitialIdEntryList(), false);
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    public void showInterstitialAd() {
        try {

        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    public void loadCategories() {
        new LoadCategoriesAsyncTask(this).execute();
    }

    private void switchFeeds() {
        new SwitchLanguageAsyncTask(this).execute();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        mTabsEventListener.onTabSelected(tab.getPosition(), false);
    }

    @Override
    public void onPageSelected(int position) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onResume() {
        super.onResume();
        /*initInterstitialAd();

        try {
            if (mAdMobAdViewBanner != null) mAdMobAdViewBanner.resume();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onPause() {
      /*  try {
            if (mAdMobAdViewBanner != null) mAdMobAdViewBanner.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        super.onPause();
    }

    @Override
    public void onDestroy() {
        /*try {
            if (mAdMobAdViewBanner != null) mAdMobAdViewBanner.destroy();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
*/



        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        try {
            if (mAdViewBannerContainer != null) mAdViewBannerContainer.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroyView();

       /* try {
            if (mMoPubInterstitial != null) {
                mMoPubInterstitial.destroy();
                mMoPubInterstitial = null;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }*/

       /* try {
            if (mMoPubViewBanner != null) {
                mMoPubViewBanner.destroy();
                mMoPubViewBanner = null;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }*/
    }

    private static class LoadCategoriesAsyncTask extends AsyncTask<Void, Void, List<CategoryEntity>> {
        private WeakReference<EntryListViewPagerFragment> mFragment;

        LoadCategoriesAsyncTask(EntryListViewPagerFragment fragment) {
            this.mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected List<CategoryEntity> doInBackground(Void... voids) {
            try {
                EntryListViewPagerFragment frag = mFragment.get();
                if (frag != null && frag.isAdded() && frag.getActivity() != null) {
                    Context context = frag.getActivity().getApplicationContext();
                    DataRepository repo = ((MyApplication) context).getRepository();
                    return repo.getAllCategories();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<CategoryEntity> categories) {
            try {
                EntryListViewPagerFragment frag = mFragment.get();
                if (frag != null && frag.isAdded()) {
                    if (categories == null) return;

                    frag.mAdapter = new EntryListPagerAdapter(frag.getChildFragmentManager());

                    if (categories.isEmpty())
                        frag.mAdapter.addFragment(new DummyFragment(), "");
                    else if (RemoteConfig.isShowFeedCategories()) {
                        for (int i = 0; i < categories.size(); i++) {
                            boolean isCategory = true;
                            CategoryEntity c = categories.get(i);
                            frag.mAdapter.addFragment(EntryListFragment
                                    .newInstance(frag.mPosition, c.getId(), isCategory), c.getTitle());
                        }
                    } else {
                        CategoryEntity c = categories.get(0);
                        frag.mAdapter.addFragment(EntryListFragment
                                .newInstance(frag.mPosition, c.getId(), false), c.getTitle());
                    }

                    if (frag.mViewPager != null) {
                        frag.mViewPager.setAdapter(frag.mAdapter);
                        frag.mViewPager.setCurrentItem(frag.mCurrentItem);
                    }

                    if (frag.mTabLayout != null) {
                        if (MainActivity.ALL_ITEMS == frag.mPosition) {
                            if (frag.mTabLayout.getTabCount() > 1) {
                                frag.mTabLayout.setVisibility(View.VISIBLE);
                            } else {
                                frag.mTabLayout.setVisibility(View.GONE);
                            }
                        } else {
                            frag.mTabLayout.setVisibility(View.GONE);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class SwitchLanguageAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<EntryListViewPagerFragment> mFragment;

        SwitchLanguageAsyncTask(EntryListViewPagerFragment fragment) {
            this.mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                EntryListViewPagerFragment frag = mFragment.get();
                if (frag != null && frag.isAdded() && frag.getActivity() != null) {
                    Context context = frag.getActivity().getApplicationContext();
                    DataRepository repo = ((MyApplication) context).getRepository();

                    repo.deleteAllFeeds();
                    repo.deleteAllCategories();
                    repo.deleteAllYoutubeTypes();

                    String jsonString = RemoteConfig.getDefaultFeedsJson();

                    if (TextUtils.isEmpty(jsonString)) {
                        jsonString = AppUtils.getAssetJsonData(context, "default_feeds_new.json");
                    } else {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        if (!jsonObject.isNull("version")) {
                            int remoteVersion = jsonObject.getInt("version");
                            PrefUtils.setDefaultFeedsVersion(context, remoteVersion);
                        }
                    }

                    JSONObject jsonObject = new JSONObject(jsonString);

                    JSONArray defaultsArray = jsonObject.getJSONArray("defaults");

                    List<String> languageCodes = new ArrayList<>();
                    for (int i = 0; i < defaultsArray.length(); i++) {
                        languageCodes.add(defaultsArray.getJSONObject(i).getString("id"));
                    }

                    String languageCode = PrefUtils.getFeedsLanguageCode(context);
                    int index = languageCodes.contains(languageCode) ?
                            languageCodes.indexOf(languageCode) : 0;

                    JSONObject defaultObj = defaultsArray.getJSONObject(index);

                    List<FeedEntity> feeds = DataGenerator.populateFeedsByCategory(repo.getDatabase(), defaultObj);

                    List<YoutubeTypeEntity> youtubeTypes = DataGenerator.populateYoutubeTypes(defaultObj);

                    repo.insertFeeds(feeds);
                    repo.insertYoutubeTypes(youtubeTypes);

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
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Timber.e("doInBackground() error= %s", e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                EntryListViewPagerFragment frag = mFragment.get();
                if (frag != null && frag.isAdded() && frag.getActivity() != null) {
                    frag.loadCategories();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class EntryListPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        EntryListPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
