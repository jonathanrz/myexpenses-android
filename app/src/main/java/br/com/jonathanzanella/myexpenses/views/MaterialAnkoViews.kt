package br.com.jonathanzanella.myexpenses.views

import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatEditText
import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.appCompatEditText(theme: Int = 0, init: AppCompatEditText.() -> Unit) = ankoView(::AppCompatEditText, theme, init)
inline fun ViewManager.textInputLayout(theme: Int = 0, init: TextInputLayout.() -> Unit) = ankoView(::TextInputLayout, theme, init)