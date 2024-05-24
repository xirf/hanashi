package com.andka.penpal.ui.auth

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import com.andka.penpal.R
import com.andka.penpal.databinding.FragmentLoginBinding
import com.andka.penpal.ui.main.MainActivity
import com.andka.penpal.utils.UserPreferences
import com.andka.penpal.utils.datastore
import com.andka.penpal.utils.showToast
import com.andka.penpal.utils.validateField
import com.andka.penpal.viewmodels.AuthViewModel
import com.andka.penpal.viewmodels.UserViewModel
import com.andka.penpal.viewmodels.factory.UserViewModelFactory

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var userViewModel: UserViewModel
    private val viewModel by lazy { ViewModelProvider(this)[AuthViewModel::class.java] }
    private var email: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transition = TransitionInflater
            .from(requireContext())
            .inflateTransition(android.R.transition.move)

        transition?.epicenterCallback = object : Transition.EpicenterCallback() {
            override fun onGetEpicenter(transition: Transition): Rect {
                return Rect(binding.root.width / 2, binding.root.height / 2, 0, 0)
            }
        }
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences.getInstance((activity as AuthActivity).datastore)
        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(userPreferences)
        )[UserViewModel::class.java]

        with(binding) {
            edLoginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            btnRegister.setOnClickListener { goToRegister() }
            btnLogin.setOnClickListener {
                if (!validateField(
                        edLoginEmail,
                        getString(R.string.empty_field)
                    )
                ) return@setOnClickListener
                if (!validateField(
                        edLoginPassword,
                        getString(R.string.empty_field)
                    )
                ) return@setOnClickListener
                if ((edLoginPassword.error?.length ?: 0) > 0) return@setOnClickListener
                email = edLoginEmail.text.toString()
                viewModel.doLogin(email!!, edLoginPassword.text.toString())
            }
        }

        listenToRegisterResult()
        checkLoading()
        checkError()
    }

    private fun checkError() {
        viewModel.errorContent.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Log.d("LoginFragment", "Error: $error")
                showToast(requireContext(), error)
            }
        }
    }

    private fun listenToRegisterResult() {
        viewModel.loginResponse.observe(viewLifecycleOwner) { response ->
            if (response?.error == false) {
                userViewModel.setUserPreferences(
                    token = response.loginResult?.token!!,
                    userId = response.loginResult.userId!!,
                    userName = response.loginResult.name!!,
                    userEmail = email!!
                )
                showToast(requireContext(), getString(R.string.login_success))
                goToMainActivity()
            } else {
                Log.d("LoginFragment", "Login failed: ${response.message}")
                showToast(requireContext(), getString(R.string.login_failed))
            }
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
    }

    private fun checkLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.loadingComponents.root.visibility = View.VISIBLE
            } else {
                binding.loadingComponents.root.visibility = View.GONE
            }
        }
    }

    private fun goToRegister() {
        parentFragmentManager.commit {
            addSharedElement(binding.edLoginEmail, "email")
            addSharedElement(binding.edLoginPassword, "password")
            addSharedElement(binding.headline, "headline")
            addSharedElement(binding.subHeadline, "subHeadline")
            addSharedElement(binding.btnContainer, "btnContainer")
            setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            replace(R.id.main, RegisterFragment(), RegisterFragment::class.java.simpleName)
            addToBackStack(null)
        }
    }

    companion object {
        fun newInstance() = LoginFragment()
    }
}