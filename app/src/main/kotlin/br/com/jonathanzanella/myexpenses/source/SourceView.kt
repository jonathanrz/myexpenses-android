package br.com.jonathanzanella.myexpenses.source

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.FilterableView
import br.com.jonathanzanella.myexpenses.views.RefreshableView
import br.com.jonathanzanella.myexpenses.views.ResultableView
import br.com.jonathanzanella.myexpenses.views.TabableView
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton

class SourceView@JvmOverloads constructor(
		context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RefreshableView, ResultableView, FilterableView, TabableView {
	override var filter = ""
	private val ui = SourceViewUI()
    private val adapter = SourceAdapter()

	init {
		addView(ui.createView(AnkoContext.Companion.create(context, this)))

        ui.sources.adapter = adapter
        ui.sources.layoutManager = GridLayoutManager(context, 2)
        ui.sources.itemAnimator = DefaultItemAnimator()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			REQUEST_ADD_SOURCE -> if (resultCode == Activity.RESULT_OK)
                refreshData()
		}
	}

	override fun refreshData() {
        adapter.refreshData()
		adapter.notifyDataSetChanged()
	}

	companion object {
		val REQUEST_ADD_SOURCE = 1004
	}
}

class SourceViewUI: AnkoComponent<SourceView> {
	lateinit var sources : RecyclerView

	override fun createView(ui: AnkoContext<SourceView>) = with(ui) {
		frameLayout {
			sources = recyclerView { id = R.id.view_sources_list }
			floatingActionButton {
				id = R.id.view_sources_fab
				onClick {
					val i = Intent(context, EditSourceActivity::class.java)
					if (ctx is Activity) {
                        (ctx as Activity).startActivityForResult(i, SourceView.REQUEST_ADD_SOURCE)
					} else {
						ctx.startActivity(i)
					}
				}
			}
		}.applyRecursively(::applyTemplateViewStyles)
	}
}