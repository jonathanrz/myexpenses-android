package br.com.jonathanzanella.myexpenses.source

import java.util.*

class SourceAdapterPresenter(private val repository: SourceRepository) {

    private var sources: List<Source>? = null

    init {
        loadSources()
    }

    private fun loadSources() {
        sources = repository.all()
    }

    fun getSources(invalidateCache: Boolean): List<Source> {
        if (invalidateCache)
            loadSources()
        return Collections.unmodifiableList(sources!!)
    }
}
