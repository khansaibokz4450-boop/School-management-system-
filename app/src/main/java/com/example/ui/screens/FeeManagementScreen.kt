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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.StudentEntity
import com.example.ui.components.ActiveClassBanner
import com.example.ui.components.FeeChallanDialog
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AlertRed
import com.example.ui.theme.PrimaryGreen
import com.example.ui.viewmodel.SchoolViewModel
import com.example.util.WhatsAppHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeManagementScreen(
    viewModel: SchoolViewModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val bannerMetrics by viewModel.bannerMetrics.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val studentsWithAttendance by viewModel.currentClassStudentsWithAttendance.collectAsState()

    var statusFilter by remember { mutableStateOf("ALL") }
    var challanStudentTarget by remember { mutableStateOf<StudentEntity?>(null) }
    var paymentStudentTarget by remember { mutableStateOf<StudentEntity?>(null) }

    val students = studentsWithAttendance.map { it.student }
    val filteredStudents = students.filter { s ->
        when (statusFilter) {
            "PAID" -> s.feeStatus == "PAID"
            "PENDING" -> s.feeStatus == "PENDING"
            "DEFAULTER" -> s.feeStatus == "DEFAULTER"
            else -> true
        }
    }

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
                        text = "Fee Management",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    Text(
                        text = "Vouchers & Ledger • Class: $selectedClass",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // Active Class Banner
        item {
            ActiveClassBanner(
                metrics = bannerMetrics,
                selectedClass = selectedClass,
                onClassSelected = { viewModel.setSelectedClass(it) }
            )
        }

        // Financial KPIs Cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeeSummaryCard(
                    title = "Expected Fee",
                    amount = "Rs. ${bannerMetrics.totalExpectedFee}",
                    sub = "Total Expected",
                    color = PrimaryGreen,
                    modifier = Modifier.weight(1f)
                )
                FeeSummaryCard(
                    title = "Collected",
                    amount = "Rs. ${bannerMetrics.totalCollectedFee}",
                    sub = "Recovered",
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
                FeeSummaryCard(
                    title = "Pending",
                    amount = "Rs. ${(bannerMetrics.totalExpectedFee - bannerMetrics.totalCollectedFee).coerceAtLeast(0)}",
                    sub = "Remaining",
                    color = AlertRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "All (${students.size})",
                    "PAID" to "Paid (${students.count { it.feeStatus == "PAID" }})",
                    "PENDING" to "Pending (${students.count { it.feeStatus == "PENDING" }})",
                    "DEFAULTER" to "Defaulters (${students.count { it.feeStatus == "DEFAULTER" }})"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = statusFilter == key,
                        onClick = { statusFilter = key },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (key == "DEFAULTER") AlertRed.copy(alpha = 0.15f) else PrimaryGreen.copy(alpha = 0.15f),
                            selectedLabelColor = if (key == "DEFAULTER") AlertRed else PrimaryGreen
                        )
                    )
                }
            }
        }

        // Student Fee Items
        if (filteredStudents.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No fee records found in this category.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(filteredStudents, key = { it.id }) { student ->
                StudentFeeCard(
                    student = student,
                    onOpenChallan = { challanStudentTarget = student },
                    onRecordPayment = { paymentStudentTarget = student },
                    onSendWhatsAppReminder = {
                        val msg = WhatsAppHelper.buildFeeReminderMessage(
                            studentName = student.name,
                            className = student.className,
                            amount = student.monthlyFee,
                            month = "October 2026"
                        )
                        WhatsAppHelper.sendWhatsAppMessage(context, student.parentPhone, msg)
                    }
                )
            }
        }
    }

    // Challan Dialog
    challanStudentTarget?.let { student ->
        FeeChallanDialog(
            student = student,
            month = "October 2026",
            onDismiss = { challanStudentTarget = null },
            onRecordPayment = {
                viewModel.recordFeePayment(student, "October 2026", student.monthlyFee, 0, "Cash")
            }
        )
    }

    // Record Payment Dialog
    paymentStudentTarget?.let { student ->
        RecordPaymentDialog(
            student = student,
            onDismiss = { paymentStudentTarget = null },
            onConfirm = { month, amount, discount, method ->
                viewModel.recordFeePayment(student, month, amount, discount, method)
                paymentStudentTarget = null
            }
        )
    }
}

@Composable
private fun FeeSummaryCard(
    title: String,
    amount: String,
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
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = amount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = sub, fontSize = 9.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun StudentFeeCard(
    student: StudentEntity,
    onOpenChallan: () -> Unit,
    onRecordPayment: () -> Unit,
    onSendWhatsAppReminder: () -> Unit
) {
    val statusColor = when (student.feeStatus) {
        "PAID" -> Color(0xFF2E7D32)
        "PENDING" -> AccentGold
        else -> AlertRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fee_card_${student.rollNo}"),
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
                Column {
                    Text(text = student.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        text = "Roll: ${student.rollNo} • ${student.className} (${student.section}) • ${student.parentPhone}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = student.feeStatus,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Rs. ${student.monthlyFee} / month",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                // Actions row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Challan Slip
                    OutlinedButton(
                        onClick = onOpenChallan,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Challan", fontSize = 11.sp)
                    }

                    // If not paid, show Pay Fee
                    if (student.feeStatus != "PAID") {
                        Button(
                            onClick = onRecordPayment,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Collect", fontSize = 11.sp)
                        }

                        // WhatsApp reminder
                        Button(
                            onClick = onSendWhatsAppReminder,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordPaymentDialog(
    student: StudentEntity,
    onDismiss: () -> Unit,
    onConfirm: (month: String, amount: Int, discount: Int, method: String) -> Unit
) {
    var month by remember { mutableStateOf("October 2026") }
    var amountString by remember { mutableStateOf(student.monthlyFee.toString()) }
    var discountString by remember { mutableStateOf("0") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var methodExpanded by remember { mutableStateOf(false) }

    val methods = listOf("Cash", "JazzCash", "EasyPaisa", "Bank Transfer (HBL/UBL)")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddCard, contentDescription = null, tint = PrimaryGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Record Fee Payment", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Student: ${student.name} (${student.rollNo} - ${student.className})",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                OutlinedTextField(
                    value = month,
                    onValueChange = { month = it },
                    label = { Text("Fee Month") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountString,
                    onValueChange = { amountString = it },
                    label = { Text("Amount Collected (PKR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = discountString,
                    onValueChange = { discountString = it },
                    label = { Text("Discount / Concession (PKR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = methodExpanded,
                    onExpandedChange = { methodExpanded = it }
                ) {
                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Channel") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = methodExpanded,
                        onDismissRequest = { methodExpanded = false }
                    ) {
                        methods.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m) },
                                onClick = {
                                    paymentMethod = m
                                    methodExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountString.toIntOrNull() ?: student.monthlyFee
                    val discount = discountString.toIntOrNull() ?: 0
                    onConfirm(month, amount, discount, paymentMethod)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Confirm & Issue Receipt")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
