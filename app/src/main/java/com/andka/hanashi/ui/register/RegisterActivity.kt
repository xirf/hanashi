package com.andka.hanashi.ui.register

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
import com.andka.hanashi.databinding.ActivityRegisterBinding
import com.andka.hanashi.ui.login.LoginActivity
import com.andka.hanashi.utils.Locator
import com.andka.hanashi.utils.ResultState
import com.andka.hanashi.utils.showToast
import com.andka.hanashi.utils.validateField
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<RegisterViewModel>(factoryProducer = { Locator.registerViewModelFactory })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupButton()

        lifecycleScope.launch {
            viewModel.registerState.collect {
                loadingObserver(it)
                when (it.resultRegister) {
                    is ResultState.Success<String> -> toLogin()
                    is ResultState.Error<String> -> {
                        showToast(baseContext, getString(R.string.register_failed))
                    }

                    else -> Unit
                }

            }
        }
    }

    private fun setupButton() {
        with(binding) {
            btnLogin.setOnClickListener { toLogin() }
            btnRegister.setOnClickListener { checkRegister() }
        }
    }

    private fun checkRegister() {
        binding.apply {
            val name = edRegisterName.text.toString()
            val email = edRegisterEmail.text.toString()
            val password = edRegisterPassword.text.toString()

            if (isValidInput()) {
                viewModel.register(name, email, password)
            }
        }
    }

    private fun isValidInput(): Boolean {
        val errorMessage = getString(R.string.empty_field)

        return with(binding) {
            validateField(edRegisterName, errorMessage) &&
                    validateField(edRegisterEmail, errorMessage) &&
                    validateField(edRegisterPassword, errorMessage) &&
                    edRegisterPassword.error.isNullOrEmpty() &&
                    edRegisterEmail.error.isNullOrEmpty()
        }
    }


    private fun toLogin() {
        val intent =
            Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val optionsCompact = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            Pair(binding.edRegisterEmail, "email"),
            Pair(binding.edRegisterPassword, "password"),
            Pair(binding.subHeadline, "subHeadline"),
            Pair(binding.haveAccount, "misc"),
            Pair(binding.imageView, "logo")
        )
        startActivity(intent, optionsCompact.toBundle())
        finish()
    }

    private fun loadingObserver(state: RegisterViewModel.RegisterViewState) {
        val isVisible = state.resultRegister is ResultState.Loading<String>
        binding.loadingComponents.root.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}