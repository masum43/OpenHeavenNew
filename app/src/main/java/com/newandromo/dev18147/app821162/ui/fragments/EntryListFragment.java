package com.newandromo.dev18147.app821162.ui.fragments;

import static com.newandromo.dev18147.app821162.service.FeedSyncWorker.TAG_FEED_REFRESH_WORK;
import static com.newandromo.dev18147.app821162.service.FeedSyncWorker.TAG_FEED_SEARCH_WORK;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_IS_CATEGORY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_POSITION;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_SEARCH_QUERY;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.EMPTY_STRING;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.IS_LAYOUT_TYPE_LIST_COMPACT_TIGHT;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.IS_LAYOUT_TYPE_LIST_IMG_LEFT;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.LAYOUT_TYPE_DOUBLE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.LAYOUT_TYPE_LIST;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_ENTRY_DETAIL_ACTIVITY;
import static com.newandromo.dev18147.app821162.utils.PrefUtils.PREF_TOOLBAR_CLICKED;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.WorkInfo;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.newandromo.dev18147.app821162.MyAnalytics;
import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.SplashScreen;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.EntryEntity;
import com.newandromo.dev18147.app821162.ui.activities.MainActivity;
import com.newandromo.dev18147.app821162.ui.adapter.EntryListAdapter;
import com.newandromo.dev18147.app821162.ui.listener.EntryListEventListener;
import com.newandromo.dev18147.app821162.utils.AdsManager;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import com.newandromo.dev18147.app821162.viewmodel.EntryViewModel;
import com.newandromo.dev18147.app821162.views.GridSpacingItemDecoration;
import com.newandromo.dev18147.app821162.views.RecyclerEmptyErrorView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EntryListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        EntryListAdapter.EntryEventListener {
    private EntryViewModel mViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerEmptyErrorView mRecyclerView;
    private EntryListAdapter mAdapter;
    // private MoPubRecyclerAdapter mMoPubRecyclerAdapter;
    //private RequestParameters mRequestParameters;
    private View mEmptyView;
    private int mPosition;
    private int mId;
    private boolean mIsCategory;
    private String mSearchQuery = EMPTY_STRING;
    private EntryListEventListener mEntryListEventListener;
    private boolean mCustomTabsOpened = false;
    private final SharedPreferences.OnSharedPreferenceChangeListener
            mPrefListener = (sharedPreferences, key) -> {
        String[] filter = new String[]{PREF_TOOLBAR_CLICKED};

        if (isAdded() && !isDetached() && !isRemoving()) {
            if (Arrays.asList(filter).indexOf(key) != -1) {
                if (PREF_TOOLBAR_CLICKED.equals(key)) {
                    scrollToTop(false);
                }
            }
        }
    };

    public EntryListFragment() {
        // Required empty public constructor
    }

    public static EntryListFragment newInstance(int position, int id, boolean isCategory) {
        EntryListFragment frag = new EntryListFragment();
        Bundle b = new Bundle();
        b.putInt(BUNDLE_POSITION, position);
        b.putInt(BUNDLE_ID, id);
        b.putBoolean(BUNDLE_IS_CATEGORY, isCategory);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            mEntryListEventListener = (EntryListEventListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrefUtils.registerOnSharedPreferenceChangeListener(getActivity(), mPrefListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(BUNDLE_POSITION);
            mId = savedInstanceState.getInt(BUNDLE_ID);
            mIsCategory = savedInstanceState.getBoolean(BUNDLE_IS_CATEGORY);
            mSearchQuery = savedInstanceState.getString(BUNDLE_SEARCH_QUERY);
        } else if (null != getArguments()) {
            mPosition = getArguments().getInt(BUNDLE_POSITION);
            mId = getArguments().getInt(BUNDLE_ID);
            mIsCategory = getArguments().getBoolean(BUNDLE_IS_CATEGORY);
        }

        View view = inflater.inflate(R.layout.fragment_entry_list, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = view.findViewById(R.id.entry_list);
        mEmptyView = view.findViewById(R.id.empty_view);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(
                    AppUtils.getSwipeRefreshColor(getActivity(), true));
            mSwipeRefreshLayout
                    .setProgressBackgroundColorSchemeResource(
                            AppUtils.getSwipeRefreshColor(getActivity(), false));
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }

        int layout;

        String layoutType = PrefUtils.getLayoutType(getActivity());

        if (LAYOUT_TYPE_LIST.equals(layoutType)) {
            if (IS_LAYOUT_TYPE_LIST_IMG_LEFT)
                layout = R.layout.list_item_entry_compact_img_left;
            else if (IS_LAYOUT_TYPE_LIST_COMPACT_TIGHT)
                layout = R.layout.list_item_entry_compact_tight;
            else layout = R.layout.list_item_entry_compact;

        } else if (LAYOUT_TYPE_DOUBLE.equals(layoutType)) {
            layout = R.layout.list_item_entry_compact_media;

        } else layout = R.layout.list_item_entry_large;

        mAdapter = new EntryListAdapter(getActivity(), layout, this);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       /* if (isShowNativeAds()) {
            setupNativeAds(getActivity(), mAdapter);
        } else {*/
        setupRecyclerView(mAdapter);
        //  }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_POSITION, mPosition);
        outState.putInt(BUNDLE_ID, mId);
        outState.putBoolean(BUNDLE_IS_CATEGORY, mIsCategory);
        outState.putString(BUNDLE_SEARCH_QUERY, getSearchQuery());
    }

    @Override
    public void onSelected(int position) {
        EntryEntity entry = getEntry(position);
        if (entry == null || getActivity() == null) return;

//        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//        CustomTabsIntent customTabsIntent = builder.build();
//        AppUtils.openCustomTab(getActivity(), customTabsIntent, Uri.parse(entry.getUrl()));

//        Intent intent = new Intent();
//
//
//        if (RemoteConfig.isShowSingleEntryDetailLayout()) {
//            intent.setClass(getActivity(), EntryDetailActivity.class);
//            intent.putExtra(INTENT_EXTRA_ENTRY_IMAGE_URL, entry.getThumbUrl());
//            intent.putExtra(INTENT_EXTRA_IS_SINGLE_LAYOUT, true);
//        } else intent.setClass(getActivity(), EntryDetailsActivity.class);
//
//
//        intent.putExtra(INTENT_EXTRA_POSITION, mPosition);
//        intent.putExtra(INTENT_EXTRA_ID, mId);
//        intent.putExtra(INTENT_EXTRA_IS_CATEGORY, mIsCategory);
//        intent.putExtra(INTENT_EXTRA_ENTRY_ID, entry.getId());
//        intent.putExtra(INTENT_EXTRA_SEARCH_QUERY, getSearchQuery());
//
        MyAnalytics.selectContent(getActivity(), entry.getTitle(), entry.getUrl(), MyAnalytics.CT_ENTRY);
//
//        ProgressDialog dialog = new ProgressDialog(getContext());
//        dialog.setMessage("Please wait...");
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        AppUtils.openCustomTab(getActivity(), customTabsIntent, Uri.parse(entry.getUrl()));
        mCustomTabsOpened = true;
        AdsManager.loadIntersAd(getActivity());
        mEntryListEventListener.onEntryListItemSelected();
        SplashScreen.showApplovinIntersitial();
//
//        IronSource.setInterstitialListener(new InterstitialListener() {
//            /**
//             * Invoked when Interstitial Ad is ready to be shown after load function was called.
//             */
//            @Override
//            public void onInterstitialAdReady() {
//                dialog.dismiss();
//                IronSource.showInterstitial("DefaultInterstitial");
//            }
//            /**
//             * invoked when there is no Interstitial Ad available after calling load function.
//             */
//            @Override
//            public void onInterstitialAdLoadFailed(IronSourceError error) {
//                dialog.dismiss();
//                //IronSource.loadInterstitial();
////                startActivityForResult(intent, REQ_ENTRY_DETAIL_ACTIVITY);
//                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//                CustomTabsIntent customTabsIntent = builder.build();
//                AppUtils.openCustomTab(getActivity(), customTabsIntent, Uri.parse(entry.getUrl()));
//                mCustomTabsOpened = true;
//                AdsManager.loadIntersAd(getActivity());
//                mEntryListEventListener.onEntryListItemSelected();
//
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
////                startActivityForResult(intent, REQ_ENTRY_DETAIL_ACTIVITY);
//                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//                CustomTabsIntent customTabsIntent = builder.build();
//                AppUtils.openCustomTab(getActivity(), customTabsIntent, Uri.parse(entry.getUrl()));
//                mCustomTabsOpened = true;
//                AdsManager.loadIntersAd(getActivity());
//                mEntryListEventListener.onEntryListItemSelected();
//
//            }
//            /**
//             * Invoked when Interstitial ad failed to show.
//             * @param error - An object which represents the reason of showInterstitial failure.
//             */
//            @Override
//            public void onInterstitialAdShowFailed(IronSourceError error) {
//                dialog.dismiss();
//                // startActivityForResult(intent, REQ_ENTRY_DETAIL_ACTIVITY);
//
//                // mEntryListEventListener.onEntryListItemSelected();
//
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

    }

    @Override
    public void onContextMenuCreated(View view, int position) {
        EntryEntity entry = getEntry(position);
        if (entry == null) return;

        Bundle shareBundle = AppUtils.createShareBundle(
                entry.getTitle(),
                entry.getUrl(),
                entry.getThumbUrl(),
                entry.getUrl(),
                getString(R.string.scheme_my_app));
        AppUtils.contentShare(getActivity(), getChildFragmentManager(), shareBundle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EntryViewModel.class);
        mViewModel.loadPagedEntries(getActivity(), mPosition, mId, mIsCategory,
                PrefUtils.isSearch(getActivity()), PrefUtils.getSearchQuery(getActivity()));
        mSearchQuery = PrefUtils.getSearchQuery(getActivity());

        // subscribeUi(mViewModel);

        new Handler().postDelayed(() ->
                        subscribeUi(mViewModel),
                TimeUnit.MILLISECONDS.toMillis(150));

        initiateRefresh(true);
    }

    private void subscribeUi(EntryViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getPagedEntries().observe(this, pagedList -> {
            if (pagedList == null) return;
            if (mAdapter != null) mAdapter.submitList(pagedList);
        });

        viewModel.getWorkInfo(TAG_FEED_REFRESH_WORK + mId).observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty()) return;
            WorkInfo workInfo = workInfos.get(0);
            boolean finished = workInfo.getState().isFinished();

            if (!finished) onRefreshStarted();
            else {
                mViewModel.refreshAll(mId, mIsCategory);
                PrefUtils.setIsCategoryRefreshed(getActivity(), mId, true);
                new Handler().postDelayed(() -> {
                    scrollToTop(true);
                    onRefreshComplete();
                }, TimeUnit.SECONDS.toMillis(1));
            }
        });

        /*viewModel.getWorkInfo(TAG_FEED_SEARCH_WORK).observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty()) return;
            WorkInfo workInfo = workInfos.get(0);
            boolean finished = workInfo.getState().isFinished();

            if (!finished) onRefreshStarted();
            else {
                new Handler().postDelayed(() -> {
                    if (mViewModel != null && getActivity() != null) {
                        mViewModel.loadPagedEntries(
                                getActivity(), mPosition, mId, true, getSearchQuery());
                        Timber.d("YOU ARE HERE--------2");
                    }
                }, TimeUnit.SECONDS.toMillis(1));
                onRefreshComplete();
            }
        });*/
    }

    @Override
    public void onPrepareOptionsMenu(@NotNull Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            final SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        setSearchQuery(query.trim());
                        if (getActivity() != null) getActivity().invalidateOptionsMenu();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                // Start our refresh background task
                initiateRefresh(false);
                return true;
            case R.id.action_mark_all_as_read:
                new Thread(() -> {
                    try {
                        if (getActivity() == null) return;
                        DataRepository repo = ((MyApplication) getActivity().getApplicationContext())
                                .getRepository();

                        SimpleSQLiteQuery sqlQuery;
                        String query = "UPDATE entries SET is_unread = 0";

                        String title = RemoteConfig.getAppName();
                        int count;

                        if (mIsCategory) {
                            title = repo.getCategoryById(mId).getTitle();
                            count = repo.loadUnreadEntriesCount(mId);
                            query += " AND feed_id IN (SELECT id FROM feed WHERE category_id = ?)";
                            sqlQuery = new SimpleSQLiteQuery(query, new Object[]{mId});
                        } else {
                            if (MainActivity.ALL_ITEMS == mPosition)
                                title = getString(R.string.nav_drawer_all_items);
                            else if (MainActivity.BOOKMARKS == mPosition)
                                title = getString(R.string.nav_drawer_bookmarks);

                            count = repo.loadUnreadEntriesCount();
                            sqlQuery = new SimpleSQLiteQuery(query);
                        }

                        if (count > 0 && getActivity() != null) {
                            String finalTitle = title;
                            getActivity().runOnUiThread(() -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(finalTitle);
                                builder.setMessage(getString(R.string.message_mark_all_as_read_question,
                                        getString(R.string.action_mark_all_as_read)));
                                builder.setPositiveButton(R.string.ok,
                                        (dialog, which) -> new Thread(() -> {
                                            try {
                                                @SuppressWarnings("unused")
                                                int results = repo.markEntriesAsRead(sqlQuery);
                                                List<String> readEntriesUrls = repo.getReadEntriesUrls();
                                                if (readEntriesUrls != null && !readEntriesUrls.isEmpty()) {
                                                    repo.updateEntriesUnreadByUrl(0, readEntriesUrls);
                                                    // repo.updateEntriesRecentReadByUrl(1, urls);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }).start());
                                builder.setNegativeButton(R.string.cancel, null);
                                builder.show();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ENTRY_DETAIL_ACTIVITY) {
            if (!RemoteConfig.isShowSingleEntryDetailLayout()) {
                new Handler().postDelayed(() -> {
                    if (RemoteConfig.isRestoreListState()) {
                        int position = PrefUtils.getCurrentListPosition(getActivity(), mId);
                        setScrollToPosition(getAdjustedPosition(position), false);
                        PrefUtils.setCurrentListPosition(getActivity(), mId, 0);
                    }
                }, TimeUnit.SECONDS.toMillis(1));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onRefreshComplete();
    }

    @Override
    public void onDestroyView() {
        // You must call this or the ad adapter may cause a memory leak.
        /*try { // TODO mMoPubRecyclerAdapter.destroy()
            if (mMoPubRecyclerAdapter != null) mMoPubRecyclerAdapter.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        super.onDestroyView();
    }

    private boolean isShowNativeAds() {
        return !mIsCategory || RemoteConfig.isShowNativesInEntryListCategories();
    }

/*
    private void setupNativeAds(Activity activity, EntryListAdapter adapter) {
        try {
            String keywords = "";
            // Setting desired assets on your request helps native ad networks and bidders
            // provide higher-quality ads.
            EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                    RequestParameters.NativeAdAsset.TITLE,
                    RequestParameters.NativeAdAsset.TEXT,
                    RequestParameters.NativeAdAsset.ICON_IMAGE,
                    RequestParameters.NativeAdAsset.MAIN_IMAGE,
                    RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);

            mRequestParameters = new RequestParameters.Builder()
                    .location(null)
                    .keywords(keywords)
                    .desiredAssets(desiredAssets)
                    .build();

            mMoPubRecyclerAdapter = new MoPubRecyclerAdapter(activity, adapter,
                    new MoPubNativeAdPositioning.MoPubServerPositioning());

            String layoutType = PrefUtils.getLayoutType(getActivity());

            int fbAdLayout = LAYOUT_TYPE_DOUBLE.equals(layoutType) ?
                    R.layout.native_ad_list_item_facebook_media : R.layout.native_ad_list_item_facebook;
            FacebookAdRenderer facebookAdRenderer = new FacebookAdRenderer(
                    new FacebookAdRenderer.FacebookViewBinder.Builder(fbAdLayout)
                            // .titleId(R.id.native_ad_title)
                            .textId(R.id.native_ad_body)
                            // Binding to new layouts from Facebook 4.99.0+
                            .mediaViewId(R.id.native_ad_media)
                            .adIconViewId(R.id.native_ad_icon)
                            .adChoicesRelativeLayoutId(R.id.ad_choices_container)
                            .advertiserNameId(R.id.native_ad_title)
                            // End of binding to new layouts
                            .callToActionId(R.id.native_ad_call_to_action)
                            .build());

            int adMobVideoAdLayout = LAYOUT_TYPE_DOUBLE.equals(layoutType) ?
                    R.layout.native_ad_list_item_admob_media : R.layout.native_ad_list_item_admob;

            GooglePlayServicesAdRenderer googlePlayServicesAdRenderer = new GooglePlayServicesAdRenderer(
                    new MediaViewBinder.Builder(adMobVideoAdLayout)
                            .mediaLayoutId(R.id.native_media_layout) // bind to your `com.mopub.nativeads.MediaLayout` element
                            .iconImageId(R.id.native_icon_image)
                            .titleId(R.id.native_title)
                            .textId(R.id.native_text)
                            .callToActionId(R.id.native_cta)
                            //.privacyInformationIconImageId(R.id.native_ad_daa_icon_image)
                            .build());

            int mpVideoAdLayout = LAYOUT_TYPE_DOUBLE.equals(layoutType) ?
                    R.layout.native_ad_video_list_mopub_media : R.layout.native_ad_video_list_mopub;
            MoPubVideoNativeAdRenderer moPubVideoNativeAdRenderer = new MoPubVideoNativeAdRenderer(
                    new MediaViewBinder.Builder(mpVideoAdLayout)
                            .titleId(R.id.native_title)
                            .textId(R.id.native_text)
                            .mediaLayoutId(R.id.native_media_layout)
                            .iconImageId(R.id.native_icon_image)
                            .callToActionId(R.id.native_cta)
                            .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                            .build());

            int mpStaticAdLayout = LAYOUT_TYPE_DOUBLE.equals(layoutType) ?
                    R.layout.native_ad_static_list_mopub_media : R.layout.native_ad_static_list_mopub;
            MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(
                    new ViewBinder.Builder(mpStaticAdLayout)
                            .titleId(R.id.native_title)
                            .textId(R.id.native_text)
                            .mainImageId(R.id.native_main_image)
                            .iconImageId(R.id.native_icon_image)
                            .callToActionId(R.id.native_cta)
                            .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                            .build());

            mMoPubRecyclerAdapter.registerAdRenderer(facebookAdRenderer);
            mMoPubRecyclerAdapter.registerAdRenderer(googlePlayServicesAdRenderer);
            mMoPubRecyclerAdapter.registerAdRenderer(moPubVideoNativeAdRenderer);
            // Remember to register your MoPubStaticNativeAdRenderer instance last.
            mMoPubRecyclerAdapter.registerAdRenderer(moPubStaticNativeAdRenderer);

            setupRecyclerView(mMoPubRecyclerAdapter); // TODO setup mMoPubRecyclerAdapter
         //   loadOrRefreshMoPubRecyclerAdapter(true);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            setupRecyclerView(mAdapter);
        }
    }
*/

    @Override
    public void onRefresh() {
        initiateRefresh(false);
    }

    private void initiateRefresh(boolean isAutoRefresh) {
        // if (!isAutoRefresh) loadOrRefreshMoPubRecyclerAdapter(false);

        if (mViewModel != null) {
            if (isAutoRefresh) {
                if (MainActivity.ALL_ITEMS == mPosition && mIsCategory) {
                    if (!PrefUtils.isCategoryRefreshed(getActivity(), mId)) {
                        mViewModel.refresh(mId, mIsCategory);
                    }
                }
            } else {
                mViewModel.refresh(mId, mIsCategory);
            }
        }
    }

    private void onRefreshStarted() {
        if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.postOnAnimationDelayed(() -> {
                if (mSwipeRefreshLayout != null)
                    mSwipeRefreshLayout.setRefreshing(true);
            }, 150);
        }
    }

    private void onRefreshComplete() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.postOnAnimationDelayed(() -> {
                if (mSwipeRefreshLayout != null)
                    mSwipeRefreshLayout.setRefreshing(false);
                // mSwipeRefreshLayout.setEnabled(true);
            }, 250);
        }
    }

    private void setupRecyclerView(RecyclerView.Adapter adapter) {
        setupRVLayoutManagerAndItemDecor();
        // mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setEmptyView(mEmptyView);
    }

/*
    private void loadOrRefreshMoPubRecyclerAdapter(boolean isLoadAds) {
        try {// TODO loadOrRefreshMoPubRecyclerAdapter
            if (mMoPubRecyclerAdapter != null) {
                String adUnitId = RemoteConfig.getMoPubNativeIdEntryList();

                String layoutType = PrefUtils.getLayoutType(getActivity());
                if (LAYOUT_TYPE_DOUBLE.equals(layoutType)) {
                    adUnitId = RemoteConfig.getMoPubNativeIdEntryListTiny();
                }

                if (isLoadAds) {
                    if (mRequestParameters != null) {
                        mMoPubRecyclerAdapter.loadAds(adUnitId, mRequestParameters);
                    } else {
                        mMoPubRecyclerAdapter.loadAds(adUnitId);
                    }
                } else {
                    if (mRequestParameters != null) {
                        mMoPubRecyclerAdapter.refreshAds(adUnitId, mRequestParameters);
                    } else {
                        mMoPubRecyclerAdapter.refreshAds(adUnitId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    private void setupRVLayoutManagerAndItemDecor() {
        RecyclerView.LayoutManager layoutManager = createLayoutManager(getResources());
        RecyclerView.ItemDecoration itemDecoration = gridSpacingItemDecoration(getResources());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    private RecyclerView.LayoutManager createLayoutManager(Resources resources) {
        int id;

        String layoutType = PrefUtils.getLayoutType(getActivity());

        if (LAYOUT_TYPE_LIST.equals(layoutType)) {
            id = R.integer.num_entry_columns_compact;
        } else if (LAYOUT_TYPE_DOUBLE.equals(layoutType)) {
            id = R.integer.num_entry_columns_compact_media;
        } else {
            id = R.integer.num_entry_columns_large;
        }

        int spanCount = resources.getInteger(id);
        return new GridLayoutManager(getActivity(), spanCount);
    }

    private GridSpacingItemDecoration gridSpacingItemDecoration(Resources resources) {
        int id;

        String layoutType = PrefUtils.getLayoutType(getActivity());

        if (LAYOUT_TYPE_LIST.equals(layoutType)) {
            id = R.integer.num_entry_columns_compact;
        } else if (LAYOUT_TYPE_DOUBLE.equals(layoutType)) {
            id = R.integer.num_entry_columns_compact_media;
        } else {
            id = R.integer.num_entry_columns_large;
        }

        int spanCount = resources.getInteger(id);
        int paddingSide = resources.getDimensionPixelSize(R.dimen.entry_card_side_padding);
        int paddingTopBottom = resources.getDimensionPixelSize(R.dimen.entry_card_top_bottom_padding);
        return new GridSpacingItemDecoration(spanCount, paddingSide, paddingTopBottom, true);
    }

    private EntryEntity getEntry(int position) {
        EntryEntity entry = null;
        try {
            entry = mAdapter.getEntry(getOriginalPosition(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entry;
    }

    private int getAdjustedPosition(int position) {
       /* try {
            if (mMoPubRecyclerAdapter != null) {
                return mMoPubRecyclerAdapter.getAdjustedPosition(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return position;
    }

    private int getOriginalPosition(int position) {
      /*  try {
            if (mMoPubRecyclerAdapter != null) {
                return mMoPubRecyclerAdapter.getOriginalPosition(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return position;
    }

    public String getSearchQuery() {
        return mSearchQuery;
    }

    public void resetSearchQuery() {
        mSearchQuery = EMPTY_STRING;
        PrefUtils.setIsSearch(getActivity(), false);
        PrefUtils.setSearchQuery(getActivity(), "");
        updateABSubtitle("");
    }

    private void setSearchQuery(String query) {
        if (!getSearchQuery().equals(query)) {
            mSearchQuery = query;

            PrefUtils.setIsSearch(getActivity(), true);
            PrefUtils.setSearchQuery(getActivity(), getSearchQuery());

            updateABSubtitle(getSearchQuery());
            if (mViewModel != null) {
                Observer<List<WorkInfo>> mSearchObserver = workInfos -> {
                    if (workInfos == null || workInfos.isEmpty()) return;
                    WorkInfo workInfo = workInfos.get(0);
                    boolean finished = workInfo.getState().isFinished();

                    if (!finished) onRefreshStarted();
                    else {
                        onRefreshComplete();
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).getSearchResult();
                    }
                };

                mViewModel.getWorkInfo(TAG_FEED_SEARCH_WORK + mId).observe(this, mSearchObserver);
                mViewModel.search(mId, getSearchQuery());
                MyAnalytics.searchItem(getActivity(), getSearchQuery());
            }
        }
    }

    private void updateABSubtitle(String subTitle) {
        if (getActivity() != null)
            ((MainActivity) getActivity()).setActionBarTitles("", subTitle);
    }

    public void scrollToTop(boolean isSmooth) {
        setScrollToPosition(0, isSmooth);
    }

    private void setScrollToPosition(int position, boolean isSmooth) {
        if (mRecyclerView != null) {
            if (isSmooth) mRecyclerView.smoothScrollToPosition(position);
            else mRecyclerView.scrollToPosition(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCustomTabsOpened) {
            mCustomTabsOpened = false;
            AdsManager.showIntersAd(getActivity());
        }
    }
}
