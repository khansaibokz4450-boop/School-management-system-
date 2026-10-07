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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.PrimaryGreen
import com.example.ui.viewmodel.SchoolViewModel
import com.example.util.WhatsAppHelper

@Composable
fun DeveloperRoyaltyScreen(
    viewModel: SchoolViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val bannerMetrics by viewModel.bannerMetrics.collectAsState()
    val studentsWithAttendance by viewModel.currentClassStudentsWithAttendance.collectAsState()

    // Master Config State
    var isRoyaltyEnabled by remember { mutableStateOf(true) }
    var easyPaisaNumber by remember { mutableStateOf("03001234567") }
    var accountTitle by remember { mutableStateOf("Ahmad ullah okz") }
    var perStudentFeeString by remember { mutableStateOf("100") }

    // Set of Waived Student IDs (Developer choice: "معاف کر دیا گیا")
    val waivedStudentIds = remember { mutableStateListOf<Long>() }

    val perStudentFee = perStudentFeeString.toIntOrNull() ?: 100
    val totalStudents = studentsWithAttendance.size
    val waivedCount = studentsWithAttendance.count { waivedStudentIds.contains(it.student.id) }
    val chargeableCount = (totalStudents - waivedCount).coerceAtLeast(0)
    val totalRoyaltyDue = if (isRoyaltyEnabled) chargeableCount * perStudentFee else 0

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
                        text = "Developer Royalty & License Portal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    Text(
                        text = "ڈویلپر کنٹرول: فی داخلہ 100 روپے وصولی یا معافی کا اختیار",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // Developer Profile & Security Badge
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2563EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "AO", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Ahmad ullah okz", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = "Master Software Architect & Owner", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF14532D)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Active License",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Master Royalty Switch & Waiver Rule Description
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "EasyPaisa Per-Admission Cut (فی داخلہ 100 روپے)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = if (isRoyaltyEnabled) "سسٹم فعال ہے: ہر سٹوڈنٹ داخلے پر 100 روپے یا معاف کرنے کا مکمل اختیار" else "معافی موڈ (100% Free - نو رائلٹی)",
                                fontSize = 11.sp,
                                color = if (isRoyaltyEnabled) Color(0xFF4ADE80) else Color(0xFFF87171)
                            )
                        }

                        Switch(
                            checked = isRoyaltyEnabled,
                            onCheckedChange = { isRoyaltyEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF16A34A)
                            )
                        )
                    }
                }
            }
        }

        // Financial Tracker Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LIVE EARNINGS & ADMISSION LEDGER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Students", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text("$totalStudents", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                        }

                        Column {
                            Text("Chargeable (@ Rs.$perStudentFee)", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                            Text("$chargeableCount", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF16A34A))
                        }

                        Column {
                            Text("Waived (معاف شدہ)", fontSize = 11.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Bold)
                            Text("$waivedCount", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
                        }

                        Column {
                            Text("EasyPaisa Due", fontSize = 11.sp, color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold)
                            Text("Rs. $totalRoyaltyDue", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E40AF))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Bulk Actions: Waive All vs Charge All
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                waivedStudentIds.clear()
                                Toast.makeText(context, "تمام طلبہ پر 100 روپے رائلٹی فعال کر دی گئی", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text("سب سے وصول کریں", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                waivedStudentIds.clear()
                                studentsWithAttendance.forEach { waivedStudentIds.add(it.student.id) }
                                Toast.makeText(context, "تمام طلبہ کو رائلٹی معاف کر دی گئی (100% Free)", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text("سب کو معاف کریں", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // EasyPaisa Payment Account Settings
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EasyPaisa Account Settings (میرا ایزی پیسہ اکاؤنٹ)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = easyPaisaNumber,
                        onValueChange = { easyPaisaNumber = it },
                        label = { Text("EasyPaisa Mobile Number (ایزی پیسہ نمبر)") },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F172A), fontWeight = FontWeight.Bold),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF1E293B),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = accountTitle,
                        onValueChange = { accountTitle = it },
                        label = { Text("Account Title (اکاؤنٹ ہولڈر کا نام)") },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // WhatsApp Invoice Generator Button
                    Button(
                        onClick = {
                            val inv = """
                                🏫 *پاک اسکول مینجمنٹ سسٹم - لائسنس و رائلٹی بل*
                                ───────────────────
                                محترم پرنسپل صاحب!
                                آپ کے اسکول سسٹم میں نئے داخل شدہ طلبہ کی لائسنس فیس کی تفصیل:
                                
                                👨‍🎓 *کل داخل شدہ طلبہ:* $totalStudents
                                🎁 *معاف شدہ داخلے:* $waivedCount
                                💰 *قابلِ ادا طلبہ:* $chargeableCount × Rs. $perStudentFee
                                💵 *کل واجب الادا رقم:* Rs. $totalRoyaltyDue
                                
                                📱 *EasyPaisa اکاؤنٹ:* $easyPaisaNumber
                                👤 *اکاؤنٹ ٹائٹل:* $accountTitle
                                ───────────────────
                                شکریہ! احمد اللہ (سافٹ ویئر ڈویلپر)
                            """.trimIndent()
                            WhatsAppHelper.sendWhatsAppMessage(context, "+923001234567", inv)
                            Toast.makeText(context, "Opening WhatsApp with invoice for Principal...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "📲 پرنسپل کو ایزی پیسہ بل بھیجیں (Send Invoice)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Student-by-Student Royalty Control List
        item {
            Text(
                text = "INDIVIDUAL ADMISSIONS WAIVER CONTROLS (طالب علم کے لحاظ سے معافی):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.5.sp
            )
        }

        items(studentsWithAttendance, key = { it.student.id }) { item ->
            val isWaived = waivedStudentIds.contains(item.student.id)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isWaived) Color(0xFFFEF9C3) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.student.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${item.student.rollNo} • ${item.student.className} • داخلہ فیس: Rs. ${item.student.monthlyFee}",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                    }

                    // Waiver Toggle Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isWaived) Color(0xFFFEF08A) else Color(0xFFDCFCE7),
                        modifier = Modifier.clickable {
                            if (isWaived) {
                                waivedStudentIds.remove(item.student.id)
                                Toast.makeText(context, "${item.student.name}: 100 روپے فعال", Toast.LENGTH_SHORT).show()
                            } else {
                                waivedStudentIds.add(item.student.id)
                                Toast.makeText(context, "${item.student.name}: فیس معاف کر دی گئی", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isWaived) Icons.Default.MoneyOff else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (isWaived) Color(0xFF854D0E) else Color(0xFF166534),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isWaived) "معاف شدہ (Waived)" else "وصول کریں (Rs. 100)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isWaived) Color(0xFF854D0E) else Color(0xFF166534)
                            )
                        }
                    }
                }
            }
        }
    }
}
