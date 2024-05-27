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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        intent.getStringExtra(EXTRA_STORY_ID)?.let {
            viewModel.getDetail(it)
        }
        loadingObserver()
    }

    private fun renderDetail(result: StoryEntity?) {
        if (result != null) {
            with(result) {
                binding.tvDetailName.text = name
                binding.tvDetailDescription.text = description

                val shimmer = Shimmer.AlphaHighlightBuilder()
                    .setDuration(1800)
                    .setBaseAlpha(0.7f)
                    .setHighlightAlpha(0.6f)
                    .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                    .setAutoStart(true)
                    .build()

                val shimmerDrawable = ShimmerDrawable().apply {
                    setShimmer(shimmer)
                }

                Glide.with(this@DetailActivity)
                    .load(photoUrl)
                    .placeholder(shimmerDrawable)
                    .into(binding.ivDetailPhoto)
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
    }
}