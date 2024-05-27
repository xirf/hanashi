package com.andka.hanashi.ui.homepage

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.andka.hanashi.R
import com.andka.hanashi.databinding.ActivityMainBinding
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.ui.login.LoginActivity
import com.andka.hanashi.ui.new_story.NewStoryActivity
import com.andka.hanashi.utils.Locator
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.launch

// Constants
private const val ANIMATION_DURATION = 500L

@RequiresApi(Build.VERSION_CODES.S)
class MainActivity : AppCompatActivity() {

    // Properties
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainActivityViewModel>(factoryProducer = { Locator.mainActivityViewModelFactory })
    private val fabCloseAnimation by lazy { loadAnimationResource(R.anim.fab_close) }
    private val fabOpenAnimation by lazy { loadAnimationResource(R.anim.fab_open) }
    private val fabClockwiseAnimation by lazy { loadAnimationResource(R.anim.rotate_fab_clock) }
    private val fabAntiClockwiseAnimation by lazy { loadAnimationResource(R.anim.rotate_fab_anticlock) }
    private var isFabOpen = false
    private val storyAdapter: StoryAdapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        setContentView(binding.root)

        setupSplashScreen()
        setupRecyclerView()
        setupButton()
    }

    private fun setupButton() {
        binding.errorLayout.btnRetry.setOnClickListener {
            fetchAllStories()
            showError(false)
        }
        binding.fab.openFab.setOnClickListener { toggleFab() }
        binding.fab.refresh.setOnClickListener {
            fetchAllStories()
            toggleFab()
        }
        binding.fab.addStory.setOnClickListener {
            moveToAdd()
        }
    }

    private fun setupRecyclerView() {
        binding.rvStory.setAdapter(storyAdapter)
        binding.rvStory.setLayoutManager(GridLayoutManager(this, 2))
        binding.rvStory.addVeiledItems(10)

        fetchAllStories()
    }

    private fun fetchAllStories() {
        lifecycleScope.launch {
            viewModel.getStories()
            viewModel.storyState.collect {
                when (it.resultGetStory) {
                    is ResultState.Success<List<StoryEntity>> -> {
                        if (it.resultGetStory.data != null) {
                            storyAdapter.setData(it.resultGetStory.data as ArrayList<StoryEntity>)
                            binding.rvStory.unVeil()
                        } else {
                            showError(isError = true, isEmpty = true)
                        }
                    }

                    is ResultState.Loading -> binding.rvStory.veil()
                    is ResultState.Error -> {
                        showError(true)
                    }

                    is ResultState.Idle -> {}
                }
            }
        }
    }

    private fun showError(isError: Boolean, isEmpty: Boolean = false) {
        var errorMessage = getString(R.string.error_offline)
        if (isEmpty) errorMessage = getString(R.string.no_item)

        val errorVisibility = if (isError) View.VISIBLE else View.GONE
        val mainVisibility = if (!isError) View.VISIBLE else View.GONE
        with(binding) {
            errorLayout.tvErrorMessage.text = errorMessage
            errorLayout.errorLayout.visibility = errorVisibility
            rvStory.visibility = mainVisibility
            fab.openFab.visibility = mainVisibility
        }
    }

    private fun loadAnimationResource(id: Int): Animation {
        return AnimationUtils.loadAnimation(this, id)
    }

    private fun toggleFab() {
        isFabOpen = !isFabOpen
        animateFab(isFabOpen)
    }

    private fun animateFab(isOpen: Boolean) {
        with(binding.fab) {
            val animation = if (isOpen) fabOpenAnimation else fabCloseAnimation
            val visibility = if (isOpen) View.VISIBLE else View.INVISIBLE

            openFab.startAnimation(if (isOpen) fabClockwiseAnimation else fabAntiClockwiseAnimation)
            actionLogout.startAnimation(animation)
            addStory.startAnimation(animation)
            refresh.startAnimation(animation)

            textviewAdd.visibility = visibility
            textviewExit.visibility = visibility
            textviewRefresh.visibility = visibility
        }
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