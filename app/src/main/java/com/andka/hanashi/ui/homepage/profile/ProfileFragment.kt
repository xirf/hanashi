package com.andka.hanashi.ui.homepage.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.andka.hanashi.databinding.FragmentProfileBinding
import com.andka.hanashi.ui.homepage.MainActivity
import com.andka.hanashi.ui.homepage.home.StoryAdapter
import com.andka.hanashi.ui.login.LoginActivity
import com.andka.hanashi.utils.Locator
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private val binding by lazy { FragmentProfileBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<ProfileViewModel>(factoryProducer = { Locator.profileViewModelFactory })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUser()
        setupButton()
    }

    private fun setupButton() {
        lifecycleScope.launch {
            viewModel.userState.collect {
                binding.tvUsername.text = it.username
            }
        }
        binding.actionLogout.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            (activity as MainActivity).finish()
        }

        binding.actionChangeLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

}