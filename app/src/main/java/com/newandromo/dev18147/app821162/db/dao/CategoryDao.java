package com.newandromo.dev18147.app821162.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import com.newandromo.dev18147.app821162.db.entity.CategoryEntity;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryEntity> categories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCategory(CategoryEntity category);

    @Query("SELECT * FROM category WHERE id = :categoryId")
    CategoryEntity getCategoryById(int categoryId);

    @Query("SELECT * FROM category")
    List<CategoryEntity> getAllCategories();

    @Query("DELETE FROM category")
    void deleteAllCategories();
}
