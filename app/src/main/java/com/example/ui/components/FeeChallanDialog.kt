package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.StudentEntity
import com.example.ui.theme.PrimaryGreen
import com.example.util.WhatsAppHelper

@Composable
fun FeeChallanDialog(
    student: StudentEntity,
    month: String,
    onDismiss: () -> Unit,
    onRecordPayment: () -> Unit
) {
    val context = LocalContext.current
    val receiptNo = "PK-${student.id * 100 + 42}"
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("fee_challan_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header with close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Official Fee Challan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Challan Slip Card (Authentic Pakistani School voucher design)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF9FDF9),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // School Letterhead
                        Text(
                            text = "PAK SCHOOL & COLLEGE SYSTEM",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = PrimaryGreen,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "GOVT. RECOGNIZED • AFFILIATED WITH FBISE & BISE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "FEE CHALLAN VOUCHER (STUDENT & ACCOUNTS COPY)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Voucher meta
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Challan #: $receiptNo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Month: $month", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Student Info
                        ChallanInfoRow("Student Name:", student.name)
                        ChallanInfoRow("Father Name:", student.fatherName)
                        ChallanInfoRow("Class & Section:", "${student.className} (${student.section})")
                        ChallanInfoRow("Roll Number:", student.rollNo)
                        ChallanInfoRow("Contact / WhatsApp:", student.parentPhone)

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Fee Breakdown
                        Text(
                            text = "FEE PARTICULARS & BREAKDOWN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = PrimaryGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        ChallanFeeRow("Monthly Tuition Fee", "Rs. ${student.monthlyFee}")
                        ChallanFeeRow("Examination / Lab Charges", "Rs. 0")
                        ChallanFeeRow("Generator & Utilities", "Included")
                        if (student.feeStatus == "DEFAULTER") {
                            ChallanFeeRow("Previous Arrears", "Rs. 1,500", isAlert = true)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(thickness = 1.5.dp, color = PrimaryGreen)
                        Spacer(modifier = Modifier.height(6.dp))

                        val totalPayable = if (student.feeStatus == "DEFAULTER") student.monthlyFee + 1500 else student.monthlyFee
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL PAYABLE (PKR):",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = PrimaryGreen
                            )
                            Text(
                                text = "Rs. $totalPayable",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = PrimaryGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Barcode simulation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp)
                                .background(Color.White)
                                .border(1.dp, Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "||| | ||||| || |||| |||| ||| |||||",
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }

                        Text(
                            text = "Payment accepted at: EasyPaisa • JazzCash • HBL/UBL • School Cash Counter",
                            fontSize = 9.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val msg = WhatsAppHelper.buildFeeReminderMessage(
                                studentName = student.name,
                                className = student.className,
                                amount = student.monthlyFee,
                                month = month
                            )
                            WhatsAppHelper.sendWhatsAppMessage(context, student.parentPhone, msg)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("whatsapp_challan_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WhatsApp", fontSize = 12.sp)
                    }

                    if (student.feeStatus != "PAID") {
                        Button(
                            onClick = {
                                onRecordPayment()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("record_payment_button")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark Paid", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChallanInfoRow(label: String, value: String) {
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

@Composable
private fun ChallanFeeRow(label: String, value: String, isAlert: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = if (isAlert) Color.Red else Color.Black)
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isAlert) Color.Red else Color.Black
        )
    }
}
