package com.newandromo.dev18147.app821162.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.ui.activities.EntryDetailsActivity;
import com.newandromo.dev18147.app821162.ui.activities.MainActivity;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import com.newandromo.dev18147.app821162.viewmodel.EntryViewModel;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_ENTRY_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_IS_CATEGORY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_POSITION;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_SEARCH_QUERY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ENTRY_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_CATEGORY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_POSITION;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_SEARCH_QUERY;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.ALL_ENTRIES;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.EMPTY_STRING;
import static com.newandromo.dev18147.app821162.utils.PrefUtils.PREF_ENTRY_DETAIL_FONT_SIZE;

public class EntryDetailViewPagerFrag extends Fragment implements ViewPager.OnPageChangeListener {
    private MyPagerAdapter mAdapter;
    private int mPosition;
    private int mId;
    private boolean mIsCategory;
    private int mEntryId;
    private String mSearchQuery = EMPTY_STRING;
    private ViewPager mViewPager;
    private EntryDetailPagerListener mEntryDetailPagerListener;
    private EntryViewModel mViewModel;
    private final SharedPreferences.OnSharedPreferenceChangeListener
            mPrefListener = (sharedPreferences, key) -> {
        String[] fontChange = new String[]{PREF_ENTRY_DETAIL_FONT_SIZE};
        if (isAdded() && !isDetached() && !isRemoving()) {
            if (Arrays.asList(fontChange).indexOf(key) != -1) {
                if (PREF_ENTRY_DETAIL_FONT_SIZE.equals(key)) {
                    refreshViewPager();
                }
            }
        }
    };

    public EntryDetailViewPagerFrag() {
        // Required empty public constructor
    }

    private interface EntryDetailPagerListener {
        void onPageSelected(int position, int itemsCount);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
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
        View view = inflater.inflate(R.layout.viewpager, container, false);
        mViewPager = view.findViewById(R.id.view_pager);

        if (savedInstanceState != null) {
            mEntryId = savedInstanceState.getInt(BUNDLE_ENTRY_ID, 0);
            mPosition = savedInstanceState.getInt(BUNDLE_POSITION, 1);
            mId = savedInstanceState.getInt(BUNDLE_ID, 1);
            mIsCategory = savedInstanceState.getBoolean(BUNDLE_IS_CATEGORY);
            mSearchQuery = savedInstanceState.getString(BUNDLE_SEARCH_QUERY, "");
        } else if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.getExtras() != null) {
                mEntryId = intent.getIntExtra(INTENT_EXTRA_ENTRY_ID, 0);
                mPosition = intent.getIntExtra(INTENT_EXTRA_POSITION, 1);
                mId = intent.getIntExtra(INTENT_EXTRA_ID, 1);
                mIsCategory = intent.getBooleanExtra(INTENT_EXTRA_IS_CATEGORY, false);
                mSearchQuery = intent.getExtras().getString(INTENT_EXTRA_SEARCH_QUERY, "");
            }
        }

        mAdapter = new MyPagerAdapter(getChildFragmentManager(), mId);
        mViewPager.addOnPageChangeListener(this);

        mEntryDetailPagerListener = (position, itemsCount) -> {
            if (getActivity() != null)
                ((EntryDetailsActivity) getActivity()).onPageSelected(position, itemsCount);

            if (mAdapter != null && mViewModel != null) {
                int[] entryIds = mAdapter.getEntryIds();
                int entryId = entryIds[position];
                mViewModel.markEntryAsRead(entryId);
            }

            PrefUtils.setCurrentListPosition(getActivity(), mId, position);
        };

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_POSITION, mPosition);
        outState.putInt(BUNDLE_ENTRY_ID, mEntryId);
        outState.putInt(BUNDLE_ID, mId);
        outState.putBoolean(BUNDLE_IS_CATEGORY, mIsCategory);
        outState.putString(BUNDLE_SEARCH_QUERY, mSearchQuery);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EntryViewModel.class);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_POSITION, mPosition);
        bundle.putInt(BUNDLE_ID, mId);
        bundle.putBoolean(BUNDLE_IS_CATEGORY, mIsCategory);
        bundle.putString(BUNDLE_SEARCH_QUERY, getSearchQuery());
        new LoadDataAsyncTask(this).execute(bundle);
    }

    @Override
    public void onPageSelected(int position) {
        if (mAdapter == null) return;
        int[] entryIds = mAdapter.getEntryIds();
        if (entryIds != null && entryIds.length > 0) {
            mEntryId = entryIds[position];
            mEntryDetailPagerListener.onPageSelected(position, entryIds.length);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void refreshViewPager() {
        if (mViewPager != null && mAdapter != null) {
            mViewPager.invalidate();
            mAdapter.notifyDataSetChanged();
        }
    }

    public void selectEntry(boolean next) {
        int position = getEntryIdPosition(mEntryId);
        if (position != -1) {
            if (next)
                position++;
            else
                position--;

            if (mAdapter == null) return;
            int[] entryIds = mAdapter.getEntryIds();
            if (entryIds != null && entryIds.length > 0) {
                setEntryId(entryIds[position]);
            }
        }
    }

    private void setEntryId(int entryId) {
        mEntryId = entryId;

        int position = getEntryIdPosition(entryId);

        if (getView() != null) {
            ViewPager pager = getView().findViewById(R.id.view_pager);

            pager.setCurrentItem(position);
        }
    }

    private int getEntryIdPosition(int entryId) {
        int position = -1;
        if (mAdapter != null) {
            int[] entryIds = mAdapter.getEntryIds();
            if (entryIds != null && entryIds.length > 0) {
                for (int i = 0; i < entryIds.length; i++) {
                    if (entryIds[i] == entryId) {
                        position = i;
                        break;
                    }
                }
            }
        }
        return position;
    }

    private String getSearchQuery() {
        return mSearchQuery;
    }

    private static class LoadDataAsyncTask extends AsyncTask<Bundle, Void, int[]> {
        private WeakReference<EntryDetailViewPagerFrag> mFragment;

        LoadDataAsyncTask(EntryDetailViewPagerFrag fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                EntryDetailViewPagerFrag frag = mFragment.get();
                if (frag != null && frag.isAdded()) {
                    View view = frag.getView();
                    if (view != null) {
                        ProgressBar progress = view.findViewById(R.id.progress);
                        if (progress != null) progress.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected int[] doInBackground(Bundle... bundles) {
            try {
                EntryDetailViewPagerFrag frag = mFragment.get();
                if (frag != null && frag.isAdded() && frag.getActivity() != null) {
                    Context context = frag.getActivity().getApplicationContext();
                    DataRepository repo = ((MyApplication) context).getRepository();

                    int[] entryIds;

                    Bundle bundle = bundles[0];
                    int position = bundle.getInt(BUNDLE_POSITION, 1);
                    int id = bundle.getInt(BUNDLE_ID, 1);
                    boolean isCategory = bundle.getBoolean(BUNDLE_IS_CATEGORY);
                    String searchQuery = bundle.getString(BUNDLE_SEARCH_QUERY, "");

                    if (!TextUtils.isEmpty(searchQuery)) {
                        // searchQuery = String.format("%s OR *%s*", searchQuery, searchQuery);
                        if (MainActivity.BOOKMARKS == position)
                            entryIds = repo.getDatabase().entryDao().getSearchedBookmarkedEntryIds(searchQuery);
                        else
                            entryIds = repo.getDatabase().entryDao().getSearchedEntryIds(searchQuery);
                    } else {

                        if (ALL_ENTRIES.equals(PrefUtils.getViewMode(context))) { // todo ALL ENTRIES
                            if (PrefUtils.isOldestFirst(context)) { // Oldest First
                                if (MainActivity.BOOKMARKS == position) {
                                    entryIds = repo.getDatabase().entryDao().getEntryIdsBookmarkedOldestFirst();
                                } else if (isCategory) {
                                    entryIds = repo.getDatabase().entryDao()
                                            .getEntryIdsByCategoryOldestFirst(id);
                                } else {
                                    entryIds = repo.getDatabase().entryDao().getEntryIdsOldestFirst();
                                }
                            } else { // Newest First
                                if (MainActivity.BOOKMARKS == position) {
                                    entryIds = repo.getDatabase().entryDao().getEntryIdsBookmarkedNewestFirst();
                                } else if (isCategory) {
                                    entryIds = repo.getDatabase().entryDao()
                                            .getEntryIdsByCategoryNewestFirst(id);
                                } else {
                                    entryIds = repo.getDatabase().entryDao().getEntryIdsNewestFirst();
                                }
                            }
                        } else { // todo UNREAD ENTRIES
                            if (PrefUtils.isOldestFirst(context)) { // Oldest First
                                if (isCategory) {
                                    entryIds = repo.getDatabase().entryDao()
                                            .getUnreadEntryIdsByCategoryOldestFirst(id);
                                } else {
                                    entryIds = repo.getDatabase().entryDao().getUnreadEntryIdsOldestFirst();
                                }
                            } else { // Newest First
                                if (isCategory) {
                                    entryIds = repo.getDatabase().entryDao()
                                            .getUnreadEntryIdsByCategoryNewestFirst(id);
                                } else {
                                    entryIds = repo.getDatabase().entryDao().getUnreadEntryIdsNewestFirst();
                                }
                            }
                        }
                    }

                    return entryIds;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(int[] entryIds) {
            try {
                EntryDetailViewPagerFrag frag = mFragment.get();
                if (frag != null && frag.isAdded()) {
                    if (entryIds == null || entryIds.length == 0 || frag.mAdapter == null) {
                        return;
                    }

                    frag.mAdapter.submitList(entryIds);

                    int position = 0;

                    if (frag.mEntryId != 0) {
                        int pos = frag.getEntryIdPosition(frag.mEntryId);
                        if (pos != -1) position = pos;
                        if (frag.mEntryDetailPagerListener != null)
                            frag.mEntryDetailPagerListener.onPageSelected(position, entryIds.length);
                        Timber.d("entryId= %s, position= %s, mId= %s",
                                frag.mEntryId, frag.mPosition, frag.mId);
                    } else {
                        frag.mEntryId = entryIds[position];
                        if (frag.mEntryDetailPagerListener != null)
                            frag.mEntryDetailPagerListener.onPageSelected(position, entryIds.length);
                    }

                    View view = frag.getView();

                    if (view != null) {
                        ViewPager viewPager = view.findViewById(R.id.view_pager);
                        ProgressBar progress = view.findViewById(R.id.progress);

                        if (progress != null) progress.setVisibility(View.GONE);

                        if (viewPager != null) {
                            viewPager.setAdapter(frag.mAdapter);
                            viewPager.setCurrentItem(position);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCatId;
        private int[] mEntryIds;

        MyPagerAdapter(@NonNull FragmentManager fm, int catId) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.mCatId = catId;
        }

        private void submitList(int[] entryIds) {
            this.mEntryIds = entryIds;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            if (mEntryIds != null && mEntryIds.length > 0)
                return EntryDetailFragment.newInstance(mCatId, mEntryIds[position]);
            return new EntryDetailFragment();
        }

        @Override
        public int getCount() {
            return mEntryIds == null ? 0 : mEntryIds.length;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            // This forces all items to be recreated when invalidate() is called on the ViewPager.
            return POSITION_NONE;
        }

        int[] getEntryIds() {
            return mEntryIds;
        }
    }
}
