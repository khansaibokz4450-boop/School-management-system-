package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FeeRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeeDao {
    @Query("SELECT * FROM fee_records ORDER BY id DESC")
    fun getAllFeeRecords(): Flow<List<FeeRecordEntity>>

    @Query("SELECT * FROM fee_records WHERE className = :className ORDER BY id DESC")
    fun getFeeRecordsByClass(className: String): Flow<List<FeeRecordEntity>>

    @Query("SELECT * FROM fee_records WHERE status = :status ORDER BY id DESC")
    fun getFeeRecordsByStatus(status: String): Flow<List<FeeRecordEntity>>

    @Query("SELECT * FROM fee_records WHERE studentId = :studentId ORDER BY id DESC")
    fun getFeeRecordsForStudent(studentId: Long): Flow<List<FeeRecordEntity>>

    @Query("SELECT COALESCE(SUM(netAmount), 0) FROM fee_records WHERE status = 'PAID'")
    fun getTotalCollectedAmount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeRecord(record: FeeRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeRecords(records: List<FeeRecordEntity>)

    @Update
    suspend fun updateFeeRecord(record: FeeRecordEntity)

    @Delete
    suspend fun deleteFeeRecord(record: FeeRecordEntity)
}
