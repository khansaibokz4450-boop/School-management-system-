package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// All classes supported across Pakistani educational institutions
val PAK_CLASSES = listOf(
    "KG",
    "Nursery",
    "Prep",
    "Class 1",
    "Class 2",
    "Class 3",
    "Class 4",
    "Class 5",
    "Class 6",
    "Class 7",
    "Class 8",
    "Class 9",
    "Class 10",
    "1st Year",
    "2nd Year"
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rollNo: String,
    val name: String,
    val fatherName: String,
    val className: String,
    val section: String = "A",
    val parentPhone: String,
    val monthlyFee: Int = 3500,
    val feeStatus: String = "PAID", // PAID, PENDING, DEFAULTER
    val gender: String = "Male",
    val bFormOrCnic: String = "",
    val admissionDate: String = "2026-08-15"
)

@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val date: String, // YYYY-MM-DD
    val status: String, // PRESENT, ABSENT, LATE, LEAVE
    val time: String = "08:00 AM",
    val method: String = "MANUAL" // BIOMETRIC, MANUAL, NFC
)

@Entity(tableName = "fee_records")
data class FeeRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val className: String,
    val month: String, // e.g. "October 2026"
    val amount: Int,
    val discount: Int = 0,
    val netAmount: Int,
    val status: String = "PENDING", // PAID, PENDING, OVERDUE
    val paidDate: String? = null,
    val paymentMethod: String = "Cash", // Cash, JazzCash, EasyPaisa, Bank Transfer
    val receiptNo: String = "RCP-0000"
)

@Entity(tableName = "staff_members")
data class StaffMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val designation: String,
    val qualification: String,
    val phone: String,
    val subject: String,
    val monthlySalary: Int,
    val salaryStatus: String = "PAID", // PAID, PENDING, ADVANCE
    val attendanceToday: String = "PRESENT" // PRESENT, ABSENT, LEAVE
)

@Entity(tableName = "broadcast_alerts")
data class BroadcastAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetClass: String, // Specific class or "All Classes"
    val message: String,
    val urduMessage: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "ANNOUNCEMENT", // ABSENTEE, FEE_REMINDER, ANNOUNCEMENT, EXAM
    val status: String = "SENT"
)

// Combined UI Model for attendance roll call
data class StudentWithAttendance(
    val student: StudentEntity,
    val todayAttendance: AttendanceRecordEntity?
)
