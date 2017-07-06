package br.com.jonathanzanella.myexpenses.views

import android.support.annotation.UiThread

@UiThread
interface BaseView {
    var filter: String

    fun filter(s: String) {
        filter = s
    }
}