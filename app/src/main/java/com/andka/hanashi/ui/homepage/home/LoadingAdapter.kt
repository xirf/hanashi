package com.andka.hanashi.ui.homepage.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andka.hanashi.databinding.LoadingItemBinding

class LoadingAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingAdapter.LoadingViewHolder>() {
    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        val binding = LoadingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding, retry)
    }

    class LoadingViewHolder(private val binding: LoadingItemBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }

            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.btnRetry.isVisible = loadState !is LoadState.Loading
            binding.errorMsg.isVisible = loadState !is LoadState.Loading

        }
    }
}