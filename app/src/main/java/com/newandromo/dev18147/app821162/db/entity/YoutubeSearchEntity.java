package com.newandromo.dev18147.app821162.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "youtube_search")
public class YoutubeSearchEntity {
    @NonNull
    @PrimaryKey
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

    public YoutubeSearchEntity(@NonNull String videoId, String title, String channel, String date,
                               long dateMillis, String duration, String views, String thumbUrl) {
        this.videoId = videoId;
        this.title = title;
        this.channel = channel;
        this.date = date;
        this.dateMillis = dateMillis;
        this.duration = duration;
        this.views = views;
        this.thumbUrl = thumbUrl;
    }

    @NonNull
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(@NonNull String videoId) {
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
