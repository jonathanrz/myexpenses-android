package br.com.jonathanzanella.myexpenses.source

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.view.Menu
import android.view.MenuItem
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.jetbrains.anko.*

class EditSourceActivity : AppCompatActivity(), SourceContract.EditView {
    override val context = this
    private val presenter: SourcePresenter = SourcePresenter(SourceRepository())
    private val ui = EditSourceActivityUi()

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

    override fun setTitle(string: String) {
        ui.toolbar.title = string
    }

    override fun showSource(source: Source) {
        ui.editName.setText(source.name)
    }

    override fun fillSource(source: Source): Source {
        source.name = ui.editName.text.toString()
        return source
    }

    override fun finishView() {
        val i = Intent()
        i.putExtra(KEY_SOURCE_UUID, presenter.uuid)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    override fun showError(error: ValidationError) {
        when (error) {
            ValidationError.NAME -> ui.editName.error = getString(error.message)
            else -> Log.error(this.javaClass.name, "Validation unrecognized, field:" + error)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val uuid = presenter.uuid
        if (uuid != null)
            outState.putString(KEY_SOURCE_UUID, uuid)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> presenter.save()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val KEY_SOURCE_UUID = "KeySourceUuid"
    }
}

class EditSourceActivityUi : AnkoComponent<EditSourceActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var editName : AppCompatEditText

    override fun createView(ui: AnkoContext<EditSourceActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}

            tableViewFrame {
                textInputLayout {
                    editName = appCompatEditText {
                        id = R.id.act_edit_source_name
                        hint = resources.getString(R.string.name)
                    }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
