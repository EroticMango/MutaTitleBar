package com.muta.titlebar

import android.support.annotation.DimenRes
import android.view.View

/**
 * Created by YBJ on 2017/7/3.
 *
 */

fun View.getDimen(@DimenRes res: Int) = context.resources.getDimensionPixelOffset(res).toFloat()