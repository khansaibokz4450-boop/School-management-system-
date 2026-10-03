package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SchoolDatabase
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.BroadcastAlertEntity
import com.example.data.model.FeeRecordEntity
import com.example.data.model.StaffMemberEntity
import com.example.data.model.StudentEntity
import com.example.data.model.StudentWithAttendance
import com.example.data.repository.SchoolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ClassBannerMetrics(
    val activeClass: String = "Class 10",
    val totalStudents: Int = 0,
    val presentCount: Int = 0,
    val absentCount: Int = 0,
    val lateCount: Int = 0,
    val leaveCount: Int = 0,
    val attendancePercentage: Int = 0,
    val totalExpectedFee: Int = 0,
    val totalCollectedFee: Int = 0,
    val feeCollectionPercentage: Int = 0
)

class SchoolViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SchoolRepository
    val todayDateString: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val _selectedClass = MutableStateFlow("Class 10")
    val selectedClass: StateFlow<String> = _selectedClass.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _biometricScanning = MutableStateFlow(false)
    val biometricScanning: StateFlow<Boolean> = _biometricScanning.asStateFlow()

    private val _biometricSuccessStudent = MutableStateFlow<StudentEntity?>(null)
    val biometricSuccessStudent: StateFlow<StudentEntity?> = _biometricSuccessStudent.asStateFlow()

    init {
        val db = SchoolDatabase.getDatabase(application, viewModelScope)
        repository = SchoolRepository(db.schoolDao())
    }

    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayAttendance: StateFlow<List<AttendanceRecordEntity>> = repository.getAttendanceForDate(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFeeRecords: StateFlow<List<FeeRecordEntity>> = repository.allFeeRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStaff: StateFlow<List<StaffMemberEntity>> = repository.allStaff
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBroadcasts: StateFlow<List<BroadcastAlertEntity>> = repository.allBroadcasts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined filtered students for current selected class and search query
    val currentClassStudentsWithAttendance: StateFlow<List<StudentWithAttendance>> = combine(
        allStudents,
        todayAttendance,
        selectedClass,
        searchQuery
    ) { students, attendance, cls, query ->
        val filtered = students.filter { s ->
            val matchClass = (cls == "All Classes" || s.className.equals(cls, ignoreCase = true))
            val matchQuery = query.isEmpty() ||
                    s.name.contains(query, ignoreCase = true) ||
                    s.rollNo.contains(query, ignoreCase = true) ||
                    s.fatherName.contains(query, ignoreCase = true)
            matchClass && matchQuery
        }

        val attendanceMap = attendance.associateBy { it.studentId }
        filtered.map { student ->
            StudentWithAttendance(
                student = student,
                todayAttendance = attendanceMap[student.id]
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic banner metrics calculated reactively
    val bannerMetrics: StateFlow<ClassBannerMetrics> = combine(
        allStudents,
        todayAttendance,
        allFeeRecords,
        selectedClass
    ) { students, attendance, feeRecords, cls ->
        val classStudents = if (cls == "All Classes") students else students.filter { it.className.equals(cls, ignoreCase = true) }
        val studentIds = classStudents.map { it.id }.toSet()
        val classAttendance = attendance.filter { it.studentId in studentIds }

        val present = classAttendance.count { it.status == "PRESENT" }
        val absent = classAttendance.count { it.status == "ABSENT" }
        val late = classAttendance.count { it.status == "LATE" }
        val leave = classAttendance.count { it.status == "LEAVE" }

        val total = classStudents.size
        val marked = present + absent + late + leave
        val attendancePct = if (marked > 0) ((present + late) * 100 / marked) else 0

        val totalExpected = classStudents.sumOf { it.monthlyFee }
        val classFees = if (cls == "All Classes") feeRecords else feeRecords.filter { it.className.equals(cls, ignoreCase = true) }
        val totalCollected = classFees.filter { it.status == "PAID" }.sumOf { it.netAmount }
        val feePct = if (totalExpected > 0) ((totalCollected * 100) / totalExpected).coerceAtMost(100) else 0

        ClassBannerMetrics(
            activeClass = cls,
            totalStudents = total,
            presentCount = present,
            absentCount = absent,
            lateCount = late,
            leaveCount = leave,
            attendancePercentage = attendancePct,
            totalExpectedFee = totalExpected,
            totalCollectedFee = totalCollected,
            feeCollectionPercentage = feePct
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ClassBannerMetrics())

    fun setSelectedClass(className: String) {
        _selectedClass.value = className
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun markAttendance(studentId: Long, status: String, method: String = "MANUAL") {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markAttendance(studentId, todayDateString, status, method)
        }
    }

    fun markAllPresent() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentStudents = currentClassStudentsWithAttendance.value.map { it.student }
            repository.markAllPresent(currentStudents, todayDateString, method = "MANUAL")
        }
    }

    fun simulateBiometricScan(student: StudentEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            _biometricScanning.value = true
            delay(1200) // Realistic biometric reader verification delay
            repository.markAttendance(student.id, todayDateString, "PRESENT", method = "BIOMETRIC")
            _biometricScanning.value = false
            _biometricSuccessStudent.value = student
            onDone()
            delay(2500)
            if (_biometricSuccessStudent.value?.id == student.id) {
                _biometricSuccessStudent.value = null
            }
        }
    }

    fun addStudent(
        name: String,
        fatherName: String,
        rollNo: String,
        className: String,
        section: String,
        parentPhone: String,
        monthlyFee: Int,
        gender: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val student = StudentEntity(
                rollNo = rollNo,
                name = name,
                fatherName = fatherName,
                className = className,
                section = section,
                parentPhone = parentPhone,
                monthlyFee = monthlyFee,
                feeStatus = "PENDING",
                gender = gender
            )
            repository.insertStudent(student)
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStudent(student)
        }
    }

    fun recordFeePayment(
        student: StudentEntity,
        month: String,
        amount: Int,
        discount: Int,
        paymentMethod: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordFeePayment(
                studentId = student.id,
                studentName = student.name,
                className = student.className,
                month = month,
                amount = amount,
                discount = discount,
                paymentMethod = paymentMethod
            )
            repository.updateStudentFeeStatus(student, "PAID")
        }
    }

    fun updateStudentFeeStatus(student: StudentEntity, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStudentFeeStatus(student, newStatus)
        }
    }

    fun addStaffMember(
        name: String,
        designation: String,
        qualification: String,
        phone: String,
        subject: String,
        salary: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val staff = StaffMemberEntity(
                name = name,
                designation = designation,
                qualification = qualification,
                phone = phone,
                subject = subject,
                monthlySalary = salary,
                salaryStatus = "PENDING",
                attendanceToday = "PRESENT"
            )
            repository.insertStaff(staff)
        }
    }

    fun updateStaffAttendance(staff: StaffMemberEntity, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStaffAttendance(staff, status)
        }
    }

    fun updateStaffSalaryStatus(staff: StaffMemberEntity, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStaffSalaryStatus(staff, status)
        }
    }

    fun deleteStaffMember(staff: StaffMemberEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStaff(staff)
        }
    }

    fun sendBroadcast(
        title: String,
        targetClass: String,
        message: String,
        urduMessage: String,
        category: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val alert = BroadcastAlertEntity(
                title = title,
                targetClass = targetClass,
                message = message,
                urduMessage = urduMessage,
                category = category
            )
            repository.insertBroadcast(alert)
        }
    }
}
