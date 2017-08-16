package br.com.jonathanzanella.myexpenses.source

import android.os.AsyncTask
import android.support.annotation.UiThread
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SourcePresenter(private val repository: SourceRepository) {
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
        val s = source
        if (s != null) {
            val v = editView
            if (v != null) {
                v.setTitle(R.string.edit_source_title)
            } else {
                view!!.let {
                    val title = it.context.getString(R.string.source)
                    it.setTitle(title + " " + s.name)
                }
            }
            view!!.showSource(s)
        } else {
            editView?.setTitle(R.string.new_source_title)
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

    @UiThread
    fun save() {
        val v = editView ?: throw InvalidMethodCallException("save", javaClass.toString(), "View should be a Edit View")
        source = v.fillSource(source ?: Source())
        doAsync {
            val result = repository.save(source!!)

            uiThread {
                if (result.isValid) {
                    v.finishView()
                } else {
                    for (validationError in result.errors)
                        v.showError(validationError)
                }
            }
        }
    }

    val uuid: String?
        get() = source?.uuid
}
