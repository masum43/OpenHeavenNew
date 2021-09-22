package com.newandromo.dev18147.app821162.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "feed",
        foreignKeys = {
                @ForeignKey(entity = CategoryEntity.class,
                        parentColumns = "id",
                        childColumns = "category_id")},
        indices = {@Index(value = "category_id"),
                @Index(value = "feed_url", unique = true)})
public class FeedEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "category_id")
    private int categoryId;
    private String title;
    @ColumnInfo(name = "feed_url")
    private String feedUrl;
    @ColumnInfo(name = "site_url")
    private String siteUrl;
    @ColumnInfo(name = "google_blog_id")
    private String googleBlogId;
    @ColumnInfo(name = "is_json")
    private int isJson = 0;
    @ColumnInfo(name = "is_google_json")
    private int isGoogleJson = 1;
    @ColumnInfo(name = "is_wordpress_json")
    private int isWordPressJson = 0;


    public FeedEntity(int id, int categoryId, String title, String feedUrl, String siteUrl,
                      String googleBlogId, int isJson, int isGoogleJson, int isWordPressJson) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.feedUrl = feedUrl;
        this.siteUrl = siteUrl;
        this.googleBlogId = googleBlogId;
        this.isJson = isJson;
        this.isGoogleJson = isGoogleJson;
        this.isWordPressJson = isWordPressJson;
    }
    @Ignore
    public FeedEntity(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getGoogleBlogId() {
        return googleBlogId;
    }

    public void setGoogleBlogId(String googleBlogId) {
        this.googleBlogId = googleBlogId;
    }

    public int getIsJson() {
        return isJson;
    }

    public void setIsJson(int isJson) {
        this.isJson = isJson;
    }

    public int getIsGoogleJson() {
        return isGoogleJson;
    }

    public void setIsGoogleJson(int isGoogleJson) {
        this.isGoogleJson = isGoogleJson;
    }

    public int getIsWordPressJson() {
        return isWordPressJson;
    }

    public void setIsWordPressJson(int isWordPressJson) {
        this.isWordPressJson = isWordPressJson;
    }
}
