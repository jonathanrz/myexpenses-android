package br.com.jonathanzanella.myexpenses.extensions

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.v7.app.AppCompatActivity

fun Context.getLifecycleOwner(): LifecycleOwner {
    when(this) {
        is AppCompatActivity -> return this
        else -> throw UnsupportedOperationException("Context is a ${this.javaClass}")
    }
}