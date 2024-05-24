package com.andka.penpal.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.andka.penpal.R
import com.andka.penpal.databinding.FragmentHomeBinding
import com.andka.penpal.utils.UserPreferences
import com.andka.penpal.utils.datastore
import com.andka.penpal.viewmodels.StoryPaginationViewModel
import com.andka.penpal.viewmodels.UserViewModel
import com.andka.penpal.viewmodels.factory.UserViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: StoryPaginationViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var userPreferences: UserPreferences
    private val adapter: StoryAdapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.rvStory.adapter = adapter

        userPreferences = UserPreferences.getInstance(requireContext().datastore)
        viewModel = ViewModelProvider(this)[StoryPaginationViewModel::class.java]
        userViewModel = ViewModelProvider(this, UserViewModelFactory(userPreferences))[UserViewModel::class.java]

        getAllStories()

        return binding.root
    }

    private fun getAllStories() {
        lifecycleScope.launch {
            userPreferences.getUserToken().collect { token ->
                viewModel.getAllStories(token, 0)
                viewModel.stories.observe(viewLifecycleOwner) {
                    if (it != null) {
                        adapter.setData(it.listStory)
                        renderRecyclerView()
                    }
                }
            }
        }
    }

    private fun renderRecyclerView() {
        binding.rvStory.layoutManager = LinearLayoutManager(context)
        adapter.setOnItemClickCallback {
//            val intent = Intent(requireContext(), DetailActivity::class.java)
            // TODO: Add intent.putExtra
        }

    }

}