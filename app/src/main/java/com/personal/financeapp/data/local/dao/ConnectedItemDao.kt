package com.personal.financeapp.data.local.dao

import androidx.room.*
import com.personal.financeapp.data.local.entity.ConnectedItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectedItemDao {

    @Query("SELECT * FROM connected_items ORDER BY connected_at DESC")
    fun getAllItemsFlow(): Flow<List<ConnectedItemEntity>>

    @Query("SELECT * FROM connected_items ORDER BY connected_at DESC")
    suspend fun getAllItems(): List<ConnectedItemEntity>

    @Query("SELECT * FROM connected_items WHERE item_id = :itemId")
    suspend fun getItemById(itemId: String): ConnectedItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItem(item: ConnectedItemEntity)

    @Query("DELETE FROM connected_items WHERE item_id = :itemId")
    suspend fun deleteItem(itemId: String)

    @Query("UPDATE connected_items SET status = :status, last_sync_at = :lastSyncAt WHERE item_id = :itemId")
    suspend fun updateSyncStatus(itemId: String, status: String, lastSyncAt: String)
}
