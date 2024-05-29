package com.andka.hanashi.ui.new_story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.andka.hanashi.R
import com.andka.hanashi.databinding.ActivityNewStoryBinding
import com.andka.hanashi.utils.Locator
import com.andka.hanashi.utils.ResultState
import com.andka.hanashi.utils.compressImage
import com.andka.hanashi.utils.fixImageOrientation
import com.andka.hanashi.utils.makeTempFile
import com.andka.hanashi.utils.showToast
import com.andka.hanashi.utils.toFile
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class NewStoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityNewStoryBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<NewStoryViewModel>(factoryProducer = { Locator.newStoryViewModelFactory })
    private var selectedFile: File? = null
    private var imagePath: String? = null
    private val shimmerDrawable = ShimmerDrawable()
    private var imageStatus = ImageStatus.UNLOADED

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

        val shimmer = Shimmer.AlphaHighlightBuilder()
            .setDuration(1800)
            .setBaseAlpha(0.7f)
            .setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()
        shimmerDrawable.setShimmer(shimmer)

        setupButton()
        setupListener()
    }


    private fun setupListener() {
        lifecycleScope.launch {
            viewModel.newStoryState.collect { state ->
                when (state.resultNewStory) {
                    is ResultState.Success<String> -> {
                        showLoading(false)
                        showToast(this@NewStoryActivity, getString(R.string.posted))
                        finish()
                    }

                    is ResultState.Loading -> showLoading(true)
                    is ResultState.Error -> {
                        showLoading(false)
                        val errorMessage = state.resultNewStory.message
                        val finalErrorMessage = StringBuilder()
                            .append(getString(R.string.post_failed))
                            .append(getString(R.string.reason))
                            .append(errorMessage).toString()

                        showToast(this@NewStoryActivity, finalErrorMessage)
                    }

                    else -> showLoading(false)
                }
            }
        }
    }

    private fun setupButton() {
        binding.btnCamera.setOnClickListener { takePicture() }
        binding.btnGalery.setOnClickListener { getFromGallery() }
        binding.buttonBack.setOnClickListener { finish() }
        binding.buttonAdd.setOnClickListener { postStory() }
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
            intent.resolveActivity(packageManager)

            makeTempFile(baseContext).also {
                val uri = FileProvider.getUriForFile(
                    this@NewStoryActivity, packageName, it
                )

                imagePath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                launcherCameraIntent.launch(intent)
            }
        }
    }


    private val launcherCameraIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            processImage(imagePath)
        }
    }

    private fun processImage(img: String?, source: String? = null) {
        showImagePlaceholder()
        imageStatus = ImageStatus.LOADING
        // Offload the IO to speed up camera intent
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

    private fun showImagePlaceholder() {
        binding.ivPreview.setImageDrawable(shimmerDrawable)
    }

    private fun getFromGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.take_image))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            processImage(selectedImg.toFile(baseContext).path)
        }
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
        if (requestCode == CODE_PERMISSIONS) {
            if (!checkPermissions()) {
                showToast(this, getString(R.string.no_permissions))
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.loading.root.visibility = if (state) View.VISIBLE else View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (imagePath?.isNotEmpty() == true) {
            outState.putString(IMAGE_PATH_KEY, imagePath)
        }
    }

    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val CODE_PERMISSIONS = 10
        private const val IMAGE_PATH_KEY = "image_path"
    }

    enum class ImageStatus {
        LOADING,
        LOADED,
        UNLOADED
    }
}