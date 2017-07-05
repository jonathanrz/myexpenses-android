package br.com.jonathanzanella.myexpenses.views

import android.content.Context
import android.content.Intent
import android.support.annotation.UiThread
import android.support.design.widget.TabLayout
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

@UiThread
abstract class BaseView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    protected lateinit var filter: String

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}

    open fun refreshData() {}

    open fun setTabs(tabs: TabLayout) {
        tabs.visibility = View.GONE
    }

    open fun filter(s: String) {
        filter = s
    }
}