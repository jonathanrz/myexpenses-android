package br.com.jonathanzanella.myexpenses.source

import java.util.*

class SourceAdapterPresenter(private val dataSource: SourceDataSource) {

    private var sources: List<Source>? = null

    init {
        loadSources()
    }

    private fun loadSources() {
        sources = dataSource.all()
    }

    fun getSources(invalidateCache: Boolean): List<Source> {
        if (invalidateCache)
            loadSources()
        return Collections.unmodifiableList(sources!!)
    }
}
