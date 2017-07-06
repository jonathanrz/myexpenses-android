package br.com.jonathanzanella.myexpenses.views

interface FilterableView {
    var filter: String

    fun filter(s: String) {
        filter = s
    }
}