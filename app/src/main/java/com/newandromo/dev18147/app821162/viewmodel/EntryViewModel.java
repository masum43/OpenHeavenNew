package com.newandromo.dev18147.app821162.viewmodel;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.Collections;
import java.util.List;

import com.newandromo.dev18147.app821162.AppExecutors;
import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.EntryEntity;
import com.newandromo.dev18147.app821162.service.FeedSyncWorker;
import com.newandromo.dev18147.app821162.ui.activities.MainActivity;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.utils.Constants.Const.ALL_ENTRIES;

public class EntryViewModel extends AndroidViewModel {
    private static final int PAGE_SIZE = 20;
    private AppExecutors mAppExecutors;
    private WorkManager mWorkManager;
    private DataRepository mRepo;
    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<PagedList<EntryEntity>> mObservablePagedEntries;

    public EntryViewModel(@NonNull Application application) {
        super(application);
        mAppExecutors = new AppExecutors();
        mWorkManager = WorkManager.getInstance(application.getApplicationContext());

        mRepo = ((MyApplication) application).getRepository();

        mObservablePagedEntries = new MediatorLiveData<>();

        // set by default null, until we get data from the database.
        mObservablePagedEntries.setValue(null);
    }

    public LiveData<PagedList<EntryEntity>> getPagedEntries() {
        return mObservablePagedEntries;
    }

    public void loadPagedEntries(Context context,
                                 int position,
                                 int id,
                                 boolean isCategory,
                                 boolean isSearch,
                                 String searchQuery) {
        mAppExecutors.networkIO().execute(() -> {
            try {
                LiveData<PagedList<EntryEntity>> entries =
                        getPagedEntries(context, position, id, isCategory, isSearch, searchQuery);
                mObservablePagedEntries.addSource(entries, mObservablePagedEntries::postValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private LiveData<PagedList<EntryEntity>> getPagedEntries(
            Context context,
            int position,
            int id,
            boolean isCategory,
            boolean isSearch, String searchQuery) {
        DataSource.Factory<Integer, EntryEntity> factory;

        if (isSearch && !TextUtils.isEmpty(searchQuery)) {
            // searchQuery = String.format("%s OR *%s*", searchQuery, searchQuery);
            if (MainActivity.BOOKMARKS == position)
                factory = mRepo.getDatabase().entryDao().searchAllBookmarkedEntries(searchQuery);
            else factory = mRepo.getDatabase().entryDao().searchAllEntries(searchQuery);
        } else {

            if (ALL_ENTRIES.equals(PrefUtils.getViewMode(context))) { // todo ALL ENTRIES
                if (PrefUtils.isOldestFirst(context)) { // Oldest First
                    if (MainActivity.BOOKMARKS == position) {
                        factory = mRepo.getDatabase().entryDao().loadAllBookmarkedEntriesOldestFirst();
                    } else if (isCategory) {
                        factory = mRepo.getDatabase().entryDao()
                                .loadAllEntriesByCategoryOldestFirst(id);
                    } else {
                        factory = mRepo.getDatabase().entryDao().loadAllEntriesOldestFirst();
                    }
                } else { // Newest First
                    if (MainActivity.BOOKMARKS == position) {
                        factory = mRepo.getDatabase().entryDao().loadAllBookmarkedEntriesNewestFirst();
                    } else if (isCategory) {
                        factory = mRepo.getDatabase().entryDao()
                                .loadAllEntriesByCategoryNewestFirst(id);
                    } else {
                        factory = mRepo.getDatabase().entryDao().loadAllEntriesNewestFirst();
                    }
                }
            } else { // todo UNREAD ENTRIES
                if (PrefUtils.isOldestFirst(context)) { // Oldest First
                    if (isCategory) {
                        factory = mRepo.getDatabase().entryDao()
                                .loadUnreadEntriesByCategoryOldestFirst(id);
                    } else {
                        factory = mRepo.getDatabase().entryDao().loadUnreadEntriesOldestFirst();
                    }
                } else { // Newest First
                    if (isCategory) {
                        factory = mRepo.getDatabase().entryDao()
                                .loadUnreadEntriesByCategoryNewestFirst(id);
                    } else {
                        factory = mRepo.getDatabase().entryDao().loadUnreadEntriesNewestFirst();
                    }
                }
            }
        }
        return new LivePagedListBuilder<>(factory, PAGE_SIZE)
                .setFetchExecutor(mAppExecutors.networkIO())
                .build();
    }

    public void refresh(int id, boolean isCategory) {
        Context context = getApplication().getApplicationContext();
        FeedSyncWorker.refreshWork(context, id, isCategory);
    }

    public void refreshAll(int id, boolean isCategory) {
        Context context = getApplication().getApplicationContext();
        FeedSyncWorker.refreshAllWork(context, id, isCategory);
    }

    public void search(int id, String query) {
        Context context = getApplication().getApplicationContext();
        FeedSyncWorker.searchWork(context, id, query);
    }

    public LiveData<List<WorkInfo>> getWorkInfo(String tag) {
        return mWorkManager.getWorkInfosByTagLiveData(tag);
    }

    public void markEntryAsRead(int entryId) {
        mAppExecutors.networkIO().execute(() -> {
            try {
                String url = mRepo.getEntryUrlById(entryId);
                List<String> urls = Collections.singletonList(url);
                mRepo.updateEntriesUnreadByUrl(0, urls);
                mRepo.updateEntriesRecentReadByUrl(1, urls);
                if (BuildConfig.DEBUG) Timber.d("url: %s marked as read!", url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
