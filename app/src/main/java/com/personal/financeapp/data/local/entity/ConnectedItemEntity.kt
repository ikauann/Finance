package com.personal.financeapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "connected_items")
data class ConnectedItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "item_id")
    val itemId: String,

    @ColumnInfo(name = "connector_name")
    val connectorName: String,

    @ColumnInfo(name = "connector_id")
    val connectorId: Int,

    @ColumnInfo(name = "status")
    val status: String, // UPDATED, UPDATING, LOGIN_ERROR, etc.

    @ColumnInfo(name = "connected_at")
    val connectedAt: String, // ISO 8601

    @ColumnInfo(name = "last_sync_at")
    val lastSyncAt: String? = null
)
