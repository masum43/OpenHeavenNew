package com.newandromo.dev18147.app821162.db.dao;

import java.util.List;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.newandromo.dev18147.app821162.db.entity.YoutubeSearchEntity;

@Dao
public interface YoutubeSearchDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertVideos(List<YoutubeSearchEntity> videos);

    @Query("SELECT * FROM youtube_search")
    DataSource.Factory<Integer, YoutubeSearchEntity> loadPagedVideos();

    @Query("DELETE FROM youtube_search")
    void deleteSearchedVideos();
}
