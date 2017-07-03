package com.muta.titlebar.interpolator

import android.view.animation.Interpolator

/**
 * Created by YBJ on 2017/7/3.
 *
 */
class JellyInterpolator : Interpolator {

    override fun getInterpolation(t: Float) = (Math.min(1.0, Math.sin(28 * t - 6.16) / (5 * t - 1.1))).toFloat()

}