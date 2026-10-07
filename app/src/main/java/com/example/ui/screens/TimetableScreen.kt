package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ActiveClassBanner
import com.example.ui.theme.AlertRed
import com.example.ui.theme.PrimaryGreen
import com.example.ui.viewmodel.SchoolViewModel
import com.example.util.WhatsAppHelper

data class PeriodSlot(
    val periodNo: Int,
    val time: String,
    val subject: String,
    val teacher: String,
    val room: String,
    val isBreak: Boolean = false
)

@Composable
fun TimetableScreen(
    viewModel: SchoolViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val bannerMetrics by viewModel.bannerMetrics.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val staffList by viewModel.allStaff.collectAsState()

    var selectedDay by remember { mutableStateOf("Monday") }
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    val schedule = remember {
        mutableStateListOf(
            PeriodSlot(1, "08:00 - 08:45 AM", "English Language (انگریزی)", "Sir Tariq Mehmood", "Room 101"),
            PeriodSlot(2, "08:45 - 09:30 AM", "Mathematics (ریاضی)", "Ma'am Sadia Khan", "Room 101"),
            PeriodSlot(3, "09:30 - 10:15 AM", "Physics / Science (فزکس)", "Sir Imran Ali", "Science Lab"),
            PeriodSlot(4, "10:15 - 11:00 AM", "Chemistry (کیمسٹری)", "Sir Bilal", "Chemistry Lab"),
            PeriodSlot(0, "11:00 - 11:30 AM", "🥗 Lunch & Recess (تفریح و بریک)", "Staff On Duty", "Ground", isBreak = true),
            PeriodSlot(5, "11:30 - 12:15 PM", "Biology / Computer (کمپیوٹر/بیالوجی)", "Ma'am Ayesha", "Lab 2"),
            PeriodSlot(6, "12:15 - 01:00 PM", "Islamiat & Mutalia Quran (اسلامیات/قرآن)", "Qari Saeed Ahmad", "Room 101"),
            PeriodSlot(7, "01:00 - 01:30 PM", "Urdu Literature / Pashto (اردو/پشتو)", "Sir Farooq", "Room 101")
        )
    }

    var editingSlotTarget by remember { mutableStateOf<Pair<Int, PeriodSlot>?>(null) } // Index to Slot
    var showAddSlotDialog by remember { mutableStateOf(false) }
    var slotToDeleteIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Class Timetable & Periods",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    Text(
                        text = "پیریڈ ٹائم، ٹیچر اور مضامین ایڈٹ/ریموو کرنے کا مکمل اختیار • Class: $selectedClass",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Button(
                    onClick = { showAddSlotDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Add Period", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
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

        // Day of the Week Selector
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(days) { day ->
                    FilterChip(
                        selected = selectedDay == day,
                        onClick = { selectedDay = day },
                        label = {
                            Text(
                                day,
                                fontSize = 12.sp,
                                fontWeight = if (selectedDay == day) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1E40AF),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF334155)
                        )
                    )
                }
            }
        }

        // Timetable Slots List
        items(schedule.indices.toList(), key = { it }) { index ->
            val slot = schedule[index]
            if (slot.isBreak) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = slot.subject, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF92400E))
                            Text(text = slot.time, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFB45309))
                        }

                        Row {
                            IconButton(onClick = { editingSlotTarget = Pair(index, slot) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Break", tint = Color(0xFF92400E), modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { slotToDeleteIndex = index }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Break", tint = AlertRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "P${slot.periodNo}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E40AF))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = slot.subject, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(text = "${slot.teacher} • ${slot.room}", fontSize = 11.sp, color = Color(0xFF475569))
                            }
                            Text(text = slot.time, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E40AF))
                        }

                        // Principal Edit & Delete buttons
                        Row {
                            IconButton(onClick = { editingSlotTarget = Pair(index, slot) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Period", tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                            }
                            IconButton(onClick = { slotToDeleteIndex = index }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Period", tint = AlertRed, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        // WhatsApp Share Schedule
        item {
            Button(
                onClick = {
                    val sb = StringBuilder()
                    sb.append("🏫 *پاک اسکول اینڈ کالج سسٹم*\n")
                    sb.append("───────────────────\n")
                    sb.append("⏰ *کلاس ٹائم ٹیبل و شیڈول ($selectedDay)*\n\n")
                    sb.append("🏫 *کلاس:* $selectedClass\n\n")

                    schedule.forEach { p ->
                        if (p.isBreak) {
                            sb.append("• 🥗 ${p.subject} (${p.time})\n")
                        } else {
                            sb.append("• پیریڈ ${p.periodNo} (${p.time}): ${p.subject} (استاد: ${p.teacher} - ${p.room})\n")
                        }
                    }

                    sb.append("\n───────────────────\n")
                    sb.append("ٹائم ٹیبل انچارج و پرنسپل | پاک اسکول\n")

                    WhatsAppHelper.shareSchoolBroadcast(context, "Timetable - $selectedClass", sb.toString())
                    Toast.makeText(context, "Sharing Timetable to WhatsApp...", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "📲 واٹس ایپ پر ٹائم ٹیبل شیئر کریں", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }

    // Edit Period Dialog
    editingSlotTarget?.let { (idx, slot) ->
        PeriodEditDialog(
            title = "Edit Period ${slot.periodNo} (پیریڈ تبدیل کریں)",
            initialSlot = slot,
            teacherNames = staffList.map { it.name },
            onDismiss = { editingSlotTarget = null },
            onSave = { updated ->
                schedule[idx] = updated
                editingSlotTarget = null
            }
        )
    }

    // Add Period Dialog
    if (showAddSlotDialog) {
        PeriodEditDialog(
            title = "Add New Period Slot (نیا پیریڈ شامل کریں)",
            initialSlot = PeriodSlot(
                periodNo = schedule.size + 1,
                time = "01:30 - 02:15 PM",
                subject = "Chemistry / Computer (کیمسٹری)",
                teacher = staffList.firstOrNull()?.name ?: "Sir Bilal",
                room = "Room 101"
            ),
            teacherNames = staffList.map { it.name },
            onDismiss = { showAddSlotDialog = false },
            onSave = { newSlot ->
                schedule.add(newSlot)
                showAddSlotDialog = false
            }
        )
    }

    // Confirm Delete Dialog
    slotToDeleteIndex?.let { delIdx ->
        AlertDialog(
            onDismissRequest = { slotToDeleteIndex = null },
            title = { Text("Remove Period?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A)) },
            text = { Text("کیا آپ واقعی یہ پیریڈ ٹائم ٹیبل سے ہٹانا چاہتے ہیں؟", fontSize = 13.sp, color = Color(0xFF475569)) },
            confirmButton = {
                Button(
                    onClick = {
                        if (delIdx in schedule.indices) {
                            schedule.removeAt(delIdx)
                        }
                        slotToDeleteIndex = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                ) {
                    Text("Delete (ہٹائیں)", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { slotToDeleteIndex = null }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PeriodEditDialog(
    title: String,
    initialSlot: PeriodSlot,
    teacherNames: List<String>,
    onDismiss: () -> Unit,
    onSave: (PeriodSlot) -> Unit
) {
    var periodNoStr by remember { mutableStateOf(initialSlot.periodNo.toString()) }
    var time by remember { mutableStateOf(initialSlot.time) }
    var subject by remember { mutableStateOf(initialSlot.subject) }
    var teacher by remember { mutableStateOf(initialSlot.teacher) }
    var room by remember { mutableStateOf(initialSlot.room) }
    var isBreak by remember { mutableStateOf(initialSlot.isBreak) }

    val presetSubjects = listOf(
        "Chemistry (کیمسٹری)",
        "Physics (فزکس)",
        "Biology (بیالوجی)",
        "Computer Science (کمپیوٹر)",
        "Mathematics (ریاضی)",
        "English (انگریزی)",
        "Urdu (اردو)",
        "Pashto (پشتو)",
        "Islamiat (اسلامیات)",
        "Mutalia Quran (مطالعہ قرآن)",
        "Pakistan Studies (مطالعہ پاکستان)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A)) },
        text = {
            LazyColumn(
                modifier = Modifier.height(380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = periodNoStr,
                        onValueChange = { periodNoStr = it },
                        label = { Text("Period Number (پیریڈ نمبر)") },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F172A)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF1E293B),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Timing (وقت - مثلاً 08:00 - 08:45 AM)") },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F172A)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF1E293B),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text(
                        text = "Subject (مضمون منتخب کریں یا لکھیں):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject Name") },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F172A)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF1E293B),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        presetSubjects.take(6).forEach { sub ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFEFF6FF),
                                modifier = Modifier.clickable { subject = sub }
                            ) {
                                Text(sub, fontSize = 10.sp, color = Color(0xFF1E40AF), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Assigned Teacher (مقررہ استاد):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = teacher,
                        onValueChange = { teacher = it },
                        label = { Text("Teacher Name") },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F172A)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF1E293B),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (teacherNames.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            teacherNames.take(5).forEach { tName ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7),
                                    modifier = Modifier.clickable { teacher = tName }
                                ) {
                                    Text(tName, fontSize = 10.sp, color = Color(0xFF166534), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        label = { Text("Room / Lab (کمرہ یا لیب)") },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F172A)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF1E293B),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isBreak, onCheckedChange = { isBreak = it })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Is this Recess / Lunch Break? (تفریح/بریک)", fontSize = 12.sp, color = Color(0xFF475569))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pNo = periodNoStr.toIntOrNull() ?: 1
                    onSave(PeriodSlot(pNo, time, subject, teacher, room, isBreak))
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
