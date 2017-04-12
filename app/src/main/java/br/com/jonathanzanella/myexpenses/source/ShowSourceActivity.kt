package br.com.jonathanzanella.myexpenses.source

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar

class ShowSourceActivity : AppCompatActivity(), SourceContract.View {
    private val presenter = SourcePresenter(SourceRepository(RepositoryImpl<Source>(this)))
    private val ui = ShowSourceActivityUi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        storeBundle(savedInstanceState)
        storeBundle(intent.extras)
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

        setSupportActionBar(ui.toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
            ui.toolbar.setNavigationOnClickListener { finish() }
        }
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

    override fun setTitle(string: String?) {
        ui.toolbar.title = string
    }

    override fun getContext(): Context {
        return this
    }

    companion object {
        val KEY_SOURCE_UUID = "KeySourceUuid"
    }
}

class ShowSourceActivityUi : AnkoComponent<ShowSourceActivity> {
    lateinit var sourceName : TextView
    lateinit var toolbar : Toolbar

    override fun createView(ui: AnkoContext<ShowSourceActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbar(android.R.style.ThemeOverlay_Material_Dark) {
                backgroundColor = ContextCompat.getColor(ctx, R.color.color_primary)
            }.lparams(width = matchParent)

            tableLayout {
                tableRow {
                    textView {
                        text = resources.getString(R.string.name)
                    }.lparams {
                        rightMargin = dip(5)
                    }

                    sourceName = textView {
                        id = R.id.act_show_source_name
                        textColor = ContextCompat.getColor(ctx, R.color.color_primary)
                    }
                }
            }.lparams {
                margin = dip(16)
            }
        }
    }

}
