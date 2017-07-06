package br.com.jonathanzanella.myexpenses.views

import android.content.Intent
import android.support.annotation.UiThread
import android.support.design.widget.TabLayout
import android.view.View

@UiThread
interface BaseView {
    var filter: String

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}

    fun refreshData() {}

    fun setTabs(tabs: TabLayout) {
        tabs.visibility = View.GONE
    }

    fun filter(s: String) {
        filter = s
    }
}