package br.com.jonathanzanella.myexpenses.bill

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import br.com.jonathanzanella.myexpenses.helpers.DateHelper
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

@Entity
class Bill : Transaction, UnsyncModel {
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0

    @Expose override var uuid: String? = null
    @Expose override var name: String? = null
    @Expose override var amount: Int = 0
    @Expose var dueDate: Int = 0
    @Expose var initDate: DateTime? = null
        set(initDate) { field = initDate?.withMillisOfDay(0) }

    @Expose var endDate: DateTime? = null
        set(endDate) { field = endDate?.withMillisOfDay(0) }

    @Expose @SerializedName("_id") override var serverId: String? = null
    @Expose @SerializedName("created_at") override var createdAt: Long = 0
    @Expose @SerializedName("updated_at") override var updatedAt: Long = 0

    override var sync: Boolean = false
    var month: DateTime? = null

    override fun getDate(): DateTime {
        if (month == null)
            month = DateTime.now()
        val month = this.month!!
        val lastDayOfMonth = DateHelper.lastDayOfMonth(month).dayOfMonth
        if (dueDate > lastDayOfMonth)
            return month.withDayOfMonth(lastDayOfMonth)
        return month.withDayOfMonth(dueDate)
    }

    override fun credited(): Boolean {
        return true
    }

    override fun debited(): Boolean {
        return false
    }

    override fun getData(): String {
        return "name=" + name +
                "\nuuid=" + uuid +
                "\nserverId=" + serverId +
                "\namount=" + amount +
                "\ndueDate=" + dueDate +
                "\ninitDate=" + Transaction.SIMPLE_DATE_FORMAT.format(this.initDate!!.toDate()) +
                "\nendDate=" + Transaction.SIMPLE_DATE_FORMAT.format(this.endDate!!.toDate())
    }
}