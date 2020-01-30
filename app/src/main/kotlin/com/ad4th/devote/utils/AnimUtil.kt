package com.ad4th.devote.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.ad4th.devote.R


class AnimUtil {

    /**
     * A helper method to build scale up animation;
     *
     * @param target
     * @param targetScaleX
     * @param targetScaleY
     * @return
     */
    private fun buildScaleUpAnimation(target: View, targetScaleX: Float, targetScaleY: Float): AnimatorSet {

        val scaleUp = AnimatorSet()
        scaleUp.playTogether(
                ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
                ObjectAnimator.ofFloat(target, "scaleY", targetScaleY)
        )

        scaleUp.duration = 250
        return scaleUp
    }

    companion object {

        fun left(context: Activity) {
            context.overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }

        fun right(context: Activity) {
            context.overridePendingTransition(R.anim.right_in, R.anim.right_out)
        }

        fun fade(context: Activity) {
            context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        fun zoomOut(context: Activity) {
            context.overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
        }

        fun hold(context: Activity) {
            context.overridePendingTransition(R.anim.hold, R.anim.hold)
        }

        fun up(context: Activity) {
            context.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out)
        }

        fun down(context: Activity) {
            context.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out)
        }

        /**
         * A helper method to build scale down animation;
         *
         * @param target
         * @param targetScaleX
         * @param targetScaleY
         * @return
         */
        fun buildScaleAnimation(target: View, targetScaleX: Float, targetScaleY: Float): AnimatorSet {

            val scaleDown = AnimatorSet()
            scaleDown.playTogether(
                    ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
                    ObjectAnimator.ofFloat(target, "scaleY", targetScaleY)
            )
            //        scaleDown.setInterpolator(AnimationUtils.loadInterpolator(activity,
            //                android.R.anim.decelerate_interpolator));
            //        scaleDown.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleDown.interpolator = DecelerateInterpolator()
            scaleDown.duration = 0
            return scaleDown
        }
    }
}
