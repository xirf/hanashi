package com.andka.hanashi.ui.homepage.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.andka.hanashi.databinding.FragmentHomeBinding
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.utils.Locator
import com.andka.hanashi.utils.ResultState
import com.andka.hanashi.utils.SlideUpItemAnimator
import com.andka.hanashi.view.MyRecyclerView.ViewStatus
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<HomeViewModel>(factoryProducer = { Locator.profileViewModelFactory })
    private val rvStory by lazy { binding.rvStory }
    private val swipeRefreshLayout by lazy { binding.swipeContainer }
    private val adapter = StoryAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderRecyclerView()
        fetchAllStories()
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            viewModel.getStories()
        }
    }

    private fun renderRecyclerView() {
        rvStory.recyclerView.adapter = adapter
        rvStory.recyclerView.layoutManager = GridLayoutManager(context, 2)
        rvStory.setOnRetryClickListener {
            viewModel.getStories()
        }
        rvStory.recyclerView.itemAnimator = SlideUpItemAnimator()
    }

    private fun fetchAllStories() {
        lifecycleScope.launch {
            viewModel.getStories()
            viewModel.storyState.collect { state ->
                when (state.resultGetStory) {
                    is ResultState.Success -> {
                        val data = state.resultGetStory.data
                        if (data != null) {
                            if (data.isEmpty()) {
                                rvStory.showView(ViewStatus.EMPTY)
                            } else {
                                rvStory.showView(ViewStatus.ON_DATA)
                                adapter.setData(data as ArrayList<StoryEntity>)
                            }
                        }
                    }

                    is ResultState.Error -> rvStory.showView(ViewStatus.ERROR)

                    is ResultState.Loading -> rvStory.showView(ViewStatus.LOADING)

                    is ResultState.Idle -> {}
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}