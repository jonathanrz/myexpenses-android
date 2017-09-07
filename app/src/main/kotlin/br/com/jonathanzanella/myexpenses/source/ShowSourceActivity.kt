package br.com.jonathanzanella.myexpenses.source

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.jetbrains.anko.*
import javax.inject.Inject

class ShowSourceActivity : AppCompatActivity(), SourceContract.View {
    @Inject
    lateinit var sourceDataSource: SourceDataSource
    override val context = this
    private val presenter: SourcePresenter
    private val ui = ShowSourceActivityUi()

    init {
        App.getAppComponent().inject(this)
        presenter = SourcePresenter(sourceDataSource)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        storeBundle(savedInstanceState)
        storeBundle(intent.extras)

        ui.toolbar.setup(this)
    }

    fun storeBundle(extras: Bundle?) {
        if (extras != null && extras.containsKey(KEY_SOURCE_UUID))
            presenter.loadSource(extras.getString(KEY_SOURCE_UUID))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_SOURCE_UUID, presenter.uuid)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        presenter.viewUpdated()
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                val i = Intent(this, EditSourceActivity::class.java)
                i.putExtra(EditSourceActivity.KEY_SOURCE_UUID, presenter.uuid)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showSource(source: Source) {
        ui.sourceName.text = source.name
    }

    override fun setTitle(string: String) {
        ui.toolbar.title = string
    }

    companion object {
        val KEY_SOURCE_UUID = "KeySourceUuid"
    }
}

class ShowSourceActivityUi : AnkoComponent<ShowSourceActivity> {
    lateinit var sourceName : TextView
    lateinit var toolbar : TemplateToolbar

    override fun createView(ui: AnkoContext<ShowSourceActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}

            tableViewFrame {
                tableRow {
                    static { text = resources.getString(R.string.name) }
                    sourceName = staticWithData { id = R.id.act_show_source_name }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
