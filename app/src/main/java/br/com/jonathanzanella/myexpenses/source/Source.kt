package br.com.jonathanzanella.myexpenses.source

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel

class Source : UnsyncModel {
    override var id: Long = 0
    @Expose
    var name: String? = null
    @Expose
    override var uuid: String? = null
    @Expose
    @SerializedName("_id")
    override var serverId: String? = null
    @Expose
    @SerializedName("created_at")
    override var createdAt: Long = 0
    @Expose
    @SerializedName("updated_at")
    override var updatedAt: Long = 0
    override var sync: Boolean = false

    override fun getData(): String {
        return "name=" + name +
                "\nuuid=" + uuid +
                "\nserverId=" + serverId
    }
}