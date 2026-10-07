package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.SchoolViewModel

// Colors matching the clean design
private val HeaderBlue = Color(0xFF1E40AF)
private val ChipActiveBlue = Color(0xFF1E40AF)
private val ChipInactiveBg = Color(0xFFF1F5F9)
private val ChipInactiveBorder = Color(0xFFCBD5E1)
private val BannerBg = Color(0xFFEFF6FF)
private val BannerBorder = Color(0xFFBFDBFE)

// Card accent colors
private val AttendanceBlue = Color(0xFF2563EB)
private val FeeGreen = Color(0xFF16A34A)
private val TeacherAmber = Color(0xFFF59E0B)
private val MessagePink = Color(0xFFEC4899)
private val PendingAmber = Color(0xFFD97706)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: SchoolViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val context = LocalContext.current
    val bannerMetrics by viewModel.bannerMetrics.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    var showRegistrationDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showTeacherRegistrationDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

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

    if (showTeacherRegistrationDialog) {
        TeacherRegistrationDialog(
            onDismiss = { showTeacherRegistrationDialog = false },
            onTeacherRegistered = { name, desig, qual, phone, subj, sal ->
                viewModel.addStaffMember(name, desig, qual, phone, subj, sal)
                showTeacherRegistrationDialog = false
            }
        )
    }

    val classesList = listOf("KG", "Nursery", "Prep") +
            (1..10).map { "Class $it" } +
            listOf("1st Year", "2nd Year")

    val pendingFeeAmount = (bannerMetrics.totalExpectedFee - bannerMetrics.totalCollectedFee).coerceAtLeast(0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TOP HEADER BANNER
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderBlue)
                    .padding(vertical = 24.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "School Management System",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cross-Platform Control Panel & Management Portal",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFDBEAFE),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 2. "Select Target Class:" SECTION (Placed at top as requested)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Target Class:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFDCFCE7),
                                modifier = Modifier
                                    .clickable { showRegistrationDialog = true }
                                    .testTag("btn_new_admission")
                            ) {
                                Text(
                                    text = "🎒 + Student",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEF3C7),
                                modifier = Modifier
                                    .clickable { showTeacherRegistrationDialog = true }
                                    .testTag("btn_new_teacher")
                            ) {
                                Text(
                                    text = "👨‍🏫 + Teacher",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // FlowRow grid of class chips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        classesList.forEach { cls ->
                            val isSelected = cls.equals(selectedClass, ignoreCase = true)
                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) ChipActiveBlue else ChipInactiveBg,
                                label = "chipBg"
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) Color.White else Color(0xFF334155),
                                label = "chipText"
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bgColor)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) ChipActiveBlue else ChipInactiveBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setSelectedClass(cls) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("target_class_$cls"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cls,
                                    color = textColor,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. "Currently Viewing Database For: [Class]" BANNER
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BannerBg)
                    .border(1.dp, BannerBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("current_viewing_banner")
            ) {
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Currently Viewing Database For: ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E40AF)
                        )
                        Text(
                            text = selectedClass,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E3A8A)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = "${bannerMetrics.totalStudents} Students",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 4. EXECUTIVE SUMMARY METRICS (Moved down below target class selector as requested!)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXECUTIVE SUMMARY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Real-time Metrics: $selectedClass",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = HeaderBlue
                    )
                }

                // Card 1: Total Students (Material 3 Elevated Card)
                SummaryMetricCard(
                    title = "Total Students",
                    value = "${bannerMetrics.totalStudents}",
                    subtitle = "Active Enrollment in $selectedClass",
                    badgeText = "Enrolled",
                    badgeColor = Color(0xFF1E40AF),
                    icon = Icons.Default.School,
                    accentColor = Color(0xFF1E40AF),
                    containerColor = Color(0xFFF0F7FF),
                    testTag = "summary_card_students",
                    onClick = { /* View roster */ }
                )

                // Card 2: Daily Attendance Percentage (Material 3 Elevated Card)
                SummaryProgressMetricCard(
                    title = "Daily Attendance",
                    value = "${bannerMetrics.attendancePercentage}%",
                    subtitle = "${bannerMetrics.presentCount} Present • ${bannerMetrics.absentCount} Absent",
                    progress = if (bannerMetrics.totalStudents > 0) bannerMetrics.attendancePercentage / 100f else 0f,
                    badgeText = if (bannerMetrics.attendancePercentage >= 80) "Optimal" else "Needs Attention",
                    badgeColor = if (bannerMetrics.attendancePercentage >= 80) Color(0xFF16A34A) else Color(0xFFDC2626),
                    icon = Icons.Default.CheckCircle,
                    accentColor = Color(0xFF16A34A),
                    containerColor = Color(0xFFF0FDF4),
                    testTag = "summary_card_attendance",
                    onClick = { onNavigateToTab(1) }
                )

                // Card 3: Pending Fee Collections (Material 3 Elevated Card)
                SummaryProgressMetricCard(
                    title = "Pending Fee Collections",
                    value = "Rs. $pendingFeeAmount",
                    subtitle = "Recovery: ${bannerMetrics.feeCollectionPercentage}% Collected (Rs. ${bannerMetrics.totalCollectedFee})",
                    progress = if (bannerMetrics.totalExpectedFee > 0) bannerMetrics.feeCollectionPercentage / 100f else 0f,
                    badgeText = if (pendingFeeAmount == 0) "All Clear" else "Outstanding",
                    badgeColor = if (pendingFeeAmount == 0) Color(0xFF16A34A) else PendingAmber,
                    icon = Icons.Default.PendingActions,
                    accentColor = PendingAmber,
                    containerColor = Color(0xFFFFFBEB),
                    testTag = "summary_card_pending_fees",
                    onClick = { onNavigateToTab(2) }
                )
            }
        }

        // 5. THE FOUR CORE FEATURE MODULE CARDS
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // CARD 0: Instant Biometric & Camera Gate Terminal
                SimpleFeatureCard(
                    title = "Live Biometric & Camera Machine",
                    subtitle = "Instant Thumb & Face AI Gate Attendance (بغیر نام ڈھونڈے فوری حاضری)",
                    icon = Icons.Default.VerifiedUser,
                    accentColor = Color(0xFF0284C7),
                    badgeText = "⚡ فنگر پرنٹ و کیمرہ مشین",
                    onClick = { onNavigateToTab(12) },
                    testTag = "card_biometric_terminal"
                )

                // CARD 1: Attendance
                SimpleFeatureCard(
                    title = "Attendance",
                    subtitle = "Live Face Recognition & Attendance Logs",
                    icon = Icons.Default.AssignmentTurnedIn,
                    accentColor = AttendanceBlue,
                    badgeText = "${bannerMetrics.presentCount}/${bannerMetrics.totalStudents} Present",
                    onClick = { onNavigateToTab(1) },
                    testTag = "card_attendance"
                )

                // CARD 2: Fee Management
                SimpleFeatureCard(
                    title = "Fee Management",
                    subtitle = "Fee Collection, Receipts & Ledger Records",
                    icon = Icons.Default.Paid,
                    accentColor = FeeGreen,
                    badgeText = "Rs. ${bannerMetrics.totalCollectedFee / 1000}k Collected",
                    onClick = { onNavigateToTab(2) },
                    testTag = "card_fees"
                )

                // CARD 3: Teacher Records
                SimpleFeatureCard(
                    title = "Teacher Records",
                    subtitle = "Staff Profiles, Attendance & Salary Logs",
                    icon = Icons.Default.School,
                    accentColor = TeacherAmber,
                    badgeText = "Faculty Directory",
                    onClick = { onNavigateToTab(3) },
                    testTag = "card_teachers"
                )

                // CARD 4: WhatsApp & SMS
                SimpleFeatureCard(
                    title = "WhatsApp & SMS",
                    subtitle = "Send Instant Alerts, Absentee Reports & Notices",
                    icon = Icons.Default.Chat,
                    accentColor = MessagePink,
                    badgeText = "Broadcast Alerts",
                    onClick = { onNavigateToTab(4) },
                    testTag = "card_broadcast"
                )
            }
        }

        // 5b. OPTIONAL ACADEMIC & ADVANCED SCHOOL MODULES (6 NEW FEATURES)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ACADEMIC & SMART MODULES (اختیاری جدید اسکول فیچرز)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )

                // 1. Exams & Result Cards
                SimpleFeatureCard(
                    title = "Exams & DMC Result Cards",
                    subtitle = "Student Marks, Percentage, Grading & WhatsApp DMC",
                    icon = Icons.Default.Assessment,
                    accentColor = Color(0xFF2563EB),
                    badgeText = "امتحانات و ڈی ایم سی",
                    onClick = { onNavigateToTab(5) },
                    testTag = "card_exams"
                )

                // 2. Daily Diary & Homework
                SimpleFeatureCard(
                    title = "Daily Homework Diary",
                    subtitle = "Subject Homework & Direct WhatsApp Broadcast",
                    icon = Icons.Default.Book,
                    accentColor = Color(0xFF0D9488),
                    badgeText = "روزانہ ڈائری",
                    onClick = { onNavigateToTab(6) },
                    testTag = "card_homework"
                )

                // 3. Student ID Cards
                SimpleFeatureCard(
                    title = "Student ID Card Maker",
                    subtitle = "School Badges with QR Code & Principal Stamp",
                    icon = Icons.Default.Badge,
                    accentColor = Color(0xFF7C3AED),
                    badgeText = "شناختی کارڈ",
                    onClick = { onNavigateToTab(7) },
                    testTag = "card_id_card"
                )

                // 4. Timetable
                SimpleFeatureCard(
                    title = "Class Timetable & Schedule",
                    subtitle = "Daily 7-Period Bell Timings, Teachers & Subjects",
                    icon = Icons.Default.Schedule,
                    accentColor = Color(0xFFEA580C),
                    badgeText = "ٹائم ٹیبل",
                    onClick = { onNavigateToTab(8) },
                    testTag = "card_timetable"
                )

                // 5. Transport / Van
                SimpleFeatureCard(
                    title = "Transport & School Van",
                    subtitle = "Van Routes, Driver Contacts & Pick & Drop Logs",
                    icon = Icons.Default.DirectionsBus,
                    accentColor = Color(0xFFD97706),
                    badgeText = "وین ٹرانسپورٹ",
                    onClick = { onNavigateToTab(9) },
                    testTag = "card_transport"
                )

                // 6. Backup & Excel Export
                SimpleFeatureCard(
                    title = "Backup & Excel Export",
                    subtitle = "Export Students, Fees & Attendance to CSV/Excel",
                    icon = Icons.Default.FileDownload,
                    accentColor = Color(0xFF16A34A),
                    badgeText = "ایکسل ایکسپورٹ",
                    onClick = { onNavigateToTab(10) },
                    testTag = "card_export"
                )
            }
        }

        // 6. SYSTEM ADMINISTRATOR & DEVELOPER INFO CARD (Requested info)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("developer_info_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SYSTEM & DEVELOPER INFO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = "Verified Admin",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HeaderBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E40AF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AO",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Ahmad ullah okz",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Pakistan",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:khansaibokz4450@gmail.com")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = Color(0xFF1E40AF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "khansaibokz4450@gmail.com",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E40AF)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Material 3 Summary Metric Card for Total Students
 */
@Composable
private fun SummaryMetricCard(
    title: String,
    value: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(containerColor)
                    .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = badgeColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = value,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

/**
 * Material 3 Summary Metric Card with Progress Bar for Attendance & Fees
 */
@Composable
private fun SummaryProgressMetricCard(
    title: String,
    value: String,
    subtitle: String,
    progress: Float,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(containerColor)
                        .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = badgeColor.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = value,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Material 3 Linear Progress Indicator
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = accentColor,
                trackColor = accentColor.copy(alpha = 0.15f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Details",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

/**
 * Feature card with big icon, colored left bar, and subtitles.
 */
@Composable
private fun SimpleFeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    badgeText: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor.copy(alpha = 0.12f))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = accentColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = badgeText,
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Open $title",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
