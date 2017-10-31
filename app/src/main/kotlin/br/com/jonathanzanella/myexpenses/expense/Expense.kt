package br.com.jonathanzanella.myexpenses.expense

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.Environment
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.bill.BillDataSource
import br.com.jonathanzanella.myexpenses.card.Card
import br.com.jonathanzanella.myexpenses.card.CardDataSource
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import javax.inject.Inject

@Entity
class Expense : Transaction, UnsyncModel {
    @Ignore @Inject
    lateinit var accountDataSource: AccountDataSource
    @Ignore @Inject
    lateinit var billDataSource: BillDataSource
    @Ignore @Inject
    lateinit var cardDataSource: CardDataSource
    @Ignore @Inject
    lateinit var expenseDataSource: ExpenseDataSource

    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0

    @Expose override var uuid: String? = null
    @Expose override var name: String? = null
    @Expose private var date: DateTime? = null
    @Expose var value: Int = 0
    @Expose var chargeableUuid: String? = null
    @Expose var chargeableType: ChargeableType? = null
    @Expose var removed: Boolean = false
    @Expose var billUuid: String? = null
    @Expose var charged: Boolean = false
    @Expose var chargedNextMonth: Boolean = false
    @Expose var ignoreInOverview: Boolean = false
    @Expose var ignoreInResume: Boolean = false

    @Expose @SerializedName("_id") override var serverId: String? = null
    @Expose @SerializedName("created_at") override var createdAt: Long = 0
    @Expose @SerializedName("updated_at") override var updatedAt: Long = 0

    override var sync: Boolean = false
    @Ignore
    var creditCard: Card? = null
    var repetition = 1
        get() {
            return Math.max(field, installments)
        }
    var installments = 1
    @Ignore
    private var chargeable: Chargeable? = null

    override val amount: Int
        get() = value

    internal val isShowInOverview: Boolean
        get() = !ignoreInOverview

    internal val isShowInResume: Boolean
        get() = !ignoreInResume

    init {
        App.getAppComponent().inject(this)
    }

    override fun credited(): Boolean {
        return true
    }

    override fun debited(): Boolean {
        return charged
    }

    fun setChargeable(chargeable: Chargeable) {
        this.chargeable = chargeable
        chargeableType = chargeable.chargeableType
        chargeableUuid = chargeable.uuid
    }

    val chargeableFromCache: Chargeable?
        @WorkerThread
        get() = getChargeable(false)

    @WorkerThread
    private fun loadChargeable(): Chargeable? {
        return getChargeable(true)
    }

    @WorkerThread
    private fun getChargeable(ignoreCache: Boolean): Chargeable? {
        if (chargeable == null || ignoreCache)
            chargeable = findChargeable(chargeableType, chargeableUuid)
        return chargeable
    }

    @WorkerThread
    fun uncharge() {
        if (charged) {
            val c = chargeableFromCache!!
            c.credit(value)
            when (c.chargeableType) {
                ChargeableType.ACCOUNT -> accountDataSource.save(c as Account)
                ChargeableType.CREDIT_CARD, ChargeableType.DEBIT_CARD -> if (c is Card)
                    cardDataSource.save(c)
                else
                    throw UnsupportedOperationException("Chargeable should be a card")
            }
            charged = false
        }
    }

    var bill: Bill?
        @WorkerThread
        get() {
            return billUuid?.let {
                val maybeBill = billDataSource.find(it)
                if(maybeBill.isEmpty.blockingGet())
                    return null
                return maybeBill.blockingFirst()
            }
        }
        set(bill) {
            billUuid = bill?.uuid
        }

    fun repeat(originalName: String, index: Int): Expense {
        val expense = Expense()
        if (installments > 1)
            expense.name = formatExpenseName(originalName, index)
        else
            expense.name = originalName
        expense.date = date!!.plusMonths(1)
        expense.value = value
        expense.chargeableUuid = chargeableUuid
        expense.chargeableType = chargeableType
        expense.billUuid = billUuid
        expense.chargedNextMonth = chargedNextMonth
        expense.ignoreInOverview = ignoreInOverview
        expense.ignoreInResume = ignoreInResume
        expense.creditCard = creditCard
        expense.repetition = repetition
        expense.installments = installments
        expense.chargeable = chargeable
        return expense
    }

    fun formatExpenseName(originalName: String, i: Int): String {
        return String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, i, installments)
    }

    internal fun showInOverview(b: Boolean) {
        ignoreInOverview = !b
    }

    internal fun showInResume(b: Boolean) {
        ignoreInResume = !b
    }

    override fun getData(): String {
        return "name=" + name + "" +
                "\nuuid=" + uuid +
                "\nserverId=" + serverId +
                "\ndate=" + Transaction.Companion.SIMPLE_DATE_FORMAT.format(date!!.toDate()) +
                "\nvalue=" + value +
                "\nremoved=" + removed
    }

    @WorkerThread
    fun debit() {
        val c = loadChargeable()!!
        c.debit(value)
        when (c.chargeableType) {
            ChargeableType.ACCOUNT -> accountDataSource.save(c as Account)
            ChargeableType.DEBIT_CARD, ChargeableType.CREDIT_CARD -> cardDataSource.save(c as Card)
        }
        charged = true
        expenseDataSource.save(this)

    }

    val incomeFormatted: String
        get() = value.toCurrencyFormatted()

    fun delete() {
        removed = true
        sync = false
        expenseDataSource.save(this)
    }

    fun dateIsPresent() = date != null

    override fun getDate(): DateTime {
        return date!!
    }

    fun setDate(date: DateTime) {
        this.date = date
    }

    fun findChargeable(type: ChargeableType?, uuid: String?): Chargeable? {
        if (type == null || uuid == null)
            return null

        return when (type) {
            ChargeableType.ACCOUNT -> accountDataSource.find(uuid).blockingFirst()
            ChargeableType.DEBIT_CARD, ChargeableType.CREDIT_CARD -> cardDataSource.find(uuid)
        }
    }
}
