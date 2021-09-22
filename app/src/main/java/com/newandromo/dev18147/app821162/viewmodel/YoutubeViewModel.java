package com.newandromo.dev18147.app821162.viewmodel;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.List;

import com.newandromo.dev18147.app821162.AppExecutors;
import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.YoutubeSearchEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeTypeEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeVideoEntity;
import com.newandromo.dev18147.app821162.service.YoutubeSearchWorker;
import com.newandromo.dev18147.app821162.service.YoutubeTypeSyncWorker;
import com.newandromo.dev18147.app821162.utils.PrefUtils;

public class YoutubeViewModel extends AndroidViewModel {
    private static final int PAGE_SIZE = 20;
    private AppExecutors mExecutors;
    private WorkManager mWorkManager;
    private DataRepository mRepo;
    public String mQuery;
    private final MediatorLiveData<PagedList<YoutubeSearchEntity>> mObservableSearchedVideos;
    private final MediatorLiveData<PagedList<YoutubeVideoEntity>> mObservablePagedVideos;
    private final MediatorLiveData<List<YoutubeTypeEntity>> mObservableTypes;

    public YoutubeViewModel(@NonNull Application application) {
        super(application);
        mExecutors = new AppExecutors();
        mWorkManager = WorkManager.getInstance(application.getApplicationContext());
        mRepo = ((MyApplication) application).getRepository();

        mObservableSearchedVideos = new MediatorLiveData<>();
        mObservablePagedVideos = new MediatorLiveData<>();
        mObservableTypes = new MediatorLiveData<>();

        mObservableSearchedVideos.setValue(null);
        mObservablePagedVideos.setValue(null);
        mObservableTypes.setValue(null);
    }

    public LiveData<List<YoutubeTypeEntity>> getYoutubeTypes() {
        return mObservableTypes;
    }

    public void loadYoutubeTypes() {
        mExecutors.networkIO().execute(() -> {
            try {
                LiveData<List<YoutubeTypeEntity>> types = mRepo.loadYoutubeTypes();
                mObservableTypes.addSource(types, mObservableTypes::postValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LiveData<PagedList<YoutubeVideoEntity>> getPagedVideos() {
        return mObservablePagedVideos;
    }

    public void loadPagedVideos(int typeId, String uniqueId, boolean isChannel) {
        mExecutors.networkIO().execute(() -> {
            try {
                LiveData<PagedList<YoutubeVideoEntity>> videos =
                        isChannel ?
                                getPagedChannelVideos(typeId, uniqueId) :
                                getPagedPlaylistVideos(typeId, uniqueId);
                mObservablePagedVideos.addSource(videos, mObservablePagedVideos::postValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private LiveData<PagedList<YoutubeVideoEntity>> getPagedChannelVideos(
            int typeId, String uniqueId) {

        PagedList.Config.Builder builder = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(5)
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPageSize(PAGE_SIZE);

        return new LivePagedListBuilder<>(mRepo.getDatabase().youtubeVideoDao()
                .loadPagedChannelVideos(typeId), builder.build())
                .setFetchExecutor(mExecutors.networkIO())
                .setBoundaryCallback(new PagedList.BoundaryCallback<YoutubeVideoEntity>() {
                    @Override
                    public void onZeroItemsLoaded() {
                        refresh(typeId, uniqueId, true);
                    }

                    @Override
                    public void onItemAtEndLoaded(@NonNull YoutubeVideoEntity itemAtEnd) {
                        loadMore(typeId, uniqueId, true);
                    }
                }).build();
    }

    private LiveData<PagedList<YoutubeVideoEntity>> getPagedPlaylistVideos(
            int typeId, String uniqueId) {

        PagedList.Config.Builder builder = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(5)
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPageSize(PAGE_SIZE);

        return new LivePagedListBuilder<>(mRepo.getDatabase().youtubeVideoDao()
                .loadPagedPlaylistVideos(typeId), builder.build())
                .setFetchExecutor(mExecutors.networkIO())
                .setBoundaryCallback(new PagedList.BoundaryCallback<YoutubeVideoEntity>() {
                    @Override
                    public void onZeroItemsLoaded() {
                        refresh(typeId, uniqueId, false);
                    }

                    @Override
                    public void onItemAtEndLoaded(@NonNull YoutubeVideoEntity itemAtEnd) {
                        loadMore(typeId, uniqueId, false);
                    }
                }).build();
    }

    private void refresh(int typeId, String uniqueId, boolean isChannel) {
        Context context = getApplication().getApplicationContext();
        String nextPageToken = PrefUtils.getYoutubeNextPageToken(context, typeId);

        if (!PrefUtils.isYoutubeSearching(context, typeId)
                && TextUtils.isEmpty(nextPageToken)) {

            PrefUtils.setIsYoutubeSearching(context, typeId, true);
            YoutubeTypeSyncWorker.refreshWork(context, typeId, uniqueId, isChannel);
        }
    }

    private void loadMore(int typeId, String uniqueId, boolean isChannel) {
        Context context = getApplication().getApplicationContext();
        String nextPageToken = PrefUtils.getYoutubeNextPageToken(context, typeId);

        if (!PrefUtils.isYoutubeSearching(context, typeId)
                && !TextUtils.isEmpty(nextPageToken)) {

            PrefUtils.setIsYoutubeSearching(context, typeId, true);
            YoutubeTypeSyncWorker.loadMoreWork(context, typeId, uniqueId, isChannel);
        }
    }

    public void deleteAllVideosByTypeId(int typeId) {
        mExecutors.networkIO().execute(() -> {
            try {
                mRepo.deleteAllVideosByTypeId(typeId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void updateYoutubeTypeDetails() {
        YoutubeTypeSyncWorker.updateYoutubeTypeWork(getApplication().getApplicationContext());
    }

    public LiveData<PagedList<YoutubeSearchEntity>> getSearchedVideos() {
        return mObservableSearchedVideos;
    }

    public void loadSearchedVideos() {
        mExecutors.networkIO().execute(() -> {
            try {
                PagedList.Config.Builder builder = new PagedList.Config.Builder()
                        .setEnablePlaceholders(true)
                        .setPrefetchDistance(5)
                        .setInitialLoadSizeHint(PAGE_SIZE)
                        .setPageSize(PAGE_SIZE);

                LiveData<PagedList<YoutubeSearchEntity>> videos = new LivePagedListBuilder<>(mRepo.getDatabase().youtubeSearchDao()
                        .loadPagedVideos(), builder.build())
                        .setFetchExecutor(mExecutors.networkIO())
                        .setBoundaryCallback(new PagedList.BoundaryCallback<YoutubeSearchEntity>() {
                            @Override
                            public void onZeroItemsLoaded() {

                            }

                            @Override
                            public void onItemAtEndLoaded(@NonNull YoutubeSearchEntity itemAtEnd) {
                                loadMoreSearchResults(mQuery);
                            }
                        }).build();

                mObservableSearchedVideos.addSource(videos, mObservableSearchedVideos::postValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void searchVideos(String query) {
        Context context = getApplication().getApplicationContext();
        String nextPageToken = PrefUtils.getYoutubeNextPageToken(context);

        if (!PrefUtils.isYoutubeSearching(context) && TextUtils.isEmpty(nextPageToken)) {

            PrefUtils.setIsYoutubeSearching(context, true);
            YoutubeSearchWorker.searchWork(context, query);
        }
    }

    private void loadMoreSearchResults(String query) {
        Context context = getApplication().getApplicationContext();
        String nextPageToken = PrefUtils.getYoutubeNextPageToken(context);

        if (!PrefUtils.isYoutubeSearching(context) && !TextUtils.isEmpty(nextPageToken)) {

            PrefUtils.setIsYoutubeSearching(context, true);
            YoutubeSearchWorker.loadMoreWork(context, query);
        }
    }

    public void deleteSearchedVideos() {
        mExecutors.networkIO().execute(() -> {
            try {
                mRepo.deleteSearchedVideos();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LiveData<List<WorkInfo>> getSearchInfo(String tag) {
        return mWorkManager.getWorkInfosByTagLiveData(tag);
    }

    public LiveData<List<WorkInfo>> getLoadMoreInfo(String tag) {
        return mWorkManager.getWorkInfosByTagLiveData(tag);
    }
}
