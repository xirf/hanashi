package com.andka.hanashi.ui.homepage.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.andka.hanashi.R
import com.andka.hanashi.databinding.FragmentHomeBinding
import com.andka.hanashi.ui.homepage.MainActivity
import com.andka.hanashi.ui.login.LoginActivity
import com.andka.hanashi.utils.Locator
import com.andka.hanashi.utils.SlideUpItemAnimator
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment() {
    private val adapter by lazy { StoryAdapter() }
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<HomeViewModel>(factoryProducer = { Locator.homeViewModelFactory })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderRecyclerView()
        runSetup()
        binding.actionLogout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            (activity as MainActivity).finish()
        }
    }

    private fun runSetup() {
        lifecycleScope.launch {
            viewModel.storyState.collect {
                adapter.submitData(lifecycle, it.resultGetStory)
                binding.tvUsername.text = it.username
            }
        }
        getGreeting()
    }

    private fun getGreeting() {
        val tod = resources.getStringArray(R.array.tod)
        val currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        when (currentTime) {
            in 0..5 -> binding.tvGreeting.text = tod[0]
            in 6..11 -> binding.tvGreeting.text = tod[1]
            in 12..17 -> binding.tvGreeting.text = tod[2]
            in 18..23 -> binding.tvGreeting.text = tod[3]
        }
    }

    private fun renderRecyclerView() {
        binding.rvStory.adapter = adapter.withLoadStateFooter(footer = LoadingAdapter {
            adapter.retry()
        })
        binding.rvStory.layoutManager = GridLayoutManager(context, 2)
        binding.rvStory.itemAnimator = SlideUpItemAnimator()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getUser()
        viewModel.getStories()
    }
}
