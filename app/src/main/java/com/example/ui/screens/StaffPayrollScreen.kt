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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.StaffMemberEntity
import com.example.ui.components.AddStaffDialog
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AlertRed
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryTeal
import com.example.ui.viewmodel.SchoolViewModel
import com.example.util.WhatsAppHelper

@Composable
fun StaffPayrollScreen(
    viewModel: SchoolViewModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val staffList by viewModel.allStaff.collectAsState()
    var showAddStaffDialog by remember { mutableStateOf(false) }
    var salarySlipStaffTarget by remember { mutableStateOf<StaffMemberEntity?>(null) }
    var monthlyRegisterTarget by remember { mutableStateOf<StaffMemberEntity?>(null) }
    var paySalaryTarget by remember { mutableStateOf<StaffMemberEntity?>(null) }
    var showAllStaffMonthlySummaryDialog by remember { mutableStateOf(false) }

    val totalSalaryBudget = staffList.sumOf { it.monthlySalary }
    val totalPaidDisbursed = staffList.sumOf { it.paidAmount }
    val totalRemainingBalance = staffList.sumOf { it.effectiveRemaining }
    val presentCount = staffList.count { it.attendanceToday == "PRESENT" }

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
                            text = "Staff & Faculty Payroll",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                        Text(
                            text = "اساتذہ کی ماہانہ حاضری، آدھی/مکمل تنخواہ اور بقایا حساب",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Button(
                    onClick = { showAddStaffDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(38.dp).testTag("btn_add_staff")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Faculty", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Summary Card: Total Budget, Paid Disbursed, and Remaining Balance
        item {
            Card(
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
                            text = "MONTHLY PAYROLL & ATTENDANCE SUMMARY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEFF6FF),
                            modifier = Modifier.clickable { showAllStaffMonthlySummaryDialog = true }
                        ) {
                            Text(
                                text = "📅 سارا مہینہ رجسٹر",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Total Salary
                        Column {
                            Text("Total Budget", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text("Rs. ${totalSalaryBudget / 1000}k", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                        }

                        // Paid Amount
                        Column {
                            Text("Paid (ادا شدہ)", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                            Text("Rs. ${totalPaidDisbursed / 1000}k", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF16A34A))
                        }

                        // Remaining Balance
                        Column {
                            Text("Balance (بقایا واجب)", fontSize = 11.sp, color = AlertRed, fontWeight = FontWeight.Bold)
                            Text("Rs. ${totalRemainingBalance / 1000}k", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = AlertRed)
                        }

                        // Present Today
                        Column {
                            Text("Present Today", fontSize = 11.sp, color = Color(0xFF2563EB))
                            Text("$presentCount / ${staffList.size}", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2563EB))
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "FACULTY MEMBERS (${staffList.size}):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.5.sp
            )
        }

        // Staff List with Attendance & Salary
        items(staffList, key = { it.id }) { staff ->
            StaffMemberCard(
                staff = staff,
                onUpdateAttendance = { newStatus ->
                    viewModel.updateStaffAttendance(staff, newStatus)
                },
                onOpenPayDialog = {
                    paySalaryTarget = staff
                },
                onOpenMonthlyRegister = {
                    monthlyRegisterTarget = staff
                },
                onViewSlip = {
                    salarySlipStaffTarget = staff
                }
            )
        }
    }

    // Add Staff Dialog
    if (showAddStaffDialog) {
        AddStaffDialog(
            onDismiss = { showAddStaffDialog = false },
            onConfirm = { name, desig, qual, phone, subj, sal ->
                viewModel.addStaffMember(name, desig, qual, phone, subj, sal)
                showAddStaffDialog = false
            }
        )
    }

    // Pay Salary & Partial / Half Payment Dialog
    paySalaryTarget?.let { staff ->
        PayStaffSalaryDialog(
            staff = staff,
            onDismiss = { paySalaryTarget = null },
            onConfirmPayment = { paidAmt ->
                viewModel.recordStaffSalaryPayment(staff, paidAmt)
                paySalaryTarget = null
                Toast.makeText(context, "Salary payment of Rs. $paidAmt recorded!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Monthly Attendance Register Dialog
    monthlyRegisterTarget?.let { staff ->
        TeacherMonthlyRegisterDialog(
            staff = staff,
            monthName = "October 2026",
            onDismiss = { monthlyRegisterTarget = null }
        )
    }

    // All Staff Monthly Summary Dialog
    if (showAllStaffMonthlySummaryDialog) {
        AllFacultyMonthlySummaryDialog(
            staffList = staffList,
            monthName = "October 2026",
            onDismiss = { showAllStaffMonthlySummaryDialog = false }
        )
    }

    // Salary Slip Dialog
    salarySlipStaffTarget?.let { staff ->
        SalarySlipDialog(
            staff = staff,
            month = "October 2026",
            onDismiss = { salarySlipStaffTarget = null },
            onShareWhatsApp = {
                val slipMsg = """
                    🏫 *پاک اسکول اینڈ کالج سسٹم*
                    ───────────────────
                    💼 *ماہانہ تنخواہ سلپ (Faculty Salary Slip)*
                    
                    👤 *استاد کا نام:* ${staff.name}
                    💼 *عہدہ:* ${staff.designation} (${staff.subject})
                    🗓️ *مہینہ:* October 2026
                    
                    💰 *ماہانہ مقررہ تنخواہ:* Rs. ${staff.monthlySalary}
                    💵 *ادا شدہ رقم:* Rs. ${staff.paidAmount}
                    ⚠️ *بقایا تنخواہ (Remaining Due):* Rs. ${staff.effectiveRemaining}
                    
                    📊 *اسٹیٹس:* ${if (staff.paidAmount >= staff.monthlySalary) "مکمل ادا شدہ (PAID)" else if (staff.paidAmount > 0) "آدھی ادا شدہ (PARTIAL 50%)" else "غیر ادا شدہ (UNPAID)"}
                    ───────────────────
                    اکاؤنٹس ڈیپارٹمنٹ و پرنسپل | پاک اسکول
                """.trimIndent()
                WhatsAppHelper.sendWhatsAppMessage(context, staff.phone, slipMsg)
            }
        )
    }
}

@Composable
private fun StaffMemberCard(
    staff: StaffMemberEntity,
    onUpdateAttendance: (String) -> Unit,
    onOpenPayDialog: () -> Unit,
    onOpenMonthlyRegister: () -> Unit,
    onViewSlip: () -> Unit
) {
    val isFullyPaid = staff.paidAmount >= staff.monthlySalary
    val isHalfOrPartial = staff.paidAmount > 0 && !isFullyPaid

    val salaryBadgeColor = if (isFullyPaid) Color(0xFF16A34A) else if (isHalfOrPartial) Color(0xFFD97706) else AlertRed
    val salaryBadgeText = if (isFullyPaid) "PAID (مکمل ادا)" else if (isHalfOrPartial) "PARTIAL (آدھی ادا)" else "UNPAID (بقایا)"

    Card(
        modifier = Modifier.fillMaxWidth().testTag("staff_card_${staff.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Profile & Designation Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFEFF6FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = staff.name.take(2).uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = staff.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${staff.designation} • ${staff.subject} • ${staff.qualification}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Salary Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = salaryBadgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = salaryBadgeText,
                        color = salaryBadgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Salary & Balance Breakdown Card (Highlights Half-paid & Pending dues as requested)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("مقررہ تنخواہ (Fixed)", fontSize = 10.sp, color = Color(0xFF64748B))
                        Text("Rs. ${staff.monthlySalary}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    }

                    Column {
                        Text("ادا شدہ (Paid)", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                        Text("Rs. ${staff.paidAmount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }

                    Column {
                        Text("بقایا واجب الادا (Due)", fontSize = 10.sp, color = AlertRed, fontWeight = FontWeight.Bold)
                        Text("Rs. ${staff.effectiveRemaining}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AlertRed)
                    }

                    Button(
                        onClick = onOpenPayDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = if (isFullyPaid) Color(0xFF16A34A) else Color(0xFF2563EB)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(if (isFullyPaid) "ادائیگی اپڈیٹ" else "تنخواہ دیں", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Attendance Action Row (Present, Absent, Leave)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "آج کی حاضری:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("PRESENT" to "حاضر (P)", "ABSENT" to "غیر حاضر (A)", "LEAVE" to "رخصت (L)").forEach { (st, label) ->
                        val isSelected = staff.attendanceToday == st
                        val btnColor = when (st) {
                            "PRESENT" -> Color(0xFF16A34A)
                            "ABSENT" -> AlertRed
                            else -> AccentGold
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) btnColor else btnColor.copy(alpha = 0.08f),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, btnColor.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .clickable { onUpdateAttendance(st) }
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else btnColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons: Monthly Register (Calendar) & Salary Slip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onOpenMonthlyRegister,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFF1E40AF))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ماہانہ حاضری رجسٹر", fontSize = 10.sp, color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onViewSlip,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFF16A34A))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تنخواہ سلپ (Slip)", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PayStaffSalaryDialog(
    staff: StaffMemberEntity,
    onDismiss: () -> Unit,
    onConfirmPayment: (Int) -> Unit
) {
    var paymentInput by remember { mutableStateOf(staff.paidAmount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pay Faculty Salary (تنخواہ ادائیگی)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF0F172A)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "استاد: ${staff.name} (${staff.designation})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E40AF)
                )
                Text(
                    text = "مقررہ ماہانہ تنخواہ: Rs. ${staff.monthlySalary}",
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )

                HorizontalDivider(color = Color(0xFFF1F5F9))

                Text(
                    text = "کتنی رقم ادا کی گئی ہے درج کریں (ادا شدہ رقم):",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                OutlinedTextField(
                    value = paymentInput,
                    onValueChange = { paymentInput = it },
                    label = { Text("Paid Amount (PKR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.Bold),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF1E293B),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                val entered = paymentInput.toIntOrNull() ?: 0
                val remaining = (staff.monthlySalary - entered).coerceAtLeast(0)

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (remaining > 0) Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("بقایا رقم (Remaining Balance):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (remaining > 0) Color(0xFF92400E) else Color(0xFF166534))
                        Text("Rs. $remaining", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = if (remaining > 0) AlertRed else Color(0xFF166534))
                    }
                }

                // Quick Preset Buttons (Half pay / Full pay)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEFF6FF),
                        modifier = Modifier.clickable { paymentInput = (staff.monthlySalary / 2).toString() }
                    ) {
                        Text("آدھی تنخواہ (50% Half)", fontSize = 11.sp, color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.clickable { paymentInput = staff.monthlySalary.toString() }
                    ) {
                        Text("مکمل تنخواہ (100% Full)", fontSize = 11.sp, color = Color(0xFF166534), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = paymentInput.toIntOrNull() ?: 0
                    onConfirmPayment(amt)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Save Payment (محفوظ کریں)", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun TeacherMonthlyRegisterDialog(
    staff: StaffMemberEntity,
    monthName: String,
    onDismiss: () -> Unit
) {
    // 30 days attendance simulation
    val attendanceDays = remember {
        mutableStateMapOf<Int, String>().apply {
            for (day in 1..30) {
                put(day, if (day % 7 == 0) "OFF" else if (day == 14) "ABSENT" else if (day == 21) "LEAVE" else "PRESENT")
            }
        }
    }

    val totalPresent = attendanceDays.count { it.value == "PRESENT" }
    val totalAbsent = attendanceDays.count { it.value == "ABSENT" }
    val totalLeave = attendanceDays.count { it.value == "LEAVE" }

    Dialog(onDismissRequest = onDismiss) {
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
                        Text("Monthly Attendance Register", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E40AF))
                        Text("${staff.name} • $monthName", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stats summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFDCFCE7)) {
                        Text("Present: $totalPresent", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFEE2E2)) {
                        Text("Absent: $totalAbsent", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AlertRed, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFEF3C7)) {
                        Text("Leave: $totalLeave", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFEFF6FF)) {
                        Text("Rate: 92%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(8.dp))

                Text("Day-by-Day Calendar (1 to 30 $monthName):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                Spacer(modifier = Modifier.height(6.dp))

                // Grid of 30 days
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items((1..30).toList()) { day ->
                        val status = attendanceDays[day] ?: "PRESENT"
                        val (bg, txtColor, label) = when (status) {
                            "PRESENT" -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), "P")
                            "ABSENT" -> Triple(Color(0xFFFEE2E2), AlertRed, "A")
                            "LEAVE" -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "Lv")
                            else -> Triple(Color(0xFFF1F5F9), Color(0xFF94A3B8), "Sun")
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = bg,
                            modifier = Modifier
                                .height(34.dp)
                                .clickable {
                                    attendanceDays[day] = when (status) {
                                        "PRESENT" -> "ABSENT"
                                        "ABSENT" -> "LEAVE"
                                        "LEAVE" -> "PRESENT"
                                        else -> "PRESENT"
                                    }
                                }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "$day", fontSize = 9.sp, color = Color(0xFF64748B))
                                Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = txtColor)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done (مکمل)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AllFacultyMonthlySummaryDialog(
    staffList: List<StaffMemberEntity>,
    monthName: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                        Text("Faculty Monthly Register Sheet", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E40AF))
                        Text("تمام اساتذہ کا ماہانہ ریکارڈ • $monthName", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(staffList) { staff ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = staff.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                    Text(text = "${staff.designation} (${staff.subject})", fontSize = 11.sp, color = Color(0xFF64748B))
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFDCFCE7)) {
                                        Text("P: 24", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534), modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                    }
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFEE2E2)) {
                                        Text("A: 1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AlertRed, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                    }
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFEF3C7)) {
                                        Text("L: 1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SalarySlipDialog(
    staff: StaffMemberEntity,
    month: String,
    onDismiss: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Faculty Salary Slip", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryGreen)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

                Text(
                    text = "PAK SCHOOL & COLLEGE SYSTEM",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PrimaryGreen,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "PAYROLL SLIP FOR THE MONTH OF $month",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                SlipRow("Employee Name:", staff.name)
                SlipRow("Designation:", staff.designation)
                SlipRow("Department / Subject:", staff.subject)
                SlipRow("Contact:", staff.phone)
                SlipRow("Today's Attendance:", staff.attendanceToday)

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(8.dp))

                SlipRow("Base Monthly Salary:", "Rs. ${staff.monthlySalary}")
                SlipRow("Paid Amount (ادا شدہ رقم):", "Rs. ${staff.paidAmount}")
                SlipRow("Remaining Balance (بقایا تنخواہ):", "Rs. ${staff.effectiveRemaining}")

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(thickness = 1.5.dp, color = PrimaryGreen)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("STATUS:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryGreen)
                    Text(
                        text = if (staff.paidAmount >= staff.monthlySalary) "PAID (مکمل ادا)" else if (staff.paidAmount > 0) "PARTIAL (آدھی ادا)" else "UNPAID (غیر ادا شدہ)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (staff.paidAmount >= staff.monthlySalary) Color(0xFF16A34A) else Color(0xFFB45309)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onShareWhatsApp,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("واٹس ایپ سلپ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("Close", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SlipRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
    }
}
