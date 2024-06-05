package com.andka.hanashi.ui.homepage.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andka.hanashi.databinding.StoryItemBinding
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.ui.detail_story.DetailActivity
import com.andka.hanashi.utils.getTimelineUpload
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

class StoryAdapter : PagingDataAdapter<StoryEntity, StoryAdapter.MyViewHolder>(itemDiffCallback) {

    class MyViewHolder(private val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val shimmerDrawable = ShimmerDrawable()
        fun bind(story: StoryEntity) {
            val shimmer = Shimmer.AlphaHighlightBuilder()
                .setBaseAlpha(1f)
                .setHighlightAlpha(0.8f)
                .setAutoStart(true)
                .setTilt(32f)
                .build()
            shimmerDrawable.setShimmer(shimmer)
            with(binding) {
                tvItemName.text = story.name
                tvItemDesc.text = story.description
                tvCreatedAt.text = getTimelineUpload(root.context, story.createdAt)
                Glide.with(root)
                    .load(story.photoUrl)
                    .placeholder(shimmerDrawable)
                    .into(ivItemPhoto)

                root.setOnClickListener {
                    val intent = Intent(root.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_STORY_ID, story.id)
                    intent.putExtra(DetailActivity.EXTRA_TITLE, story.name)
                    intent.putExtra(DetailActivity.EXTRA_IMAGE, story.photoUrl)

                    it.context.startActivity(intent)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    companion object {
        val itemDiffCallback = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}
