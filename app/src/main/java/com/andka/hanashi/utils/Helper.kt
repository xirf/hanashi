package com.andka.hanashi.utils

import android.content.Context
import android.widget.EditText
import android.widget.Toast

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