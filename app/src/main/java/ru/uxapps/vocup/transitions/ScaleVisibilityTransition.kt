package ru.uxapps.vocup.transitions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.transition.TransitionValues
import androidx.transition.Visibility

class ScaleVisibilityTransition(context: Context, attrs: AttributeSet) : Visibility(context, attrs) {

    companion object {
        private const val DEF_DURATION = 200L
    }

    override fun onAppear(
        sceneRoot: ViewGroup, view: View,
        startValues: TransitionValues?, endValues: TransitionValues?
    ): Animator? {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f)
        // need to immediately set scale to avoid blink until start
        view.scaleX = 0f
        view.scaleY = 0f
        return ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).also {
            it.duration = DEF_DURATION
            it.interpolator = DecelerateInterpolator()
            // set start delay to allow prev fab to finish hide
            it.startDelay = if (duration >= 0) duration else it.duration
        }
    }

    override fun onDisappear(
        sceneRoot: ViewGroup, view: View,
        startValues: TransitionValues?, endValues: TransitionValues?
    ): Animator? {
        // sometimes view copy contains no image and duplicated onDisappear call - skip this
        if ((view as ImageView).drawable == null) {
            return null
        }
        // imitate prev view elevation
        view.background = startValues?.view?.background
        view.elevation = startValues?.view?.elevation ?: 0f
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f)
        return ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).also {
            it.duration = DEF_DURATION
            it.interpolator = AccelerateInterpolator()
        }
    }

    override fun createAnimator(
        sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?
    ): Animator? {
        // override all created animators with transition values if provided
        return super.createAnimator(sceneRoot, startValues, endValues)?.also {
            if (duration >= 0) {
                it.duration = duration
            }
            if (interpolator != null) {
                it.interpolator = interpolator
            }
        }
    }
}