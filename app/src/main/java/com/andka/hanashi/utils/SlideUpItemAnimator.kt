package com.andka.hanashi.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class SlideUpItemAnimator : DefaultItemAnimator() {
    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        dispatchAddStarting(holder)
        val view = holder.itemView
        view.translationY = view.height.toFloat()
        val animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f)
        animator.duration = 100L
        animator.interpolator = DecelerateInterpolator()
        animator.startDelay = holder.adapterPosition * 50L
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                dispatchAddFinished(holder)
                if (!isRunning) {
                    dispatchAnimationsFinished()
                }
            }
        })
        animator.start()
        return true
    }
}