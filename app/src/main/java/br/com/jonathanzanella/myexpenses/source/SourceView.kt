package br.com.jonathanzanella.myexpenses.source

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.BaseView
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton

class SourceView : BaseView {
    private val ui = SourceViewUI()
    private val adapter = SourceAdapter(context)

	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		addView(ui.createView(AnkoContext.Companion.create(context, this)))

        ui.sources.adapter = adapter
        ui.sources.layoutManager = GridLayoutManager(context, 2)
        ui.sources.itemAnimator = DefaultItemAnimator()
	}

	override fun init() {
		//TODO: remove when convert BaseView to interface
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		when (requestCode) {
			REQUEST_ADD_SOURCE -> if (resultCode == Activity.RESULT_OK)
                refreshData()
		}
	}

	override fun refreshData() {
		super.refreshData()

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