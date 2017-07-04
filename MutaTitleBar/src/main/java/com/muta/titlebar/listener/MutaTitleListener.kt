package com.muta.titlebar.listener

/**
 * Created by YBJ on 2017/7/3.
 *
 */
abstract class MutaTitleListener {

    open fun onToolbarExpandingStarted() = Unit

    open fun onToolbarCollapsingStarted() = Unit

    open fun onToolbarExpanded() = Unit

    open fun onToolbarCollapsed() = Unit

    abstract fun onCancelIconClicked()

}