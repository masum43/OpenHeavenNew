package com.newandromo.dev18147.app821162.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.newandromo.dev18147.app821162.db.entity.YoutubeTypeEntity;

@Dao
public interface YoutubeTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertTypes(List<YoutubeTypeEntity> types);

    @Update
    int updateTypes(List<YoutubeTypeEntity> types);

    @Query("SELECT * "
            + "FROM youtube_type "
            + "ORDER BY order_num")
    LiveData<List<YoutubeTypeEntity>> loadYoutubeTypes();

    @Query("SELECT * "
            + "FROM youtube_type "
            + "ORDER BY order_num")
    List<YoutubeTypeEntity> getAllYoutubeTypes();

    @Query("SELECT * "
            + "FROM youtube_type "
            + "WHERE unique_id = :uniqueId")
    YoutubeTypeEntity getYoutubeTypeByUniqueId(String uniqueId);

    @Query("UPDATE youtube_type "
            + "SET order_num = :orderNum "
            + "WHERE unique_id = :uniqueId")
    void updateYoutubeTypeOrder(int orderNum, String uniqueId);

    @Query("DELETE FROM youtube_type WHERE unique_id = :uniqueId")
    void deleteYoutubeTypeByUniqueId(String uniqueId);

    @Query("DELETE FROM youtube_type")
    void deleteAllYoutubeTypes();
}
