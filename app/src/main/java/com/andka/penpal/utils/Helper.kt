package com.andka.penpal.utils

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.andka.penpal.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private var toast: Toast? = null

fun showToast(context: Context, message: String) {
    toast?.cancel()
    toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
    toast?.show()
}

fun validateField(field: EditText, errorString: String): Boolean {
    return if (field.text.toString().isEmpty()) {
        field.error = errorString
        field.requestFocus()
        false
    } else {
        true
    }
}

private const val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

fun getCurrentDate(): Date {
    return Date()
}

fun parseDate(date: String): Date? {
    return sdf.parse(date)
}

fun getCurrentDateString(): String = sdf.format(getCurrentDate())

fun getHourPass(date: Date): Long {
    val diff = System.currentTimeMillis() - date.time
    val diffHours = diff / (60 * 60 * 1000)
    return diffHours
}

private fun parseUTCDate(timestamp: String): Date {
    return try {
        val formatter = SimpleDateFormat(timestampFormat, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        formatter.parse(timestamp) as Date
    } catch (e: ParseException) {
        getCurrentDate()
    }
}

fun getTimelineUpload(context: Context, timestamp: String): String {
    val currentTime = getCurrentDate()
    val uploadTime = parseUTCDate(timestamp)
    val diff: Long = currentTime.time - uploadTime.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val label = when (minutes.toInt()) {
        0 -> "$seconds ${context.getString(R.string.text_seconds_ago)}"
        in 1..59 -> "$minutes ${context.getString(R.string.text_minutes_ago)}"
        in 60..1440 -> "$hours ${context.getString(R.string.text_hours_ago)}"
        else -> "$days ${context.getString(R.string.text_days_ago)}"
    }
    return label
}