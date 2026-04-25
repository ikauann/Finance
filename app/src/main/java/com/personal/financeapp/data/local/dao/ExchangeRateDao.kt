package com.personal.financeapp.data.local.dao

import androidx.room.*
import com.personal.financeapp.data.local.entity.ExchangeRateEntity

@Dao
interface ExchangeRateDao {
    @Query("""
        SELECT * FROM exchange_rates 
        WHERE currency_pair = :pair 
        ORDER BY fetched_at DESC 
        LIMIT 1
    """)
    suspend fun getLatestRate(pair: String): ExchangeRateEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(rate: ExchangeRateEntity)
    
    @Query("DELETE FROM exchange_rates WHERE currency_pair = :pair AND id NOT IN (SELECT id FROM exchange_rates WHERE currency_pair = :pair ORDER BY fetched_at DESC LIMIT 10)")
    suspend fun cleanOldRates(pair: String)
}
