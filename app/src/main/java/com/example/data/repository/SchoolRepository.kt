package com.example.data.repository

import com.example.data.local.SchoolDao
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.BroadcastAlertEntity
import com.example.data.model.FeeRecordEntity
import com.example.data.model.StaffMemberEntity
import com.example.data.model.StudentEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SchoolRepository(private val dao: SchoolDao) {
    val allStudents: Flow<List<StudentEntity>> = dao.getAllStudents()

    fun getStudentsByClass(className: String): Flow<List<StudentEntity>> {
        return if (className == "All Classes") dao.getAllStudents() else dao.getStudentsByClass(className)
    }

    suspend fun insertStudent(student: StudentEntity): Long = dao.insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = dao.updateStudent(student)
    suspend fun deleteStudent(student: StudentEntity) = dao.deleteStudent(student)

    // Attendance
    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecordEntity>> = dao.getAttendanceForDate(date)

    fun getAttendanceForClassAndDate(className: String, date: String): Flow<List<AttendanceRecordEntity>> {
        return if (className == "All Classes") {
            dao.getAttendanceForDate(date)
        } else {
            dao.getAttendanceForClassAndDate(className, date)
        }
    }

    suspend fun markAttendance(studentId: Long, date: String, status: String, method: String = "MANUAL"): Long {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val existing = dao.getAttendanceForStudentAndDate(studentId, date)
        val record = if (existing != null) {
            existing.copy(status = status, time = currentTime, method = method)
        } else {
            AttendanceRecordEntity(
                studentId = studentId,
                date = date,
                status = status,
                time = currentTime,
                method = method
            )
        }
        return dao.insertOrUpdateAttendance(record)
    }

    suspend fun markAllPresent(students: List<StudentEntity>, date: String, method: String = "MANUAL") {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val records = students.map { student ->
            val existing = dao.getAttendanceForStudentAndDate(student.id, date)
            if (existing != null) {
                existing.copy(status = "PRESENT", time = currentTime, method = method)
            } else {
                AttendanceRecordEntity(
                    studentId = student.id,
                    date = date,
                    status = "PRESENT",
                    time = currentTime,
                    method = method
                )
            }
        }
        dao.insertAttendanceList(records)
    }

    // Fees
    val allFeeRecords: Flow<List<FeeRecordEntity>> = dao.getAllFeeRecords()

    fun getFeeRecordsByClass(className: String): Flow<List<FeeRecordEntity>> {
        return if (className == "All Classes") dao.getAllFeeRecords() else dao.getFeeRecordsByClass(className)
    }

    suspend fun recordFeePayment(
        studentId: Long,
        studentName: String,
        className: String,
        month: String,
        amount: Int,
        discount: Int,
        paymentMethod: String
    ): Long {
        val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val receiptNo = "PK-${(1000..9999).random()}"
        val record = FeeRecordEntity(
            studentId = studentId,
            studentName = studentName,
            className = className,
            month = month,
            amount = amount,
            discount = discount,
            netAmount = (amount - discount).coerceAtLeast(0),
            status = "PAID",
            paidDate = today,
            paymentMethod = paymentMethod,
            receiptNo = receiptNo
        )
        return dao.insertFeeRecord(record)
    }

    suspend fun updateStudentFeeStatus(student: StudentEntity, newStatus: String) {
        dao.updateStudent(student.copy(feeStatus = newStatus))
    }

    // Staff
    val allStaff: Flow<List<StaffMemberEntity>> = dao.getAllStaff()

    suspend fun insertStaff(staff: StaffMemberEntity): Long = dao.insertStaff(staff)
    suspend fun updateStaff(staff: StaffMemberEntity) = dao.updateStaff(staff)
    suspend fun deleteStaff(staff: StaffMemberEntity) = dao.deleteStaff(staff)

    suspend fun updateStaffAttendance(staff: StaffMemberEntity, status: String) {
        dao.updateStaff(staff.copy(attendanceToday = status))
    }

    suspend fun updateStaffSalaryStatus(staff: StaffMemberEntity, status: String) {
        dao.updateStaff(staff.copy(salaryStatus = status))
    }

    // Broadcasts
    val allBroadcasts: Flow<List<BroadcastAlertEntity>> = dao.getAllBroadcasts()

    suspend fun insertBroadcast(broadcast: BroadcastAlertEntity): Long = dao.insertBroadcast(broadcast)
    suspend fun deleteBroadcast(broadcast: BroadcastAlertEntity) = dao.deleteBroadcast(broadcast)
}
