package com.newandromo.dev18147.app821162.service;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.newandromo.dev18147.app821162.AppExecutors;
import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.CategoryEntity;
import com.newandromo.dev18147.app821162.db.entity.EntryEntity;
import com.newandromo.dev18147.app821162.db.entity.FeedEntity;
import com.newandromo.dev18147.app821162.parser.JsonParser;
import com.newandromo.dev18147.app821162.parser.XmlParser;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.NetworkUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_CATEGORY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_REFRESH_ALL;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_SEARCH;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_SEARCH_QUERY;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.ALLOWED_URI_CHARACTERS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.USER_AGENT;

public class FeedSyncWorker extends Worker {
    private static final String TAG = FeedSyncWorker.class.getSimpleName();
    private static final String FEED_REFRESH_WORK_NAME = "feed_refresh_work_name";
    private static final String FEED_SEARCH_WORK_NAME = "feed_search_work_name";
    public static final String TAG_FEED_REFRESH_WORK = "tag_feed_refresh_work";
    public static final String TAG_FEED_SEARCH_WORK = "tag_feed_search_work";

    private Context mContext;

    public FeedSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
    }

    public static void refreshWork(Context context, int id, boolean isCategory) {
        try {
            Data data = new Data.Builder()
                    .putInt(INTENT_EXTRA_ID, id)
                    .putBoolean(INTENT_EXTRA_IS_CATEGORY, isCategory)
                    .putBoolean(INTENT_EXTRA_IS_REFRESH_ALL, false)
                    .putBoolean(INTENT_EXTRA_IS_SEARCH, false)
                    .build();

            OneTimeWorkRequest refreshWork = new OneTimeWorkRequest.Builder(FeedSyncWorker.class)
                    .setInputData(data)
                    .addTag(TAG_FEED_REFRESH_WORK + id)
                    .build();

            WorkManager.getInstance(context).beginUniqueWork(FEED_REFRESH_WORK_NAME + id,
                    ExistingWorkPolicy.REPLACE, refreshWork).enqueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void refreshAllWork(Context context, int id, boolean isCategory) {
        try {
            Data data = new Data.Builder()
                    .putInt(INTENT_EXTRA_ID, id)
                    .putBoolean(INTENT_EXTRA_IS_CATEGORY, isCategory)
                    .putBoolean(INTENT_EXTRA_IS_REFRESH_ALL, true)
                    .putBoolean(INTENT_EXTRA_IS_SEARCH, false)
                    .build();

            OneTimeWorkRequest refreshWork = new OneTimeWorkRequest.Builder(FeedSyncWorker.class)
                    .setInputData(data)
                    .addTag(TAG_FEED_REFRESH_WORK)
                    .build();

            WorkManager.getInstance(context).beginUniqueWork(FEED_REFRESH_WORK_NAME,
                    ExistingWorkPolicy.REPLACE, refreshWork).enqueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchWork(Context context, int id, String query) {
        try {
            Data data = new Data.Builder()
                    .putInt(INTENT_EXTRA_ID, id)
                    .putBoolean(INTENT_EXTRA_IS_SEARCH, true)
                    .putString(INTENT_EXTRA_SEARCH_QUERY, query)
                    .build();

            OneTimeWorkRequest searchWork = new OneTimeWorkRequest.Builder(FeedSyncWorker.class)
                    .setInputData(data)
                    .addTag(TAG_FEED_SEARCH_WORK + id)
                    .build();

            WorkManager.getInstance(context).beginUniqueWork(FEED_SEARCH_WORK_NAME + id,
                    ExistingWorkPolicy.REPLACE, searchWork).enqueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            if (mContext == null) mContext = getApplicationContext();
            DataRepository repo = ((MyApplication) mContext).getRepository();
            int id = getInputData().getInt(INTENT_EXTRA_ID, 0);
            boolean isCategory = getInputData().getBoolean(INTENT_EXTRA_IS_CATEGORY, false);
            boolean isRefreshAll = getInputData().getBoolean(INTENT_EXTRA_IS_REFRESH_ALL, false);
            boolean isSearch = getInputData().getBoolean(INTENT_EXTRA_IS_SEARCH, false);
            String query = getInputData().getString(INTENT_EXTRA_SEARCH_QUERY);

            OkHttpClient client = NetworkUtils.getOkHttpClient(true, 15, 20);

            if (RemoteConfig.isLimitOkHttpMaxRequests()) {
                client.dispatcher().setMaxRequests(RemoteConfig.getOkHttpMaxRequests());
            }

            if (isSearch) {
                List<CategoryEntity> categories = repo.getAllCategories();
                int firstCatId = categories.get(0).getId();
                List<FeedEntity> feeds = repo.getFeedsByCategory(firstCatId);

                if (feeds != null) {
                    Collections.reverse(feeds);
                    if (!feeds.isEmpty()) {
                        for (FeedEntity feed : feeds) {
                            try {
                                fetchSynchronously(repo, client, feed, true, query);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                List<FeedEntity> feeds;

                if (isCategory) feeds = repo.getFeedsByCategory(id);
                else feeds = repo.getAllFeeds();

                if (feeds == null || feeds.isEmpty()) return Result.failure();

                boolean isFetchSynchronously = true; // do this for only the first feed in list.
                for (FeedEntity feed : feeds)
                    try {
                        if (!TextUtils.isEmpty(feed.getFeedUrl())) {
                            if (isFetchSynchronously && !isRefreshAll) {
                                isFetchSynchronously = false;
                                fetchSynchronously(repo, client, feed, false, "");
                            } else {
                                fetchAsynchronously(repo, client, feed);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.success();
    }

    private void fetchSynchronously(DataRepository repo, OkHttpClient client, FeedEntity feed,
                                    boolean isSearch, String searchQuery) {
        try {
            String url = "";

            if (isSearch) {
                boolean isGoogleJson = feed.getIsGoogleJson() == 1;
                url = isGoogleJson ?
                        AppUtils.buildGoogleBlogPostSearchUrl(searchQuery, feed.getGoogleBlogId()) :
                        AppUtils.buildWordPressPostSearchUrl(searchQuery, feed.getSiteUrl());
                if (BuildConfig.DEBUG) Timber.tag(TAG).d("searchURL= %s", url);
            } else {
                try {
                    url = Uri.encode(feed.getFeedUrl(), ALLOWED_URI_CHARACTERS);
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Timber.e("fetchWithJSoup() error= %s", e.getMessage());
                }
            }

            Response response = getResponse(client, url);
            onResponse(response, repo, feed, isSearch);
        } catch (IOException e) {
            e.printStackTrace();
            fetchWithJSoup(repo, feed, isSearch, searchQuery, false);
        }
    }

    private void fetchAsynchronously(DataRepository repo, OkHttpClient client, FeedEntity feed) {
        String url = Uri.encode(feed.getFeedUrl(), ALLOWED_URI_CHARACTERS);

        client.newCall(getRequest(url)).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (BuildConfig.DEBUG) {
                    Timber.tag(TAG).e("onFailure() error= %s, URL= %s", e.getMessage(), url);
                    e.printStackTrace();
                }
                fetchWithJSoup(repo, feed, false, "", true);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                FeedSyncWorker.this.onResponse(response, repo, feed, false);
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void onResponse(Response response, DataRepository repo, FeedEntity feed, boolean isSearch) {
        try {
            boolean isJson = feed.getIsJson() == 1;
            boolean isGoogleJson = feed.getIsGoogleJson() == 1;
            if (response.isSuccessful()) {
                try {
                    List<EntryEntity> parsedEntries;
                    if (isSearch) {
                        JsonParser parser = new JsonParser();
                        if (isGoogleJson) {
                            parsedEntries = parser.getEntriesFromGoogleJson(feed.getId(), response.body().byteStream());
                        } else {
                            parsedEntries = parser.getEntriesFromWordPressJson(feed.getId(), response.body().byteStream());
                        }
                    } else {
                        if (isJson) {
                            JsonParser parser = new JsonParser();
                            parsedEntries = parser.getEntriesFromWordPressJson(feed.getId(), response.body().byteStream());
                        } else {
                            XmlParser parser = new XmlParser();
                            parsedEntries = parser.getEntriesFromXml(feed.getId(), response.body().byteStream());
                        }
                    }

                    insertParsedEntries(repo, parsedEntries);
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                        Timber.e("onResponse() error= %s class= %s", e.getMessage(), e.getClass().getSimpleName());
                    }
                } finally {
                    try {
                        response.body().byteStream().close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.body().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchWithJSoup(DataRepository repo, FeedEntity feed,
                                boolean isSearch, String searchQuery, boolean isBackground) {
        if (isBackground) {
            new AppExecutors().diskIO().execute(() -> {
                // Asynchronously fetching...
                fetchWithJSoup(repo, feed, isSearch, searchQuery);
            });
        } else fetchWithJSoup(repo, feed, isSearch, searchQuery);
    }

    private void fetchWithJSoup(DataRepository repo, FeedEntity feed, boolean isSearch, String searchQuery) {
        try {
            boolean isJson = feed.getIsJson() == 1;
            boolean isGoogleJson = feed.getIsGoogleJson() == 1;

            String url = "";

            if (isSearch) {
                url = isGoogleJson ?
                        AppUtils.buildGoogleBlogPostSearchUrl(searchQuery, feed.getGoogleBlogId()) :
                        AppUtils.buildWordPressPostSearchUrl(searchQuery, feed.getSiteUrl());
                if (BuildConfig.DEBUG) Timber.tag(TAG).d("searchURL= %s", url);
            } else {
                try {
                    url = Uri.encode(feed.getFeedUrl(), ALLOWED_URI_CHARACTERS);
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Timber.e("fetchWithJSoup() error= %s", e.getMessage());
                }
            }

            Document document = getHtmlDocument(url);

            List<EntryEntity> parsedEntries;
            if (isSearch) {
                JsonParser parser = new JsonParser();
                if (isGoogleJson) {
                    parsedEntries = parser.getEntriesFromGoogleJson(feed.getId(), document);
                } else {
                    parsedEntries = parser.getEntriesFromWordPressJson(feed.getId(), document);
                }
            } else {
                if (isJson) {
                    JsonParser parser = new JsonParser();
                    parsedEntries = parser.getEntriesFromWordPressJson(feed.getId(), document);
                } else {
                    XmlParser parser = new XmlParser();
                    parsedEntries = parser.getEntriesFromXml(feed.getId(), document);
                }
            }

            insertParsedEntries(repo, parsedEntries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertParsedEntries(DataRepository repo, List<EntryEntity> parsedEntries) {
        if (parsedEntries != null && !parsedEntries.isEmpty()) {
            List<EntryEntity> newEntries = new ArrayList<>();
            List<EntryEntity> oldEntries = new ArrayList<>();

            for (EntryEntity entry : parsedEntries) {
                if (!isEntryUrlExists(repo, entry) && !isEntryTitleDateExists(repo, entry)) {
                    newEntries.add(entry);
                } else {
                    EntryEntity e = repo.getEntryByFeedIdAndUrl(entry.getFeedId(), entry.getUrl());
                    EntryEntity oldEntry = new EntryEntity(e.getId(), e.getFeedId(),
                            entry.getTitle(), // might have changed
                            entry.getAuthor(), // might have changed
                            entry.getDate(), // might have changed
                            entry.getDateMillis(), // might have changed
                            e.getUrl(),
                            entry.getThumbUrl(), // might have changed
                            entry.getContent(), // might have changed
                            entry.getExcerpt(), // might have changed
                            e.isUnread(), e.isRecentRead(), e.isBookmarked());
                    oldEntries.add(oldEntry);
                }
            }

            if (!newEntries.isEmpty()) {
                List<Long> rowIds = repo.insertEntries(newEntries);

                if (BuildConfig.DEBUG)
                    Timber.tag(TAG).d("%s new feed entrie(s) inserted! IDs= %s",
                            String.valueOf(rowIds.size()), Arrays.toString(rowIds.toArray()));
            }

            if (!oldEntries.isEmpty()) {
                int result = repo.updateEntries(oldEntries);

                if (BuildConfig.DEBUG)
                    Timber.tag(TAG).d("%s feed entrie(s) updated!", String.valueOf(result));
            }

            trimAndSyncData(repo);
        }
    }

    private Response getResponse(OkHttpClient client, String url) throws IOException {
        return client.newCall(getRequest(url)).execute();
    }

    private Request getRequest(String url) {
        return new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(url)))
                .build();
    }

    private Document getHtmlDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .ignoreContentType(true)
                .userAgent(USER_AGENT)
                .timeout((int) TimeUnit.SECONDS.toMillis(10))
                .get();
    }

    private boolean isEntryUrlExists(DataRepository repo, EntryEntity entry) {
        try {
            return repo.getNumEntriesByFeed(entry.getFeedId(), entry.getUrl()) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isEntryTitleDateExists(DataRepository repo, EntryEntity entry) {
        try {
            return repo.getNumEntriesByFeed(
                    entry.getFeedId(), entry.getTitle(), entry.getDateMillis()) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void trimAndSyncData(DataRepository repo) {
        deleteExcessEntries(repo);
        updateReadAndBookmarkedEntries(repo);
    }

    private void deleteExcessEntries(DataRepository repo) {
        int offset = Integer.parseInt(PrefUtils.getItemsStorageLimit(mContext));
        try {
            List<Integer> entriesIds = repo.getEntriesIds();

            if (entriesIds != null && !entriesIds.isEmpty()) {
                if (entriesIds.size() > offset) {
                    int itemsToKeep = offset - 1;

                    List<Integer> ids = new ArrayList<>();
                    for (int i = 0; i < entriesIds.size(); i++) {
                        if (i > itemsToKeep) {
                            int entryId = entriesIds.get(i);
                            ids.add(entryId);
                        }
                    }
                    repo.deleteEntriesByIds(ids);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateReadAndBookmarkedEntries(DataRepository repo) {
        try {
            List<String> readEntriesUrls = repo.getReadEntriesUrls();
            if (readEntriesUrls != null && !readEntriesUrls.isEmpty()) {
                repo.updateEntriesUnreadByUrl(0, readEntriesUrls);
                // repo.updateEntriesRecentReadByUrl(0, urls);
            }

            List<String> bookmarkedEntriesUrls = repo.getBookmarkedEntriesUrls();
            if (bookmarkedEntriesUrls != null && !bookmarkedEntriesUrls.isEmpty()) {
                repo.updateEntriesBookmarkByUrl(1, bookmarkedEntriesUrls);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
