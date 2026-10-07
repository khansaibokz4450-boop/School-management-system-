package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.WhatsAppHelper

data class VanRoute(
    val routeId: String,
    val routeName: String,
    val driverName: String,
    val driverPhone: String,
    val vehicleNo: String,
    val capacity: String,
    val monthlyVanFee: Int
)

@Composable
fun TransportScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val vanRoutes = listOf(
        VanRoute("R-1", "Main City & Colony Route (روٹ 1: مین سٹی)", "Chaudhry Riaz", "+923012345671", "ICT-LE-4580 (Toyota Hiace)", "14 / 15 Students", 2500),
        VanRoute("R-2", "West Canal & Satellite Town (روٹ 2: نہر پل)", "Ustad Gulzar", "+923023456782", "LHR-MN-9921 (Coaster 24)", "22 / 24 Students", 2800),
        VanRoute("R-3", "East Bypass & Cantt Road (روٹ 3: کینٹ)", "Malik Aslam", "+923034567893", "RWP-AB-3342 (Bolan Van)", "7 / 8 Students", 2200),
        VanRoute("R-4", "Airport Road & Garden Town (روٹ 4: ایئرپورٹ)", "Mama Zahoor", "+923045678904", "PES-CD-1120 (Hiace AC)", "15 / 15 Students", 3000)
    )

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
                        text = "Transport & School Van",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                    Text(
                        text = "اسکول وین و پک اینڈ ڈراپ سروس",
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "School Transport Management", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF92400E))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "طلبہ کے محفوظ سفر کے لیے تمام وین روٹس، ڈرائیور کے رابطے اور وین فیس کی نگرانی۔",
                        fontSize = 12.sp,
                        color = Color(0xFF78350F)
                    )
                }
            }
        }

        // Van Routes
        items(vanRoutes) { van ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = van.routeName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = "Rs. ${van.monthlyVanFee}/mo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Vehicle: ${van.vehicleNo}", fontSize = 12.sp, color = Color(0xFF475569))
                    Text(text = "Driver: ${van.driverName} (${van.driverPhone})", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E40AF))
                    Text(text = "Passengers: ${van.capacity}", fontSize = 11.sp, color = Color(0xFF64748B))

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${van.driverPhone}"))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Driver", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val msg = """
                                    🚌 *پاک اسکول ٹرانسپورٹ پورٹل*
                                    ───────────────────
                                    محترم والدین!
                                    ${van.routeName} کی وین اسکول پہنچ چکی ہے اور واپسی کے لیے روانہ ہو رہی ہے۔ ڈرائیور: ${van.driverName} (${van.driverPhone})۔
                                    ───────────────────
                                    ٹرانسپورٹ انچارج | پاک اسکول
                                """.trimIndent()
                                WhatsAppHelper.shareSchoolBroadcast(context, "Van Notification - ${van.routeId}", msg)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("وین الرٹ واٹس ایپ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
