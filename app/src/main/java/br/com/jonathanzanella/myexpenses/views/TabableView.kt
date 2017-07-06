package br.com.jonathanzanella.myexpenses.views

import android.support.design.widget.TabLayout
import android.view.View

interface TabableView {
    fun setTabs(tabs: TabLayout) {
        tabs.visibility = View.GONE
     }
}