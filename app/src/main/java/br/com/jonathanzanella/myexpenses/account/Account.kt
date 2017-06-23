package br.com.jonathanzanella.myexpenses.account

import br.com.jonathanzanella.myexpenses.chargeable.Chargeable
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Account : Chargeable, UnsyncModel {
    override var id: Long = 0
    override var sync: Boolean = false
    override val chargeableType = ChargeableType.ACCOUNT

    @Expose override var uuid: String? = null
    @Expose override var name: String? = null
    @Expose var balance: Int = 0
    @Expose var isAccountToPayCreditCard: Boolean = false
    @Expose var isAccountToPayBills: Boolean = false
    @Expose var showInResume = true
    @Expose @SerializedName("_id") override var serverId: String? = null
    @Expose @SerializedName("created_at") override var createdAt: Long = 0
    @Expose @SerializedName("updated_at") override var updatedAt: Long = 0

    override fun credit(value: Int) {
        balance += value
    }

    override fun canBePaidNextMonth(): Boolean {
        return false
    }

    override fun debit(value: Int) {
        balance -= value
    }

    override fun getData(): String {
        return "name=" + name +
                "\nuuid=" + uuid +
                "\nserverId=" + serverId +
                "\nbalance=" + balance +
                "\naccountToPayCreditCard=" + isAccountToPayCreditCard
    }
}