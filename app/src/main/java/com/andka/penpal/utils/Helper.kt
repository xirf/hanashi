package com.andka.penpal.utils

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

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