package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.StudentEntity
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

    /**
     * Urdu WhatsApp Receipt for Parents upon Fee Payment
     */
    fun buildFeeReceiptUrduMessage(
        studentName: String,
        rollNo: String,
        className: String,
        month: String,
        amountPaid: Int,
        receiptId: String,
        date: String
    ): String {
        return """
            🎓 *پاک اسکول اینڈ کالج سسٹم*
            ───────────────────
            🧾 *فیس وصولی کی تصدیقی رسید (Fee Receipt)*
            
            محترم والدین،
            السلام علیکم!
            اطلاع دی جاتی ہے کہ آپ کے بچے کی فیس کامیابی سے موصول ہو گئی ہے۔
            
            👤 *طالب علم کا نام:* $studentName
            🔢 *رول نمبر:* $rollNo
            🏫 *کلاس:* $className
            📅 *مہینہ:* $month
            💰 *وصول شدہ رقم:* Rs. $amountPaid
            🔖 *رسید نمبر:* $receiptId
            🗓️ *تاریخ ادائیگی:* $date
            
            بروقت ادائیگی اور آپ کے تعاون کا شکریہ!
            ───────────────────
            دفتر اکاؤنٹس و انتظامیہ | پاک اسکول سسٹم
        """.trimIndent()
    }

    /**
     * Urdu Absentee Notification for Parents
     */
    fun buildAbsenteeUrduMessage(
        studentName: String,
        rollNo: String,
        className: String,
        date: String
    ): String {
        return """
            🏫 *پاک اسکول اینڈ کالج سسٹم*
            ───────────────────
            ⚠️ *اطلاع غیر حاضری (Absentee Notice)*
            
            محترم والدین،
            السلام علیکم!
            آپ کا بچہ *$studentName* (رول نمبر: *$rollNo*، کلاس: *$className*) آج مورخہ *$date* کو اسکول سے *غیر حاضر* ہے۔
            
            تعلیم کے تسلسل کے لیے روزانہ حاضری انتہائی ضروری ہے۔ برائے مہربانی اپنے بچے کی غیر حاضری کی وجہ تحریری طور پر یا کال کر کے اسکول آفس کو فوری مطلع فرمائیں۔
            
            شکریہ،
            ───────────────────
            پرنسپل و انتظامیہ | پاک اسکول سسٹم
        """.trimIndent()
    }

    /**
     * Daily Attendance Sheet for Principal (WhatsApp)
     * Shows:
     * - Total Count Present (تعداد)
     * - Total Count Absent
     * - Complete List of Absent Students with Roll No & Name (ایبسنس رولر کے نام)
     */
    fun buildPrincipalDailyAttendanceReport(
        date: String,
        className: String,
        totalStudents: Int,
        presentCount: Int,
        absentCount: Int,
        absentStudents: List<StudentEntity>,
        isHoliday: Boolean = false,
        holidayReason: String = "عوامی چھٹی / اتوار"
    ): String {
        if (isHoliday) {
            return """
                🏫 *پاک اسکول اینڈ کالج سسٹم*
                ───────────────────────
                📢 *روزانہ رپورٹ برائے پرنسپل صاحب (Holiday Report)*
                
                📅 *تاریخ:* $date
                🏫 *کلاس / سیکشن:* $className
                🏖️ *صورتحال:* *اسکول بند ہے (چھٹی کا دن)*
                📝 *تفصیل:* $holidayReason
                
                نوٹ: چھٹی کے باعث طلبہ کے والدین کو غیر حاضری کے پیغامات نہیں بھیجے گئے۔
                ───────────────────────
                رپورٹ خودکار سسٹم | پاک اسکول
            """.trimIndent()
        }

        val attendanceRate = if (totalStudents > 0) (presentCount * 100) / totalStudents else 0
        val absenteeListText = if (absentStudents.isEmpty()) {
            "✅ ماشاءاللہ! آج کوئی طالب علم غیر حاضر نہیں ہے (100% حاضری)۔"
        } else {
            val sb = java.lang.StringBuilder()
            sb.append("📋 *غیر حاضر طلبہ کی فہرست (Roll No & Name):*\n")
            absentStudents.forEachIndexed { index, student ->
                sb.append("${index + 1}. [${student.rollNo}] ${student.name} (${student.className}) - فون: ${student.parentPhone}\n")
            }
            sb.toString().trimEnd()
        }

        return """
            🏫 *پاک اسکول اینڈ کالج سسٹم*
            ───────────────────────
            📊 *روزانہ حاضری شیٹ برائے پرنسپل صاحب*
            
            📅 *تاریخ:* $date
            🏫 *کلاس / دائرہ کار:* $className
            👥 *کل رجسٹرڈ طلبہ:* $totalStudents
            
            📈 *حاضری سمری:*
            🟢 *حاضر طلبہ کی تعداد:* $presentCount ($attendanceRate%)
            🔴 *غیر حاضر طلبہ کی تعداد:* $absentCount
            
            ───────────────────────
            $absenteeListText
            ───────────────────────
            رپورٹ تیار کردہ: اے آئی اسمارٹ اسکول منیجمنٹ سسٹم
        """.trimIndent()
    }

    fun buildFeeReminderMessage(
        studentName: String,
        className: String,
        amount: Int,
        month: String
    ): String {
        return """
            📢 *پاک اسکول اینڈ کالج سسٹم*
            ───────────────────
            💳 *ماہانہ فیس کی یاددہانی (Fee Reminder)*
            
            محترم والدین،
            السلام علیکم!
            طالب علم *$studentName* ($className) کی ماہ *$month* کی اسکول فیس مبلغ *Rs. $amount* واجب الادا ہے۔
            
            تعلیمی نظام کو بلاتعطل جاری رکھنے کے لیے برائے مہربانی مقررہ تاریخ سے قبل فیس جمع کرا دیں۔
            
            ادائیگی کے ذرائع: کیش کاؤنٹر / JazzCash / EasyPaisa / بینک اکاؤنٹ
            شکریہ!
            ───────────────────
            دفتر اکاؤنٹس | پاک اسکول سسٹم
        """.trimIndent()
    }
}
