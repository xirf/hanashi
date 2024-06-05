package com.andka.hanashi.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHandler(private val context: Context) {

    companion object {
        val REQUIRED_CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        val REQUIRED_LOCATION_PERMISSION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        const val CAMERA_CODE = 10
        const val LOCATION_CODE = 11
    }

    fun checkPermission(permission: Array<String>): Boolean {
        return permission.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
    }

    fun requestPermissions(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(context as AppCompatActivity, permissions, requestCode)
    }
}