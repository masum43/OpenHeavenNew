package com.newandromo.dev18147.app821162.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.YoutubeTypeEntity;
import com.newandromo.dev18147.app821162.ui.activities.MainActivity;
import com.newandromo.dev18147.app821162.ui.listener.TabsEventListener;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import com.newandromo.dev18147.app821162.viewmodel.YoutubeViewModel;

import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_YOUTUBE_TYPE_MANAGER;
import static com.newandromo.dev18147.app821162.utils.PrefUtils.PREF_YOUTUBE_TERMS_AND_PRIVACY;

public class YoutubeTypeViewPagerFrag extends Fragment implements
        TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {
    private final SharedPreferences.OnSharedPreferenceChangeListener
            mPrefListener = (sharedPreferences, key) -> {
        String[] filter = new String[]{PREF_YOUTUBE_TERMS_AND_PRIVACY};
        if (Arrays.asList(filter).indexOf(key) != -1) {
            if (PREF_YOUTUBE_TERMS_AND_PRIVACY.equals(key)) {
                if (getActivity() != null)
                    getActivity().invalidateOptionsMenu();
                loadYoutubeType();
            }
        }
    };

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabsEventListener mTabsEventListener;
    private YouTubeListPagerAdapter mAdapter;
    private int mCurrentItem;

    private RelativeLayout mAdViewBannerContainer;

    public YoutubeTypeViewPagerFrag() {
    }

    public static YoutubeTypeViewPagerFrag newInstance() {
        return new YoutubeTypeViewPagerFrag();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager, container, false);

        if (getActivity() != null) {
            Resources.Theme theme = getActivity().getTheme();
            TypedValue typedValue = new TypedValue();
            theme.resolveAttribute(R.attr.videoListBackground, typedValue, true);
            view.setBackgroundColor(typedValue.data);
        }

        if (savedInstanceState != null) {
            mCurrentItem = savedInstanceState.getInt("currentItem");
        }

        mViewPager = view.findViewById(R.id.view_pager);

        mAdapter = new YouTubeListPagerAdapter(getChildFragmentManager());
        mAdapter.addFragment(new DummyFragment(), "");

        mViewPager.setAdapter(mAdapter);

        if (getActivity() != null) {
            ((MainActivity) getActivity()).setActionBarTitles(
                    getString(R.string.nav_drawer_youtube),
                    getString(R.string.youtube_api_service));
            mTabLayout = getActivity().findViewById(R.id.tabs);
            if (mTabLayout != null) {
                mTabLayout.setupWithViewPager(mViewPager, true);
                mTabLayout.addOnTabSelectedListener(this);
                if (mTabLayout.getTabCount() > 1)
                    mTabLayout.setVisibility(View.VISIBLE);
                else mTabLayout.setVisibility(View.GONE);
            }
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (getActivity() != null) {
                if (RemoteConfig.isShowBannerAdsYoutubeList()) {

                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentItem", mViewPager.getCurrentItem());
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        try {
            inflater.inflate(R.menu.menu_youtube_list, menu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NotNull Menu menu) {
        menu.setGroupVisible(R.id.onAcceptPolicy, PrefUtils.isYoutubeTermsAccepted(getActivity()));

        MenuItem search = menu.findItem(R.id.action_search);
        if (search != null) {
            AppUtils.tintMenuItemIcon(getActivity(), search);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                AppUtils.inAppYouTubeSearch(getActivity());
                return true;
            case R.id.menu_player_options:
                if (getActivity() != null) {
                    final int checkedItem = Integer.parseInt(PrefUtils.getYouTubePlayerOption(getActivity()));
                    AlertDialog.Builder _builder = new AlertDialog.Builder(getActivity());
                    _builder.setTitle(R.string.youtube_player_options);
                    _builder.setSingleChoiceItems(getResources()
                                    .getStringArray(R.array.pref_youtube_player_modes_entries),
                            checkedItem,
                            (dialog, which) -> {
                                if (checkedItem == which) {
                                    dialog.cancel();
                                    return;
                                }
                                PrefUtils.setYouTubePlayerOption(getActivity(), String.valueOf(which));
                                dialog.cancel();
                            });
                    _builder.show();
                }
                return true;
            case R.id.menu_youtube_terms:
                LegalDocsFragment legalDocsFragment = LegalDocsFragment.newInstance(false);
                legalDocsFragment.show(getChildFragmentManager(), "legalDocsFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_YOUTUBE_TYPE_MANAGER) {
            loadYoutubeType();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        YoutubeViewModel viewModel = new ViewModelProvider(this).get(YoutubeViewModel.class);

        if (!PrefUtils.isYoutubeTypesUpdated(getActivity())) {
            PrefUtils.setYoutubeTypesUpdated(getActivity(), true);
            viewModel.updateYoutubeTypeDetails();
        }

        loadYoutubeType();
    }

    private void loadYoutubeType() {
        if (PrefUtils.isYoutubeTermsAccepted(getActivity())) {
            new LoadTypesAsyncTask(this).execute();
        } else {
            new Handler().postDelayed(() -> {
                try {
                    if (isAdded() && !isRemoving()) {
                        LegalDocsFragment legalDocsFragment = LegalDocsFragment.newInstance(false);
                        legalDocsFragment.show(getChildFragmentManager(), "legalDocsFragment");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, TimeUnit.SECONDS.toMillis(1));
        }
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
        mTabsEventListener.onTabSelected(tab.getPosition(), true);
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

    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onDestroy() {


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
    }

    private static class LoadTypesAsyncTask extends AsyncTask<Void, Void, List<YoutubeTypeEntity>> {
        private WeakReference<YoutubeTypeViewPagerFrag> mFragment;

        LoadTypesAsyncTask(YoutubeTypeViewPagerFrag fragment) {
            this.mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected List<YoutubeTypeEntity> doInBackground(Void... voids) {
            try {
                YoutubeTypeViewPagerFrag frag = mFragment.get();
                if (frag != null && frag.isAdded() && !frag.isRemoving() && frag.getActivity() != null) {
                    Context context = frag.getActivity().getApplicationContext();
                    DataRepository repo = ((MyApplication) context).getRepository();
                    return repo.getAllYoutubeTypes();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<YoutubeTypeEntity> types) {
            try {
                YoutubeTypeViewPagerFrag frag = mFragment.get();
                if (frag != null && frag.isAdded() && !frag.isRemoving()) {
                    if (types == null) return;

                    frag.mAdapter = new YouTubeListPagerAdapter(frag.getChildFragmentManager());
                    if (types.isEmpty()) {
                        frag.mAdapter.addFragment(new DummyFragment(), "");
                    } else {
                        for (YoutubeTypeEntity t : types) {
                            frag.mAdapter.addFragment(YoutubeTypeFragment
                                    .newInstance(t.getId(), t.getUniqueId(), t.isChannel()), t.getTitle());
                        }
                    }

                    if (frag.mViewPager != null) {
                        frag.mViewPager.setAdapter(frag.mAdapter);
                        frag.mViewPager.setCurrentItem(frag.mCurrentItem);
                    }

                    if (frag.mTabLayout != null) {
                        if (frag.mTabLayout.getTabCount() > 0)
                            frag.mTabLayout.setVisibility(View.VISIBLE);
                        else frag.mTabLayout.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class YouTubeListPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        YouTubeListPagerAdapter(FragmentManager fm) {
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
