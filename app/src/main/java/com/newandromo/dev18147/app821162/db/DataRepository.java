package com.newandromo.dev18147.app821162.db;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

import com.newandromo.dev18147.app821162.db.entity.CategoryEntity;
import com.newandromo.dev18147.app821162.db.entity.EntryEntity;
import com.newandromo.dev18147.app821162.db.entity.FeedEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeSearchEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeTypeEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeVideoEntity;

public class DataRepository {
    private static DataRepository mInstance;
    private final AppDatabase mDatabase;

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
        /*mObservableEntries = new MediatorLiveData<>();

        mObservableEntries.addSource(mDatabase.entryDao().loadAllEntries(), entryEntities -> {
            if (mDatabase.getDatabaseCreated().getValue() != null) {
                mObservableEntries.postValue(entryEntities);
            }
        });*/
    }

    public static DataRepository getInstance(final AppDatabase database) {
        if (mInstance == null) {
            synchronized (DataRepository.class) {
                if (mInstance == null) {
                    mInstance = new DataRepository(database);
                }
            }
        }
        return mInstance;
    }

    public AppDatabase getDatabase() {
        return mDatabase;
    }

    //-- TODO Categories --// ----------------------------------------------------------------------

    public CategoryEntity getCategoryById(int categoryId) {
        return mDatabase.categoryDao().getCategoryById(categoryId);
    }

    public List<CategoryEntity> getAllCategories() {
        return mDatabase.categoryDao().getAllCategories();
    }

    public void deleteAllCategories() {
        mDatabase.categoryDao().deleteAllCategories();
    }

    //-- TODO Feeds --// ---------------------------------------------------------------------------

    public void insertFeeds(List<FeedEntity> feeds) {
        mDatabase.feedDao().insertAll(feeds);
    }

    public List<FeedEntity> getAllFeeds() {
        return mDatabase.feedDao().loadAllFeeds();
    }

    public List<FeedEntity> getFeedsByCategory(int categoryId) {
        return mDatabase.feedDao().loadFeedsByCategory(categoryId);
    }

    public void deleteAllFeeds() {
        mDatabase.feedDao().deleteAllFeeds();
    }

    //-- TODO Entries --// -------------------------------------------------------------------------

    public List<Long> insertEntries(List<EntryEntity> entries) {
        return mDatabase.entryDao().insertEntries(entries);
    }

    public int updateEntries(List<EntryEntity> entries) {
        return mDatabase.entryDao().updateEntries(entries);
    }

    public EntryEntity loadEntryById(int entryId) {
        return mDatabase.entryDao().loadEntryById(entryId);
    }

    public List<Integer> getEntriesIds() {
        return mDatabase.entryDao().loadEntriesIds();
    }

    public EntryEntity getEntryByFeedIdAndUrl(int feedId, String url) {
        return mDatabase.entryDao().getEntryByFeedIdAndUrl(feedId, url);
    }

    public EntryEntity getEntryByUrl(String url) {
        return mDatabase.entryDao().getEntryByUrl(url);
    }

    public String getEntryUrlById(int entryId) {
        return mDatabase.entryDao().getEntryUrlById(entryId);
    }

    public List<String> getReadEntriesUrls() {
        return mDatabase.entryDao().loadReadEntriesUrls();
    }

    public List<String> getBookmarkedEntriesUrls() {
        return mDatabase.entryDao().loadBookmarkedEntriesUrls();
    }

    public LiveData<Integer> getUnreadEntriesCount() {
        return mDatabase.entryDao().getUnreadEntriesCount();
    }

    public int loadUnreadEntriesCount() {
        return mDatabase.entryDao().loadUnreadEntriesCount();
    }

    public int loadUnreadEntriesCount(int categoryId) {
        return mDatabase.entryDao().loadUnreadEntriesCount(categoryId);
    }

    public LiveData<Integer> getUnreadBookmarkedEntriesCount() {
        return mDatabase.entryDao().getUnreadBookmarkedEntriesCount();
    }

    public int getNumEntriesByFeed(int feedId, String url) {
        return mDatabase.entryDao().getNumEntriesByFeed(feedId, url);
    }

    public int getNumEntriesByFeed(int feedId, String title, long dateMillis) {
        return mDatabase.entryDao().getNumEntriesByFeed(feedId, title, dateMillis);
    }

    public void updateEntriesUnreadByUrl(int isUnread, List<String> urls) {
        mDatabase.entryDao().updateEntriesUnreadByUrl(isUnread, urls);
    }

    public void updateEntriesRecentReadByUrl(int isRecentRead, List<String> urls) {
        mDatabase.entryDao().updateEntriesRecentReadByUrl(isRecentRead, urls);
    }

    public void updateEntriesBookmarkByUrl(int isBookmarked, List<String> urls) {
        mDatabase.entryDao().updateEntriesBookmarkByUrl(isBookmarked, urls);
    }

    public void resetAllRecentRead() {
        mDatabase.entryDao().resetAllRecentRead();
    }

    public int markEntriesAsRead(SupportSQLiteQuery query) {
        return mDatabase.entryDao().markEntriesAsRead(query);
    }

    public void deleteEntriesByIds(List<Integer> ids) {
        mDatabase.entryDao().deleteEntriesByIds(ids);
    }


    //-- TODO YouTube --// -------------------------------------------------------------------------

    public void insertYoutubeTypes(List<YoutubeTypeEntity> types) {
        mDatabase.youtubeTypeDao().insertTypes(types);
    }

    public int updateTypes(List<YoutubeTypeEntity> types) {
        return mDatabase.youtubeTypeDao().updateTypes(types);
    }

    public List<YoutubeTypeEntity> getAllYoutubeTypes() {
        return mDatabase.youtubeTypeDao().getAllYoutubeTypes();
    }

    public LiveData<List<YoutubeTypeEntity>> loadYoutubeTypes() {
        return mDatabase.youtubeTypeDao().loadYoutubeTypes();
    }

    public YoutubeTypeEntity getYoutubeTypeByUniqueId(String uniqueId) {
        return mDatabase.youtubeTypeDao().getYoutubeTypeByUniqueId(uniqueId);
    }

    public long[] insertVideos(List<YoutubeVideoEntity> videos) {
        return mDatabase.youtubeVideoDao().insertVideos(videos);
    }

    public int updateVideos(List<YoutubeVideoEntity> videos) {
        return mDatabase.youtubeVideoDao().updateVideos(videos);
    }

    public int getNumVideosByType(int typeId, String videoId) {
        return mDatabase.youtubeVideoDao().getNumVideosByType(typeId, videoId);
    }

    public YoutubeVideoEntity getVideoByTypeIdAndVideoId(int typeId, String videoId) {
        return mDatabase.youtubeVideoDao().getVideoByTypeIdAndVideoId(typeId, videoId);
    }

    public void deleteAllVideosByTypeId(int typId) {
        mDatabase.youtubeVideoDao().deleteAllVideosByTypeId(typId);
    }

    public void deleteAllYoutubeVideos() {
        mDatabase.youtubeVideoDao().deleteAllYoutubeVideos();
    }

    public void deleteAllYoutubeTypes() {
        mDatabase.youtubeTypeDao().deleteAllYoutubeTypes();
    }

    public void insertSearchedVideos(List<YoutubeSearchEntity> videos) {
        mDatabase.youtubeSearchDao().insertVideos(videos);
    }

    public void deleteSearchedVideos() {
        mDatabase.youtubeSearchDao().deleteSearchedVideos();
    }
}
