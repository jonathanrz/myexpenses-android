package br.com.jonathanzanella.myexpenses.helpers

import android.content.Context
import android.support.annotation.StringRes

import java.lang.ref.WeakReference

class ResourcesHelper(context: Context) {
    private val contextWeakReference: WeakReference<Context> = WeakReference(context)

    fun getString(@StringRes string: Int): String {
        return contextWeakReference.get()!!.getString(string)
    }
}
