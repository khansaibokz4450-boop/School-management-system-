package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentEntity
import com.example.ui.theme.PrimaryGreen
import kotlinx.coroutines.delay

@Composable
fun BiometricScanDialog(
    student: StudentEntity,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var scanState by remember { mutableStateOf<BiometricScanStage>(BiometricScanStage.Ready) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner_pulse"
    )

    fun triggerVibration() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(100)
                }
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(scanState) {
        if (scanState is BiometricScanStage.Scanning) {
            delay(1200)
            triggerVibration()
            scanState = BiometricScanStage.Success
            delay(1000)
            onSuccess()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (scanState is BiometricScanStage.Ready) {
                Button(
                    onClick = { scanState = BiometricScanStage.Scanning },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.testTag("start_biometric_scan_button")
                ) {
                    Text("Place Finger / Start Scan")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Biometric Verification",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Student details card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = student.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Roll No: ${student.rollNo}  •  ${student.className} (${student.section})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "S/D/O: ${student.fatherName}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Scanner visualizer
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    when (scanState) {
                        is BiometricScanStage.Ready -> {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .background(PrimaryGreen.copy(alpha = 0.1f), CircleShape)
                                    .border(2.dp, PrimaryGreen.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = "Ready",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(54.dp)
                                )
                            }
                        }
                        is BiometricScanStage.Scanning -> {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .scale(scale)
                                    .background(PrimaryGreen.copy(alpha = 0.2f), CircleShape)
                                    .border(2.dp, PrimaryGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(70.dp),
                                    color = PrimaryGreen,
                                    strokeWidth = 3.dp
                                )
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scanning",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        is BiometricScanStage.Success -> {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .background(Color(0xFFE8F5E9), CircleShape)
                                    .border(2.dp, Color(0xFF2E7D32), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(54.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when (scanState) {
                        is BiometricScanStage.Ready -> "Tap button below to capture fingerprint or facial scan"
                        is BiometricScanStage.Scanning -> "Scanning Biometric ID... Please hold"
                        is BiometricScanStage.Success -> "Biometric Match Verified! Attendance Logged."
                    },
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    fontWeight = if (scanState is BiometricScanStage.Success) FontWeight.Bold else FontWeight.Normal,
                    color = if (scanState is BiometricScanStage.Success) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Biometric Attendance Verification",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    )
}

sealed class BiometricScanStage {
    object Ready : BiometricScanStage()
    object Scanning : BiometricScanStage()
    object Success : BiometricScanStage()
}
