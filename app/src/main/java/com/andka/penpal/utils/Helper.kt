package com.andka.penpal.utils

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

fun getCurrentDate(): Date {
    return Date()
}

fun parseDate(date: String): Date? {
    return sdf.parse(date)
}

fun getCurrentDateString(): String = sdf.format(getCurrentDate())
fun parseDateToString(date: Date): String = sdf.format(date).toString()
fun getDateString(date: String): String = parseDate(date)?.let { sdf.format(it).toString() } ?: date
val getTimestamp: String
    get() = System.currentTimeMillis().toString()

fun getHourPass(date: Date): Long {
    val diff = System.currentTimeMillis() - date.time
    val diffHours = diff / (60 * 60 * 1000)
    return diffHours
}