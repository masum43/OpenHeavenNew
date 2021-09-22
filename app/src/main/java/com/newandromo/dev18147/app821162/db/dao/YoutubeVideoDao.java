package com.newandromo.dev18147.app821162.db.dao;

import java.util.List;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Update;
import com.newandromo.dev18147.app821162.db.entity.YoutubeVideoEntity;

@Dao
public interface YoutubeVideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertVideos(List<YoutubeVideoEntity> videos);

    @Update
    int updateVideos(List<YoutubeVideoEntity> videos);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT youtube_video.* "
            + "FROM youtube_video "
            + "LEFT JOIN youtube_type ON (youtube_video.type_id = youtube_type.id) "
            + "WHERE (youtube_video.type_id = :typeId) "
            + "ORDER BY youtube_video.date_millis DESC")
    DataSource.Factory<Integer, YoutubeVideoEntity> loadPagedChannelVideos(int typeId);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT youtube_video.* "
            + "FROM youtube_video "
            + "LEFT JOIN youtube_type ON (youtube_video.type_id = youtube_type.id) "
            + "WHERE (youtube_video.type_id = :typeId) "
            + "ORDER BY youtube_video.id ASC")
    DataSource.Factory<Integer, YoutubeVideoEntity> loadPagedPlaylistVideos(int typeId);

    @Query("SELECT COUNT(*) "
            + "FROM youtube_video "
            + "WHERE type_id = :typeId AND video_id = :videoId")
    int getNumVideosByType(int typeId, String videoId);

    // @SkipQueryVerification
    @Query("SELECT * "
            + "FROM youtube_video "
            + "WHERE type_id = :typeId AND video_id = :videoId "
            + "LIMIT 1")
    YoutubeVideoEntity getVideoByTypeIdAndVideoId(int typeId, String videoId);

    @Query("DELETE FROM youtube_video "
            + "WHERE type_id = :typId")
    void deleteAllVideosByTypeId(int typId);

    @Query("DELETE FROM youtube_video")
    void deleteAllYoutubeVideos();
}
