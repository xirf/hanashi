package com.andka.hanashi.ui.homepage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.andka.hanashi.R
import com.andka.hanashi.databinding.ActivityMainBinding
import com.andka.hanashi.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val fabClose by lazy { AnimationUtils.loadAnimation(this, R.anim.fab_close) }
    private val fabOpen by lazy { AnimationUtils.loadAnimation(this, R.anim.fab_open) }
    private val fabClock by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_fab_clock) }
    private val fabAntiClock by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_fab_anticlock
        )
    }
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