package com.andka.hanashi.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.util.Property
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class SlideUpItemAnimator : DefaultItemAnimator() {
    private fun createAnimator(
        target: View,
        property: Property<View, Float>,
        endValue: Float,
        startDelay: Long
    ): ObjectAnimator {
        return ObjectAnimator.ofFloat(target, property, endValue).apply {
            duration = 500L
            interpolator = DecelerateInterpolator()
            this.startDelay = startDelay
        }
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        dispatchAddStarting(holder)
        val view = holder.itemView
        view.translationY = view.height.toFloat()
        view.alpha = 0f
        val startDelay = (holder.adapterPosition * 50L).coerceAtMost(1000L)

        val animator = createAnimator(view, View.TRANSLATION_Y, 0f, startDelay)
        val fadeAnimator = createAnimator(view, View.ALPHA, 1f, startDelay)

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                dispatchAddFinished(holder)
                if (!isRunning) {
                    dispatchAnimationsFinished()
                }
            }
        })

        fadeAnimator.start()
        animator.start()
        return true
    }
}