package com.andka.penpal.ui.auth

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.andka.penpal.R
import com.andka.penpal.databinding.FragmentLoginBinding
import com.andka.penpal.utils.UserPreferences
import com.andka.penpal.utils.datastore
import com.andka.penpal.viewmodels.UserViewModel
import com.andka.penpal.viewmodels.factory.UserViewModelFactory
import com.bumptech.glide.load.Transformation

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var userViewModel: UserViewModel
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

        binding.edLoginPassword.transformationMethod = PasswordTransformationMethod.getInstance()

    }

    companion object {
        fun newInstance() = LoginFragment()
    }
}