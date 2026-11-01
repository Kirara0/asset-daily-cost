package com.assetcalc.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {

    @Query("SELECT * FROM asset_items ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<AssetItem>>

    @Insert
    suspend fun insert(item: AssetItem): Long

    @Delete
    suspend fun delete(item: AssetItem)

    @Query("DELETE FROM asset_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_items")
    suspend fun deleteAll()
}
