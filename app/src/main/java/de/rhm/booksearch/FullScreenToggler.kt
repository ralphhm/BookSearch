package de.rhm.booksearch

import android.annotation.SuppressLint
import android.os.Handler
import android.support.v7.app.ActionBar
import android.view.View

/**
 * Whether or not the system UI should be auto-hidden after
 * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
 */
const val AUTO_HIDE = true

/**
 * If [AUTO_HIDE] is set, the number of milliseconds to wait after
 * user interaction before hiding the system UI.
 */
const val AUTO_HIDE_DELAY_MILLIS = 3000

/**
 * Some older devices needs a small delay between UI widget updates
 * and a change of the status and navigation bar.
 */
const val UI_ANIMATION_DELAY = 300L

class FullScreenToggler(val actionBar: ActionBar, val contentView: View, vararg val controls: View) {
    private val mHideHandler = Handler()
    @SuppressLint("InlinedApi")
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        contentView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        actionBar.show()
        controls.forEach { it.visibility = View.VISIBLE }
    }

    private var mVisible: Boolean = true
    private val mHideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    init {
        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener { toggle() }
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        actionBar.hide()
        controls.forEach { it.visibility = View.GONE}
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY)
    }

    private fun show() {
        // Show the system bar
        contentView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY)
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }
}