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
import com.andka.hanashi.R
import com.andka.hanashi.databinding.ActivityMainBinding
import com.andka.hanashi.ui.homepage.home.HomeFragment
import com.andka.hanashi.ui.homepage.profile.ProfileFragment
import com.andka.hanashi.ui.login.LoginActivity
import com.andka.hanashi.ui.new_story.NewStoryActivity
import com.andka.hanashi.utils.Locator
import com.andka.hanashi.utils.ResultState

// Constants
private const val ANIMATION_DURATION = 500L

@RequiresApi(Build.VERSION_CODES.S)
class MainActivity : AppCompatActivity() {

    // Properties
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainActivityViewModel>(factoryProducer = { Locator.mainActivityViewModelFactory })
    private var currentFragment = R.id.navigation_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContentView(binding.root)
        setupSplashScreen()

        setupNavigation()
        binding.buttonAdd.setOnClickListener { moveToAdd() }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment.newInstance())
            .commit()
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            moveTo(item.itemId)
        }
    }

    private fun moveTo(fragmentId: Int): Boolean {
        // to top
        if (fragmentId == currentFragment) return false

        val fragment = when (fragmentId) {
            R.id.navigation_home -> HomeFragment.newInstance()
            R.id.navigation_profile -> ProfileFragment.newInstance()
            // Add other cases for other fragments
            else -> return false
        }

        supportFragmentManager.beginTransaction()
//        .setCustomAnimations(
//            R.anim.slide_in_right,  // enter
//            R.anim.slide_out_left,  // exit
//            R.anim.slide_in_left,  // popEnter
//            R.anim.slide_out_right  // popExit
//        )
            .replace(R.id.fragment_container, fragment)
            .commit()

        currentFragment = fragmentId
        return true
    }

    private fun setupSplashScreen() {
        val content: View = binding.root
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
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
            }
        )

        setupSplashScreenExitAnimation()
    }

    private fun setupSplashScreenExitAnimation() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.height.toFloat()
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