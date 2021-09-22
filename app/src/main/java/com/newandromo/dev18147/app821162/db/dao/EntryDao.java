package com.newandromo.dev18147.app821162.db.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.RoomWarnings;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

import com.newandromo.dev18147.app821162.db.entity.EntryEntity;

@Dao
public interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertEntries(List<EntryEntity> entries);

    @Update
    int updateEntries(List<EntryEntity> entries);

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, feed_id, title, date, date_millis, url, thumb_url, excerpt, " +
            "is_unread, is_recent_read, is_bookmarked "
            + "FROM entries "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id")
    DataSource.Factory<Integer, EntryEntity> loadAllEntriesNewestFirst();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id "
            + "FROM entries "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id")
    int[] getEntryIdsNewestFirst();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, feed_id, title, date, date_millis, url, thumb_url, excerpt, " +
            "is_unread, is_recent_read, is_bookmarked "
            + "FROM entries "
            + "WHERE feed_id IN (SELECT id FROM feed WHERE category_id = :categoryId) "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id")
    DataSource.Factory<Integer, EntryEntity> loadAllEntriesByCategoryNewestFirst(int categoryId);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id "
            + "FROM entries "
            + "WHERE feed_id IN (SELECT id FROM feed WHERE category_id = :categoryId) "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id")
    int[] getEntryIdsByCategoryNewestFirst(int categoryId);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, feed_id, title, date, date_millis, url, thumb_url, excerpt, " +
            "is_unread, is_recent_read, is_bookmarked "
            + "FROM entries "
            + "WHERE is_bookmarked = 1 "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id")
    DataSource.Factory<Integer, EntryEntity> loadAllBookmarkedEntriesNewestFirst();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id "
            + "FROM entries "
            + "WHERE is_bookmarked = 1 "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id")
    int[] getEntryIdsBookmarkedNewestFirst();

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, feed_id, title, date, date_millis, url, thumb_url, excerpt, " +
            "is_unread, is_recent_read, is_bookmarked "
            + "FROM entries "
            + "GROUP BY url "
            + "ORDER BY date_millis, id DESC")
    DataSource.Factory<Integer, EntryEntity> loadAllEntriesOldestFirst();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id "
            + "FROM entries "
            + "GROUP BY url "
            + "ORDER BY date_millis, id DESC")
    int[] getEntryIdsOldestFirst();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, feed_id, title, date, date_millis, url, thumb_url, excerpt, " +
            "is_unread, is_recent_read, is_bookmarked "
            + "FROM entries "
            + "WHERE feed_id IN (SELECT id FROM feed WHERE category_id = :categoryId) "
            + "GROUP BY url "
            + "ORDER BY date_millis, id DESC")
    DataSource.Factory<Integer, EntryEntity> loadAllEntriesByCategoryOldestFirst(int categoryId);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id "
            + "FROM entries "
            + "WHERE feed_id IN (SELECT id FROM feed WHERE category_id = :categoryId) "
            + "GROUP BY url "
            + "ORDER BY date_millis, id DESC")
    int[] getEntryIdsByCategoryOldestFirst(int categoryId);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, feed_id, title, date, date_millis, url, thumb_url, excerpt, " +
            "is_unread, is_recent_read, is_bookmarked "
            + "FROM entries "
            + "WHERE is_bookmarked = 1 "
            + "GROUP BY url "
            + "ORDER BY date_millis, id DESC")
    DataSource.Factory<Integer, EntryEntity> loadAllBookmarkedEntriesOldestFirst();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id "
            + "FROM entries "
            + "WHERE is_bookmarked = 1 "
            + "GROUP BY url "
            + "ORDER BY date_millis, id DESC")
    int[] getEntryIdsBookmarkedOldestFirst();

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, feed_id, title, date, date_millis, url, thumb_url, excerpt, " +
            "is_unread, is_recent_read, is_bookmarked "
            + "FROM entries "
            + "WHERE is_unread = 1 OR is_recent_read = 1 "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id")
    DataSource.Factory<Integer, EntryEntity> loadUnreadEntriesNewestFirst();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id "
            + "FROM entries "
            + "WHERE is_unread = 1 OR is_recent_read = 1 "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id")
    int[] getUnreadEntryIdsNewestFirst();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, feed_id, title, date, date_millis, url, thumb_url, excerpt, " +
            "is_unread, is_recent_read, is_bookmarked "
            + "FROM entries "
            + "WHERE (is_unread = 1 OR is_recent_read = 1) "
            + "AND feed_id IN (SELECT id FROM feed WHERE category_id = :categoryId) "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id")
    DataSource.Factory<Integer, EntryEntity> loadUnreadEntriesByCategoryNewestFirst(int categoryId);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id "
            + "FROM entries "
            + "WHERE (is_unread = 1 OR is_recent_read = 1) "
            + "AND feed_id IN (SELECT id FROM feed WHERE category_id = :categoryId) "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id")
    int[] getUnreadEntryIdsByCategoryNewestFirst(int categoryId);

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, feed_id, title, date, date_millis, url, thumb_url, excerpt, " +
            "is_unread, is_recent_read, is_bookmarked "
            + "FROM entries "
            + "WHERE is_unread = 1 OR is_recent_read = 1 "
            + "GROUP BY url "
            + "ORDER BY date_millis, id DESC")
    DataSource.Factory<Integer, EntryEntity> loadUnreadEntriesOldestFirst();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id "
            + "FROM entries "
            + "WHERE is_unread = 1 OR is_recent_read = 1 "
            + "GROUP BY url "
            + "ORDER BY date_millis, id DESC")
    int[] getUnreadEntryIdsOldestFirst();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, feed_id, title, date, date_millis, url, thumb_url, excerpt, " +
            "is_unread, is_recent_read, is_bookmarked "
            + "FROM entries "
            + "WHERE (is_unread = 1 OR is_recent_read = 1) "
            + "AND feed_id IN (SELECT id FROM feed WHERE category_id = :categoryId) "
            + "GROUP BY url "
            + "ORDER BY date_millis, id DESC")
    DataSource.Factory<Integer, EntryEntity> loadUnreadEntriesByCategoryOldestFirst(int categoryId);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id "
            + "FROM entries "
            + "WHERE (is_unread = 1 OR is_recent_read = 1) "
            + "AND feed_id IN (SELECT id FROM feed WHERE category_id = :categoryId) "
            + "GROUP BY url "
            + "ORDER BY date_millis, id DESC")
    int[] getUnreadEntryIdsByCategoryOldestFirst(int categoryId);

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT entries.id, entries.feed_id, entries.title, entries.date, entries.date_millis, " +
            "entries.url, entries.thumb_url, entries.excerpt, entries.is_unread, entries.is_recent_read, " +
            "entries.is_bookmarked "
            + "FROM entries "
            + "JOIN entries_fts ON (entries.id = entries_fts.docid) "
            + "WHERE entries_fts MATCH :search "
            + "GROUP BY entries.url "
            + "ORDER BY entries.date_millis DESC, entries.id")
    DataSource.Factory<Integer, EntryEntity> searchAllEntries(String search);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT entries.id "
            + "FROM entries "
            + "JOIN entries_fts ON (entries.id = entries_fts.docid) "
            + "WHERE entries_fts MATCH :search "
            + "GROUP BY entries.url "
            + "ORDER BY entries.date_millis DESC, entries.id")
    int[] getSearchedEntryIds(String search);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT entries.id, entries.feed_id, entries.title, entries.date, entries.date_millis, " +
            "entries.url, entries.thumb_url, entries.excerpt, entries.is_unread, entries.is_recent_read, " +
            "entries.is_bookmarked "
            + "FROM entries "
            + "JOIN entries_fts ON (entries.id = entries_fts.docid) "
            + "WHERE entries_fts MATCH :search "
            + "AND (entries.is_bookmarked = 1) "
            + "GROUP BY entries.url "
            + "ORDER BY entries.date_millis DESC, entries.id")
    DataSource.Factory<Integer, EntryEntity> searchAllBookmarkedEntries(String search);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT entries.id "
            + "FROM entries "
            + "JOIN entries_fts ON (entries.id = entries_fts.docid) "
            + "WHERE entries_fts MATCH :search "
            + "AND (entries.is_bookmarked = 1) "
            + "GROUP BY entries.url "
            + "ORDER BY entries.date_millis DESC, entries.id ")
    int[] getSearchedBookmarkedEntryIds(String search);

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, feed_id, title, date, date_millis, url, thumb_url, is_unread, " +
            "is_recent_read, is_bookmarked "
            + "FROM entries "
            + "WHERE feed_id IN (SELECT id FROM feed WHERE category_id = :categoryId) "
            + "AND url NOT IN (:url) "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id "
            + "LIMIT :limit")
    List<EntryEntity> getEntriesByCategoryFilterUrl(int categoryId, String url, int limit);

    @Query("SELECT * "
            + "FROM entries "
            + "WHERE id = :entryId")
    EntryEntity loadEntryById(int entryId);

    @Query("SELECT * "
            + "FROM entries "
            + "WHERE id IN (:ids) "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id")
    List<EntryEntity> getEntriesByIds(List<Long> ids);

    @Query("SELECT COUNT(*) "
            + "FROM entries "
            + "WHERE feed_id = :feedId AND url = :url")
    int getNumEntriesByFeed(int feedId, String url);

    @Query("SELECT COUNT(*) "
            + "FROM entries "
            + "WHERE feed_id = :feedId AND title = :title AND date_millis = :dateMillis")
    int getNumEntriesByFeed(int feedId, String title, long dateMillis);

    @Query("SELECT id "
            + "FROM entries "
            + "GROUP BY url "
            + "ORDER BY date_millis DESC, id")
    List<Integer> loadEntriesIds();

    @Query("SELECT * "
            + "FROM entries "
            + "WHERE feed_id = :feedId AND url = :url "
            + "LIMIT 1")
    EntryEntity getEntryByFeedIdAndUrl(int feedId, String url);

    @Query("SELECT * "
            + "FROM entries "
            + "WHERE url = :url "
            + "LIMIT 1")
    EntryEntity getEntryByUrl(String url);

    @Query("SELECT id "
            + "FROM entries "
            + "WHERE url = :url "
            + "LIMIT 1")
    int getEntryIdByUrl(String url);

    @Query("SELECT feed_id "
            + "FROM entries "
            + "WHERE url = :url")
    int getFeedIdByUrl(String url);


    @Query("SELECT url "
            + "FROM entries "
            + "WHERE id = :entryId")
    String getEntryUrlById(int entryId);

    @Query("SELECT url "
            + "FROM entries "
            + "WHERE is_unread = 0")
    List<String> loadReadEntriesUrls();

    @Query("SELECT url "
            + "FROM entries "
            + "WHERE is_bookmarked = 1")
    List<String> loadBookmarkedEntriesUrls();

    @Query("SELECT COUNT(DISTINCT url) "
            + "FROM entries "
            + "WHERE is_unread = 1")
    LiveData<Integer> getUnreadEntriesCount();

    @Query("SELECT SUM(is_unread) FROM entries")
    int loadUnreadEntriesCount();

    @Query("SELECT SUM(is_unread) "
            + "FROM entries "
            + "WHERE feed_id IN "
            + "(SELECT id FROM feed WHERE category_id = :categoryId)")
    int loadUnreadEntriesCount(int categoryId);

    @Query("SELECT COUNT(DISTINCT url) "
            + "FROM entries "
            + "WHERE is_unread = 1 AND is_bookmarked = 1")
    LiveData<Integer> getUnreadBookmarkedEntriesCount();

    @Query("UPDATE entries "
            + "SET is_unread = :isUnread "
            + "WHERE url IN (:urls)")
    void updateEntriesUnreadByUrl(int isUnread, List<String> urls);

    @Query("UPDATE entries "
            + "SET is_recent_read = :isRecentRead "
            + "WHERE url IN (:urls)")
    void updateEntriesRecentReadByUrl(int isRecentRead, List<String> urls);

    @Query("UPDATE entries "
            + "SET is_bookmarked = :isBookmarked "
            + "WHERE url IN (:urls)")
    void updateEntriesBookmarkByUrl(int isBookmarked, List<String> urls);

    @Query("UPDATE entries "
            + "SET is_recent_read = 0 "
            + "WHERE is_recent_read = 1")
    void resetAllRecentRead();

    @RawQuery
    int markEntriesAsRead(SupportSQLiteQuery query);

    @Query("DELETE FROM entries WHERE id IN (:ids)")
    void deleteEntriesByIds(List<Integer> ids);
}
