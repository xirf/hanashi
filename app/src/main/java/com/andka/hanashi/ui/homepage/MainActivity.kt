package com.andka.hanashi.ui.homepage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.andka.hanashi.R
import com.andka.hanashi.databinding.ActivityMainBinding
import com.andka.hanashi.ui.login.LoginActivity
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.launch
import org.xml.sax.Locator

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainActivityViewModel> {
        factoryProducer = { Locator.mainActivityViewModels }
    }
    private val fabClose by lazy { getAnim(R.anim.fab_close) }
    private val fabOpen by lazy { getAnim(R.anim.fab_open) }
    private val fabClock by lazy { getAnim(R.anim.rotate_fab_clock) }
    private val fabAntiClock by lazy { getAnim(R.anim.rotate_fab_anticlock) }
    private var isFabOpen = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.fab.openFab.setOnClickListener { toggleFab() }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun getAnim(id: Int): Animation {
        return AnimationUtils.loadAnimation(this, id)
    }

    private fun checkLogin() {
        lifecycleScope.launch {
            viewModel.isLoggedIn.collect {
                if (it.resultGetUser is ResultState.Success) {
                    if (it.resultGetUser.data == false) {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun toggleFab() {
        with(binding.fab) {
            if (isFabOpen) {
                openFab.startAnimation(fabAntiClock)
                exit.startAnimation(fabClose)
                addStory.startAnimation(fabClose)
                refresh.startAnimation(fabClose)
                textviewAdd.visibility = View.INVISIBLE
                textviewExit.visibility = View.INVISIBLE
                textviewRefresh.visibility = View.INVISIBLE
            } else {
                openFab.startAnimation(fabClock)
                exit.startAnimation(fabOpen)
                addStory.startAnimation(fabOpen)
                refresh.startAnimation(fabOpen)
                textviewAdd.visibility = View.VISIBLE
                textviewExit.visibility = View.VISIBLE
                textviewRefresh.visibility = View.VISIBLE
            }
            isFabOpen = !isFabOpen
        }
    }
}