package com.andka.penpal.ui.auth

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import com.andka.penpal.R
import com.andka.penpal.databinding.FragmentRegisterBinding
import com.andka.penpal.utils.showToast
import com.andka.penpal.utils.validateField
import com.andka.penpal.viewmodels.AuthViewModel


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by lazy { AuthViewModel() }
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
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            edRegisterPassword.transformationMethod = PasswordTransformationMethod.getInstance()

            btnRegister.setOnClickListener {
                if (!validateField(
                        edRegisterName,
                        getString(R.string.empty_field)
                    )
                ) return@setOnClickListener
                if (!validateField(
                        edRegisterEmail,
                        getString(R.string.empty_field)
                    )
                ) return@setOnClickListener
                if (!validateField(
                        edRegisterPassword,
                        getString(R.string.empty_field)
                    )
                ) return@setOnClickListener

                if ((edRegisterPassword.error?.length ?: 0) > 0) {
                    return@setOnClickListener
                }
                if ((edRegisterEmail.error?.length ?: 0) > 0) {
                    return@setOnClickListener
                }

                viewModel.doRegister(
                    edRegisterName.text.toString(),
                    edRegisterEmail.text.toString(),
                    edRegisterPassword.text.toString()
                )
            }
            btnLogin.setOnClickListener { goToLogin() }
        }

        listenToRegisterResult()
        checkLoading()
    }

    private fun listenToRegisterResult() {
        viewModel.registerResponse.observe(viewLifecycleOwner) { response ->
            if (!response.error) {
                showToast(requireContext(), getString(R.string.register_success))
                goToLogin()
            } else {
                showToast(requireContext(), getString(R.string.register_failed))
            }
        }
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

    companion object {
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }

    private fun goToLogin() {
        viewModel.errorContent.postValue(null)
        parentFragmentManager.commit {
            addSharedElement(binding.edRegisterEmail, "email")
            addSharedElement(binding.edRegisterPassword, "password")
            addSharedElement(binding.headline, "headline")
            addSharedElement(binding.subHeadline, "subHeadline")
            addSharedElement(binding.btnContainer, "btnContainer")
            setCustomAnimations(
                R.anim.slide_right,
                R.anim.slide_left,
            )
            replace(R.id.main, LoginFragment(), LoginFragment::class.java.simpleName)
            addToBackStack(null)
        }
    }
}