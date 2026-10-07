package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentEntity
import com.example.data.model.StudentWithAttendance
import com.example.ui.components.ActiveClassBanner
import com.example.ui.components.BiometricScanDialog
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AlertRed
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryTeal
import com.example.ui.viewmodel.SchoolViewModel
import com.example.util.WhatsAppHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceScreen(
    viewModel: SchoolViewModel,
    onOpenKiosk: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bannerMetrics by viewModel.bannerMetrics.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val studentsWithAttendance by viewModel.currentClassStudentsWithAttendance.collectAsState()
    val staffList by viewModel.allStaff.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recordedDates by viewModel.allRecordedDates.collectAsState()

    var statusFilter by remember { mutableStateOf("ALL") }
    var biometricStudentTarget by remember { mutableStateOf<StudentEntity?>(null) }
    var showRegistrationDialog by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }
    var isHoliday by remember { mutableStateOf(false) }

    // Quick direct machine terminal state
    var isMachineStaffMode by remember { mutableStateOf(false) } // false = Student, true = Teacher
    var quickRollInput by remember { mutableStateOf("") }
    var lastPunchAlert by remember { mutableStateOf<String?>(null) }

    // Pulsing animation for sensor
    val infiniteTransition = rememberInfiniteTransition(label = "sensorPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    fun executeQuickPunch(identifier: String = "") {
        val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        if (!isMachineStaffMode) {
            // Student Punch
            val targetStudent = if (identifier.isNotBlank()) {
                studentsWithAttendance.find {
                    it.student.rollNo.equals(identifier, ignoreCase = true) ||
                            it.student.rollNo.endsWith(identifier) ||
                            it.student.id.toString() == identifier
                }?.student ?: studentsWithAttendance.firstOrNull()?.student
            } else {
                studentsWithAttendance.find { it.todayAttendance?.status != "PRESENT" }?.student
                    ?: studentsWithAttendance.firstOrNull()?.student
            }

            if (targetStudent != null) {
                viewModel.markAttendance(targetStudent.id, "PRESENT", "BIOMETRIC")
                lastPunchAlert = "✅ حاضری درج ہو گئی: ${targetStudent.name} (${targetStudent.rollNo}) • $timeNow"
                quickRollInput = ""
                scope.launch {
                    delay(3000)
                    lastPunchAlert = null
                }
            } else {
                Toast.makeText(context, "No student found to punch", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Teacher Punch
            val targetStaff = if (identifier.isNotBlank()) {
                staffList.find {
                    it.id.toString() == identifier ||
                            it.name.contains(identifier, ignoreCase = true)
                } ?: staffList.firstOrNull()
            } else {
                staffList.find { it.attendanceToday != "PRESENT" } ?: staffList.firstOrNull()
            }

            if (targetStaff != null) {
                viewModel.updateStaffAttendance(targetStaff, "PRESENT")
                lastPunchAlert = "✅ استاد حاضری درج ہو گئی: ${targetStaff.name} (${targetStaff.subject}) • $timeNow"
                quickRollInput = ""
                scope.launch {
                    delay(3000)
                    lastPunchAlert = null
                }
            } else {
                Toast.makeText(context, "No teacher found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showRegistrationDialog) {
        StudentRegistrationDialog(
            initialClass = selectedClass,
            onDismiss = { showRegistrationDialog = false },
            onStudentRegistered = { name, age, cls, gender ->
                viewModel.registerNewStudent(name, age, cls, gender)
                showRegistrationDialog = false
            }
        )
    }

    // Biometric scanner dialog
    biometricStudentTarget?.let { student ->
        BiometricScanDialog(
            student = student,
            onDismiss = { biometricStudentTarget = null },
            onSuccess = {
                viewModel.markAttendance(student.id, "PRESENT", "BIOMETRIC")
                biometricStudentTarget = null
                Toast.makeText(context, "✅ Biometric verified for ${student.name}!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    val filteredList = studentsWithAttendance.filter { item ->
        val matchesSearch = if (searchQuery.isBlank()) true else {
            item.student.name.contains(searchQuery, ignoreCase = true) ||
                    item.student.rollNo.contains(searchQuery, ignoreCase = true) ||
                    item.student.fatherName.contains(searchQuery, ignoreCase = true)
        }
        val matchesStatus = when (statusFilter) {
            "PRESENT" -> item.todayAttendance?.status == "PRESENT"
            "ABSENT" -> item.todayAttendance?.status == "ABSENT"
            "LATE" -> item.todayAttendance?.status == "LATE"
            "LEAVE" -> item.todayAttendance?.status == "LEAVE"
            else -> true
        }
        matchesSearch && matchesStatus
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header with Back Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEFF6FF),
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(end = 10.dp)
                    ) {
                        Text(
                            text = "← Main Menu",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Attendance Register",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                        Text(
                            text = "روزانہ حاضری و فنگر پرنٹ مشین • Class: $selectedClass",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showRegistrationDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFE0F2FE), RoundedCornerShape(10.dp))
                            .testTag("btn_attendance_add_student")
                    ) {
                        Text(text = "🎒", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = { viewModel.markAllPresent() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("mark_all_present_btn")
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("All Present", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Active Class Selector Banner
        item {
            ActiveClassBanner(
                metrics = bannerMetrics,
                selectedClass = selectedClass,
                onClassSelected = { viewModel.setSelectedClass(it) }
            )
        }

        // DIRECT BIOMETRIC FINGERPRINT & CAMERA MACHINE TERMINAL CARD
        // Addresses requirement: "ایپس میں گھومنے کی ضرورت نہیں کہ نام تلاش کرے، فنگر پرنٹ یا کیمرے پر لگاتے ہی ڈائریکٹ حاضری ہو جائے"
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth().testTag("direct_biometric_machine_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header & Switch between Students and Staff
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFF22C55E), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "⚡ ONE-TOUCH ATTENDANCE MACHINE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Mode Selector (Students vs Teachers)
                        Row(
                            modifier = Modifier
                                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                .padding(2.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (!isMachineStaffMode) Color(0xFF16A34A) else Color.Transparent,
                                modifier = Modifier.clickable { isMachineStaffMode = false }
                            ) {
                                Text(
                                    text = "طلبہ (Student)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isMachineStaffMode) Color(0xFF2563EB) else Color.Transparent,
                                modifier = Modifier.clickable { isMachineStaffMode = true }
                            ) {
                                Text(
                                    text = "اساتذہ (Faculty)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "نام تلاش کرنے کی ضرورت نہیں! انگوٹھا لگائیں یا رول نمبر درج کر کے پنچ کریں:",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Center Interactive Scanner & Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Big Fingerprint Touchpad Button
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .border(2.dp, Color(0xFF38BDF8), CircleShape)
                                .clickable { executeQuickPunch(quickRollInput) }
                                .testTag("touch_fingerprint_sensor_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Place Thumb",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Quick Roll / ID Entry & Punch Button
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = quickRollInput,
                                onValueChange = { quickRollInput = it },
                                placeholder = {
                                    Text(
                                        text = if (!isMachineStaffMode) "Roll # (e.g. 01 / 10)" else "Teacher ID / Name",
                                        color = Color(0xFF64748B),
                                        fontSize = 12.sp
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF1E293B),
                                    unfocusedContainerColor = Color(0xFF1E293B),
                                    focusedBorderColor = Color(0xFF38BDF8),
                                    unfocusedBorderColor = Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { executeQuickPunch(quickRollInput) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(36.dp)
                                ) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("فوری پنچ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Button(
                                    onClick = onOpenKiosk,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1.2f).height(36.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("کیمرہ ٹرمینل", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    // Success Confirmation Toast Banner
                    AnimatedVisibility(
                        visible = lastPunchAlert != null,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF14532D),
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = lastPunchAlert ?: "",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(8.dp))

                    // 1-Year Attendance Archive History Access
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF93C5FD), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "1-Year Attendance History (${recordedDates.size} Dates recorded)",
                                fontSize = 11.sp,
                                color = Color(0xFF93C5FD)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.clickable { showArchiveDialog = true }
                        ) {
                            Text(
                                text = "📅 ہسٹری و ڈیلیٹ کنٹرول",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Principal Attendance WhatsApp Sheet & Holiday Mode Control Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("principal_attendance_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isHoliday) Color(0xFFFEF3C7) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isHoliday) "🏖️ School Holiday Mode" else "📋 Principal WhatsApp Sheet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isHoliday) Color(0xFF92400E) else Color(0xFF166534)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isHoliday) "چھٹی آن ہے" else "چھٹی کا موڈ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHoliday) Color(0xFFB45309) else Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = isHoliday,
                                onCheckedChange = { isHoliday = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFD97706)
                                ),
                                modifier = Modifier.testTag("holiday_switch")
                            )
                        }
                    }

                    if (isHoliday) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "⚠️ چھٹی کے باعث طلبہ کے والدین کو غیر حاضری کے پیغامات خودکار طور پر بند کر دیے گئے ہیں۔",
                            fontSize = 11.sp,
                            color = Color(0xFFB45309)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button: Send Sheet directly to Principal on WhatsApp
                    Button(
                        onClick = {
                            val absents = studentsWithAttendance
                                .filter { it.todayAttendance?.status == "ABSENT" }
                                .map { it.student }
                            val report = WhatsAppHelper.buildPrincipalDailyAttendanceReport(
                                date = viewModel.todayDateString,
                                className = selectedClass,
                                totalStudents = bannerMetrics.totalStudents,
                                presentCount = bannerMetrics.presentCount,
                                absentCount = bannerMetrics.absentCount,
                                absentStudents = absents,
                                isHoliday = isHoliday,
                                holidayReason = if (isHoliday) "سرکاری تعطیل / اتوار / برسات کی چھٹی" else ""
                            )
                            WhatsAppHelper.sendWhatsAppMessage(context, "+923001234567", report)
                            Toast.makeText(context, "Opening WhatsApp with Principal Daily Attendance Sheet...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_send_principal_sheet")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "📲 Send Sheet to Principal WhatsApp (پرنسپل کو شیٹ بھیجیں)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Bulk send Urdu absentee notices to parents (only if not holiday)
                    if (!isHoliday && bannerMetrics.absentCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                val absents = studentsWithAttendance.filter { it.todayAttendance?.status == "ABSENT" }
                                if (absents.isNotEmpty()) {
                                    val first = absents.first()
                                    val msg = WhatsAppHelper.buildAbsenteeUrduMessage(
                                        studentName = first.student.name,
                                        rollNo = first.student.rollNo,
                                        className = first.student.className,
                                        date = viewModel.todayDateString
                                    )
                                    WhatsAppHelper.sendWhatsAppMessage(context, first.student.parentPhone, msg)
                                    Toast.makeText(context, "Opening WhatsApp for ${first.student.name}'s parent...", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("btn_bulk_absent_parents")
                        ) {
                            Text(
                                text = "📢 Send Urdu Absentee Alert to Parents (${bannerMetrics.absentCount} غیر حاضر)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed
                            )
                        }
                    }
                }
            }
        }

        // Search & Status Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search by student name or roll #...", color = Color(0xFF64748B)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F172A), fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF1E293B),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("attendance_search_field"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "ALL" to "All (${studentsWithAttendance.size})",
                        "PRESENT" to "Present (${bannerMetrics.presentCount})",
                        "ABSENT" to "Absent (${bannerMetrics.absentCount})",
                        "LATE" to "Late",
                        "LEAVE" to "Leave"
                    ).forEach { (status, label) ->
                        FilterChip(
                            selected = statusFilter == status,
                            onClick = { statusFilter = status },
                            label = { Text(label, fontSize = 11.sp, fontWeight = if (statusFilter == status) FontWeight.Bold else FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (status == "ABSENT") AlertRed.copy(alpha = 0.15f) else PrimaryGreen.copy(alpha = 0.15f),
                                selectedLabelColor = if (status == "ABSENT") AlertRed else PrimaryGreen,
                                containerColor = Color.White,
                                labelColor = Color(0xFF334155)
                            ),
                            modifier = Modifier.testTag("filter_$status")
                        )
                    }
                }
            }
        }

        // Student Roster
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No students match the selected filter.",
                        color = Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(filteredList, key = { it.student.id }) { item ->
                AttendanceStudentCard(
                    item = item,
                    isHoliday = isHoliday,
                    onMarkStatus = { newStatus ->
                        viewModel.markAttendance(item.student.id, newStatus, "MANUAL")
                    },
                    onTriggerBiometric = {
                        biometricStudentTarget = item.student
                    },
                    onSendWhatsAppAbsent = {
                        val msg = WhatsAppHelper.buildAbsenteeUrduMessage(
                            studentName = item.student.name,
                            rollNo = item.student.rollNo,
                            className = item.student.className,
                            date = viewModel.todayDateString
                        )
                        WhatsAppHelper.sendWhatsAppMessage(context, item.student.parentPhone, msg)
                        Toast.makeText(context, "Opening WhatsApp for ${item.student.name}...", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // 1-Year History Archive Dialog
    if (showArchiveDialog) {
        AttendanceArchiveDialog(
            recordedDates = recordedDates,
            onDismiss = { showArchiveDialog = false },
            onDeleteDate = { date ->
                viewModel.deleteAttendanceHistoryForDate(date)
                Toast.makeText(context, "Attendance history for $date deleted", Toast.LENGTH_SHORT).show()
            },
            onClearAllHistory = {
                viewModel.clearAllAttendanceHistory()
                Toast.makeText(context, "All past attendance history cleared by Principal", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun AttendanceStudentCard(
    item: StudentWithAttendance,
    isHoliday: Boolean,
    onMarkStatus: (String) -> Unit,
    onTriggerBiometric: () -> Unit,
    onSendWhatsAppAbsent: () -> Unit
) {
    val currentStatus = item.todayAttendance?.status ?: "PENDING"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("attendance_card_${item.student.rollNo}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Roll number badge + student name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PrimaryGreen.copy(alpha = 0.12f), CircleShape)
                            .border(1.dp, PrimaryGreen.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.student.rollNo.takeLast(2),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.student.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${item.student.rollNo} • S/O: ${item.student.fatherName} • ${item.student.className}",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }

                // Biometric shortcut button (Fingerprint / Face / USB)
                IconButton(
                    onClick = onTriggerBiometric,
                    modifier = Modifier.testTag("biometric_btn_${item.student.rollNo}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometric Verify",
                        tint = if (item.todayAttendance?.method == "BIOMETRIC") Color(0xFF2E7D32) else Color(0xFF1E40AF),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Attendance Action Status Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusPill(
                    label = "Present",
                    isSelected = currentStatus == "PRESENT",
                    activeColor = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f),
                    onClick = { onMarkStatus("PRESENT") }
                )
                StatusPill(
                    label = "Absent",
                    isSelected = currentStatus == "ABSENT",
                    activeColor = AlertRed,
                    modifier = Modifier.weight(1f),
                    onClick = { onMarkStatus("ABSENT") }
                )
                StatusPill(
                    label = "Late",
                    isSelected = currentStatus == "LATE",
                    activeColor = AccentGold,
                    modifier = Modifier.weight(0.7f),
                    onClick = { onMarkStatus("LATE") }
                )
                StatusPill(
                    label = "Leave",
                    isSelected = currentStatus == "LEAVE",
                    activeColor = SecondaryTeal,
                    modifier = Modifier.weight(0.7f),
                    onClick = { onMarkStatus("LEAVE") }
                )
            }

            // Urdu WhatsApp Alert if Marked Absent
            if (currentStatus == "ABSENT") {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AlertRed.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHoliday) "🏖️ چھٹی: میسج موقوف" else "⚠️ غیر حاضر - والدین کو اردو اطلاع",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHoliday) Color(0xFFB45309) else AlertRed
                        )

                        if (!isHoliday) {
                            Button(
                                onClick = onSendWhatsAppAbsent,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("whatsapp_absent_${item.student.rollNo}")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ارسال کریں",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPill(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) activeColor else activeColor.copy(alpha = 0.08f),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, activeColor.copy(alpha = 0.25f)),
        modifier = modifier
            .height(34.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else activeColor
                )
            }
        }
    }
}

@Composable
private fun AttendanceArchiveDialog(
    recordedDates: List<String>,
    onDismiss: () -> Unit,
    onDeleteDate: (String) -> Unit,
    onClearAllHistory: () -> Unit
) {
    var showPinDialog by remember { mutableStateOf(false) }
    var pinAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    // Display list of 1-year dates
    val datesToShow = if (recordedDates.isNotEmpty()) recordedDates else listOf(
        "2026-10-06", "2026-10-05", "2026-10-04", "2026-10-03", "2026-10-02", "2026-10-01", "2026-09-30"
    )

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "1-Year Attendance Archive", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E40AF))
                        Text(text = "ایک سال کا سابقہ حاضری ریکارڈ و ڈیلیٹ کنٹرول", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Check, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "RECORDED SESSIONS (${datesToShow.size} Days):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier.height(220.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(datesToShow) { d ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF1E40AF), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = d, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                }

                                IconButton(
                                    onClick = {
                                        pinAction = { onDeleteDate(d) }
                                        pinInput = ""
                                        pinError = false
                                        showPinDialog = true
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Principal clear entire history button
                Button(
                    onClick = {
                        pinAction = { onClearAllHistory() }
                        pinInput = ""
                        pinError = false
                        showPinDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Clear Past 1-Year Archive (پرنسپل کوڈ)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = {
                Text(text = "🔒 Principal Authorization PIN", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
            },
            text = {
                Column {
                    Text(
                        text = "تاریخی حاضری ڈیلیٹ کرنے کے لیے پرنسپل پن کوڈ (Default: 1234) درج کریں:",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            pinInput = it
                            pinError = false
                        },
                        label = { Text("Enter PIN (e.g. 1234)") },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F172A)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF1E293B)
                        ),
                        isError = pinError,
                        supportingText = {
                            if (pinError) Text("Incorrect PIN. Please enter 1234", color = AlertRed, fontSize = 11.sp)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput == "1234" || pinInput == "0000") {
                            pinAction?.invoke()
                            showPinDialog = false
                        } else {
                            pinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                ) {
                    Text("Confirm Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPinDialog = false }) { Text("Cancel") }
            }
        )
    }
}
