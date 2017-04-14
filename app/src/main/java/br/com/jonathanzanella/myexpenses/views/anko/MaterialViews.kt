package br.com.jonathanzanella.myexpenses.views.anko

import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatEditText
import android.view.ViewManager
import br.com.jonathanzanella.myexpenses.R
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.appCompatEditText(theme: Int = R.style.AppCompatEditText, init: AppCompatEditText.() -> Unit) = ankoView(::AppCompatEditText, theme, init)
inline fun ViewManager.textInputLayout(theme: Int = R.style.TextInputLayout, init: TextInputLayout.() -> Unit) = ankoView(::TextInputLayout, theme, init)