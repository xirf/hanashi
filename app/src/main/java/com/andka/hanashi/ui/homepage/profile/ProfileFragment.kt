package com.andka.hanashi.ui.homepage.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.andka.hanashi.databinding.FragmentProfileBinding
import com.andka.hanashi.ui.homepage.home.HomeViewModel
import com.andka.hanashi.ui.homepage.home.StoryAdapter
import com.andka.hanashi.utils.Locator

class ProfileFragment : Fragment() {
    private val binding by lazy { FragmentProfileBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<HomeViewModel>(factoryProducer = { Locator.profileViewModelFactory })
    private val adapter = StoryAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderRecyclerView()

    }

    private fun renderRecyclerView() {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}