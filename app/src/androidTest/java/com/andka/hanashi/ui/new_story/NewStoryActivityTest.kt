package com.andka.hanashi.ui.new_story

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.andka.hanashi.R
import com.andka.hanashi.utils.ImageCompressionIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
@Suppress("Deprecation")
class NewStoryActivityTest {


    @get:Rule
    var activityRule = ActivityScenarioRule(NewStoryActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
        Espresso.registerIdlingResources(ImageCompressionIdlingResource.getIdlingResource())
    }

    @After
    fun tearDown() {
        Intents.release()
        Espresso.unregisterIdlingResources(ImageCompressionIdlingResource.getIdlingResource())
    }

    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @Test
    fun test_isActivityInView() {
        onView(withId(R.id.button_add)).check(matches(isDisplayed()))
    }

    @Test
    fun test_typingInTextBox() {
        onView(withId(R.id.ed_add_description)).perform(
            typeText("Test Description"),
            closeSoftKeyboard()
        )
    }

    /* This test failed and no workaround for me
     * Time Wasted = 8 Hour
     */
    @Test
    fun test_selectImageFromGallery() {
        val resultData = Intent()
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        Intents.intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)
        onView(withId(R.id.btn_galery)).perform(click())
        Intents.intended(hasAction(Intent.ACTION_GET_CONTENT))
        onView(withId(R.id.iv_preview)).check(matches(isDisplayed()))
    }
}
