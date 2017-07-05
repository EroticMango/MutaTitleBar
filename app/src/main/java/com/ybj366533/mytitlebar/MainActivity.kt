package com.ybj366533.mytitlebar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.muta.titlebar.listener.MutaTitleListener
import com.muta.titlebar.widget.MutaTitleBar

class MainActivity : AppCompatActivity() {

    private var toolbar: MutaTitleBar? = null
    private val mutaListener = object : MutaTitleListener() {
        override fun onCancelIconClicked() {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as MutaTitleBar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }
}
