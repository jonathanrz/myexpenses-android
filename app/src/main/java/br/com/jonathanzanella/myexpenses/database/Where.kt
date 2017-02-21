package br.com.jonathanzanella.myexpenses.database

import java.util.*

class Where(field: Fields) {
    private inner class Query internal constructor(internal var field: Fields) {
        internal var operation: String? = null

        override fun toString(): String {
            return field.toString() + " " + operation + " ?"
        }
    }

    private val queries: MutableList<Query>
    private val values: MutableList<String> = ArrayList()
    private var orderBy: Fields = Fields.NAME

    init {
        queries = ArrayList<Query>()
        queries.add(Query(field))
    }

    private fun setLastQueryOperation(operation: String) {
        val query = queries[queries.size - 1]
        query.operation = operation
    }

    fun notEq(s: String): Where {
        isExpectingFieldDefinition()
        setLastQueryOperation("!=")
        values.add(s)
        return this
    }

    fun eq(s: String): Where {
        isExpectingFieldDefinition()
        setLastQueryOperation("=")
        values.add(s)
        return this
    }

    fun eq(l: Long?): Where {
        isExpectingFieldDefinition()
        setLastQueryOperation("=")
        values.add(l.toString())
        return this
    }

    fun eq(b: Boolean?): Where {
        isExpectingFieldDefinition()
        setLastQueryOperation("=")
        values.add((if (b != null && b) 1 else 0).toString())
        return this
    }

    fun lessThanOrEq(l: Long?): Where {
        isExpectingFieldDefinition()
        setLastQueryOperation("<=")
        values.add(l.toString())
        return this
    }

    fun greaterThanOrEq(l: Long?): Where {
        isExpectingFieldDefinition()
        setLastQueryOperation(">=")
        values.add(l.toString())
        return this
    }

    fun queryIn(list: List<String>): Where {
        var string = "("
        for (s in list) {
            string = string.plus("?,")
            values.add(s)
        }
        string = string.removeRange(string.length - 1, string.length) + ")"
        setLastQueryOperation(string)
        return this
    }

    fun and(field: Fields): Where {
        queries.add(Query(field))
        return this
    }

    fun query(): Select {
        var query = StringBuilder()
        for (i in queries.indices) {
            query = query.append(queries[i].toString())
            if (i != queries.size - 1) {
                query = query.append(" and ")
            }
        }
        return Select(query.toString(), values.toTypedArray())
    }

    fun orderBy(field: Fields): Where {
        orderBy = field
        return this
    }

    fun orderBy(): Fields {
        return orderBy
    }

    private fun isExpectingFieldDefinition() {
        if (queries.size - 1 != values.size)
            throw UnsupportedOperationException("More fields than values added")
    }
}