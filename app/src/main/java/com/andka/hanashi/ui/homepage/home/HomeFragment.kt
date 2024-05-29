package com.andka.hanashi.ui.homepage.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.andka.hanashi.R
import com.andka.hanashi.databinding.FragmentHomeBinding
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.ui.homepage.MainActivity
import com.andka.hanashi.ui.login.LoginActivity
import com.andka.hanashi.utils.Locator
import com.andka.hanashi.utils.ResultState
import com.andka.hanashi.utils.SlideUpItemAnimator
import com.andka.hanashi.utils.showToast
import com.andka.hanashi.widget.MyRecyclerView.ViewStatus
import kotlinx.coroutines.launch
import java.time.LocalTime


class HomeFragment : Fragment() {
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<HomeViewModel>(factoryProducer = { Locator.homeViewModelFactory })
    private val rvStory by lazy { binding.rvStory }
    private val swipeRefreshLayout by lazy { binding.swipeContainer }
    private val adapter = StoryAdapter()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderRecyclerView()
        runSetup()
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            viewModel.getStories()
        }

        val tod = resources.getStringArray(R.array.tod)
        val currentTime = LocalTime.now().hour

        when (currentTime) {
            in 0..5 -> binding.tvGreeting.text = tod[0]
            in 6..11 -> binding.tvGreeting.text = tod[1]
            in 12..17 -> binding.tvGreeting.text = tod[2]
            in 18..23 -> binding.tvGreeting.text = tod[3]
        }
    }

    private fun runSetup() {
        viewModel.getUser()
        binding.actionLogout.setOnClickListener {
            viewModel.logout()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            showToast(requireContext(), getString(R.string.see_you_later))
            (activity as MainActivity).finish()
        }
    }

    override fun onStart() {
        super.onStart()
        fetchAllStories()
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
                binding.tvUsername.text = state.username
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}