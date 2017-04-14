package br.com.jonathanzanella.myexpenses.views.anko

import android.content.Context
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewManager
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.dip
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent

class TableViewFrame(context: Context) : TableLayout(context)

fun ViewManager.toolbar_template() = toolbar(R.style.Toolbar) {}
inline fun ViewManager.toolbar(theme: Int = 0, init: Toolbar.() -> Unit) = ankoView(::Toolbar, theme, init)

inline fun ViewManager.static(theme: Int = R.style.Static, init: TextView.() -> Unit) = ankoView(::TextView, theme, init)
inline fun ViewManager.staticWithData(theme: Int = R.style.StaticWithData, init: TextView.() -> Unit) = ankoView(::TextView, theme, init)
inline fun ViewManager.tableViewFrame(theme: Int = R.style.ViewFrame, init: TableViewFrame.() -> Unit) = ankoView(::TableViewFrame, theme, init)

fun applyTemplateViewLayouts(view: View) {
    when(view) {
        is TableViewFrame -> {
            when(view.layoutParams) {
                is LinearLayout.LayoutParams -> {
                    view.layoutParams.height = matchParent
                    view.layoutParams.width = matchParent
                    (view.layoutParams as LinearLayout.LayoutParams).margin = view.dip(16)
                }
            }
        }
    }
}