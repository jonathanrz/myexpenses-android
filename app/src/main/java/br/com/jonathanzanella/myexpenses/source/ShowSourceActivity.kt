package br.com.jonathanzanella.myexpenses.source

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.views.BaseActivity
import butterknife.Bind

class ShowSourceActivity : BaseActivity(), SourceContract.View {

    @Bind(R.id.act_show_source_name)
    lateinit var sourceName: TextView

    private val presenter = SourcePresenter(SourceRepository(RepositoryImpl<Source>(this)))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_source)
    }

    override fun storeBundle(extras: Bundle?) {
        super.storeBundle(extras)

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
        sourceName.text = source.name
    }

    companion object {
        val KEY_SOURCE_UUID = "KeySourceUuid"
    }
}
