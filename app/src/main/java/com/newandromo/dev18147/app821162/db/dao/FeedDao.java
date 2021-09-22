package com.newandromo.dev18147.app821162.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.newandromo.dev18147.app821162.db.entity.FeedEntity;

@Dao
public interface FeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FeedEntity> feeds);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateFeed(FeedEntity feed);

    @Query("SELECT * FROM feed WHERE id = :feedId")
    FeedEntity getFeedById(int feedId);

    @Query("SELECT * FROM feed WHERE category_id = :categoryId")
    List<FeedEntity> loadFeedsByCategory(int categoryId);

    @Query("SELECT * FROM feed")
    List<FeedEntity> loadAllFeeds();

    @Query("SELECT category_id "
            + "FROM feed "
            + "WHERE id = :feedId")
    int getCategoryIdById(int feedId);

    @Query("DELETE FROM feed")
    void deleteAllFeeds();
}
