package br.com.jonathanzanella.myexpenses.views

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.UiThread
import android.support.design.widget.TabLayout
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

@UiThread
abstract class BaseView : FrameLayout {
    protected lateinit var filter: String

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    protected abstract fun init()

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {}

    open fun refreshData() {}

    open fun setTabs(tabs: TabLayout) {
        tabs.visibility = View.GONE
    }

    open fun filter(s: String) {
        filter = s
    }
}