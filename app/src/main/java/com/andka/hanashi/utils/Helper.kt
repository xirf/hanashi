package com.andka.hanashi.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.widget.EditText
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import com.andka.hanashi.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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
fun getCurrentDate(): Date {
    return Date()
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


fun Uri.toFile(context: Context): File {
    val inputStream = context.contentResolver.openInputStream(this) as InputStream
    val file = File(context.cacheDir, "tempFile")
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)
    return file
}

fun File.compressImage(): File {
    val bitmap = BitmapFactory.decodeFile(path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(this))
    return this
}

fun File.fixImageOrientation(): File {
    val bitmap = BitmapFactory.decodeFile(this.path)
    val exif = ExifInterface(this.path)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )
    val matrix = android.graphics.Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }
    val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    val out = FileOutputStream(this)
    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    out.flush()
    out.close()
    return this
}

fun makeTempFile(context: Context): File {
    val dir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(Date().toString(), ".jpg", dir)
}