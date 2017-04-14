package br.com.jonathanzanella.myexpenses.views.anko

import android.support.v7.widget.Toolbar
import android.view.ViewManager
import android.widget.TableLayout
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import org.jetbrains.anko.custom.ankoView

fun ViewManager.toolbar_template() = toolbar(R.style.Toolbar) {}
inline fun ViewManager.toolbar(theme: Int = 0, init: Toolbar.() -> Unit) = ankoView(::Toolbar, theme, init)

inline fun ViewManager.static(theme: Int = R.style.Static, init: TextView.() -> Unit) = ankoView(::TextView, theme, init)
inline fun ViewManager.staticWithData(theme: Int = R.style.StaticWithData, init: TextView.() -> Unit) = ankoView(::TextView, theme, init)
inline fun ViewManager.tableViewFrame(theme: Int = R.style.ViewFrame, init: TableLayout.() -> Unit) = ankoView(::TableLayout, theme, init)