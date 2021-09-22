package com.newandromo.dev18147.app821162.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.WorkInfo;

import com.google.android.youtube.player.YouTubeIntents;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import com.newandromo.dev18147.app821162.MyAnalytics;
import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.YoutubeTypeEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeVideoEntity;
import com.newandromo.dev18147.app821162.ui.adapter.YoutubeTypeAdapter;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import com.newandromo.dev18147.app821162.viewmodel.YoutubeViewModel;
import com.newandromo.dev18147.app821162.views.RecyclerEmptyErrorView;

import static com.newandromo.dev18147.app821162.MyAnalytics.CT_VIDEO;
import static com.newandromo.dev18147.app821162.service.YoutubeTypeSyncWorker.TYPE_LOAD_MORE_WORK;
import static com.newandromo.dev18147.app821162.service.YoutubeTypeSyncWorker.TYPE_VIDEO_REFRESH_WORK;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_IS_CHANNEL;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_TYPE_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_TYPE_UNIQUE_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.YOUTUBE_SHORT_URL;

public class YoutubeTypeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        YoutubeTypeAdapter.YoutubeTypeListener {
    private YoutubeViewModel mViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerEmptyErrorView mRecyclerView;
    private YoutubeTypeAdapter mAdapter;
    private ProgressBar mLoadMoreBar;
    private int mTypeId;
    private String mTypeUniqueId;
    private int mIsChannel;

    public YoutubeTypeFragment() {
    }

    public static YoutubeTypeFragment newInstance(int typeId, String uniqueId, int isChannel) {
        YoutubeTypeFragment frag = new YoutubeTypeFragment();
        Bundle b = new Bundle();
        b.putInt(BUNDLE_TYPE_ID, typeId);
        b.putString(BUNDLE_TYPE_UNIQUE_ID, uniqueId);
        b.putInt(BUNDLE_IS_CHANNEL, isChannel);
        frag.setArguments(b);
        return frag;
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
        View view = inflater.inflate(R.layout.fragment_youtube_videos, container, false);

        if (savedInstanceState != null) {
            mTypeId = savedInstanceState.getInt(BUNDLE_TYPE_ID, 0);
            mTypeUniqueId = savedInstanceState.getString(BUNDLE_TYPE_UNIQUE_ID, "");
            mIsChannel = savedInstanceState.getInt(BUNDLE_IS_CHANNEL, 1);
        } else if (getArguments() != null) {
            mTypeId = getArguments().getInt(BUNDLE_TYPE_ID, 0);
            mTypeUniqueId = getArguments().getString(BUNDLE_TYPE_UNIQUE_ID, "");
            mIsChannel = getArguments().getInt(BUNDLE_IS_CHANNEL, 1);
        }

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = view.findViewById(R.id.video_list);
        mLoadMoreBar = view.findViewById(R.id.loadMoreBar);
        ImageView emptyIcon = view.findViewById(R.id.empty_icon);
        if (emptyIcon != null) {
            emptyIcon.setImageResource(R.drawable.empty_video_list);
        }

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources
                    (AppUtils.getSwipeRefreshColor(getActivity(), true));
            mSwipeRefreshLayout
                    .setProgressBackgroundColorSchemeResource(
                            AppUtils.getSwipeRefreshColor(getActivity(), false));
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }

        mAdapter = new YoutubeTypeAdapter(getActivity(), this);
        mAdapter.setIsChannel(mIsChannel == 1);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEmptyView(view.findViewById(R.id.empty_view));
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_TYPE_ID, mTypeId);
        outState.putString(BUNDLE_TYPE_UNIQUE_ID, mTypeUniqueId);
        outState.putInt(BUNDLE_IS_CHANNEL, mIsChannel);

        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            int scrollPosition = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();

            if (scrollPosition == -1) {
                scrollPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            }

            int itemId = getItemIdAtPosition(scrollPosition);
            int position = getPositionByItemId(itemId);

            if (position >= 0) {
                PrefUtils.setScrolledPosition(getActivity(), mTypeId, position);
            }
        }
    }

    @Override
    public void onSelected(YoutubeVideoEntity video) {
        int playerOption = Integer.parseInt(PrefUtils.getYouTubePlayerOption(getActivity()));

        String url = String.format(YOUTUBE_SHORT_URL, video.getVideoId());
        MyAnalytics.selectContent(getActivity(), video.getTitle(), url, CT_VIDEO);

        AppUtils.openYouTube(getActivity(), video.getVideoId(), playerOption);
    }

    @Override
    public void onContextMenuAction(YoutubeVideoEntity video) {
        String url = String.format(YOUTUBE_SHORT_URL, video.getVideoId());
        Bundle shareBundle = AppUtils.createShareBundle(video.getTitle(), url, video.getThumbUrl(),
                url, getString(R.string.scheme_youtube));
        AppUtils.contentShare(getActivity(), getChildFragmentManager(), shareBundle);
    }

    @Override
    public void onPrepareOptionsMenu(@NotNull Menu menu) {
        MenuItem refresh = menu.findItem(R.id.menu_refresh);
        if (refresh != null) refresh.setVisible(mTypeId > 0);

        MenuItem share = menu.findItem(R.id.menu_share);
        if (share != null) {
            share.setVisible(mTypeId > 0);
            AppUtils.tintMenuItemIcon(getActivity(), share);
        }

        MenuItem openInYoutube = menu.findItem(R.id.menu_open_on_youtube);
        if (openInYoutube != null) {
            openInYoutube.setVisible(mTypeId > 0);
            AppUtils.tintMenuItemIcon(getActivity(), openInYoutube);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                initiateRefresh();
                return true;
            case R.id.menu_share:
                new Thread(() -> {
                    try {
                        if (getActivity() == null) return;
                        DataRepository repo = ((MyApplication) getActivity().getApplicationContext())
                                .getRepository();
                        String link;
                        String customScheme;

                        YoutubeTypeEntity type = repo.getYoutubeTypeByUniqueId(mTypeUniqueId);
                        if (type.isChannel() == 1) {
                            link = AppUtils.buildChannelVideosPageUrl(type.getUniqueId());
                            customScheme = getString(R.string.scheme_channel);
                        } else {
                            link = AppUtils.buildPlaylistPageUrl(type.getUniqueId());
                            customScheme = getString(R.string.scheme_playlist);
                        }

                        Bundle shareBundle = AppUtils.createShareBundle(
                                type.getTitle(),
                                link,
                                "",
                                type.getUniqueId(),
                                customScheme);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> AppUtils.contentShare(getActivity(),
                                    getChildFragmentManager(), shareBundle));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                return true;
            case R.id.menu_open_on_youtube:
                if (!TextUtils.isEmpty(mTypeUniqueId) && getActivity() != null) {
                    boolean isChannel = mIsChannel == 1;
                    try {
                        Intent ytIntent = isChannel ? YouTubeIntents.createChannelIntent(getActivity(), mTypeUniqueId)
                                : YouTubeIntents.createOpenPlaylistIntent(getActivity(), mTypeUniqueId);
                        startActivity(ytIntent);
                    } catch (Exception e) {
                        String url = isChannel ? AppUtils.buildChannelVideosPageUrl(mTypeUniqueId)
                                : AppUtils.buildPlaylistPageUrl(mTypeUniqueId);
                        AppUtils.openExternalBrowser(getActivity(), url);
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(YoutubeViewModel.class);
        mViewModel.loadPagedVideos(mTypeId, mTypeUniqueId, mIsChannel == 1);

        // subscribeUi(mViewModel);

        new Handler().postDelayed(() ->
                        subscribeUi(mViewModel),
                TimeUnit.MILLISECONDS.toMillis(150));
    }

    private void subscribeUi(YoutubeViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getPagedVideos().observe(this, pagedList -> {
            if (pagedList == null) return;
            if (mAdapter != null) mAdapter.submitList(pagedList);
        });

        viewModel.getSearchInfo(TYPE_VIDEO_REFRESH_WORK + mTypeId).observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty()) return;
            WorkInfo workInfo = workInfos.get(0);
            boolean finished = workInfo.getState().isFinished();

            if (!finished) onRefreshStarted();
            else {
                onRefreshComplete();
                PrefUtils.setScrolledPosition(getActivity(), mTypeId, 0);
            }
        });

        viewModel.getLoadMoreInfo(TYPE_LOAD_MORE_WORK + mTypeId).observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty()) return;
            WorkInfo workInfo = workInfos.get(0);
            boolean finished = workInfo.getState().isFinished();

            if (!finished) onLoadMoreStarted();
            else onLoadMoreComplete();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeListPosition();
    }

    private void resumeListPosition() {
        int position = PrefUtils.getScrolledPosition(getActivity(), mTypeId);
        setScrollToPosition(position, false);
        // PrefUtils.setScrolledPosition(getActivity(), mTypeId, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        onRefreshComplete();
    }

    @Override
    public void onRefresh() {
        initiateRefresh();
    }

    private void initiateRefresh() {
        if (mViewModel != null) {
            PrefUtils.setYoutubeNextPageToken(getActivity(), mTypeId, "");
            // PrefUtils.setIsYoutubeSearching(getActivity(), mTypeId, false);
            mViewModel.deleteAllVideosByTypeId(mTypeId);
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

    private void onLoadMoreStarted() {
        if (mLoadMoreBar != null) mLoadMoreBar.setVisibility(View.VISIBLE);
    }

    private void onLoadMoreComplete() {
        if (mLoadMoreBar != null) mLoadMoreBar.setVisibility(View.GONE);
    }

    private int getPositionByItemId(int itemId) {
        try {
            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                if (itemId == mAdapter.getItemId(i))
                    return i;
            }
        } catch (Exception ignore) {
        }
        return -1;
    }

    private int getItemIdAtPosition(int position) {
        try {
            if (mAdapter != null && mAdapter.getItemCount() > 0) {
                return (int) mAdapter.getItemId(position);
            }
        } catch (Exception ignore) {
        }
        return 0;
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
}
