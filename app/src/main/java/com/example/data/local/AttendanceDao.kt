package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecordEntity>>

    @Query("""
        SELECT a.* FROM attendance_records a
        INNER JOIN students s ON a.studentId = s.id
        WHERE s.className = :className AND a.date = :date
    """)
    fun getAttendanceForClassAndDate(className: String, date: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId AND date = :date LIMIT 1")
    suspend fun getAttendanceForStudentAndDate(studentId: Long, date: String): AttendanceRecordEntity?

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceHistoryForStudent(studentId: Long): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(record: AttendanceRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(records: List<AttendanceRecordEntity>)

    @Update
    suspend fun updateAttendance(record: AttendanceRecordEntity)

    @Delete
    suspend fun deleteAttendance(record: AttendanceRecordEntity)
}
