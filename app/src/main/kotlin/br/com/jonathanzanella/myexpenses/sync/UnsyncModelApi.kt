package br.com.jonathanzanella.myexpenses.sync

interface UnsyncModelApi<out T : UnsyncModel> {
    fun index(): List<T>
    fun save(model: UnsyncModel)
    fun syncAndSave(unsync: UnsyncModel)
    fun unsyncModels(): List<T>
    fun greaterUpdatedAt(): Long
}