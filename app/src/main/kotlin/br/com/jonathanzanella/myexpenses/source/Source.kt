package br.com.jonathanzanella.myexpenses.source

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class Source : UnsyncModel {
    @PrimaryKey(autoGenerate = true)
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
