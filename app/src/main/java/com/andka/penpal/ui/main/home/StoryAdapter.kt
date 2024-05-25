package com.andka.penpal.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andka.penpal.databinding.StoryItemBinding
import com.andka.penpal.domain.ListStoryItem
import com.andka.penpal.utils.getTimelineUpload
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {
    private val listStory = ArrayList<ListStoryItem>()
    private val loaderList = ArrayList<Int>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = StoryItemBinding.bind(itemView)
        fun bind(user: ListStoryItem, position: Int) {
            with(binding) {
                val shimmer = Shimmer.AlphaHighlightBuilder()
                    .setBaseAlpha(0.7f)
                    .setHighlightAlpha(0.6f)
                    .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                    .setAutoStart(true)
                    .build()

                val shimmerDrawable = ShimmerDrawable().apply {
                    setShimmer(shimmer)
                }

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

    fun setData(items: ArrayList<ListStoryItem>) {
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
        holder.bind(listStory[position], position)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listStory[holder.adapterPosition])
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    class DiffUtilCallback(
        private val oldList: List<ListStoryItem>,
        private val newList: List<ListStoryItem>
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