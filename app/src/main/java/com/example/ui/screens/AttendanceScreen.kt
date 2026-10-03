package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun AttendanceScreen(
    viewModel: SchoolViewModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val bannerMetrics by viewModel.bannerMetrics.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val studentsWithAttendance by viewModel.currentClassStudentsWithAttendance.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var statusFilter by remember { mutableStateOf("ALL") }
    var biometricStudentTarget by remember { mutableStateOf<StudentEntity?>(null) }

    val filteredList = studentsWithAttendance.filter { item ->
        when (statusFilter) {
            "PRESENT" -> item.todayAttendance?.status == "PRESENT"
            "ABSENT" -> item.todayAttendance?.status == "ABSENT"
            "LATE" -> item.todayAttendance?.status == "LATE"
            "LEAVE" -> item.todayAttendance?.status == "LEAVE"
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Screen Header with Back Button & Title
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
                            text = "Attendance",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                        Text(
                            text = "Daily Roll Call • Class: $selectedClass",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Button(
                    onClick = { viewModel.markAllPresent() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("mark_all_present_btn")
                ) {
                    Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("All Present", fontSize = 11.sp)
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

        // Search & Status Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search by student name or roll #...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("attendance_search_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "ALL" to "All (${studentsWithAttendance.size})",
                        "PRESENT" to "P (${bannerMetrics.presentCount})",
                        "ABSENT" to "A (${bannerMetrics.absentCount})",
                        "LATE" to "L (${bannerMetrics.lateCount})",
                        "LEAVE" to "Lv (${bannerMetrics.leaveCount})"
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = statusFilter == key,
                            onClick = { statusFilter = key },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen.copy(alpha = 0.15f),
                                selectedLabelColor = PrimaryGreen
                            )
                        )
                    }
                }
            }
        }

        // Student Attendance Cards
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No students found matching this criteria.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(filteredList, key = { it.student.id }) { item ->
                AttendanceStudentCard(
                    item = item,
                    onMarkStatus = { status ->
                        viewModel.markAttendance(item.student.id, status, "MANUAL")
                    },
                    onTriggerBiometric = {
                        biometricStudentTarget = item.student
                    },
                    onSendWhatsAppAbsent = {
                        val msg = WhatsAppHelper.buildAbsenteeMessage(
                            item.student.name,
                            item.student.className,
                            viewModel.todayDateString
                        )
                        WhatsAppHelper.sendWhatsAppMessage(context, item.student.parentPhone, msg)
                    }
                )
            }
        }
    }

    biometricStudentTarget?.let { student ->
        BiometricScanDialog(
            student = student,
            onDismiss = { biometricStudentTarget = null },
            onSuccess = {
                viewModel.markAttendance(student.id, "PRESENT", "BIOMETRIC")
                biometricStudentTarget = null
            }
        )
    }
}

@Composable
private fun AttendanceStudentCard(
    item: StudentWithAttendance,
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            fontSize = 14.sp
                        )
                        Text(
                            text = "S/O: ${item.student.fatherName} • ${item.student.className} (${item.student.section})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Biometric shortcut button
                IconButton(
                    onClick = onTriggerBiometric,
                    modifier = Modifier.testTag("biometric_btn_${item.student.rollNo}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometric Verify",
                        tint = if (item.todayAttendance?.method == "BIOMETRIC") Color(0xFF2E7D32) else PrimaryGreen,
                        modifier = Modifier.size(24.dp)
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

            // WhatsApp Quick Alert if Marked Absent
            if (currentStatus == "ABSENT") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF3F3))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Marked Absent today. Send alert?",
                        fontSize = 11.sp,
                        color = AlertRed,
                        fontWeight = FontWeight.Medium
                    )

                    Button(
                        onClick = onSendWhatsAppAbsent,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 10.sp)
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
        color = if (isSelected) activeColor else Color.LightGray.copy(alpha = 0.2f),
        modifier = modifier
            .height(32.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color.DarkGray
            )
        }
    }
}
