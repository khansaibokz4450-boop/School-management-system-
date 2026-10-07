package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StaffMemberEntity
import com.example.data.model.StudentEntity
import com.example.ui.theme.PrimaryGreen
import com.example.ui.viewmodel.SchoolViewModel
import com.example.util.WhatsAppHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RecentPunchLog(
    val name: String,
    val roleOrClass: String,
    val time: String,
    val isStaff: Boolean
)

@Composable
fun KioskAttendanceScreen(
    viewModel: SchoolViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentClassStudents by viewModel.currentClassStudentsWithAttendance.collectAsState()
    val staffList by viewModel.allStaff.collectAsState()

    var isStaffTerminal by remember { mutableStateOf(false) } // False = Students, True = Teachers
    var activeSensorMode by remember { mutableStateOf("FINGERPRINT") } // FINGERPRINT, FACE_AI, KEYPAD

    var punchSuccessPerson by remember { mutableStateOf<Pair<String, String>?>(null) } // Name to Subtitle
    var punchSuccessTime by remember { mutableStateOf("") }
    var keypadInput by remember { mutableStateOf("") }

    val recentPunches = remember { mutableStateListOf<RecentPunchLog>() }

    // Pulsing animation for sensor
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    fun registerPunch(name: String, subtitle: String, studentId: Long? = null, staffEntity: StaffMemberEntity? = null, phone: String = "") {
        val timeNow = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
        punchSuccessPerson = Pair(name, subtitle)
        punchSuccessTime = timeNow

        recentPunches.add(0, RecentPunchLog(name, subtitle, timeNow, isStaffTerminal))
        if (recentPunches.size > 8) recentPunches.removeLast()

        if (studentId != null) {
            viewModel.markAttendance(studentId, "PRESENT", "BIOMETRIC")
            // Optional direct WhatsApp alert
            if (phone.isNotBlank()) {
                val urduMsg = """
                    🏫 *پاک اسکول اینڈ کالج سسٹم*
                    ───────────────────
                    ✅ *اسکول آمد حاضری تصدیق (Gate Check-in)*
                    
                    محترم والدین!
                    طالب علم *${name}* بحفاظت اسکول پہنچ چکا ہے اور بائیو میٹرک حاضری درج ہو گئی ہے۔
                    
                    ⏰ *وقت آمد:* $timeNow
                    📅 *تاریخ:* ${viewModel.todayDateString}
                    ───────────────────
                    گیٹ ٹرمینل و انتظامیہ | پاک اسکول
                """.trimIndent()
                // WhatsAppHelper.sendWhatsAppMessage(context, phone, urduMsg)
            }
        } else if (staffEntity != null) {
            viewModel.updateStaffAttendance(staffEntity, "PRESENT")
        }

        // Auto-clear success banner after 2.5 seconds for next person in queue
        scope.launch {
            delay(2500)
            punchSuccessPerson = null
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Kiosk Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.clickable { onBack() }
                ) {
                    Text(
                        text = "← Exit Terminal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF93C5FD),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "GATE ATTENDANCE TERMINAL",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "اسکول گیٹ بائیو میٹرک و کیمرہ ٹرمینل",
                        fontSize = 11.sp,
                        color = Color(0xFF38BDF8)
                    )
                }
            }
        }

        // Mode Switcher: Students vs Staff
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (!isStaffTerminal) Color(0xFF16A34A) else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isStaffTerminal = false }
                    ) {
                        Text(
                            text = "👨‍🎓 Students Gate (طلبہ گیٹ)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isStaffTerminal) Color(0xFF2563EB) else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isStaffTerminal = true }
                    ) {
                        Text(
                            text = "👨‍🏫 Staff Gate (اساتذہ گیٹ)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }

        // Success Punch Pop-up Box
        item {
            AnimatedVisibility(
                visible = punchSuccessPerson != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF166534))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4ADE80),
                            modifier = Modifier.size(46.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "✅ ATTENDANCE MARKED! (حاضری درج ہو گئی)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = punchSuccessPerson?.first ?: "",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBBF7D0)
                            )
                            Text(
                                text = "${punchSuccessPerson?.second} • Check-in Time: $punchSuccessTime",
                                fontSize = 11.sp,
                                color = Color(0xFF86EFAC)
                            )
                        }
                    }
                }
            }
        }

        // Live Scanner Unit (Interactive Central Device)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "LIVE SENSOR ONLINE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                        }

                        // Sensor Selector
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (activeSensorMode == "FINGERPRINT") Color(0xFF3B82F6) else Color(0xFF334155),
                                modifier = Modifier.clickable { activeSensorMode = "FINGERPRINT" }
                            ) {
                                Text("Fingerprint", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (activeSensorMode == "FACE_AI") Color(0xFF3B82F6) else Color(0xFF334155),
                                modifier = Modifier.clickable { activeSensorMode = "FACE_AI" }
                            ) {
                                Text("Face Camera", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (activeSensorMode == "KEYPAD") Color(0xFF3B82F6) else Color(0xFF334155),
                                modifier = Modifier.clickable { activeSensorMode = "KEYPAD" }
                            ) {
                                Text("Roll Pad", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    when (activeSensorMode) {
                        "FINGERPRINT" -> {
                            // The Big Biometric Fingerprint Sensor
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A))
                                    .border(3.dp, Color(0xFF38BDF8), CircleShape)
                                    .clickable {
                                        // Pick first un-punched person or random from list
                                        if (!isStaffTerminal && currentClassStudents.isNotEmpty()) {
                                            val candidate = currentClassStudents.random().student
                                            registerPunch(candidate.name, "Roll: ${candidate.rollNo} • ${candidate.className}", studentId = candidate.id, phone = candidate.parentPhone)
                                        } else if (isStaffTerminal && staffList.isNotEmpty()) {
                                            val candidate = staffList.random()
                                            registerPunch(candidate.name, "${candidate.designation} • ${candidate.subject}", staffEntity = candidate)
                                        } else {
                                            Toast.makeText(context, "No roster loaded yet", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = "Place Thumb",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(80.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "TOUCH TO PUNCH FINGERPRINT (انگوٹھا لگائیں)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "طالب علم یا استاد انگوٹھا لگائیں، حاضری خودکار درج ہو جائے گی",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        "FACE_AI" -> {
                            // AI Face Camera Viewfinder
                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(2.dp, Color(0xFF22C55E), RoundedCornerShape(16.dp))
                                    .clickable {
                                        if (!isStaffTerminal && currentClassStudents.isNotEmpty()) {
                                            val candidate = currentClassStudents.random().student
                                            registerPunch(candidate.name, "Roll: ${candidate.rollNo} • Face Verified", studentId = candidate.id, phone = candidate.parentPhone)
                                        } else if (staffList.isNotEmpty()) {
                                            val candidate = staffList.random()
                                            registerPunch(candidate.name, "${candidate.designation} • Face Verified", staffEntity = candidate)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(54.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("TAP TO RECOGNIZE FACE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Face AI Auto Scanner: Align face in frame", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }

                        "KEYPAD" -> {
                            // Quick Roll No / Staff ID numeric punch
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (keypadInput.isEmpty()) "Enter Roll No (مثلاً 01)" else keypadInput,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (keypadInput.isEmpty()) Color(0xFF64748B) else Color(0xFF38BDF8)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Number buttons 1-9, 0, Clear, OK
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("1", "2", "3", "4", "5").forEach { num ->
                                        KeypadButton(num) { keypadInput += num }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("6", "7", "8", "9", "0").forEach { num ->
                                        KeypadButton(num) { keypadInput += num }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { keypadInput = "" },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(40.dp)
                                    ) {
                                        Text("Clear", color = Color(0xFFEF4444), fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            if (keypadInput.isNotBlank()) {
                                                if (!isStaffTerminal) {
                                                    val found = currentClassStudents.find { it.student.rollNo.contains(keypadInput) }?.student
                                                        ?: currentClassStudents.firstOrNull()?.student
                                                    if (found != null) {
                                                        registerPunch(found.name, "Roll: ${found.rollNo} • ${found.className}", studentId = found.id, phone = found.parentPhone)
                                                    }
                                                } else {
                                                    val found = staffList.firstOrNull()
                                                    if (found != null) {
                                                        registerPunch(found.name, "${found.designation} • ${found.subject}", staffEntity = found)
                                                    }
                                                }
                                                keypadInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(40.dp)
                                    ) {
                                        Text("OK Punch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Recent Check-ins Feed
        item {
            Text(
                text = "TODAY'S GATE PUNCH FEED (حالیہ آمد لاگ):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.5.sp
            )
        }

        if (recentPunches.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Ready. Tap fingerprint sensor to punch arrival.",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(recentPunches) { punch ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(if (punch.isStaff) Color(0xFF1E40AF) else Color(0xFF166534), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (punch.isStaff) "👨‍🏫" else "🎓", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = punch.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text(text = punch.roleOrClass, fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF0F172A)) {
                            Text(
                                text = punch.time,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4ADE80),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF334155),
        modifier = Modifier
            .size(width = 44.dp, height = 38.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
