package com.newandromo.dev18147.app821162.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "youtube_type",
        indices = {@Index(value = "unique_id", unique = true)})
public class YoutubeTypeEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "unique_id")
    private String uniqueId;
    private String title;
    @ColumnInfo(name = "is_channel")
    private int isChannel;
    @ColumnInfo(name = "order_num")
    private int orderNum;

    @Ignore
    public YoutubeTypeEntity(int id, String uniqueId, String title, int isChannel, int orderNum) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.title = title;
        this.isChannel = isChannel;
        this.orderNum = orderNum;
    }

    public YoutubeTypeEntity(String uniqueId, String title, int isChannel, int orderNum) {
        this.uniqueId = uniqueId;
        this.title = title;
        this.isChannel = isChannel;
        this.orderNum = orderNum;
    }

    public YoutubeTypeEntity(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int isChannel() {
        return isChannel;
    }

    public void setIsChannel(int channel) {
        isChannel = channel;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }
}
