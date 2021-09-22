package com.newandromo.dev18147.app821162.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "entries",
        foreignKeys = {
                @ForeignKey(entity = FeedEntity.class,
                        parentColumns = "id",
                        childColumns = "feed_id",
                        onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "feed_id")})
public class EntryEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "feed_id")
    private int feedId;
    private String title;
    private String author;
    private String date;
    @ColumnInfo(name = "date_millis")
    private long dateMillis;
    private String url;
    @ColumnInfo(name = "thumb_url")
    private String thumbUrl;
    private String content;
    private String excerpt;
    @ColumnInfo(name = "is_unread")
    private int isUnread = 1;
    @ColumnInfo(name = "is_recent_read")
    private int isRecentRead = 0;
    @ColumnInfo(name = "is_bookmarked", defaultValue = "0")
    private int isBookmarked = 0;

    @Ignore
    public EntryEntity(int feedId, String title, String author, String date, long dateMillis,
                       String url, String thumbUrl, String content, String excerpt,
                       int isUnread, int isRecentRead, int isBookmarked) {
        this.feedId = feedId;
        this.title = title;
        this.author = author;
        this.date = date;
        this.dateMillis = dateMillis;
        this.url = url;
        this.thumbUrl = thumbUrl;
        this.content = content;
        this.excerpt = excerpt;
        this.isUnread = isUnread;
        this.isRecentRead = isRecentRead;
        this.isBookmarked = isBookmarked;
    }

    public EntryEntity(int id, int feedId, String title, String author, String date, long dateMillis,
                       String url, String thumbUrl, String content, String excerpt,
                       int isUnread, int isRecentRead, int isBookmarked) {
        this.id = id;
        this.feedId = feedId;
        this.title = title;
        this.author = author;
        this.date = date;
        this.dateMillis = dateMillis;
        this.url = url;
        this.thumbUrl = thumbUrl;
        this.content = content;
        this.excerpt = excerpt;
        this.isUnread = isUnread;
        this.isRecentRead = isRecentRead;
        this.isBookmarked = isBookmarked;
    }

    public EntryEntity(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
        this.feedId = feedId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public int isUnread() {
        return isUnread;
    }

    public void setIsUnread(int unread) {
        this.isUnread = unread;
    }

    public int isRecentRead() {
        return isRecentRead;
    }

    public void setIsRecentRead(int isRecentRead) {
        this.isRecentRead = isRecentRead;
    }

    public int isBookmarked() {
        return isBookmarked;
    }

    public void setIsBookmarked(int bookmarked) {
        this.isBookmarked = bookmarked;
    }
}
