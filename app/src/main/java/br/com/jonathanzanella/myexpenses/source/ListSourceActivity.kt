package br.com.jonathanzanella.myexpenses.source

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.jetbrains.anko.*

class ListSourceActivity : AppCompatActivity(), SourceAdapterCallback {
    private val ui = ListSourceActivityUi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        ui.toolbar.title = getString(R.string.select_source_title)
        ui.toolbar.setup(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val adapter = SourceAdapter()
        adapter.setCallback(this)

        ui.sources.adapter = adapter
        ui.sources.setHasFixedSize(true)
        ui.sources.layoutManager = GridLayoutManager(this, 2)
        ui.sources.itemAnimator = DefaultItemAnimator()

        ui.emptyListView.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun onSourceSelected(source: Source) {
        val i = Intent()
        i.putExtra(KEY_SOURCE_SELECTED_UUID, source.uuid)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    companion object {
        val KEY_SOURCE_SELECTED_UUID = "KeySourceSelectUuid"
    }
}

class ListSourceActivityUi : AnkoComponent<ListSourceActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var sources : RecyclerView
    lateinit var emptyListView : TextView

    override fun createView(ui: AnkoContext<ListSourceActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}
            sources = recyclerView {}
            emptyListView = emptyListMessageView { text = resources.getString(R.string.message_no_sources) }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}