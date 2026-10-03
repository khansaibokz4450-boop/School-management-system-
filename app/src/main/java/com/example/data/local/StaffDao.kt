package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StaffMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StaffDao {
    @Query("SELECT * FROM staff_members ORDER BY name ASC")
    fun getAllStaff(): Flow<List<StaffMemberEntity>>

    @Query("SELECT * FROM staff_members WHERE id = :id LIMIT 1")
    suspend fun getStaffById(id: Long): StaffMemberEntity?

    @Query("SELECT * FROM staff_members WHERE attendanceToday = :status")
    fun getStaffByAttendance(status: String): Flow<List<StaffMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffMemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffList(staffList: List<StaffMemberEntity>)

    @Update
    suspend fun updateStaff(staff: StaffMemberEntity)

    @Delete
    suspend fun deleteStaff(staff: StaffMemberEntity)
}
