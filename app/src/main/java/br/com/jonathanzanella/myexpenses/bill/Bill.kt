package br.com.jonathanzanella.myexpenses.bill

import br.com.jonathanzanella.myexpenses.helpers.DateHelper
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

open class Bill : Transaction, UnsyncModel {
    override var id: Long = 0

    @Expose override var uuid: String? = null
    @Expose override var name: String? = null
    @Expose override var amount: Int = 0
    @Expose var dueDate: Int = 0
    @Expose var initDate: DateTime? = null
        set(initDate) = if (initDate != null)
            field = initDate.withMillisOfDay(0)
        else
            field = null

    @Expose var endDate: DateTime? = null
        set(endDate) = if (endDate != null)
            field = endDate.withMillisOfDay(0)
        else
            field = null

    @Expose @SerializedName("_id") override var serverId: String? = null
    @Expose @SerializedName("created_at") override var createdAt: Long = 0
    @Expose @SerializedName("updated_at") override var updatedAt: Long = 0

    override var sync: Boolean = false
    var month: DateTime? = null

    override fun getDate(): DateTime {
        if (month == null)
            month = DateTime.now()
        val lastDayOfMonth = DateHelper.lastDayOfMonth(month).dayOfMonth
        if (dueDate > lastDayOfMonth)
            return month!!.withDayOfMonth(lastDayOfMonth)
        return month!!.withDayOfMonth(dueDate)
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