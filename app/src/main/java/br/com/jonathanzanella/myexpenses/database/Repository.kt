package br.com.jonathanzanella.myexpenses.database

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel

interface Repository<T : UnsyncModel> {
    fun find(table: Table<T>, uuid: String?): T?
    fun querySingle(table: Table<T>, where: Where?): T?
    fun query(table: Table<T>, where: Where?): List<T>
    fun query(table: Table<T>, where: Where?, single: Boolean): List<T>
    fun unsync(table: Table<T>): List<T>
    fun greaterUpdatedAt(table: Table<T>): Long
    fun saveAtDatabase(table: Table<T>, data: T)
    fun syncAndSave(table: Table<T>, unsyncModel: T)
}
