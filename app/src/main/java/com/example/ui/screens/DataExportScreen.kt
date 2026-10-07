package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryGreen
import com.example.ui.viewmodel.SchoolViewModel

@Composable
fun DataExportScreen(
    viewModel: SchoolViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val bannerMetrics by viewModel.bannerMetrics.collectAsState()
    val studentsWithAttendance by viewModel.currentClassStudentsWithAttendance.collectAsState()
    val staffList by viewModel.allStaff.collectAsState()

    fun shareExportData(title: String, content: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(Intent.createChooser(shareIntent, "Export $title via..."))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open share menu", Toast.LENGTH_SHORT).show()
        }
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
                        text = "Backup & Excel Export",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    Text(
                        text = "ڈیٹا بیک اپ اور ایکسل شیٹ ایکسپورٹ",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color(0xFF1E40AF), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Data Backup & Spreadsheet Utility", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E40AF))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اسکول کا تمام ریکارڈ (طلبہ، فیس، حاضری اور اساتذہ) ایکسل (.csv) فارمیٹ میں ڈاؤن لوڈ اور شیئر کریں تاکہ ریکارڈ ہمیشہ محفوظ رہے۔",
                        fontSize = 12.sp,
                        color = Color(0xFF334155)
                    )
                }
            }
        }

        // Export Option 1: Students Directory
        item {
            ExportCard(
                title = "Students Directory (رجسٹرڈ طلبہ کی لسٹ)",
                description = "تمام کلاسز کے طلبہ کے نام، رول نمبر، کلاس، والد کا نام، اور موبائل نمبر۔",
                rowCount = "${bannerMetrics.totalStudents} Students",
                onExportCsv = {
                    val sb = StringBuilder()
                    sb.append("RollNo,Name,FatherName,Class,Section,ParentPhone,MonthlyFee,FeeStatus\n")
                    studentsWithAttendance.forEach { s ->
                        sb.append("${s.student.rollNo},${s.student.name},${s.student.fatherName},${s.student.className},${s.student.section},${s.student.parentPhone},${s.student.monthlyFee},${s.student.feeStatus}\n")
                    }
                    shareExportData("PakSchool_Students_List.csv", sb.toString())
                }
            )
        }

        // Export Option 2: Fee Ledger
        item {
            ExportCard(
                title = "Fee Ledger & Accounts (فیس و اکاونٹس لیجر)",
                description = "متوقع فیس، وصول شدہ رقم، بقایا جات، اور ادائیگی کے ذرائع کی مکمل شیٹ۔",
                rowCount = "Rs. ${bannerMetrics.totalCollectedFee} Collected",
                onExportCsv = {
                    val sb = StringBuilder()
                    sb.append("RollNo,Name,Class,MonthlyFee,FeeStatus,Contact\n")
                    studentsWithAttendance.forEach { s ->
                        sb.append("${s.student.rollNo},${s.student.name},${s.student.className},${s.student.monthlyFee},${s.student.feeStatus},${s.student.parentPhone}\n")
                    }
                    shareExportData("PakSchool_Fee_Ledger.csv", sb.toString())
                }
            )
        }

        // Export Option 3: Today's Attendance Sheet
        item {
            ExportCard(
                title = "Attendance Register (حاضری شیٹ)",
                description = "آج کے حاضر، غیر حاضر، اور رخصت پر طلبہ کا مکمل ریکارڈ بمع تاریخ و وقت۔",
                rowCount = "${bannerMetrics.presentCount} Present / ${bannerMetrics.absentCount} Absent",
                onExportCsv = {
                    val sb = StringBuilder()
                    sb.append("Date,RollNo,Name,Class,Status,Method\n")
                    studentsWithAttendance.forEach { s ->
                        val status = s.todayAttendance?.status ?: "PENDING"
                        val method = s.todayAttendance?.method ?: "MANUAL"
                        sb.append("${viewModel.todayDateString},${s.student.rollNo},${s.student.name},${s.student.className},$status,$method\n")
                    }
                    shareExportData("PakSchool_Attendance_${viewModel.todayDateString}.csv", sb.toString())
                }
            )
        }

        // Export Option 4: Staff Register
        item {
            ExportCard(
                title = "Staff & Faculty Register (اساتذہ و عملہ ریکارڈ)",
                description = "تمام اساتذہ کے نام، عہدہ، مضمون، رابطہ نمبر، اور ماہانہ تنخواہ کا ریکارڈ۔",
                rowCount = "${staffList.size} Faculty Members",
                onExportCsv = {
                    val sb = StringBuilder()
                    sb.append("Name,Designation,Subject,Qualification,Phone,MonthlySalary,SalaryStatus\n")
                    staffList.forEach { st ->
                        sb.append("${st.name},${st.designation},${st.subject},${st.qualification},${st.phone},${st.monthlySalary},${st.salaryStatus}\n")
                    }
                    shareExportData("PakSchool_Faculty_List.csv", sb.toString())
                }
            )
        }
    }
}

@Composable
private fun ExportCard(
    title: String,
    description: String,
    rowCount: String,
    onExportCsv: () -> Unit
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFDCFCE7), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Text(text = rowCount, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E40AF), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = description, fontSize = 12.sp, color = Color(0xFF64748B))

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onExportCsv,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "📊 Export to Excel (.csv) / شیئر کریں", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
