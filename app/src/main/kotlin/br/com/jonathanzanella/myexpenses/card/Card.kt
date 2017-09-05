package br.com.jonathanzanella.myexpenses.card

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountRepository
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import timber.log.Timber
import javax.inject.Inject

@Entity
class Card : Chargeable, UnsyncModel {
    @Ignore
    @Inject
    lateinit var accountRepository: AccountRepository
        set

    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0

    @Expose override var uuid: String? = null
    @Expose override var name: String? = null
    @Expose var type: CardType? = null
    @Expose var accountUuid: String? = null

    @Expose @SerializedName("_id") override var serverId: String? = null
    @Expose @SerializedName("created_at") override var createdAt: Long = 0
    @Expose @SerializedName("updated_at") override var updatedAt: Long = 0

    override var sync: Boolean = false

    internal constructor() {
        App.getAppComponent().inject(this)
    }

    constructor(accountRepository: AccountRepository) {
        this.accountRepository = accountRepository
    }

    var account: Account?
        @WorkerThread
        get() {
            return accountUuid?.let {
                accountRepository?.find(it)
            }
        }
        set(account) {
            accountUuid = account!!.uuid
        }

    override val chargeableType: ChargeableType
    get() {
        when (type) {
            CardType.CREDIT -> return ChargeableType.CREDIT_CARD
            CardType.DEBIT -> return ChargeableType.DEBIT_CARD
        }

        Timber.e("new card type?")
        return ChargeableType.DEBIT_CARD
    }

    override fun canBePaidNextMonth(): Boolean {
        return type == CardType.CREDIT
    }

    @WorkerThread
    override fun debit(value: Int) {
        if (type == CardType.DEBIT) {
            val account = account
            account!!.debit(value)
            accountRepository.save(account)
        }
    }

    @WorkerThread
    override fun credit(value: Int) {
        if (type == CardType.DEBIT) {
            val account = account
            account!!.credit(value)
            accountRepository.save(account)
        }
    }

    override fun getData(): String {
        return "name=" + name +
                "\nuuid=" + uuid +
                "\nserverId=" + serverId +
                "\ntype=" + type +
                "\naccount=" + accountUuid
    }
}
