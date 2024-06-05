package com.andka.hanashi.utils

import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicInteger

object ImageCompressionIdlingResource {

    private const val RESOURCE = "IMAGE_COMPRESSION"

    private val counter = AtomicInteger(0)

    private val idlingResource = object : IdlingResource {
        var callback: IdlingResource.ResourceCallback? = null
        override fun getName() = RESOURCE
        override fun isIdleNow() = counter.get() == 0
        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            this.callback = callback
        }
    }

    fun increment() {
        counter.getAndIncrement()
    }

    fun decrement() {
        val counterVal = counter.decrementAndGet()
        if (counterVal == 0) {
            idlingResource.callback?.onTransitionToIdle()
        }
    }

    fun getIdlingResource(): IdlingResource {
        return idlingResource
    }
}