package br.com.jonathanzanella.myexpenses.views.anko

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewManager
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

class TemplateToolbar(context: Context) : Toolbar(context)
class TableViewFrame(context: Context) : TableLayout(context)
class Static(context: Context) : TextView(context)
class StaticWithData(context: Context) : TextView(context)

inline fun ViewManager.toolbarTemplate(theme: Int = R.style.ThemeOverlay_AppCompat_Dark, init: TemplateToolbar.() -> Unit) = ankoView(::TemplateToolbar, theme, init)
inline fun ViewManager.tableViewFrame(theme: Int = 0, init: TableViewFrame.() -> Unit) = ankoView(::TableViewFrame, theme, init)
inline fun ViewManager.static(theme: Int = 0, init: Static.() -> Unit) = ankoView(::Static, theme, init)
inline fun ViewManager.staticWithData(theme: Int = 0, init: StaticWithData.() -> Unit) = ankoView(::StaticWithData, theme, init)

fun applyTemplateViewStyles(view: View) {
    when(view) {
        is TemplateToolbar -> {
            view.backgroundColor = ResourcesCompat.getColor(view.resources, R.color.color_primary, null)
            view.layoutParams.width = matchParent
        }
        is TableViewFrame -> {
            view.layoutParams.height = matchParent
            view.layoutParams.width = matchParent
            when(view.layoutParams) {
                is LinearLayout.LayoutParams -> {
                    (view.layoutParams as LinearLayout.LayoutParams).margin = view.resources.getDimensionPixelSize(R.dimen.default_spacing)
                }
            }
        }
        is Static -> {
            when(view.layoutParams) {
                is LinearLayout.LayoutParams -> {
                    (view.layoutParams as LinearLayout.LayoutParams).marginEnd = view.resources.getDimensionPixelSize(R.dimen.min_spacing)
                }
            }
        }
        is StaticWithData -> {
            view.textColor = ResourcesCompat.getColor(view.resources, R.color.color_primary, null)
        }
    }

    applyMaterialViewStyles(view)
}