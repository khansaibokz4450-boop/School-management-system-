package com.example.util

import java.util.regex.Pattern

object SecurityHelper {

    private var adminPin: String = "1234"
    private var lastAdminUnlockTimestamp: Long = 0L
    private const val SESSION_TIMEOUT_MS: Long = 10 * 60 * 1000L // 10 minutes

    private val actionRateLimitMap = mutableMapOf<String, Long>()
    private const val MIN_ACTION_INTERVAL_MS: Long = 1500L // 1.5 seconds between sensitive actions

    /**
     * Rule 7: Prevent negative fees (-1000), overflow or zero
     */
    fun validatePositiveAmount(input: String, fallback: Int = 0): Result<Int> {
        val trimmed = input.trim()
        val parsed = trimmed.toIntOrNull()
            ?: return Result.failure(IllegalArgumentException("رقم صرف اعداد پر مشتمل ہونی چاہیے (Numbers only)."))

        if (parsed <= 0) {
            return Result.failure(IllegalArgumentException("رقم کا 0 سے زیادہ اور مثبت ہونا لازمی ہے (Cannot be negative or zero)."))
        }

        if (parsed > 1_000_000) {
            return Result.failure(IllegalArgumentException("رقم حد سے زیادہ ہے (Exceeds maximum allowable limit)."))
        }

        return Result.success(parsed)
    }

    /**
     * Validates discount: must be >= 0 and <= amount
     */
    fun validateDiscount(input: String, totalAmount: Int): Result<Int> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return Result.success(0)

        val parsed = trimmed.toIntOrNull()
            ?: return Result.failure(IllegalArgumentException("رعایت صرف اعداد پر مشتمل ہو سکتی ہے۔"))

        if (parsed < 0) {
            return Result.failure(IllegalArgumentException("رعایت منفی (-ve) نہیں ہو سکتی۔"))
        }

        if (parsed > totalAmount) {
            return Result.failure(IllegalArgumentException("رعایت کل فیس سے زیادہ نہیں ہو سکتی۔"))
        }

        return Result.success(parsed)
    }

    /**
     * Rule 5: Sanitize user input (removes script injection, html tags, dangerous SQL control chars)
     */
    fun sanitizeInput(input: String, maxLength: Int = 100): String {
        var clean = input.trim()
        if (clean.length > maxLength) {
            clean = clean.substring(0, maxLength)
        }
        // Remove script tags, angle brackets, SQL comment tokens, and null bytes
        clean = clean.replace(Regex("<[^>]*>"), "")
            .replace("--", "")
            .replace("/*", "")
            .replace("*/", "")
            .replace(";", "")
            .replace("\u0000", "")
        return clean
    }

    /**
     * Rule 4: Rate Limiting / DoS Prevention
     */
    @Synchronized
    fun isRateLimited(actionKey: String): Boolean {
        val now = System.currentTimeMillis()
        val lastTime = actionRateLimitMap[actionKey] ?: 0L
        if (now - lastTime < MIN_ACTION_INTERVAL_MS) {
            return true
        }
        actionRateLimitMap[actionKey] = now
        return false
    }

    /**
     * Rule 2 & 3: Admin PIN Verification (RBAC & Authentication)
     */
    fun isSessionUnlocked(): Boolean {
        val now = System.currentTimeMillis()
        return (now - lastAdminUnlockTimestamp) < SESSION_TIMEOUT_MS
    }

    fun verifyPin(enteredPin: String): Boolean {
        return if (enteredPin == adminPin) {
            lastAdminUnlockTimestamp = System.currentTimeMillis()
            true
        } else {
            false
        }
    }

    fun setAdminPin(currentPin: String, newPin: String): Boolean {
        if (currentPin == adminPin && newPin.length == 4 && newPin.all { it.isDigit() }) {
            adminPin = newPin
            lastAdminUnlockTimestamp = System.currentTimeMillis()
            return true
        }
        return false
    }

    fun lockSession() {
        lastAdminUnlockTimestamp = 0L
    }
}
