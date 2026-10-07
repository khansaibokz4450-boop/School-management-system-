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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentEntity
import com.example.ui.components.ActiveClassBanner
import com.example.ui.theme.PrimaryGreen
import com.example.ui.viewmodel.SchoolViewModel
import com.example.util.WhatsAppHelper

// Comprehensive Science, Arts & Humanities Subjects for Pakistan Educational Institutions
val COMPREHENSIVE_SUBJECTS = listOf(
    "Biology" to "بیالوجی",
    "Physics" to "فزکس",
    "Chemistry" to "کیمسٹری",
    "Computer Science" to "کمپیوٹر سائنس",
    "Mathematics" to "ریاضی (میتھس)",
    "English" to "انگریزی",
    "Urdu" to "اردو",
    "Pashto" to "پشتو",
    "Islamiat" to "اسلامیات",
    "Mutalia Quran" to "مطالعہ قرآن (ترجمۃ القرآن)",
    "Pakistan Studies" to "مطالعہ پاکستان (پی ایس)",
    "General Science" to "جنرل سائنس",
    "General Math" to "جنرل ریاضی",
    "Civics" to "شہریت (سوکس)",
    "Economics" to "معاشیات (اکنامکس)",
    "History & Geo" to "تاریخ و جغرافیہ",
    "Arabic" to "عربی"
)

data class StudentExamReport(
    val marksBySubject: Map<String, Int>
) {
    val total: Int get() = marksBySubject.values.sum()
    val maxTotal: Int get() = (marksBySubject.size * 100).coerceAtLeast(100)
    val percentage: Int get() = if (maxTotal > 0) (total * 100) / maxTotal else 0
    val grade: String get() = when {
        percentage >= 80 -> "A+"
        percentage >= 70 -> "A"
        percentage >= 60 -> "B"
        percentage >= 50 -> "C"
        percentage >= 40 -> "D"
        else -> "F"
    }
}

@Composable
fun ExamManagementScreen(
    viewModel: SchoolViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val bannerMetrics by viewModel.bannerMetrics.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val studentsWithAttendance by viewModel.currentClassStudentsWithAttendance.collectAsState()

    var selectedExamTerm by remember { mutableStateOf("Midterm Exam 2026") }
    val examTerms = listOf("Monthly Test", "Midterm Exam 2026", "Final Term 2026")

    // Marks state map: Student ID -> Map of (SubjectName -> Marks)
    val reportsMap = remember {
        mutableStateMapOf<Long, Map<String, Int>>().apply {
            studentsWithAttendance.forEachIndexed { i, s ->
                val base = 75 + (i % 15)
                put(
                    s.student.id,
                    mapOf(
                        "English" to base,
                        "Urdu" to (base + 3).coerceAtMost(100),
                        "Mathematics" to (base + 8).coerceAtMost(100),
                        "Physics" to (base + 2).coerceAtMost(100),
                        "Chemistry" to (base + 4).coerceAtMost(100),
                        "Biology" to (base + 6).coerceAtMost(100),
                        "Islamiat" to 95,
                        "Mutalia Quran" to 92
                    )
                )
            }
        }
    }

    var editingStudentTarget by remember { mutableStateOf<StudentEntity?>(null) }

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

                Column {
                    Text(
                        text = "Exams & Result Cards (DMC)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    Text(
                        text = "سائنس و آرٹس مضامین (بیالوجی، فزکس، کیمسٹری، کمپیوٹر، پشتو، قرآن) • Class: $selectedClass",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
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

        // Exam Term Selection Chips
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Select Exam Session (امتحان منتخب کریں):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        examTerms.forEach { term ->
                            FilterChip(
                                selected = selectedExamTerm == term,
                                onClick = { selectedExamTerm = term },
                                label = { Text(term, fontSize = 11.sp, fontWeight = if (selectedExamTerm == term) FontWeight.Bold else FontWeight.Medium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFEFF6FF),
                                    selectedLabelColor = Color(0xFF1E40AF),
                                    containerColor = Color.White,
                                    labelColor = Color(0xFF334155)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Students Marks List
        items(studentsWithAttendance, key = { it.student.id }) { item ->
            val studentReport = StudentExamReport(reportsMap[item.student.id] ?: emptyMap())
            StudentExamCard(
                student = item.student,
                examTerm = selectedExamTerm,
                report = studentReport,
                onEditMarks = { editingStudentTarget = item.student },
                onSendWhatsAppDmc = {
                    val sb = StringBuilder()
                    sb.append("🎓 *پاک اسکول اینڈ کالج سسٹم*\n")
                    sb.append("───────────────────\n")
                    sb.append("📊 *امتحانی نتیجہ و رزلٹ کارڈ (Student DMC)*\n\n")
                    sb.append("محترم والدین، السلام علیکم!\n")
                    sb.append("طالب علم *${item.student.name}* کا *${selectedExamTerm}* کا رزلٹ کارڈ درج ذیل ہے:\n\n")
                    sb.append("🔢 *رول نمبر:* ${item.student.rollNo}\n")
                    sb.append("🏫 *کلاس:* ${item.student.className}\n")
                    sb.append("📝 *امتحان:* $selectedExamTerm\n\n")
                    sb.append("📚 *حاصل کردہ نمبرات (منتخب مضامین):*\n")

                    studentReport.marksBySubject.forEach { (sub, marks) ->
                        val urdu = COMPREHENSIVE_SUBJECTS.find { it.first == sub }?.second ?: sub
                        sb.append("• $sub ($urdu): $marks / 100\n")
                    }

                    sb.append("\n📈 *کل نمبر:* ${studentReport.total} / ${studentReport.maxTotal} (${studentReport.percentage}%)\n")
                    sb.append("🏆 *گریڈ:* ${studentReport.grade} (${if (studentReport.percentage >= 40) "کامیاب (PASSED)" else "ناکام (FAILED)"})\n")
                    sb.append("───────────────────\n")
                    sb.append("پرنسپل و امتحان برانچ | پاک اسکول\n")

                    WhatsAppHelper.sendWhatsAppMessage(context, item.student.parentPhone, sb.toString())
                    Toast.makeText(context, "Opening WhatsApp with DMC for ${item.student.name}...", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // Edit Marks & Subject Selection Dialog
    editingStudentTarget?.let { student ->
        val currentMarks = reportsMap[student.id] ?: mapOf(
            "Biology" to 85, "Physics" to 80, "Chemistry" to 82, "Mathematics" to 90,
            "English" to 80, "Urdu" to 85, "Islamiat" to 95, "Mutalia Quran" to 90
        )
        EditCustomMarksDialog(
            student = student,
            initialMarks = currentMarks,
            onDismiss = { editingStudentTarget = null },
            onSave = { updatedMap ->
                reportsMap[student.id] = updatedMap
                editingStudentTarget = null
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudentExamCard(
    student: StudentEntity,
    examTerm: String,
    report: StudentExamReport,
    onEditMarks: () -> Unit,
    onSendWhatsAppDmc: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = student.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                    Text(text = "Roll: ${student.rollNo} • $examTerm (${report.marksBySubject.size} Subjects)", fontSize = 11.sp, color = Color(0xFF64748B))
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (report.percentage >= 60) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "Grade: ${report.grade} (${report.percentage}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (report.percentage >= 60) Color(0xFF166534) else Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Marks FlowRow preview
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                report.marksBySubject.forEach { (subject, marks) ->
                    val shortName = subject.take(6)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = "$shortName: $marks",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ${report.total} / ${report.maxTotal}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onEditMarks,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFF1E40AF))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مضامین و نمبرات", fontSize = 11.sp, color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onSendWhatsAppDmc,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("📲 واٹس ایپ رزلٹ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditCustomMarksDialog(
    student: StudentEntity,
    initialMarks: Map<String, Int>,
    onDismiss: () -> Unit,
    onSave: (Map<String, Int>) -> Unit
) {
    // Current subject inputs map
    val marksInputMap = remember {
        mutableStateMapOf<String, String>().apply {
            initialMarks.forEach { (sub, mark) ->
                put(sub, mark.toString())
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Select Subjects & Enter Marks", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text("${student.name} • ${student.className}", fontSize = 12.sp, color = Color(0xFF64748B))
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "1. CHOOSE ENROLLED SUBJECTS (مضامین کا انتخاب کریں):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    Text(
                        text = "جو سبجیکٹ سٹوڈنٹ پڑھتا ہے صرف وہ منتخب کریں۔ باقی چھوڑ دیں۔",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Preset Quick Groups
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFDCFCE7),
                            modifier = Modifier.clickable {
                                listOf("Biology", "Physics", "Chemistry", "English", "Urdu", "Islamiat", "Mutalia Quran").forEach {
                                    if (!marksInputMap.containsKey(it)) marksInputMap[it] = "80"
                                }
                            }
                        ) {
                            Text("🔬 Pre-Medical", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEFF6FF),
                            modifier = Modifier.clickable {
                                listOf("Computer Science", "Physics", "Mathematics", "English", "Urdu", "Mutalia Quran").forEach {
                                    if (!marksInputMap.containsKey(it)) marksInputMap[it] = "80"
                                }
                            }
                        ) {
                            Text("💻 Computer / ICS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF3C7),
                            modifier = Modifier.clickable {
                                listOf("Civics", "Economics", "Pashto", "General Math", "Urdu", "Islamiat").forEach {
                                    if (!marksInputMap.containsKey(it)) marksInputMap[it] = "80"
                                }
                            }
                        ) {
                            Text("🎨 Arts", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Chips to toggle individual subjects on/off
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        COMPREHENSIVE_SUBJECTS.forEach { (eng, urdu) ->
                            val isSelected = marksInputMap.containsKey(eng)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        marksInputMap.remove(eng)
                                    } else {
                                        marksInputMap[eng] = "80"
                                    }
                                },
                                label = { Text("$eng ($urdu)", fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFEFF6FF),
                                    selectedLabelColor = Color(0xFF1E40AF),
                                    containerColor = Color.White,
                                    labelColor = Color(0xFF334155)
                                )
                            )
                        }
                    }
                }

                item {
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "2. ENTER MARKS (نمبر درج کریں - Out of 100):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                }

                items(marksInputMap.keys.toList()) { sub ->
                    val urdu = COMPREHENSIVE_SUBJECTS.find { it.first == sub }?.second ?: ""
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = sub, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                            Text(text = urdu, fontSize = 11.sp, color = Color(0xFF64748B))
                        }

                        OutlinedTextField(
                            value = marksInputMap[sub] ?: "",
                            onValueChange = { marksInputMap[sub] = it },
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F172A), fontSize = 13.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF1E293B),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = PrimaryGreen,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            ),
                            singleLine = true,
                            modifier = Modifier.width(90.dp).height(50.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val result = mutableMapOf<String, Int>()
                    marksInputMap.forEach { (sub, str) ->
                        val mark = str.toIntOrNull()?.coerceIn(0, 100) ?: 70
                        result[sub] = mark
                    }
                    onSave(result)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Save DMC", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
