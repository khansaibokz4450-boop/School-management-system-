package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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

@Composable
fun StaffPayrollScreen(
    viewModel: SchoolViewModel,
    onBack: () -> Unit = {}
) {
    val staffList by viewModel.allStaff.collectAsState()
    var showAddStaffDialog by remember { mutableStateOf(false) }
    var salarySlipStaffTarget by remember { mutableStateOf<StaffMemberEntity?>(null) }

    val totalSalaryBudget = staffList.sumOf { it.monthlySalary }
    val paidCount = staffList.count { it.salaryStatus == "PAID" }
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
                            text = "Teacher Records",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                        Text(
                            text = "Faculty Directory & Payroll Register",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Button(
                    onClick = { showAddStaffDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_teacher_btn")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Faculty", fontSize = 11.sp)
                }
            }
        }

        // Summary Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StaffMetricCard(
                    label = "Total Faculty",
                    value = "${staffList.size} Staff",
                    sub = "${presentCount} Present Today",
                    color = PrimaryGreen,
                    modifier = Modifier.weight(1f)
                )
                StaffMetricCard(
                    label = "Monthly Payroll",
                    value = "Rs. ${totalSalaryBudget / 1000}k",
                    sub = "${paidCount}/${staffList.size} Disbursed",
                    color = SecondaryTeal,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Staff List Header
        item {
            Text(
                text = "FACULTY MEMBERS (${staffList.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
        }

        // Staff Item Cards
        items(staffList, key = { it.id }) { staff ->
            StaffCard(
                staff = staff,
                onUpdateAttendance = { status ->
                    viewModel.updateStaffAttendance(staff, status)
                },
                onToggleSalaryStatus = {
                    val next = if (staff.salaryStatus == "PAID") "PENDING" else "PAID"
                    viewModel.updateStaffSalaryStatus(staff, next)
                },
                onViewSlip = { salarySlipStaffTarget = staff }
            )
        }
    }

    if (showAddStaffDialog) {
        AddStaffDialog(
            onDismiss = { showAddStaffDialog = false },
            onConfirm = { name, desig, qual, ph, sub, sal ->
                viewModel.addStaffMember(name, desig, qual, ph, sub, sal)
            }
        )
    }

    salarySlipStaffTarget?.let { staff ->
        SalarySlipDialog(
            staff = staff,
            month = "October 2026",
            onDismiss = { salarySlipStaffTarget = null }
        )
    }
}

@Composable
private fun StaffMetricCard(
    label: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = sub, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun StaffCard(
    staff: StaffMemberEntity,
    onUpdateAttendance: (String) -> Unit,
    onToggleSalaryStatus: () -> Unit,
    onViewSlip: () -> Unit
) {
    val attColor = when (staff.attendanceToday) {
        "PRESENT" -> Color(0xFF2E7D32)
        "ABSENT" -> AlertRed
        else -> SecondaryTeal
    }

    val salColor = if (staff.salaryStatus == "PAID") Color(0xFF2E7D32) else AccentGold

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("staff_card_${staff.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            .size(40.dp)
                            .background(SecondaryTeal.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = SecondaryTeal, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = staff.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = "${staff.designation} • ${staff.subject}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Qualification: ${staff.qualification} • Phone: ${staff.phone}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 0.8.dp, color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Attendance & Salary controls row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Today's attendance pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Today: ", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    listOf("PRESENT" to "P", "ABSENT" to "A", "LEAVE" to "Lv").forEach { (st, label) ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (staff.attendanceToday == st) attColor else Color.LightGray.copy(alpha = 0.2f),
                            modifier = Modifier
                                .size(width = 28.dp, height = 26.dp)
                                .clickable { onUpdateAttendance(st) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (staff.attendanceToday == st) Color.White else Color.DarkGray
                                )
                            }
                        }
                    }
                }

                // Salary Slip & Status button
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onViewSlip,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Slip", fontSize = 10.sp)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = salColor.copy(alpha = 0.15f),
                        modifier = Modifier
                            .height(30.dp)
                            .clickable { onToggleSalaryStatus() }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                            Text(
                                text = "Rs. ${staff.monthlySalary / 1000}k • ${staff.salaryStatus}",
                                color = salColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SalarySlipDialog(
    staff: StaffMemberEntity,
    month: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Faculty Salary Slip", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryGreen)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

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
                    color = Color.Gray,
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
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(8.dp))

                SlipRow("Base Monthly Salary:", "Rs. ${staff.monthlySalary}")
                SlipRow("Academic Allowance:", "Rs. 2,000")
                SlipRow("Provident Fund / Tax:", "- Rs. 1,000")

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(thickness = 1.5.dp, color = PrimaryGreen)
                Spacer(modifier = Modifier.height(6.dp))

                val netPay = staff.monthlySalary + 2000 - 1000
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("NET PAYABLE (PKR):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryGreen)
                    Text("Rs. $netPay", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryGreen)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close Slip")
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
        Text(text = label, fontSize = 11.sp, color = Color.DarkGray)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
