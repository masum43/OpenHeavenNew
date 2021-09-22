package com.newandromo.dev18147.app821162.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "category",
        indices = {@Index(value = "title", unique = true)})
public class CategoryEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;


    public CategoryEntity(int id, String title) {
        this.id = id;
        this.title = title;
    }
    @Ignore
    public CategoryEntity(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
