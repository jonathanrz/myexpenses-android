package br.com.jonathanzanella.myexpenses.sync

interface UnsyncModel {
    var id: Long
    var serverId: String?
    var uuid: String?
    var createdAt: Long
    var updatedAt: Long
    var sync: Boolean

    fun getData(): String
}