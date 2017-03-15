package br.com.jonathanzanella.myexpenses.database

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel

class RepositoryMock<T : UnsyncModel>() : Repository<T> {
    override fun find(table: Table<T>, uuid: String?): T? {
        throw NotImplementedError()
    }

    override fun querySingle(table: Table<T>, where: Where?): T? {
        throw NotImplementedError()
    }

    override fun query(table: Table<T>, where: Where?): List<T> {
        throw NotImplementedError()
    }

    override fun query(table: Table<T>, where: Where?, single: Boolean): List<T> {
        throw NotImplementedError()
    }

    override fun userData(table: Table<T>): List<T> {
        throw NotImplementedError()
    }

    override fun unsync(table: Table<T>): List<T> {
        throw NotImplementedError()
    }

    override fun greaterUpdatedAt(table: Table<T>): Long {
        throw NotImplementedError()
    }

    override fun saveAtDatabase(table: Table<T>, data: T) {
    }

    override fun syncAndSave(table: Table<T>, unsyncModel: T) {
    }
}
