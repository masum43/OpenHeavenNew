package com.newandromo.dev18147.app821162.db.entity;

import androidx.room.Entity;
import androidx.room.Fts4;

@Fts4(contentEntity = EntryEntity.class)
@Entity(tableName = "entries_fts")
public class EntryEntityFts {
    public String title;
    public String content;
}
