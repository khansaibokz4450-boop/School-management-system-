package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PAK_CLASSES
import com.example.ui.viewmodel.SchoolViewModel

// Colors matching the clean video design
private val HeaderBlue = Color(0xFF1E40AF)
private val HeaderBlueDark = Color(0xFF1E3A8A)
private val ChipActiveBlue = Color(0xFF1E40AF)
private val ChipInactiveBg = Color(0xFFF1F5F9)
private val ChipInactiveBorder = Color(0xFFCBD5E1)
private val BannerBg = Color(0xFFEFF6FF)
private val BannerBorder = Color(0xFFBFDBFE)

// Card accent colors
private val AttendanceBlue = Color(0xFF2563EB)
private val FeeGreen = Color(0xFF16A34A)
private val TeacherAmber = Color(0xFFF59E0B)
private val MessagePink = Color(0xFFEC4899)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: SchoolViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val bannerMetrics by viewModel.bannerMetrics.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()

    val classesList = listOf("KG", "Nursery", "Prep") +
            (1..10).map { "Class $it" } +
            listOf("1st Year", "2nd Year")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TOP HEADER BANNER (Exact style from video: Deep Blue with white text)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderBlue)
                    .padding(vertical = 24.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "School Management System",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cross-Platform Control Panel & Management Portal",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFDBEAFE),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 2. "Select Target Class:" SECTION (Exact grid/flow of classes from video)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Select Target Class:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // FlowRow grid of class chips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        classesList.forEach { cls ->
                            val isSelected = cls.equals(selectedClass, ignoreCase = true)
                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) ChipActiveBlue else ChipInactiveBg,
                                label = "chipBg"
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) Color.White else Color(0xFF334155),
                                label = "chipText"
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bgColor)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) ChipActiveBlue else ChipInactiveBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setSelectedClass(cls) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("target_class_$cls"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cls,
                                    color = textColor,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. "Currently Viewing Database For: [Class]" BANNER (From video)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BannerBg)
                    .border(1.dp, BannerBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("current_viewing_banner")
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Currently Viewing Database For: ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E40AF)
                        )
                        Text(
                            text = selectedClass,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E3A8A)
                        )
                    }

                    // Simple stats pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = "${bannerMetrics.totalStudents} Students",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 4. THE FOUR CORE FEATURE MODULE CARDS (Matching the video layout)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // CARD 1: Attendance
                SimpleFeatureCard(
                    title = "Attendance",
                    subtitle = "Live Face Recognition & Attendance Logs",
                    icon = Icons.Default.AssignmentTurnedIn,
                    accentColor = AttendanceBlue,
                    badgeText = "${bannerMetrics.presentCount}/${bannerMetrics.totalStudents} Present",
                    onClick = { onNavigateToTab(1) },
                    testTag = "card_attendance"
                )

                // CARD 2: Fee Management
                SimpleFeatureCard(
                    title = "Fee Management",
                    subtitle = "Fee Collection, Receipts & Ledger Records",
                    icon = Icons.Default.Paid,
                    accentColor = FeeGreen,
                    badgeText = "Rs. ${bannerMetrics.totalCollectedFee / 1000}k Collected",
                    onClick = { onNavigateToTab(2) },
                    testTag = "card_fees"
                )

                // CARD 3: Teacher Records
                SimpleFeatureCard(
                    title = "Teacher Records",
                    subtitle = "Staff Profiles, Attendance & Salary Logs",
                    icon = Icons.Default.School,
                    accentColor = TeacherAmber,
                    badgeText = "Faculty Directory",
                    onClick = { onNavigateToTab(3) },
                    testTag = "card_teachers"
                )

                // CARD 4: WhatsApp & SMS
                SimpleFeatureCard(
                    title = "WhatsApp & SMS",
                    subtitle = "Send Instant Alerts, Absentee Reports & Notices",
                    icon = Icons.Default.Chat,
                    accentColor = MessagePink,
                    badgeText = "Broadcast Alerts",
                    onClick = { onNavigateToTab(4) },
                    testTag = "card_broadcast"
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Clean, kid-friendly feature card with big icon, colored left bar, and easy subtitles.
 */
@Composable
private fun SimpleFeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    badgeText: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left color accent bar + Big Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor.copy(alpha = 0.12f))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Main Titles
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = accentColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = badgeText,
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Chevron
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Open $title",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
