package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.BroadcastAlertEntity
import com.example.data.model.FeeRecordEntity
import com.example.data.model.StaffMemberEntity
import com.example.data.model.StudentEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        StudentEntity::class,
        AttendanceRecordEntity::class,
        FeeRecordEntity::class,
        StaffMemberEntity::class,
        BroadcastAlertEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SchoolDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun feeDao(): FeeDao
    abstract fun staffDao(): StaffDao
    abstract fun schoolDao(): SchoolDao

    companion object {
        @Volatile
        private var INSTANCE: SchoolDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SchoolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SchoolDatabase::class.java,
                    "pakschool_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(SchoolDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SchoolDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.schoolDao())
                }
            }
        }

        suspend fun populateInitialData(dao: SchoolDao) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val students = listOf(
                // KG
                StudentEntity(rollNo = "KG-01", name = "Ayaan Ali", fatherName = "Muhammad Ali", className = "KG", section = "A", parentPhone = "+923001234501", monthlyFee = 2500, feeStatus = "PAID", gender = "Male"),
                StudentEntity(rollNo = "KG-02", name = "Zoya Fatima", fatherName = "Tariq Mehmood", className = "KG", section = "A", parentPhone = "+923214567802", monthlyFee = 2500, feeStatus = "PENDING", gender = "Female"),
                StudentEntity(rollNo = "KG-03", name = "Rayyan Ahmed", fatherName = "Ahmed Raza", className = "KG", section = "A", parentPhone = "+923335551203", monthlyFee = 2500, feeStatus = "PAID", gender = "Male"),

                // Nursery
                StudentEntity(rollNo = "NUR-01", name = "Muhammad Musa", fatherName = "Kashif Khan", className = "Nursery", section = "Rose", parentPhone = "+923459876504", monthlyFee = 2800, feeStatus = "PAID", gender = "Male"),
                StudentEntity(rollNo = "NUR-02", name = "Anaya Zahra", fatherName = "Syed Imran", className = "Nursery", section = "Rose", parentPhone = "+923017778805", monthlyFee = 2800, feeStatus = "DEFAULTER", gender = "Female"),

                // Prep
                StudentEntity(rollNo = "PRP-01", name = "Hassan Bilal", fatherName = "Bilal Farooq", className = "Prep", section = "Tulip", parentPhone = "+923124443306", monthlyFee = 3000, feeStatus = "PAID", gender = "Male"),
                StudentEntity(rollNo = "PRP-02", name = "Meerab Tariq", fatherName = "Tariq Jamil", className = "Prep", section = "Tulip", parentPhone = "+923361112207", monthlyFee = 3000, feeStatus = "PENDING", gender = "Female"),

                // Class 1
                StudentEntity(rollNo = "C01-01", name = "Hamza Shah", fatherName = "Shahid Hussain", className = "Class 1", section = "A", parentPhone = "+923023334408", monthlyFee = 3200, feeStatus = "PAID", gender = "Male"),
                StudentEntity(rollNo = "C01-02", name = "Dua Fatima", fatherName = "Munir Ahmed", className = "Class 1", section = "A", parentPhone = "+923246667709", monthlyFee = 3200, feeStatus = "PAID", gender = "Female"),

                // Class 5
                StudentEntity(rollNo = "C05-01", name = "Usman Ghani", fatherName = "Abdul Rehman", className = "Class 5", section = "Blue", parentPhone = "+923007654310", monthlyFee = 3800, feeStatus = "PAID", gender = "Male"),
                StudentEntity(rollNo = "C05-02", name = "Aiman Noor", fatherName = "Noor Muhammad", className = "Class 5", section = "Blue", parentPhone = "+923339998811", monthlyFee = 3800, feeStatus = "DEFAULTER", gender = "Female"),
                StudentEntity(rollNo = "C05-03", name = "Saad Farooq", fatherName = "Farooq Azam", className = "Class 5", section = "Blue", parentPhone = "+923451239812", monthlyFee = 3800, feeStatus = "PAID", gender = "Male"),

                // Class 9 (Matric Part 1)
                StudentEntity(rollNo = "C09-01", name = "Zain Abbas", fatherName = "Ghulam Abbas", className = "Class 9", section = "Science", parentPhone = "+923004455613", monthlyFee = 4500, feeStatus = "PAID", gender = "Male"),
                StudentEntity(rollNo = "C09-02", name = "Mahnoor Asif", fatherName = "Asif Mehmood", className = "Class 9", section = "Science", parentPhone = "+923228877614", monthlyFee = 4500, feeStatus = "PENDING", gender = "Female"),
                StudentEntity(rollNo = "C09-03", name = "Abdullah Jan", fatherName = "Jan Muhammad", className = "Class 9", section = "Computer", parentPhone = "+923157776615", monthlyFee = 4500, feeStatus = "PAID", gender = "Male"),

                // Class 10 (Matric)
                StudentEntity(rollNo = "C10-01", name = "Muhammad Ali", fatherName = "Liaqat Ali", className = "Class 10", section = "A-Science", parentPhone = "+923001122334", monthlyFee = 5000, feeStatus = "PAID", gender = "Male"),
                StudentEntity(rollNo = "C10-02", name = "Fatima Zahra", fatherName = "Syed Zafar", className = "Class 10", section = "A-Science", parentPhone = "+923214455667", monthlyFee = 5000, feeStatus = "PAID", gender = "Female"),
                StudentEntity(rollNo = "C10-03", name = "Bilal Siddiqui", fatherName = "Rashid Siddiqui", className = "Class 10", section = "B-Arts", parentPhone = "+923337788990", monthlyFee = 4800, feeStatus = "DEFAULTER", gender = "Male"),
                StudentEntity(rollNo = "C10-04", name = "Ayesha Khan", fatherName = "Shahid Khan", className = "Class 10", section = "A-Science", parentPhone = "+923459988776", monthlyFee = 5000, feeStatus = "PENDING", gender = "Female"),
                StudentEntity(rollNo = "C10-05", name = "Hamza Yousaf", fatherName = "Muhammad Yousaf", className = "Class 10", section = "B-Arts", parentPhone = "+923025544332", monthlyFee = 4800, feeStatus = "PAID", gender = "Male"),

                // 1st Year (FSc / ICS Part 1)
                StudentEntity(rollNo = "FY-01", name = "Daniyal Baig", fatherName = "Mirza Baig", className = "1st Year", section = "FSc Pre-Med", parentPhone = "+923001239916", monthlyFee = 7000, feeStatus = "PAID", gender = "Male"),
                StudentEntity(rollNo = "FY-02", name = "Khadija Bibi", fatherName = "Hafiz Nazeer", className = "1st Year", section = "ICS", parentPhone = "+923218882217", monthlyFee = 6500, feeStatus = "DEFAULTER", gender = "Female"),
                StudentEntity(rollNo = "FY-03", name = "Waleed Akhtar", fatherName = "Akhtar Rasool", className = "1st Year", section = "FSc Pre-Eng", parentPhone = "+923334449918", monthlyFee = 7000, feeStatus = "PAID", gender = "Male"),

                // 2nd Year (FSc / ICS Part 2)
                StudentEntity(rollNo = "SY-01", name = "Mustafa Hashmi", fatherName = "Hashmi Saeed", className = "2nd Year", section = "FSc Pre-Med", parentPhone = "+923019991119", monthlyFee = 7500, feeStatus = "PAID", gender = "Male"),
                StudentEntity(rollNo = "SY-02", name = "Hira Qureshi", fatherName = "Qureshi Bashir", className = "2nd Year", section = "ICS", parentPhone = "+923456663320", monthlyFee = 7000, feeStatus = "PENDING", gender = "Female")
            )
            dao.insertStudents(students)

            // Seed Attendance for Today
            val attendanceList = listOf(
                AttendanceRecordEntity(studentId = 1, date = today, status = "PRESENT", time = "07:48 AM", method = "BIOMETRIC"),
                AttendanceRecordEntity(studentId = 2, date = today, status = "ABSENT", time = "-", method = "MANUAL"),
                AttendanceRecordEntity(studentId = 3, date = today, status = "PRESENT", time = "07:55 AM", method = "BIOMETRIC"),
                AttendanceRecordEntity(studentId = 4, date = today, status = "PRESENT", time = "07:50 AM", method = "MANUAL"),
                AttendanceRecordEntity(studentId = 5, date = today, status = "LATE", time = "08:15 AM", method = "BIOMETRIC"),
                AttendanceRecordEntity(studentId = 16, date = today, status = "PRESENT", time = "07:42 AM", method = "BIOMETRIC"),
                AttendanceRecordEntity(studentId = 17, date = today, status = "PRESENT", time = "07:45 AM", method = "BIOMETRIC"),
                AttendanceRecordEntity(studentId = 18, date = today, status = "ABSENT", time = "-", method = "MANUAL"),
                AttendanceRecordEntity(studentId = 19, date = today, status = "LEAVE", time = "08:00 AM", method = "MANUAL"),
                AttendanceRecordEntity(studentId = 20, date = today, status = "PRESENT", time = "07:52 AM", method = "BIOMETRIC")
            )
            dao.insertAttendanceList(attendanceList)

            // Seed Fee Records
            val feeRecords = listOf(
                FeeRecordEntity(studentId = 16, studentName = "Muhammad Ali", className = "Class 10", month = "October 2026", amount = 5000, discount = 0, netAmount = 5000, status = "PAID", paidDate = "02 Oct 2026", paymentMethod = "JazzCash", receiptNo = "PK-1001"),
                FeeRecordEntity(studentId = 17, studentName = "Fatima Zahra", className = "Class 10", month = "October 2026", amount = 5000, discount = 500, netAmount = 4500, status = "PAID", paidDate = "01 Oct 2026", paymentMethod = "Cash", receiptNo = "PK-1002"),
                FeeRecordEntity(studentId = 18, studentName = "Bilal Siddiqui", className = "Class 10", month = "October 2026", amount = 4800, discount = 0, netAmount = 4800, status = "OVERDUE", paidDate = null, paymentMethod = "Cash", receiptNo = "PK-1003"),
                FeeRecordEntity(studentId = 19, studentName = "Ayesha Khan", className = "Class 10", month = "October 2026", amount = 5000, discount = 0, netAmount = 5000, status = "PENDING", paidDate = null, paymentMethod = "EasyPaisa", receiptNo = "PK-1004"),
                FeeRecordEntity(studentId = 2, studentName = "Zoya Fatima", className = "KG", month = "October 2026", amount = 2500, discount = 0, netAmount = 2500, status = "PENDING", paidDate = null, paymentMethod = "Cash", receiptNo = "PK-1005"),
                FeeRecordEntity(studentId = 5, studentName = "Anaya Zahra", className = "Nursery", month = "October 2026", amount = 2800, discount = 0, netAmount = 2800, status = "OVERDUE", paidDate = null, paymentMethod = "Cash", receiptNo = "PK-1006"),
                FeeRecordEntity(studentId = 11, studentName = "Aiman Noor", className = "Class 5", month = "October 2026", amount = 3800, discount = 0, netAmount = 3800, status = "OVERDUE", paidDate = null, paymentMethod = "Cash", receiptNo = "PK-1007"),
                FeeRecordEntity(studentId = 22, studentName = "Khadija Bibi", className = "1st Year", month = "October 2026", amount = 6500, discount = 0, netAmount = 6500, status = "OVERDUE", paidDate = null, paymentMethod = "Bank Transfer", receiptNo = "PK-1008")
            )
            dao.insertFeeRecords(feeRecords)

            // Seed Staff
            val staff = listOf(
                StaffMemberEntity(name = "Sir Tariq Mahmood", designation = "Senior Principal", qualification = "M.Sc Physics (QAU), B.Ed", phone = "+923009988771", subject = "Physics & Administration", monthlySalary = 85000, salaryStatus = "PAID", attendanceToday = "PRESENT"),
                StaffMemberEntity(name = "Ma'am Sadia Noor", designation = "Vice Principal", qualification = "M.A English (PU), M.Ed", phone = "+923214433221", subject = "English Literature", monthlySalary = 65000, salaryStatus = "PAID", attendanceToday = "PRESENT"),
                StaffMemberEntity(name = "Sir Asif Javed", designation = "Senior Math Teacher", qualification = "M.Sc Mathematics", phone = "+923337766551", subject = "Mathematics (9th/10th)", monthlySalary = 55000, salaryStatus = "PENDING", attendanceToday = "PRESENT"),
                StaffMemberEntity(name = "Ma'am Rabia Basri", designation = "Urdu & Islamiyat Head", qualification = "M.A Urdu, Fazil-e-Dars", phone = "+923451122331", subject = "Urdu & Islamic Studies", monthlySalary = 48000, salaryStatus = "PAID", attendanceToday = "LEAVE"),
                StaffMemberEntity(name = "Sir Farhan Qureshi", designation = "IT & Computer Incharge", qualification = "BS Computer Science (FAST)", phone = "+923129988001", subject = "Computer Science / ICS", monthlySalary = 52000, salaryStatus = "PENDING", attendanceToday = "PRESENT"),
                StaffMemberEntity(name = "Ma'am Zainab Hashmi", designation = "Primary Section Head", qualification = "Montessori Diploma, B.A", phone = "+923365544331", subject = "Primary & KG Section", monthlySalary = 42000, salaryStatus = "PAID", attendanceToday = "PRESENT")
            )
            dao.insertStaffList(staff)

            // Seed Broadcast Alerts
            val broadcasts = listOf(
                BroadcastAlertEntity(
                    title = "Daily Absentee Alert",
                    targetClass = "All Classes",
                    message = "Respected Parents, your child was marked ABSENT today. Please contact the school office to confirm reason of absence.",
                    urduMessage = "",
                    category = "ABSENTEE"
                ),
                BroadcastAlertEntity(
                    title = "Monthly Fee Reminder (Due Oct 10)",
                    targetClass = "All Classes",
                    message = "Dear Parents, monthly tuition fee for October 2026 is due by 10th October. Please submit dues on time to avoid late surcharge.",
                    urduMessage = "",
                    category = "FEE_REMINDER"
                ),
                BroadcastAlertEntity(
                    title = "Matric & Inter Exam Schedule",
                    targetClass = "Class 10",
                    message = "Class 10 send-up preparatory examinations start next Monday at 08:30 AM sharp. Full uniform and roll number slips mandatory.",
                    urduMessage = "",
                    category = "EXAM"
                )
            )
            dao.insertBroadcastList(broadcasts)
        }
    }
}
