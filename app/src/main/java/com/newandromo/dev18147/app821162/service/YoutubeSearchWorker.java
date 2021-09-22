package com.newandromo.dev18147.app821162.service;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.YoutubeSearchEntity;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.MyDateUtils;
import com.newandromo.dev18147.app821162.utils.NetworkUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_LOAD_MORE;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_SEARCH_QUERY;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_CHANNEL_TITLE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_CONTENT_DETAILS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_DURATION;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_ITEMS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_NEXTPAGETOKEN;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_PUBLISHED_AT;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_SNIPPET;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_STATISTICS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_TITLE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_VIDEO_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_VIEW_COUNT;

public class YoutubeSearchWorker extends Worker {
    private static final String VIDEO_SEARCH_WORK_NAME = "video_search_work";
    public static final String VIDEO_SEARCH_WORK = "tag_video_search_work";

    private static final String LOAD_MORE_WORK_NAME = "load_more_work";
    public static final String LOAD_MORE_WORK = "tag_load_more_work";

    public YoutubeSearchWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void searchWork(Context context, String query) {
        try {
            Data data = new Data.Builder()
                    .putString(INTENT_EXTRA_SEARCH_QUERY, query)
                    .putBoolean(INTENT_EXTRA_IS_LOAD_MORE, false)
                    .build();

            OneTimeWorkRequest searchWork = new OneTimeWorkRequest.Builder(YoutubeSearchWorker.class)
                    .setInputData(data)
                    .addTag(VIDEO_SEARCH_WORK)
                    .build();

            WorkManager.getInstance(context).beginUniqueWork(VIDEO_SEARCH_WORK_NAME,
                    ExistingWorkPolicy.REPLACE, searchWork).enqueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadMoreWork(Context context, String query) {
        try {
            Data data = new Data.Builder()
                    .putString(INTENT_EXTRA_SEARCH_QUERY, query)
                    .putBoolean(INTENT_EXTRA_IS_LOAD_MORE, true)
                    .build();

            OneTimeWorkRequest searchWork = new OneTimeWorkRequest.Builder(YoutubeSearchWorker.class)
                    .setInputData(data)
                    .addTag(LOAD_MORE_WORK)
                    .build();

            WorkManager.getInstance(context).beginUniqueWork(LOAD_MORE_WORK_NAME,
                    ExistingWorkPolicy.REPLACE, searchWork).enqueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Context context = getApplicationContext();
            DataRepository repo = ((MyApplication) context).getRepository();

            OkHttpClient client = NetworkUtils.getOkHttpClient(true, 15, 20);

            if (RemoteConfig.isLimitOkHttpMaxRequests()) {
                client.dispatcher().setMaxRequests(RemoteConfig.getOkHttpMaxRequests());
            }

            String query = getInputData().getString(INTENT_EXTRA_SEARCH_QUERY);
            boolean isLoadMore = getInputData().getBoolean(INTENT_EXTRA_IS_LOAD_MORE, false);

            String url = "";

            if (isLoadMore) {
                String nextPageToken = PrefUtils.getYoutubeNextPageToken(context);
                if (!TextUtils.isEmpty(nextPageToken))
                    url = AppUtils.buildYoutubeVideoSearchUrl(query, nextPageToken, true);
            } else {
                url = AppUtils.buildYoutubeVideoSearchUrl(query, "", false);
            }

            // Timber.d("query= %s isLoadMore= %s, url= %s", query, isLoadMore, url);

            List<String> videoIds = getYoutubeVideoIds(context, client, url);
            if (!videoIds.isEmpty()) {
                String ids = TextUtils.join(",", videoIds);

                String videoDetailUrl = AppUtils.buildYoutubeVideoDetailUrl(ids);
                // if (BuildConfig.DEBUG) Timber.d("VideoDetail_URL= %s", videoDetailUrl);

                inputSearchedVideos(repo, client, videoDetailUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrefUtils.setIsYoutubeSearching(getApplicationContext(), false);
        return Result.success();
    }

    private void inputSearchedVideos(DataRepository repo, OkHttpClient client, String videoDetailUrl)
            throws IOException, JSONException {
        List<YoutubeSearchEntity> videoList = getVideoDetails(client, videoDetailUrl);

        if (!videoList.isEmpty()) {
            repo.insertSearchedVideos(videoList);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private List<YoutubeSearchEntity> getVideoDetails(OkHttpClient client, String videoDetailUrl)
            throws IOException, JSONException {
        List<YoutubeSearchEntity> videoList = new ArrayList<>();

        Response response = getResponse(client, videoDetailUrl);

        if (response.isSuccessful()) {
            DecimalFormat formatter = new DecimalFormat("#,###,###");

            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray itemsArray = jsonObject.getJSONArray(TAG_ITEMS);

            for (int i = 0; i < itemsArray.length(); i++) {
                try {
                    String channelTitle = "", duration = "";
                    JSONObject item = itemsArray.getJSONObject(i);
                    String videoId = item.getString(TAG_ID);
                    JSONObject snippet = item.getJSONObject(TAG_SNIPPET);
                    String title = snippet.getString(TAG_TITLE);
                    String publishedAt = snippet.getString(TAG_PUBLISHED_AT);
                    JSONObject contentDetails = item.getJSONObject(TAG_CONTENT_DETAILS);
                    JSONObject statistics = item.getJSONObject(TAG_STATISTICS);
                    String viewCount = statistics.getString(TAG_VIEW_COUNT);
                    String thumb = AppUtils.getVideoThumb(snippet, videoId);
                    long dateMillis = MyDateUtils.parseTimestampToMillis(publishedAt, true);

                    if (!snippet.isNull(TAG_CHANNEL_TITLE)) {
                        channelTitle = snippet.getString(TAG_CHANNEL_TITLE);
                    }

                    if (!contentDetails.isNull(TAG_DURATION)) {
                        duration = contentDetails.getString(TAG_DURATION);
                        if (!TextUtils.isEmpty(duration)) {
                            duration = MyDateUtils.formatDuration(duration);
                            String[] separated = TextUtils.split(duration, "\\:");
                            if (separated[0].startsWith("0")) {
                                duration = duration.substring(1);
                            }
                        } else duration = "";
                    }

                    if (!TextUtils.isEmpty(viewCount)) {
                        try {
                            viewCount = AppUtils.formatViewCount(formatter, viewCount);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    YoutubeSearchEntity video = new YoutubeSearchEntity(
                            videoId, title, channelTitle, publishedAt, dateMillis,
                            duration, viewCount, thumb);
                    videoList.add(video);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return videoList;
    }

    @SuppressWarnings("ConstantConditions")
    private List<String> getYoutubeVideoIds(Context context, OkHttpClient client, String url)
            throws IOException, JSONException {
        List<String> videoIds = new ArrayList<>();

        Response response = getResponse(client, url);

        if (response.isSuccessful()) {
            JSONObject jsonObject = new JSONObject(response.body().string());

            String oldNextPageToken = PrefUtils.getYoutubeNextPageToken(context);
            if (!jsonObject.isNull(TAG_NEXTPAGETOKEN)) {
                String newNextPageToken = jsonObject.getString(TAG_NEXTPAGETOKEN).trim();
                if (!oldNextPageToken.equalsIgnoreCase(newNextPageToken))
                    PrefUtils.setYoutubeNextPageToken(context, newNextPageToken);
            } else PrefUtils.setYoutubeNextPageToken(context, "");

            JSONArray items = jsonObject.getJSONArray(TAG_ITEMS);
            for (int i = 0; i < items.length(); i++) {
                try {
                    JSONObject item = items.getJSONObject(i);
                    String videoId = item.getJSONObject(TAG_ID).getString(TAG_VIDEO_ID);
                    videoIds.add(videoId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return videoIds;
    }

    private Response getResponse(OkHttpClient client, String url) throws IOException {
        return client.newCall(getRequest(url)).execute();
    }

    private Request getRequest(String url) {
        return new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(url)))
                .build();
    }
}
