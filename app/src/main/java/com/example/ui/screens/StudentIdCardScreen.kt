package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentEntity
import com.example.ui.components.ActiveClassBanner
import com.example.ui.viewmodel.SchoolViewModel
import com.example.util.WhatsAppHelper

@Composable
fun StudentIdCardScreen(
    viewModel: SchoolViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val bannerMetrics by viewModel.bannerMetrics.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val studentsWithAttendance by viewModel.currentClassStudentsWithAttendance.collectAsState()

    var activeCardStudent by remember {
        mutableStateOf<StudentEntity?>(studentsWithAttendance.firstOrNull()?.student)
    }

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
                        text = "Student ID Card Maker",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    Text(
                        text = "طالب علم اسکول شناختی کارڈ • Class: $selectedClass",
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

        // Student Card Preview (The Physical Badge)
        item {
            val s = activeCardStudent ?: studentsWithAttendance.firstOrNull()?.student
            if (s != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column {
                        // Card Header (Royal Blue & Gold trim)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E3A8A))
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "PAK SCHOOL & COLLEGE SYSTEM",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "STUDENT IDENTITY CARD • SESSION 2026-27",
                                    fontSize = 10.sp,
                                    color = Color(0xFFFDE047),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Card Body
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFF6FF))
                                        .border(2.dp, Color(0xFF16A34A), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🎓", fontSize = 36.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Blood: B+",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = s.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "S/O: ${s.fatherName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF475569)
                                )
                                Text(
                                    text = "Roll No: ${s.rollNo} • Class: ${s.className}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E40AF)
                                )
                                Text(
                                    text = "Emergency: ${s.parentPhone}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            // QR Code
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "QR Code",
                                    tint = Color(0xFF1E293B),
                                    modifier = Modifier.size(54.dp)
                                )
                                Text(text = "VERIFIED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        // Card Footer: Stamp and Principal Signature
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Campus: Main Campus, Pakistan", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text(
                                text = "Principal Signature ✍️",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons for this card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val msg = """
                                🪪 *پاک اسکول اینڈ کالج سسٹم*
                                ───────────────────
                                🎓 *طالب علم اسکول شناختی کارڈ (Digital ID Card)*
                                
                                محترم والدین!
                                طالب علم *${s.name}* کا اسکول کارڈ تفصیلات درج ذیل ہیں:
                                
                                👤 *نام:* ${s.name}
                                🔢 *رول نمبر:* ${s.rollNo}
                                🏫 *کلاس:* ${s.className} (${s.section})
                                👨 *والد کا نام:* ${s.fatherName}
                                📞 *ایمرجنسی رابطہ:* ${s.parentPhone}
                                🩸 *بلڈ گروپ:* B+
                                🗓️ *سیشن:* 2026 - 2027
                                
                                برائے مہربانی اسکول آتے وقت بچے کو کارڈ ہمراہ رکھنے کی تاکید فرمائیں۔
                                ───────────────────
                                دفتر انتظامیہ | پاک اسکول
                            """.trimIndent()
                            WhatsAppHelper.sendWhatsAppMessage(context, s.parentPhone, msg)
                            Toast.makeText(context, "Sending Digital ID Card to WhatsApp...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "📲 واٹس ایپ کارڈ بھیجیں", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "ID Card ready to print in PDF!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🖨️ پرنٹ کارڈ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Student Selection List
        item {
            Text(
                text = "SELECT STUDENT TO VIEW & PRINT CARD:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.5.sp
            )
        }

        items(studentsWithAttendance, key = { it.student.id }) { item ->
            val isSelected = activeCardStudent?.id == item.student.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { activeCardStudent = item.student },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1E40AF)) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF1E40AF), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = item.student.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "${item.student.rollNo} • S/O: ${item.student.fatherName}", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }

                    Text(
                        text = if (isSelected) "● ACTIVE" else "Select",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF1E40AF) else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
