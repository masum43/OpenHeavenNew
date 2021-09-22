package com.newandromo.dev18147.app821162.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.newandromo.dev18147.app821162.MyAnalytics;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.db.entity.YoutubeSearchEntity;
import com.newandromo.dev18147.app821162.ui.adapter.SuggestionListAdapter;
import com.newandromo.dev18147.app821162.ui.adapter.YoutubeSearchAdapter;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.NetworkUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import com.newandromo.dev18147.app821162.viewmodel.YoutubeViewModel;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.newandromo.dev18147.app821162.MyAnalytics.CT_VIDEO;
import static com.newandromo.dev18147.app821162.service.YoutubeSearchWorker.LOAD_MORE_WORK;
import static com.newandromo.dev18147.app821162.service.YoutubeSearchWorker.VIDEO_SEARCH_WORK;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_SEARCH_QUERY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.YOUTUBE_SHORT_URL;

public class YoutubeSearchFragment extends Fragment implements YoutubeSearchAdapter.YoutubeSearchListener {
    private YoutubeViewModel mViewModel;
    private YoutubeSearchAdapter mAdapter;
    private String mSearchQuery;
    private SearchView mSearchView;
    private ProgressBar mProgressBar;
    private ProgressBar mLoadMoreBar;
    private SuggestionListAdapter suggestionListAdapter;// todo: for suggestion

    public YoutubeSearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube_search, container, false);

        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(BUNDLE_SEARCH_QUERY, "");
        }

        RecyclerView resultList = view.findViewById(R.id.recycler_view_list);
        mProgressBar = view.findViewById(R.id.progress);
        mLoadMoreBar = view.findViewById(R.id.loadMoreBar);

        mAdapter = new YoutubeSearchAdapter(getActivity(), this);
        mAdapter.setIsChannel(false);

        // resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(getActivity()));
        resultList.setItemAnimator(new DefaultItemAnimator());
        resultList.setVerticalScrollBarEnabled(true);
        resultList.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_SEARCH_QUERY, mSearchQuery);
    }

    @Override
    public void onSelected(YoutubeSearchEntity video) {
        int playerOption = Integer.parseInt(PrefUtils.getYouTubePlayerOption(getActivity()));

        String url = String.format(YOUTUBE_SHORT_URL, video.getVideoId());
        MyAnalytics.selectContent(getActivity(), video.getTitle(), url, CT_VIDEO);

        AppUtils.openYouTube(getActivity(), video.getVideoId(), playerOption);
    }

    @Override
    public void onContextMenuAction(YoutubeSearchEntity video) {
        String url = String.format(YOUTUBE_SHORT_URL, video.getVideoId());
        Bundle shareBundle = AppUtils.createShareBundle(video.getTitle(), url, video.getThumbUrl(),
                url, getString(R.string.scheme_youtube));
        AppUtils.contentShare(getActivity(), getChildFragmentManager(), shareBundle);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        try {
            inflater.inflate(R.menu.menu_youtube_search, menu);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NotNull Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            searchItem.expandActionView();
            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    if (getActivity() != null)
                        getActivity().onBackPressed();
                    return false;
                }
            });

            mSearchView = (SearchView) searchItem.getActionView();
            if (mSearchView != null) {
                String search = getString(R.string.search);
                String youtube = getString(R.string.youtube_api_service);
                mSearchView.setQueryHint(String.format("%s - %s", search, youtube));
                mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        try {
                            setSearchQuery(query.trim());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        clearSearchFocus();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        try {// todo: for suggestion
                            if (RemoteConfig.isEnableYoutubeSearchSuggestions()) {
                                searchSuggestions(newText.trim());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                });

                mSearchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                    try {
                        mSearchQuery = mSearchView.getQuery().toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (hasFocus) mViewModel.deleteSearchedVideos();
                });

                suggestionListAdapter = new SuggestionListAdapter(getActivity());
                mSearchView.setSuggestionsAdapter(suggestionListAdapter);// todo: for suggestion
                mSearchView.setOnSuggestionListener(new SearchSuggestionListener(mSearchView));

                setSearchQuery();
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(YoutubeViewModel.class);
        mViewModel.loadSearchedVideos();

        // subscribeUi(mViewModel);

        new Handler().postDelayed(() -> subscribeUi(mViewModel), TimeUnit.MILLISECONDS.toMillis(150));
    }

    private void subscribeUi(YoutubeViewModel viewModel) {
        viewModel.getSearchedVideos().observe(this, pagedList -> {
            if (pagedList == null) return;
            if (mAdapter != null) mAdapter.submitList(pagedList);
        });

        viewModel.getSearchInfo(VIDEO_SEARCH_WORK).observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty()) return;
            WorkInfo workInfo = workInfos.get(0);
            boolean finished = workInfo.getState().isFinished();

            if (!finished) onRefreshStarted();
            else onRefreshComplete();
        });

        viewModel.getLoadMoreInfo(LOAD_MORE_WORK).observe(this, workInfos -> {
            if (workInfos == null || workInfos.isEmpty()) return;
            WorkInfo workInfo = workInfos.get(0);
            boolean finished = workInfo.getState().isFinished();

            if (!finished) onLoadMoreStarted();
            else onLoadMoreComplete();
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        onLoadMoreComplete();
    }

    @Override
    public void onDestroy() {
        try {
            mViewModel.deleteSearchedVideos();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void setSearchQuery() {
        if (!TextUtils.isEmpty(mSearchQuery))
            if (mSearchView != null) {
                mSearchView.setQuery(mSearchQuery, true);
            }
    }

    private void setSearchQuery(String query) {
        if (!TextUtils.isEmpty(query)) {
            mSearchQuery = query;
            PrefUtils.setYoutubeNextPageToken(getActivity(), "");
            mViewModel.deleteSearchedVideos();
            mViewModel.mQuery = query;
            mViewModel.searchVideos(query);
            MyAnalytics.searchItem(getActivity(), query);
        }
    }

    private void clearSearchFocus() {
        if (mSearchView != null) mSearchView.clearFocus();
    }

    private void onRefreshStarted() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
    }

    private void onRefreshComplete() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void onLoadMoreStarted() {
        if (mLoadMoreBar != null) mLoadMoreBar.setVisibility(View.VISIBLE);
    }

    private void onLoadMoreComplete() {
        if (mLoadMoreBar != null) mLoadMoreBar.setVisibility(View.GONE);
    }

    private void searchSuggestions(String query) {// todo: for suggestion
        if (!TextUtils.isEmpty(query)) {
            SuggestionSearchRunnable suggestionSearchRunnable = new SuggestionSearchRunnable(query);
            Thread searchThread = new Thread(suggestionSearchRunnable);
            searchThread.start();
        }
    }

    // todo: for suggestion
    private class SearchSuggestionListener implements SearchView.OnSuggestionListener {

        private SearchView searchView;

        private SearchSuggestionListener(SearchView searchView) {
            this.searchView = searchView;
        }

        @Override
        public boolean onSuggestionSelect(int position) {
            String suggestion = suggestionListAdapter.getSuggestion(position);
            if (!TextUtils.isEmpty(suggestion)) {
                searchView.setQuery(suggestion, true);
            }
            return false;
        }

        @Override
        public boolean onSuggestionClick(int position) {
            String suggestion = suggestionListAdapter.getSuggestion(position);
            if (!TextUtils.isEmpty(suggestion)) {
                searchView.setQuery(suggestion, true);
            }
            return false;
        }
    }

    // todo: for suggestion
    private class SuggestionResultRunnable implements Runnable {

        private ArrayList<String> suggestions;

        private SuggestionResultRunnable(ArrayList<String> suggestions) {
            this.suggestions = suggestions;
        }

        @Override
        public void run() {
            suggestionListAdapter.updateAdapter(suggestions);
        }
    }

    // todo: for suggestion
    private class SuggestionSearchRunnable implements Runnable {
        final OkHttpClient client = NetworkUtils.getOkHttpClient(true, 2, 2);
        final Handler h = new Handler();
        private final String query;

        private SuggestionSearchRunnable(String query) {
            this.query = query;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void run() {
            try {
                ArrayList<String> suggestions = new ArrayList<>();

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("suggestqueries.google.com")
                        .appendPath("complete")
                        .appendPath("search")
                        .appendQueryParameter("ds", "yt")
                        .appendQueryParameter("client", "toolbar")
                        // .appendQueryParameter("hl", contentCountry)
                        .appendQueryParameter("q", query);
                String url = builder.build().toString();

                // Timber.d("URL= %s", url);

                Request request = new Request.Builder().url(HttpUrl.parse(url)).build();

                OkHttpClient copy = client.newBuilder()
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .readTimeout(2, TimeUnit.SECONDS)
                        .build();

                Response response = copy.newCall(request).execute();

                if (response.isSuccessful()) {
                    Document doc = Jsoup.parse(response.body().string(), "", Parser.xmlParser());
                    Elements elements = doc.select("toplevel").select("CompleteSuggestion");

                    for (Element element : elements) {
                        suggestions.add(element.select("suggestion").attr("data"));
                    }

                    h.post(new SuggestionResultRunnable(suggestions));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
