package com.andka.hanashi.ui.homepage.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andka.hanashi.R
import com.andka.hanashi.databinding.StoryItemBinding
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.ui.detail_story.DetailActivity
import com.andka.hanashi.utils.getTimelineUpload
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

class StoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listStory = ArrayList<StoryEntity>()

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = StoryItemBinding.bind(itemView)
        fun bind(user: StoryEntity) {
            val shimmerDrawable = ShimmerDrawable()
            val shimmer = Shimmer.AlphaHighlightBuilder()
                .setBaseAlpha(1f)
                .setHighlightAlpha(0.7f)
                .setAutoStart(true)
                .setTilt(32f)
                .build()

            shimmerDrawable.setShimmer(shimmer)

            with(binding) {
                tvItemName.text = user.name
                Glide.with(itemView.context)
                    .load(user.photoUrl)
                    .placeholder(shimmerDrawable)
                    .into(ivItemPhoto)
                tvItemDesc.text = user.description
                tvCreatedAt.text = getTimelineUpload(itemView.context, user.createdAt)
            }
        }
    }

    fun setData(newItems: ArrayList<StoryEntity>) {
        val oldList = ArrayList(listStory)
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(oldList, newItems))
        listStory.clear()
        listStory.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding.root)
    }

    override fun getItemViewType(position: Int): Int {
        return if (listStory.isEmpty()) 1 else 0
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ListViewHolder && listStory.isNotEmpty()) {
            holder.bind(listStory[position])
            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_STORY_ID, listStory[position].id)
                intent.putExtra(DetailActivity.EXTRA_IMAGE, listStory[position].photoUrl)

                it.context.startActivity(intent)
            }
        }
    }

    class DiffUtilCallback(
        private val oldList: List<StoryEntity>,
        private val newList: List<StoryEntity>
    ) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.javaClass == newItem.javaClass
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem == newItem
        }
    }
}