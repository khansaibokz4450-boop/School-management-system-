package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.BroadcastAlertEntity
import com.example.data.model.FeeRecordEntity
import com.example.data.model.StaffMemberEntity
import com.example.data.model.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    // --- Students ---
    @Query("SELECT * FROM students ORDER BY rollNo ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE className = :className ORDER BY rollNo ASC")
    fun getStudentsByClass(className: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Long): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    // --- Attendance ---
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(record: AttendanceRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(records: List<AttendanceRecordEntity>)

    // --- Fees ---
    @Query("SELECT * FROM fee_records ORDER BY id DESC")
    fun getAllFeeRecords(): Flow<List<FeeRecordEntity>>

    @Query("SELECT * FROM fee_records WHERE className = :className ORDER BY id DESC")
    fun getFeeRecordsByClass(className: String): Flow<List<FeeRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeRecord(record: FeeRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeRecords(records: List<FeeRecordEntity>)

    @Update
    suspend fun updateFeeRecord(record: FeeRecordEntity)

    // --- Staff ---
    @Query("SELECT * FROM staff_members ORDER BY name ASC")
    fun getAllStaff(): Flow<List<StaffMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffMemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffList(staffList: List<StaffMemberEntity>)

    @Update
    suspend fun updateStaff(staff: StaffMemberEntity)

    @Delete
    suspend fun deleteStaff(staff: StaffMemberEntity)

    // --- Broadcasts ---
    @Query("SELECT * FROM broadcast_alerts ORDER BY timestamp DESC")
    fun getAllBroadcasts(): Flow<List<BroadcastAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBroadcast(broadcast: BroadcastAlertEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBroadcastList(broadcasts: List<BroadcastAlertEntity>)

    @Delete
    suspend fun deleteBroadcast(broadcast: BroadcastAlertEntity)
}
