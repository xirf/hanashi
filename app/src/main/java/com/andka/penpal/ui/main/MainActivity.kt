package com.andka.penpal.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import android.view.animation.AnticipateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.andka.penpal.R
import com.andka.penpal.databinding.ActivityMainBinding
import com.andka.penpal.ui.auth.AuthActivity
import com.andka.penpal.ui.main.home.HomeFragment
import com.andka.penpal.utils.Constant
import com.andka.penpal.utils.UserPreferences
import com.andka.penpal.utils.datastore
import com.andka.penpal.utils.getHourPass
import com.andka.penpal.utils.parseDate
import com.andka.penpal.viewmodels.UserViewModel
import com.andka.penpal.viewmodels.factory.UserViewModelFactory

@RequiresApi(Build.VERSION_CODES.S)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var userViewModel: UserViewModel
    private var isLogin: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        enableEdgeToEdge()

        userPreferences = UserPreferences.getInstance(datastore)
        userViewModel = ViewModelProvider(
            this,
            UserViewModelFactory(userPreferences)
        )[UserViewModel::class.java]

        checkUserPreferences()
        setClickListener()
        setupSplashScreen()
    }

    private fun renderFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .commit()
    }

    private fun setClickListener() {
        with(binding) {
//            fab.setOnClickListener {
//                //TODO: Add click listener
//            }
        }
    }

    private fun checkUserPreferences() {
        userViewModel.getUserPreferences(Constant.PreferenceProperty.USER_LAST_LOGIN.name)
            .observe(this@MainActivity) { lastLoginDate ->
                val lastLogin = parseDate(lastLoginDate)
                var hourDiff: Long = 0
                if (lastLogin != null) {
                    hourDiff = getHourPass(lastLogin)
                    isLogin = hourDiff < 1
                } else {
                    isLogin = false
                }

                if (isLogin == false) {
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    renderFragment(HomeFragment())
                }
            }
    }

    private fun setupSplashScreen() {
        binding.main.viewTreeObserver.addOnPreDrawListener(
            object : OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isLogin != null) {
                        binding.main.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else false
                }
            }
        )

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.height.toFloat()
            )
            val fadeOut = ObjectAnimator.ofFloat(
                splashScreenView,
                View.ALPHA,
                1f,
                0f
            )

            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 200L
            fadeOut.duration = 200L

            slideUp.doOnEnd { splashScreenView.remove() }
            slideUp.start()
            fadeOut.start()
        }
    }
}