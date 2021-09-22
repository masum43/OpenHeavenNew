package com.newandromo.dev18147.app821162.service;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.newandromo.dev18147.app821162.AppLifecycleObserver;
import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.CategoryEntity;
import com.newandromo.dev18147.app821162.db.entity.EntryEntity;
import com.newandromo.dev18147.app821162.db.entity.FeedEntity;
import com.newandromo.dev18147.app821162.parser.JsonParser;
import com.newandromo.dev18147.app821162.parser.XmlParser;
import com.newandromo.dev18147.app821162.utils.NetworkUtils;
import com.newandromo.dev18147.app821162.utils.NotificationUtil;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_ONETIME_WORK;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.ALLOWED_URI_CHARACTERS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.USER_AGENT;

public class FeedPeriodicSyncWorker extends Worker {
    private static final String PERIODIC_REFRESH_WORK_NAME = "periodic_refresh_work_name";
    private static final String TAG_PERIODIC_REFRESH_WORK = "tag_periodic_refresh_work";
    private static final long DEFAULT_INTERVAL = 15; // Default 15 minutes

    private Context mContext;

    public FeedPeriodicSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
    }

    public static void scheduleOneTimeWork(Context context) {
        try {
            Data data = new Data.Builder()
                    .putBoolean(INTENT_EXTRA_IS_ONETIME_WORK, true)
                    .build();

            OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(FeedPeriodicSyncWorker.class)
                    .setInputData(data)
                    .build();
            WorkManager.getInstance(context).beginUniqueWork("one_time",
                    ExistingWorkPolicy.REPLACE, work).enqueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void schedulePeriodicWork(Context context) {
        try {
            long duration = DEFAULT_INTERVAL;

            if (TimeUnit.MINUTES.toMillis(duration) < PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS)
                duration = TimeUnit.MILLISECONDS.toMinutes(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS);

            PeriodicWorkRequest.Builder periodicWorkBuilder =
                    new PeriodicWorkRequest.Builder(FeedPeriodicSyncWorker.class, duration, TimeUnit.MINUTES);

            Constraints workConstraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest periodicWork = periodicWorkBuilder
                    .setConstraints(workConstraints)
                    .addTag(TAG_PERIODIC_REFRESH_WORK)
                    .build();

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(PERIODIC_REFRESH_WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP, periodicWork);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            if (mContext == null) mContext = getApplicationContext();

            boolean isOneTimeWork = getInputData().getBoolean(INTENT_EXTRA_IS_ONETIME_WORK, false);

            OkHttpClient client = NetworkUtils.getOkHttpClient(true, 15, 20);

            DataRepository repo = ((MyApplication) mContext).getRepository();

            List<CategoryEntity> categories = repo.getAllCategories();
            int firstCatId = categories.get(0).getId();
            List<FeedEntity> feeds = repo.getFeedsByCategory(firstCatId);

            if (feeds == null || feeds.isEmpty()) return Result.failure();

            List<Long> rowIds = new ArrayList<>();

            for (FeedEntity feed : feeds) {
                try {
                    String feedUrl = Uri.encode(feed.getFeedUrl(), ALLOWED_URI_CHARACTERS);
                    if (!TextUtils.isEmpty(feedUrl)) {
                        List<Long> ids = getNewlyInsertedRowIds(repo, client, feed);
                        if (ids != null && !ids.isEmpty()) {
                            rowIds.addAll(ids);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    if (!TextUtils.isEmpty(feed.getFeedUrl())) {
                        List<Long> ids = getNewlyInsertedRowIdsJSoup(repo, feed);
                        if (ids != null && !ids.isEmpty()) {
                            rowIds.addAll(ids);
                        }
                    }
                }
            }

            if (!rowIds.isEmpty()) {
                List<EntryEntity> entries = repo.getDatabase().entryDao().getEntriesByIds(rowIds);

                if (entries != null && !entries.isEmpty()) {
                    initializeNotification(entries, isOneTimeWork);
                }
            }

            RemoteConfig.initiateRemoteConfig(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.success();
    }

    private void initializeNotification(List<EntryEntity> entries, boolean isOneTimeWork) {
        if (!AppLifecycleObserver.isAppOnForeground || isOneTimeWork) {
            NotificationUtil notificationUtil = new NotificationUtil(mContext);
            notificationUtil.createNewPostsNotification(entries);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private List<Long> getNewlyInsertedRowIds(DataRepository repo, OkHttpClient client, FeedEntity feed)
            throws IOException {
        List<Long> insertedRowIds = new ArrayList<>();

        boolean isJson = feed.getIsJson() == 1;

        String url = Uri.encode(feed.getFeedUrl(), ALLOWED_URI_CHARACTERS);

        Response response = getResponse(client, url);
        if (!response.isSuccessful()) return null;

        List<EntryEntity> parsedEntries;

        try {
            if (isJson) {
                JsonParser parser = new JsonParser();
                parsedEntries = parser.getEntriesFromWordPressJson(feed.getId(), response.body().byteStream());
            } else {
                XmlParser parser = new XmlParser();
                parsedEntries = parser.getEntriesFromXml(feed.getId(), response.body().byteStream());
            }

            insertedRowIds = getInsertedRowIds(repo, parsedEntries);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.body().byteStream().close();
                response.body().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return insertedRowIds;
    }

    private List<Long> getNewlyInsertedRowIdsJSoup(DataRepository repo, FeedEntity feed) {
        List<Long> insertedRowIds = new ArrayList<>();
        try {
            boolean isJson = feed.getIsJson() == 1;

            String url = Uri.encode(feed.getFeedUrl(), ALLOWED_URI_CHARACTERS);

            Document document = getHtmlDocument(url);

            List<EntryEntity> parsedEntries;

            if (isJson) {
                JsonParser parser = new JsonParser();
                parsedEntries = parser.getEntriesFromWordPressJson(feed.getId(), document);
            } else {
                XmlParser parser = new XmlParser();
                parsedEntries = parser.getEntriesFromXml(feed.getId(), document);
            }

            insertedRowIds = getInsertedRowIds(repo, parsedEntries);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insertedRowIds;
    }

    private List<Long> getInsertedRowIds(DataRepository repo, List<EntryEntity> parsedEntries) {
        List<Long> insertedRowIds = new ArrayList<>();
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
                if (rowIds != null && !rowIds.isEmpty()) {
                    insertedRowIds.addAll(rowIds);
                } else {
                    for (EntryEntity entry : newEntries) {
                        try {
                            int entryId = repo.getDatabase().entryDao().getEntryIdByUrl(entry.getUrl());
                            insertedRowIds.add((long) entryId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (!oldEntries.isEmpty()) {
                int result = repo.updateEntries(oldEntries);

                if (BuildConfig.DEBUG)
                    Timber.d("%s feed entrie(s) updated!", String.valueOf(result));
            }

            trimAndSyncData(repo);
        }
        return insertedRowIds;
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

    private boolean isEntryUrlExists(DataRepository repo, EntryEntity entity) {
        try {
            return repo.getNumEntriesByFeed(entity.getFeedId(), entity.getUrl()) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isEntryTitleDateExists(DataRepository repo, EntryEntity entity) {
        try {
            return repo.getNumEntriesByFeed(
                    entity.getFeedId(), entity.getTitle(), entity.getDateMillis()) > 0;
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
