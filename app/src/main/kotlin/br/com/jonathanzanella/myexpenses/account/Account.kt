package br.com.jonathanzanella.myexpenses.account

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
open class Account : Chargeable, UnsyncModel {
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0
    override var sync: Boolean = false
    @Ignore
    override val chargeableType = ChargeableType.ACCOUNT

    @Expose override var uuid: String? = null
    @Expose override var name: String? = null
    @Expose var balance: Int = 0
    @Expose var accountToPayCreditCard: Boolean = false
    @Expose var accountToPayBills: Boolean = false
    @Expose var showInResume = true
    @Expose @SerializedName("_id") override var serverId: String? = null
    @Expose @SerializedName("created_at") override var createdAt: Long = 0
    @Expose @SerializedName("updated_at") override var updatedAt: Long = 0

    override fun credit(value: Int) {
        balance += value
    }

    override fun canBePaidNextMonth() = false

    override fun debit(value: Int) {
        balance -= value
    }

    override fun getData(): String {
        return "name=$name" +
                "\nuuid=$uuid" +
                "\nserverId=$serverId" +
                "\nbalance=$balance" +
                "\naccountToPayCreditCard=$accountToPayCreditCard"
    }
}