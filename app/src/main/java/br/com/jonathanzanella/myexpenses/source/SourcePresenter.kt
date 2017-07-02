package br.com.jonathanzanella.myexpenses.source

import android.os.AsyncTask
import android.support.annotation.UiThread
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException

internal class SourcePresenter(private val repository: SourceRepository) {
    private var view: SourceContract.View? = null
    private var editView: SourceContract.EditView? = null
    private var source: Source? = null

    fun attachView(view: SourceContract.View) {
        this.view = view
    }

    fun attachView(view: SourceContract.EditView) {
        this.view = view
        this.editView = view
    }

    fun detachView() {
        this.view = null
        this.editView = null
    }

    fun viewUpdated() {
        if (source != null) {
            if (editView != null) {
                editView!!.setTitle(R.string.edit_source_title)
            } else {
                val title = view!!.context.getString(R.string.source)
                view!!.setTitle(title + " " + source!!.name)
            }
            view!!.showSource(source!!)
        } else {
            if (editView != null)
                editView!!.setTitle(R.string.new_source_title)
        }
    }

    @UiThread
    fun loadSource(uuid: String) {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                source = repository.find(uuid)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                viewUpdated()
            }
        }.execute()
    }

    fun save() {
        val v = editView ?: throw InvalidMethodCallException("save", javaClass.toString(), "View should be a Edit View")
        source = editView!!.fillSource(source ?: Source())
        val result = repository.save(source!!)

        if (result.isValid) {
            v.finishView()
        } else {
            for (validationError in result.errors)
                v.showError(validationError)
        }
    }

    val uuid: String?
        get() = if (source != null) source!!.uuid else null
}
