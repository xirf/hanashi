package com.andka.hanashi.ui.new_story

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.andka.hanashi.R
import com.andka.hanashi.databinding.ActivityNewStoryBinding
import com.andka.hanashi.utils.*
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class NewStoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityNewStoryBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<NewStoryViewModel> { Locator.newStoryViewModelFactory }
    private val permissionHandler by lazy { PermissionHandler(this) }
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val shimmerDrawable = ShimmerDrawable()

    private var imageStatus = ImageStatus.UNLOADED
    private var selectedFile: File? = null
    private var imagePath: String? = null
    private var userLocation: LatLng? = null

    private val launcherCameraIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                processImage(imagePath)
            }
        }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.data?.let { uri ->
                    processImage(uri.toFile(baseContext).path)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        savedInstanceState?.getString(IMAGE_PATH_KEY)?.let {
            File(it).also { file ->
                selectedFile = file
                binding.ivPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }

        shimmerDrawable.setShimmer(createShimmer())

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.btnCamera.setOnClickListener { handleCameraClick() }
        binding.btnGalery.setOnClickListener { getFromGallery() }
        binding.buttonBack.setOnClickListener { finish() }
        binding.buttonAdd.setOnClickListener { postStory() }
        binding.btnLocation.setOnClickListener { handleLocationClick() }
    }

    private fun setupListeners() {
        lifecycleScope.launch {
            viewModel.newStoryState.collect { state ->
                when (val result = state.resultNewStory) {
                    is ResultState.Success -> handleSuccess()
                    is ResultState.Loading -> showLoading(true)
                    is ResultState.Error -> handleError(result.message)
                    else -> showLoading(false)
                }
            }
        }
    }

    private fun handleCameraClick() {
        if (permissionHandler.checkPermission(PermissionHandler.REQUIRED_CAMERA_PERMISSION)) {
            takePicture()
        } else {
            permissionHandler.requestPermissions(
                PermissionHandler.REQUIRED_CAMERA_PERMISSION,
                PermissionHandler.CAMERA_CODE
            )
        }
    }

    private fun handleLocationClick() {
        if (permissionHandler.checkPermission(PermissionHandler.REQUIRED_LOCATION_PERMISSION)) {
            getLocation()
        } else {
            permissionHandler.requestPermissions(
                PermissionHandler.REQUIRED_LOCATION_PERMISSION,
                PermissionHandler.LOCATION_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        binding.locationLoading.visibility = View.VISIBLE
        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            binding.locationLoading.visibility = View.GONE
            loc?.let {
                userLocation = LatLng(it.latitude, it.longitude)
                binding.tvLocation.text =
                    getString(R.string.location_format, it.latitude, it.longitude)
            } ?: run {
                binding.btnLocation.isChecked = false
                binding.tvLocation.text = ""
                showToast(this, getString(R.string.unknown_location))
            }
        }
    }

    private fun postStory() {
        val description = binding.edAddDescription.text.toString().takeIf { it.isNotEmpty() }
            ?: run {
                binding.edAddDescription.error = getString(R.string.empty_field)
                return
            }

        if (imageStatus == ImageStatus.LOADING) {
            showToast(this, getString(R.string.image_loading))
        } else {
            selectedFile?.let {
                viewModel.newStory(it, description.trim())
            } ?: showToast(this, getString(R.string.no_image))
        }
    }

    @Suppress("QueryPermissionsNeeded")
    private fun takePicture() {
        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, CODE_PERMISSIONS)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)?.let {
                makeTempFile(baseContext).also { file ->
                    val uri = FileProvider.getUriForFile(this, packageName, file)
                    imagePath = file.absolutePath
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    launcherCameraIntent.launch(intent)
                }
            }
        }
    }

    private fun processImage(img: String?, source: String? = null) {
        binding.ivPreview.setImageDrawable(shimmerDrawable)
        imageStatus = ImageStatus.LOADING

        lifecycleScope.launch(Dispatchers.IO) {
            img?.let { path ->
                File(path).let { file ->
                    if (source != null) file.fixImageOrientation()
                    selectedFile = file.compressImage()

                    withContext(Dispatchers.Main) {
                        binding.ivPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
                        imageStatus = ImageStatus.LOADED
                    }
                }
            }
        }
    }

    private fun getFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, getString(R.string.take_image))
        launcherIntentGallery.launch(chooser)
    }

    private fun checkPermissions() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionHandler.CAMERA_CODE -> {
                if (permissionHandler.checkPermission(PermissionHandler.REQUIRED_CAMERA_PERMISSION)) {
                    takePicture()
                } else {
                    showToast(this, getString(R.string.no_permissions))
                }
            }

            PermissionHandler.LOCATION_CODE -> {
                if (permissionHandler.checkPermission(PermissionHandler.REQUIRED_LOCATION_PERMISSION)) {
                    getLocation()
                } else {
                    showToast(this, getString(R.string.no_permissions))
                }
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.loading.root.visibility = if (state) View.VISIBLE else View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        imagePath?.takeIf { it.isNotEmpty() }?.let {
            outState.putString(IMAGE_PATH_KEY, it)
        }
    }

    private fun handleSuccess() {
        showLoading(false)
        showToast(this, getString(R.string.posted))
        Log.d("NewStoryActivity", "handleSuccess: Sending broadcast")
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(BROADCAST_ACTION))
        finish()
    }

    private fun handleError(message: String?) {
        showLoading(false)
        val finalErrorMessage =
            getString(R.string.post_failed) + getString(R.string.reason) + (message ?: "")
        showToast(this, finalErrorMessage)
    }

    private fun createShimmer() = Shimmer.AlphaHighlightBuilder()
        .setBaseAlpha(0.7f)
        .setHighlightAlpha(0.6f)
        .setTilt(45f)
        .setAutoStart(true)
        .build()

    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val CODE_PERMISSIONS = 10
        private const val IMAGE_PATH_KEY = "image_path"
        const val BROADCAST_ACTION = "com.andka.hanashi.NEW_STORY_ADDED"
    }

    enum class ImageStatus {
        LOADING,
        LOADED,
        UNLOADED
    }
}
