package com.andka.hanashi.ui.detail_story

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.andka.hanashi.databinding.ActivityDetailBinding
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.utils.Locator
import com.andka.hanashi.utils.ResultState
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<DetailViewModel>(factoryProducer = { Locator.detailViewModelFactory })
    private val shimmerDrawable = ShimmerDrawable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        intent.getStringExtra(EXTRA_STORY_ID)?.let {
            viewModel.getDetail(it)
        }


        val shimmer = Shimmer.AlphaHighlightBuilder()
            .setBaseAlpha(0.7f)
            .setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

        shimmerDrawable.setShimmer(shimmer)

        val imgUrl = intent.getStringExtra(EXTRA_IMAGE)
        Glide.with(this)
            .load(imgUrl)
            .placeholder(shimmerDrawable)
            .into(binding.ivDetailPhoto)
        loadingObserver()
    }

    private fun renderDetail(result: StoryEntity?) {
        if (result != null) {
            with(result) {
                binding.tvDetailName.text = name
                binding.tvDetailDescription.text = description
            }
        }
    }

    private fun loadingObserver() {
        lifecycleScope.launch {
            viewModel.detailViewState.collect {
                when (it.resultGetDetail) {
                    is ResultState.Error -> {}
                    is ResultState.Idle -> {}
                    is ResultState.Loading -> {}
                    is ResultState.Success -> {
                        renderDetail(it.resultGetDetail.data)
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
        const val EXTRA_IMAGE = "extra_image"
    }
}