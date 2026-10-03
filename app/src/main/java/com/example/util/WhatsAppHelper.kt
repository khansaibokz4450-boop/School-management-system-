package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object WhatsAppHelper {

    fun normalizePakistaniPhone(rawPhone: String): String {
        var clean = rawPhone.replace(Regex("[^0-9+]"), "")
        if (clean.startsWith("+")) {
            clean = clean.substring(1)
        }
        if (clean.startsWith("03")) {
            clean = "92" + clean.substring(1)
        }
        if (!clean.startsWith("92") && clean.length == 10) {
            clean = "92$clean"
        }
        return clean
    }

    fun sendWhatsAppMessage(
        context: Context,
        rawPhone: String,
        message: String,
        onFallbackSms: Boolean = true
    ) {
        val phone = normalizePakistaniPhone(rawPhone)
        val encodedMessage = try {
            URLEncoder.encode(message, "UTF-8")
        } catch (e: Exception) {
            message
        }

        val whatsappUri = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=$encodedMessage")
        val whatsappIntent = Intent(Intent.ACTION_VIEW, whatsappUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(whatsappIntent)
        } catch (e: Exception) {
            if (onFallbackSms) {
                sendSms(context, rawPhone, message)
            } else {
                Toast.makeText(context, "WhatsApp is not installed on this device", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendSms(context: Context, rawPhone: String, message: String) {
        val smsUri = Uri.parse("smsto:$rawPhone")
        val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
            putExtra("sms_body", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(smsIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "No SMS application found on device", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareSchoolBroadcast(context: Context, title: String, message: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "$title\n\n$message\n\n— PakSchool SMS Administration")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(Intent.createChooser(shareIntent, "Share Announcement via..."))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open share menu", Toast.LENGTH_SHORT).show()
        }
    }

    fun buildAbsenteeMessage(studentName: String, className: String, date: String): String {
        return """
            📢 *PAK SCHOOL & COLLEGE*
            -----------------------------
            ⚠️ *STUDENT ABSENCE NOTIFICATION*
            
            Respected Parent,
            Your child *${studentName}* (${className}) was marked *ABSENT* on ${date}.
            Please contact the school administration office to confirm the reason for absence.
            
            Regards,
            Administration | PakSchool SMS
        """.trimIndent()
    }

    fun buildFeeReminderMessage(
        studentName: String,
        className: String,
        amount: Int,
        month: String
    ): String {
        return """
            📢 *PAK SCHOOL & COLLEGE*
            -----------------------------
            💳 *MONTHLY FEE REMINDER*
            
            Respected Parent,
            The school tuition fee for *${studentName}* (${className}) for the month of *${month}* is *Rs. ${amount}*.
            Kindly deposit the dues before the scheduled due date to ensure smooth academic progress.
            
            Payment Channels: Cash Counter / JazzCash / EasyPaisa / Bank Transfer.
            Regards,
            Accounts Office | PakSchool SMS
        """.trimIndent()
    }
}
