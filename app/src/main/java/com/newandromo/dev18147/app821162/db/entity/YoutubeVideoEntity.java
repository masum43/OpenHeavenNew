package com.newandromo.dev18147.app821162.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "youtube_video",
        foreignKeys = {
                @ForeignKey(entity = YoutubeTypeEntity.class,
                        parentColumns = "id",
                        childColumns = "type_id",
                        onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "type_id")})
public class YoutubeVideoEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "type_id")
    private int typeId;
    @ColumnInfo(name = "video_id")
    private String videoId;
    private String title;
    private String channel;
    private String date;
    @ColumnInfo(name = "date_millis")
    private long dateMillis;
    private String duration;
    private String views;
    @ColumnInfo(name = "thumb_url")
    private String thumbUrl;

    @Ignore
    public YoutubeVideoEntity(int id, int typeId, String videoId, String title,
                              String channel, String date, long dateMillis, String duration,
                              String views, String thumbUrl) {
        this.id = id;
        this.typeId = typeId;
        this.videoId = videoId;
        this.title = title;
        this.channel = channel;
        this.date = date;
        this.dateMillis = dateMillis;
        this.duration = duration;
        this.views = views;
        this.thumbUrl = thumbUrl;
    }

    public YoutubeVideoEntity(int typeId, String videoId, String title, String channel, String date,
                              long dateMillis, String duration, String views, String thumbUrl) {
        this.typeId = typeId;
        this.videoId = videoId;
        this.title = title;
        this.channel = channel;
        this.date = date;
        this.dateMillis = dateMillis;
        this.duration = duration;
        this.views = views;
        this.thumbUrl = thumbUrl;
    }

    public YoutubeVideoEntity(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int type_id) {
        this.typeId = type_id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public void setDateMillis(long dateMillis) {
        this.dateMillis = dateMillis;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
}
