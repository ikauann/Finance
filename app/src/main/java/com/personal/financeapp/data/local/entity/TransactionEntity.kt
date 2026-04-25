package com.personal.financeapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val date: String, // ISO 8601: "2026-04-25"
    
    val amount: Double,
    
    val type: String, // "income" | "expense"
    
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    
    @ColumnInfo(name = "subcategory_id")
    val subcategoryId: Long? = null,
    
    val description: String? = null,
    
    val tags: String? = null, // JSON array: ["#jac_j2", "#gasolina"]
    
    @ColumnInfo(name = "receipt_image_path")
    val receiptImagePath: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String = Instant.DISTANT_PAST.toString()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val icon: String,
    val color: String
)

@Entity(
    tableName = "subcategories",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SubcategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    
    val name: String
)

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "currency_pair")
    val currencyPair: String,
    
    val rate: Double,
    
    @ColumnInfo(name = "fetched_at")
    val fetchedAt: String
)

@Entity(tableName = "vehicle_alerts")
data class VehicleAlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "alert_type")
    val alertType: String,
    
    @ColumnInfo(name = "last_km")
    val lastKm: Int? = null,
    
    @ColumnInfo(name = "last_date")
    val lastDate: String? = null,
    
    @ColumnInfo(name = "next_km")
    val nextKm: Int? = null,
    
    @ColumnInfo(name = "next_date")
    val nextDate: String? = null,
    
    @ColumnInfo(name = "is_dismissed")
    val isDismissed: Boolean = false
)
