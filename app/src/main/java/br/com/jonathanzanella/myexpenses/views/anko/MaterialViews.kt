package br.com.jonathanzanella.myexpenses.views.anko

import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatEditText
import android.view.View
import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.matchParent

inline fun ViewManager.appCompatEditText(theme: Int = 0, init: AppCompatEditText.() -> Unit) = ankoView(::AppCompatEditText, theme, init)
inline fun ViewManager.textInputLayout(theme: Int = 0, init: TextInputLayout.() -> Unit) = ankoView(::TextInputLayout, theme, init)

fun applyMaterialViewStyles(view: View) {
    when(view) {
        is AppCompatEditText -> {
            view.layoutParams.width = matchParent
        }
        is TextInputLayout -> {
            view.layoutParams.width = matchParent
        }
    }
}