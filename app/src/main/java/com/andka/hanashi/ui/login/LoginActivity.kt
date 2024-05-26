package com.andka.hanashi.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import com.andka.hanashi.R
import com.andka.hanashi.databinding.ActivityLoginBinding
import com.andka.hanashi.ui.homepage.MainActivity
import com.andka.hanashi.ui.register.RegisterActivity
import com.andka.hanashi.utils.Locator
import com.andka.hanashi.utils.ResultState
import com.andka.hanashi.utils.showToast
import com.andka.hanashi.utils.validateField
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<LoginViewModel>(factoryProducer = { Locator.loginViewModelFactory })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initButton()

        lifecycleScope.launch {
            viewModel.isLoggedIn.collect {
                loadingObserver(it)
                when (it.resultVerifyUser) {
                    is ResultState.Success<String> -> toMainActivity()
                    is ResultState.Error<String> -> {
                        showToast(baseContext, getString(R.string.login_failed))
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun toMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initButton() {
        with(binding) {
            btnRegister.setOnClickListener {
                goToRegister()
            }
            btnLogin.setOnClickListener {
                checkLogin()
            }
        }
    }

    private fun checkLogin() {
        with(binding) {
            val email = edLoginEmail.text.toString()
            val password = edLoginPassword.text.toString()

            if (isValidInput()) {
                viewModel.login(email, password)
            }
        }
    }

    private fun isValidInput(): Boolean {
        val errorMessage = getString(R.string.empty_field)

        return with(binding) {
            validateField(edLoginEmail, errorMessage) &&
                    validateField(edLoginPassword, errorMessage) &&
                    edLoginEmail.error.isNullOrEmpty() &&
                    edLoginPassword.error.isNullOrEmpty()
        }
    }

    private fun goToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        val optionsCompact = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            Pair(binding.imageView, "logo"),
            Pair(binding.edLoginEmail, "email"),
            Pair(binding.edLoginPassword, "password"),
            Pair(binding.subHeadline, "subHeadline"),
            Pair(binding.noAccount, "misc")
        )
        startActivity(intent, optionsCompact.toBundle())
        finish()
    }

    private fun loadingObserver(state: LoginViewModel.LoginViewState) {
        val isVisible = state.resultVerifyUser is ResultState.Loading<String>
        binding.loadingComponents.root.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

}