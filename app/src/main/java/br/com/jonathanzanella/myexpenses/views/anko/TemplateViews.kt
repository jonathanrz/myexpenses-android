package br.com.jonathanzanella.myexpenses.views.anko

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.transactions.TransactionsView
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView

class TemplateToolbar(context: Context) : Toolbar(context) {
    fun setup(activity : AppCompatActivity) {
        activity.setSupportActionBar(this)
        val actionBar = activity.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
            setNavigationOnClickListener { activity.finish() }
        }
    }
}
class TableViewFrame(context: Context) : TableLayout(context)
class Static(context: Context) : TextView(context)
class StaticWithData(context: Context) : TextView(context)
class EmptyListMessageView(context: Context) : TextView(context)
class ResumeRowCell(context: Context) : LinearLayout(context)

inline fun ViewManager.toolbarTemplate(theme: Int = R.style.ThemeOverlay_AppCompat_Dark, init: TemplateToolbar.() -> Unit) = ankoView(::TemplateToolbar, theme, init)
inline fun ViewManager.tableViewFrame(theme: Int = 0, init: TableViewFrame.() -> Unit) = ankoView(::TableViewFrame, theme, init)
inline fun ViewManager.static(theme: Int = 0, init: Static.() -> Unit) = ankoView(::Static, theme, init)
inline fun ViewManager.staticWithData(theme: Int = 0, init: StaticWithData.() -> Unit) = ankoView(::StaticWithData, theme, init)
inline fun ViewManager.emptyListMessageView(theme: Int = 0, init: EmptyListMessageView.() -> Unit) = ankoView(::EmptyListMessageView, theme, init)
inline fun ViewManager.resumeRowCell(theme: Int = 0, init: ResumeRowCell.() -> Unit) = ankoView(::ResumeRowCell, theme, init)
inline fun ViewManager.recyclerView(theme: Int = 0, init: RecyclerView.() -> Unit) = ankoView(::RecyclerView, theme, init)

inline fun ViewManager.transactionsView(theme: Int = 0, init: TransactionsView.() -> Unit) = ankoView(::TransactionsView, theme, init)

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
        is EmptyListMessageView -> {
            view.layoutParams.height = matchParent
            view.layoutParams.width = matchParent
            view.gravity = View.TEXT_ALIGNMENT_CENTER
            view.visibility = View.GONE
        }
        is ResumeRowCell -> {
            view.layoutParams.height = view.dip(28)
            view.layoutParams.width = matchParent
            view.padding = 5
        }
        is RecyclerView -> {
            view.layoutParams.width = matchParent
        }
        is FloatingActionButton -> {
            view.imageResource = R.drawable.ic_add_white_24dp
            view.elevation = view.dip(6).toFloat()
            view.layoutParams.height = view.dip(56)
            view.layoutParams.width = view.dip(56)
            when(view.layoutParams) {
                is FrameLayout.LayoutParams -> {
                    (view.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.BOTTOM.or(Gravity.END)
                    (view.layoutParams as FrameLayout.LayoutParams).margin = view.resources.getDimensionPixelSize(R.dimen.default_spacing)
                }
            }
        }
    }

    applyMaterialViewStyles(view)
}