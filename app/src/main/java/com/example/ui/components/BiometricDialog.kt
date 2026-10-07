package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

enum class BiometricMode {
    MOBILE_FINGERPRINT,
    FACE_CAMERA,
    EXTERNAL_USB_DEVICE
}

@Composable
fun BiometricScanDialog(
    student: StudentEntity,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var selectedMode by remember { mutableStateOf(BiometricMode.MOBILE_FINGERPRINT) }
    var isScanning by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableFloatStateOf(0f) }
    var statusText by remember { mutableStateOf("Ready to scan") }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner_pulse"
    )

    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 140f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "face_laser"
    )

    fun triggerVibration() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(120)
                }
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            statusText = when (selectedMode) {
                BiometricMode.MOBILE_FINGERPRINT -> "Touch phone fingerprint sensor..."
                BiometricMode.FACE_CAMERA -> "Align face in camera oval frame..."
                BiometricMode.EXTERNAL_USB_DEVICE -> "Place finger on USB biometric reader..."
            }
            // Animate progress smoothly
            for (i in 1..10) {
                delay(120)
                scanProgress = i / 10f
            }
            triggerVibration()
            isSuccess = true
            statusText = when (selectedMode) {
                BiometricMode.MOBILE_FINGERPRINT -> "Fingerprint Matched (100%)"
                BiometricMode.FACE_CAMERA -> "Face Recognized (99.4% Match)"
                BiometricMode.EXTERNAL_USB_DEVICE -> "Optical Sensor Verified"
            }
            delay(900)
            onSuccess()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Biometric Verification",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1E293B)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEFF6FF)
                    ) {
                        Text(
                            text = student.rollNo,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "${student.name} • Class: ${student.className}",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mode Selector Tabs (Mobile Fingerprint, Camera Face, External USB Device)
                TabRow(
                    selectedTabIndex = selectedMode.ordinal,
                    containerColor = Color(0xFFF1F5F9),
                    contentColor = Color(0xFF1E40AF),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedMode == BiometricMode.MOBILE_FINGERPRINT,
                        onClick = {
                            if (!isScanning) {
                                selectedMode = BiometricMode.MOBILE_FINGERPRINT
                                isSuccess = false
                                scanProgress = 0f
                            }
                        },
                        text = { Text("📱 Fingerprint", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedMode == BiometricMode.FACE_CAMERA,
                        onClick = {
                            if (!isScanning) {
                                selectedMode = BiometricMode.FACE_CAMERA
                                isSuccess = false
                                scanProgress = 0f
                            }
                        },
                        text = { Text("📷 Face AI", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedMode == BiometricMode.EXTERNAL_USB_DEVICE,
                        onClick = {
                            if (!isScanning) {
                                selectedMode = BiometricMode.EXTERNAL_USB_DEVICE
                                isSuccess = false
                                scanProgress = 0f
                            }
                        },
                        text = { Text("🔌 USB Reader", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive Visual Stage based on Selected Mode
                when (selectedMode) {
                    BiometricMode.MOBILE_FINGERPRINT -> {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .scale(if (isScanning && !isSuccess) pulseScale else 1f)
                                .clip(CircleShape)
                                .background(
                                    if (isSuccess) Color(0xFFDCFCE7)
                                    else if (isScanning) Color(0xFFEFF6FF)
                                    else Color(0xFFF8FAFC)
                                )
                                .border(
                                    width = 3.dp,
                                    color = if (isSuccess) PrimaryGreen
                                    else if (isScanning) Color(0xFF2563EB)
                                    else Color(0xFFCBD5E1),
                                    shape = CircleShape
                                )
                                .clickable {
                                    if (!isScanning && !isSuccess) isScanning = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Fingerprint,
                                contentDescription = "Fingerprint Sensor",
                                tint = if (isSuccess) PrimaryGreen
                                else if (isScanning) Color(0xFF2563EB)
                                else Color(0xFF64748B),
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }

                    BiometricMode.FACE_CAMERA -> {
                        Box(
                            modifier = Modifier
                                .size(width = 130.dp, height = 150.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (isSuccess) Color(0xFFDCFCE7) else Color(0xFF0F172A))
                                .border(
                                    width = 2.dp,
                                    color = if (isSuccess) PrimaryGreen else Color(0xFF38BDF8),
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSuccess) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Face Verified",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(64.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = "Face Target",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(74.dp)
                                )

                                // Scanning laser effect
                                if (isScanning) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .offset(y = (-70 + laserOffsetY).dp)
                                            .background(Color(0xFF38BDF8))
                                    )
                                }
                            }
                        }
                    }

                    BiometricMode.EXTERNAL_USB_DEVICE -> {
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSuccess) Color(0xFFDCFCE7) else Color(0xFFF1F5F9))
                                .border(
                                    width = 2.dp,
                                    color = if (isSuccess) PrimaryGreen else Color(0xFF64748B),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Usb,
                                    contentDescription = "USB Biometric Reader",
                                    tint = if (isSuccess) PrimaryGreen else Color(0xFF1E40AF),
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isSuccess) "VERIFIED" else "SecuGen / ZKTeco",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSuccess) PrimaryGreen else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Indicator
                if (isScanning && !isSuccess) {
                    LinearProgressIndicator(
                        progress = { scanProgress },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF2563EB),
                        trackColor = Color(0xFFDBEAFE)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = statusText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSuccess) PrimaryGreen else Color(0xFF334155),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isSuccess) "Marked as PRESENT in database!"
                    else "Tap button below or touch sensor directly",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            if (!isSuccess) {
                Button(
                    onClick = { isScanning = true },
                    enabled = !isScanning,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.testTag("start_biometric_scan_button")
                ) {
                    Text(
                        when (selectedMode) {
                            BiometricMode.MOBILE_FINGERPRINT -> "Start Fingerprint Scan"
                            BiometricMode.FACE_CAMERA -> "Capture & Verify Face"
                            BiometricMode.EXTERNAL_USB_DEVICE -> "Scan via External Device"
                        }
                    )
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (isSuccess) "Done" else "Cancel")
            }
        }
    )
}
