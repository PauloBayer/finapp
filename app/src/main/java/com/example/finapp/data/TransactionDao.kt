package com.example.finapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(tx: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :t ORDER BY id DESC")
    fun getByType(t: TransactionType): Flow<List<TransactionEntity>>

    @Query("SELECT COALESCE(SUM(CASE WHEN type='CREDIT' THEN value ELSE -value END),0) FROM transactions")
    fun balance(): Flow<Double>

}