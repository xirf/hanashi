package com.andka.hanashi.ui.homepage

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
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

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {
    private val listStory = ArrayList<StoryEntity>()
    private val shimmerDrawable = ShimmerDrawable()

    init {
        val shimmer = Shimmer.AlphaHighlightBuilder()
            .setBaseAlpha(0.7f)
            .setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

        shimmerDrawable.setShimmer(shimmer)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = StoryItemBinding.bind(itemView)
        fun bind(user: StoryEntity, shimmerDrawable: Drawable) {
            with(binding) {
                title.text = user.name
                Glide.with(itemView.context)
                    .load(user.photoUrl)
                    .placeholder(shimmerDrawable)
                    .into(photo)
                description.text = user.description
                createdAt.text = getTimelineUpload(itemView.context, user.createdAt)

            }
        }
    }

    fun setData(items: ArrayList<StoryEntity>) {
        val isDifferent = DiffUtil.calculateDiff(DiffUtilCallback(listStory, items))
        listStory.clear()
        listStory.addAll(items)
        isDifferent.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding.root)
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position], shimmerDrawable)
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_STORY_ID, listStory[position].id)
            intent.putExtra(DetailActivity.EXTRA_IMAGE, listStory[position].photoUrl)

            it.context.startActivity(intent)
        }

        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_up)
        animation.startOffset = position * 100L
        animation.interpolator = DecelerateInterpolator()
        holder.itemView.startAnimation(animation)
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