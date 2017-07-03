package com.muta.titlebar.listener

import android.animation.Animator

/**
 * Created by YBJ on 2017/7/3.
 *
 */
abstract class AnimationListener : Animator.AnimatorListener {

    override fun onAnimationRepeat(animation: Animator?) = Unit

    override fun onAnimationStart(animation: Animator?) = Unit

    override fun onAnimationCancel(animation: Animator?) = Unit

    override abstract fun onAnimationEnd(animation: Animator?)

}