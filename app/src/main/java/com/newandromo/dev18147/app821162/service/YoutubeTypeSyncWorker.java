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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.YoutubeTypeEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeVideoEntity;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.MyDateUtils;
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
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_CHANNEL;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_LOAD_MORE;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_TYPE_VIDEO;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_UNIQUE_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_CHANNEL;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_CHANNEL_TITLE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_CONTENT_DETAILS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_DURATION;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_ITEMS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_KIND;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_NEXTPAGETOKEN;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_PLAYLIST;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_PUBLISHED_AT;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_SNIPPET;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_STATISTICS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_TITLE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_UNTITLED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_VIDEO_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_VIEW_COUNT;

public class YoutubeTypeSyncWorker extends Worker {
    private static final String TYPE_UPDATE_WORK_NAME = "type_update_work";

    private static final String TYPE_VIDEO_REFRESH_WORK_NAME = "type_video_refresh_work";
    public static final String TYPE_VIDEO_REFRESH_WORK = "tag_type_video_refresh_work";

    private static final String TYPE_LOAD_MORE_WORK_NAME = "type_load_more_work";
    public static final String TYPE_LOAD_MORE_WORK = "tag_type_load_more_work";

    public YoutubeTypeSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void refreshWork(Context context, int typeId, String uniqueId, boolean isChannel) {
        try {
            Data data = new Data.Builder()
                    .putInt(INTENT_EXTRA_ID, typeId)
                    .putString(INTENT_EXTRA_UNIQUE_ID, uniqueId)
                    .putBoolean(INTENT_EXTRA_IS_CHANNEL, isChannel)
                    .putBoolean(INTENT_EXTRA_IS_TYPE_VIDEO, true)
                    .putBoolean(INTENT_EXTRA_IS_LOAD_MORE, false)
                    .build();

            OneTimeWorkRequest updateWork = new OneTimeWorkRequest.Builder(YoutubeTypeSyncWorker.class)
                    .setInputData(data)
                    .addTag(TYPE_VIDEO_REFRESH_WORK + typeId)
                    .build();

            WorkManager.getInstance(context).beginUniqueWork(TYPE_VIDEO_REFRESH_WORK_NAME + typeId,
                    ExistingWorkPolicy.REPLACE, updateWork).enqueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadMoreWork(Context context, int typeId, String uniqueId, boolean isChannel) {
        try {
            Data data = new Data.Builder()
                    .putInt(INTENT_EXTRA_ID, typeId)
                    .putString(INTENT_EXTRA_UNIQUE_ID, uniqueId)
                    .putBoolean(INTENT_EXTRA_IS_CHANNEL, isChannel)
                    .putBoolean(INTENT_EXTRA_IS_TYPE_VIDEO, true)
                    .putBoolean(INTENT_EXTRA_IS_LOAD_MORE, true)
                    .build();

            OneTimeWorkRequest updateWork = new OneTimeWorkRequest.Builder(YoutubeTypeSyncWorker.class)
                    .setInputData(data)
                    .addTag(TYPE_LOAD_MORE_WORK + typeId)
                    .build();

            WorkManager.getInstance(context).beginUniqueWork(TYPE_LOAD_MORE_WORK_NAME + typeId,
                    ExistingWorkPolicy.REPLACE, updateWork).enqueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateYoutubeTypeWork(Context context) {
        try {
            Data data = new Data.Builder()
                    .putBoolean(INTENT_EXTRA_IS_TYPE_VIDEO, false)
                    .build();

            OneTimeWorkRequest updateWork = new OneTimeWorkRequest.Builder(YoutubeTypeSyncWorker.class)
                    .setInputData(data)
                    .build();

            WorkManager.getInstance(context).beginUniqueWork(TYPE_UPDATE_WORK_NAME,
                    ExistingWorkPolicy.REPLACE, updateWork).enqueue();
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

            boolean isTypeVideo = getInputData().getBoolean(INTENT_EXTRA_IS_TYPE_VIDEO, true);

            if (isTypeVideo) {
                int typeId = getInputData().getInt(INTENT_EXTRA_ID, 0);
                String uniqueId = getInputData().getString(INTENT_EXTRA_UNIQUE_ID);
                boolean isChannel = getInputData().getBoolean(INTENT_EXTRA_IS_CHANNEL, true);
                boolean isLoadMore = getInputData().getBoolean(INTENT_EXTRA_IS_LOAD_MORE, false);

                String url = "";

                if (isLoadMore) {
                    String nextPageToken = PrefUtils.getYoutubeNextPageToken(context, typeId);
                    if (!TextUtils.isEmpty(nextPageToken))
                        url = isChannel ?
                                AppUtils.buildYoutubeChannelUrl(uniqueId, nextPageToken, true) :
                                AppUtils.buildYoutubePlaylistUrl(uniqueId, nextPageToken, true);
                } else {
                    url = isChannel ?
                            AppUtils.buildYoutubeChannelUrl(uniqueId, "", false) :
                            AppUtils.buildYoutubePlaylistUrl(uniqueId, "", false);
                }

                try {
                    List<String> videoIds = getYoutubeTypeVideoIds(context, client, typeId, url, isChannel);
                    if (!videoIds.isEmpty()) {
                        String ids = TextUtils.join(",", videoIds);

                        String videoDetailUrl = AppUtils.buildYoutubeVideoDetailUrl(ids);
                        // if (BuildConfig.DEBUG) Timber.d("VideoDetail_URL= %s", videoDetailUrl);

                        inputTypeVideos(repo, client, typeId, videoDetailUrl);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                PrefUtils.setIsYoutubeSearching(context, typeId, false);
            } else {
                List<YoutubeTypeEntity> types = repo.getAllYoutubeTypes();

                if (types == null || types.isEmpty()) return Result.failure();

                List<String> channelIds = new ArrayList<>();
                List<String> playlistIds = new ArrayList<>();

                for (YoutubeTypeEntity type : types) {
                    boolean isChannel = type.isChannel() == 1;
                    if (isChannel) channelIds.add(type.getUniqueId());
                    else playlistIds.add(type.getUniqueId());
                }

                String channelUrl = AppUtils.buildChannelDetailUrl(TextUtils.join(",", channelIds));
                String playlistUrl = AppUtils.buildPlaylistDetailUrl(TextUtils.join(",", playlistIds));

                fetchYouTubeTypeDetails(context, repo, client, channelUrl);
                fetchYouTubeTypeDetails(context, repo, client, playlistUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
        return Result.success();
    }

    private void inputTypeVideos(DataRepository repo, OkHttpClient client, int typeId, String videoDetailUrl)
            throws IOException, JSONException {
        List<YoutubeVideoEntity> videoList = getVideoDetails(client, typeId, videoDetailUrl);

        if (!videoList.isEmpty()) {
            List<YoutubeVideoEntity> newVideoList = new ArrayList<>();
            List<YoutubeVideoEntity> oldVideoList = new ArrayList<>();

            for (YoutubeVideoEntity video : videoList) {
                if (!isTypeVideoExists(repo, video)) {
                    newVideoList.add(video);
                } else {
                    YoutubeVideoEntity v = repo.getVideoByTypeIdAndVideoId(
                            video.getTypeId(), video.getVideoId());
                    YoutubeVideoEntity oldVideo = new YoutubeVideoEntity(
                            v.getId(), v.getTypeId(), v.getVideoId(),
                            video.getTitle(), // might have changed
                            video.getChannel(), // might have changed
                            video.getDate(), // might have changed
                            video.getDateMillis(), // might have changed
                            v.getDuration(),
                            video.getViews(), // might have changed
                            video.getThumbUrl()); // might have changed
                    oldVideoList.add(oldVideo);
                }
            }

            if (!newVideoList.isEmpty()) {
                long[] rowIds = repo.insertVideos(newVideoList);
                if (BuildConfig.DEBUG) {
                    Timber.d("%s new typeId-%s video(s) inserted! IDs= %s",
                            String.valueOf(rowIds.length), typeId, Arrays.toString(rowIds));
                }
            }

            if (!oldVideoList.isEmpty()) {
                int result = repo.updateVideos(oldVideoList);

                if (BuildConfig.DEBUG)
                    Timber.d("%s typeId-%s video(s) updated!", String.valueOf(result), typeId);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private List<YoutubeVideoEntity> getVideoDetails(OkHttpClient client, int typeId, String videoDetailUrl)
            throws IOException, JSONException {
        List<YoutubeVideoEntity> videoList = new ArrayList<>();

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

                    YoutubeVideoEntity video = new YoutubeVideoEntity(
                            typeId, videoId, title, channelTitle, publishedAt, dateMillis,
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
    private List<String> getYoutubeTypeVideoIds(Context context, OkHttpClient client,
                                                int typeId, String url, boolean isChannel)
            throws IOException, JSONException {
        List<String> videoIds = new ArrayList<>();

        Response response = getResponse(client, url);

        if (response.isSuccessful()) {
            JSONObject jsonObject = new JSONObject(response.body().string());

            String oldNextPageToken = PrefUtils.getYoutubeNextPageToken(context, typeId);
            if (!jsonObject.isNull(TAG_NEXTPAGETOKEN)) {
                String newNextPageToken = jsonObject.getString(TAG_NEXTPAGETOKEN).trim();
                if (!oldNextPageToken.equalsIgnoreCase(newNextPageToken))
                    PrefUtils.setYoutubeNextPageToken(context, typeId, newNextPageToken);
            } else PrefUtils.setYoutubeNextPageToken(context, typeId, "");

            JSONArray items = jsonObject.getJSONArray(TAG_ITEMS);
            for (int i = 0; i < items.length(); i++) {
                try {
                    JSONObject item = items.getJSONObject(i);
                    String videoId = item.getJSONObject(isChannel ?
                            TAG_ID : TAG_CONTENT_DETAILS).getString(TAG_VIDEO_ID);
                    videoIds.add(videoId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return videoIds;
    }

    private void fetchYouTubeTypeDetails(Context context, DataRepository repo, OkHttpClient client, String url) {
        client.newCall(getRequest(url)).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (BuildConfig.DEBUG) {
                    Timber.e("onFailure() error= %s, URL= %s", e.getMessage(), url);
                    e.printStackTrace();
                }
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    YoutubeTypeSyncWorker.this.onResponse(context, response, repo);
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
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void onResponse(Context context, Response response, DataRepository repo)
            throws IOException, JSONException {
        if (!response.isSuccessful()) return;

        JSONObject jObject = new JSONObject(response.body().string());
        JSONArray items = jObject.getJSONArray(TAG_ITEMS);

        List<YoutubeTypeEntity> types = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            try {
                JSONObject item = items.getJSONObject(i);
                JSONObject snippet = item.getJSONObject(TAG_SNIPPET);

                String typeId = item.getString(TAG_ID);

                String title = "";

                if (!snippet.isNull(TAG_TITLE)) {
                    title = snippet.getString(TAG_TITLE);
                }

                if (TextUtils.isEmpty(title)) {
                    try {
                        title = context.getString(android.R.string.untitled)
                                .replaceAll("<", "")
                                .replaceAll(">", "");
                    } catch (Exception e) {
                        title = TAG_UNTITLED;
                    }
                }

                boolean isChannel = true;
                if (!item.isNull(TAG_KIND)) {
                    String kind = item.getString(TAG_KIND).replace("youtube#", "");
                    if (kind.equalsIgnoreCase(TAG_CHANNEL)) {
                        isChannel = true;
                    } else if (kind.equalsIgnoreCase(TAG_PLAYLIST)) {
                        isChannel = false;
                    }
                }

                YoutubeTypeEntity t = repo.getYoutubeTypeByUniqueId(typeId);
                YoutubeTypeEntity newType =
                        new YoutubeTypeEntity(t.getId(), t.getUniqueId(),
                                title,
                                isChannel ? 1 : 0,
                                t.getOrderNum());
                types.add(newType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (types != null && !types.isEmpty()) {
            int result = repo.updateTypes(types);
            if (BuildConfig.DEBUG) Timber.d("%s Youtube type(s) updated!", String.valueOf(result));
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

    private boolean isTypeVideoExists(DataRepository repo, YoutubeVideoEntity video) {
        try {
            return repo.getNumVideosByType(video.getTypeId(), video.getVideoId()) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
