package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ActiveClassBanner
import com.example.ui.viewmodel.SchoolViewModel
import com.example.util.WhatsAppHelper

@Composable
fun HomeworkDiaryScreen(
    viewModel: SchoolViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val bannerMetrics by viewModel.bannerMetrics.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val studentsWithAttendance by viewModel.currentClassStudentsWithAttendance.collectAsState()

    var engWork by remember { mutableStateOf("Unit 3 Reading + Write Questions 1 & 2 on notebook.") }
    var urduWork by remember { mutableStateOf("سبق نمبر 4 کے الفاظ معنی یاد کریں اور خوشخطی لکھیں۔") }
    var mathWork by remember { mutableStateOf("Exercise 2.4 Questions 1 to 6 practice on rough register.") }
    var sciWork by remember { mutableStateOf("Chapter 3: Learn Solar System diagram and key terms.") }
    var islamiatWork by remember { mutableStateOf("سورۃ الفلق زبانی ترجمے کے ساتھ یاد کریں۔") }

    fun buildDiaryMessage(): String {
        return """
            🏫 *پاک اسکول اینڈ کالج سسٹم*
            ───────────────────
            📖 *روزانہ ہوم ورک و اسکول ڈائری (Daily Diary)*
            
            📅 *تاریخ:* ${viewModel.todayDateString}
            🏫 *کلاس:* $selectedClass
            
            📚 *آج کا ہوم ورک:*
            • 🇬🇧 *انگریزی (English):* $engWork
            • 🇵🇰 *اردو (Urdu):* $urduWork
            • 📐 *ریاضی (Mathematics):* $mathWork
            • 🔬 *سائنس (General Science):* $sciWork
            • 📖 *اسلامیات / ناظرہ:* $islamiatWork
            
            ⚠️ *محترم والدین!* برائے مہربانی شام کو بچے کا ہوم ورک چیک فرما کر ڈائری پر دستخط کریں۔
            ───────────────────
            کلاس انچارج و پرنسپل | پاک اسکول
        """.trimIndent()
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
                        text = "Daily Homework Diary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    Text(
                        text = "روزانہ اسکول ڈائری و ہوم ورک • Class: $selectedClass",
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

        // Homework Subject Inputs
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Book, contentDescription = null, tint = Color(0xFF1E40AF))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Class Subjects Homework", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Text(text = viewModel.todayDateString, fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedTextField(
                        value = engWork,
                        onValueChange = { engWork = it },
                        label = { Text("🇬🇧 English Homework") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2
                    )

                    OutlinedTextField(
                        value = urduWork,
                        onValueChange = { urduWork = it },
                        label = { Text("🇵🇰 اردو کا ہوم ورک (Urdu)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2
                    )

                    OutlinedTextField(
                        value = mathWork,
                        onValueChange = { mathWork = it },
                        label = { Text("📐 Mathematics Homework") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2
                    )

                    OutlinedTextField(
                        value = sciWork,
                        onValueChange = { sciWork = it },
                        label = { Text("🔬 Science Homework") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2
                    )

                    OutlinedTextField(
                        value = islamiatWork,
                        onValueChange = { islamiatWork = it },
                        label = { Text("📖 اسلامیات و ناظرہ قران") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2
                    )
                }
            }
        }

        // Action Buttons: Send via WhatsApp to Parents
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📢 Send Today's Diary on WhatsApp to Parents",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E40AF)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "کلاس کے تمام والدین کو آج کا ہوم ورک باوقار اردو انداز میں بھیجیں۔",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val msg = buildDiaryMessage()
                            if (studentsWithAttendance.isNotEmpty()) {
                                val first = studentsWithAttendance.first()
                                WhatsAppHelper.sendWhatsAppMessage(context, first.student.parentPhone, msg)
                            } else {
                                WhatsAppHelper.shareSchoolBroadcast(context, "Daily Homework - $selectedClass", msg)
                            }
                            Toast.makeText(context, "Opening WhatsApp with Today's Diary...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("send_diary_whatsapp_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "📲 Send Diary via WhatsApp (ڈائری ارسال کریں)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val msg = buildDiaryMessage()
                            WhatsAppHelper.shareSchoolBroadcast(context, "Daily Diary - $selectedClass", msg)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text(text = "📤 Share to WhatsApp Group (گروپ میں شیئر کریں)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
