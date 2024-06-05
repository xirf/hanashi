package com.andka.hanashi.ui.homepage

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.andka.hanashi.R
import com.andka.hanashi.databinding.ActivityMainBinding
import com.andka.hanashi.ui.homepage.home.HomeFragment
import com.andka.hanashi.ui.homepage.maps.MapsFragment
import com.andka.hanashi.ui.homepage.profile.ProfileFragment
import com.andka.hanashi.ui.login.LoginActivity
import com.andka.hanashi.ui.new_story.NewStoryActivity
import com.andka.hanashi.utils.Locator
import com.andka.hanashi.utils.ResultState

private const val ANIMATION_DURATION = 500L

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainActivityViewModel>(factoryProducer = { Locator.mainActivityViewModelFactory })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContentView(binding.root)
        setupSplashScreen()

        setupNavigation()
        binding.buttonAdd.setOnClickListener { moveToAdd() }
    }

    private fun setupNavigation() {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment())
            .commit()
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                R.id.navigation_maps -> {
                    loadFragment(MapsFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(fragment.tag)
            .commit()
    }

    private fun setupSplashScreen() {
        val content: View = binding.root
        content.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val isLoggedIn = viewModel.isLoggedIn.value

                if (isLoggedIn.resultGetUser is ResultState.Success) {
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    if (isLoggedIn.resultGetUser.data == false) {
                        navigateToLogin()
                    }
                    return true
                } else {
                    return false
                }
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setupSplashScreenExitAnimation()
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupSplashScreenExitAnimation() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView, View.TRANSLATION_Y, 0f, splashScreenView.height.toFloat()
            )
            val alpha = ObjectAnimator.ofFloat(splashScreenView, View.ALPHA, 1f, 0f)

            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = ANIMATION_DURATION
            alpha.duration = ANIMATION_DURATION

            slideUp.doOnEnd { splashScreenView.remove() }

            alpha.start()
            slideUp.start()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    private fun moveToAdd() {
        startActivity(Intent(this, NewStoryActivity::class.java))
    }
}