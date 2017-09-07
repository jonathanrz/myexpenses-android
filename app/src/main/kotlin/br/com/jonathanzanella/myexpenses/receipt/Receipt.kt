package br.com.jonathanzanella.myexpenses.receipt

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.Environment
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.source.Source
import br.com.jonathanzanella.myexpenses.source.SourceRepository
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import javax.inject.Inject

@Entity
class Receipt : Transaction, UnsyncModel {
    @Ignore @Inject
    lateinit var accountDataSource: AccountDataSource
    @Ignore @Inject
    lateinit var receiptRepository: ReceiptRepository
    @Ignore @Inject
    lateinit var sourceRepository: SourceRepository

    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0
    @Expose
    override var uuid: String? = null
    @Expose
    override var name: String? = null
    @Expose
    private var date: DateTime? = null
    @Expose
    var income: Int = 0
    @Expose
    var sourceUuid: String? = null
    @Expose
    var accountUuid: String? = null
    @Expose
    var credited: Boolean = false
    @Expose
    var ignoreInResume: Boolean = false
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
    @Expose
    var removed: Boolean = false
    var repetition = 1
        get() = Math.max(field, installments)
    var installments = 1
    @Ignore
    private var account: Account? = null
    @Ignore
    var source: Source? = null
        get() {
            val uuid = sourceUuid
            if (field == null && uuid != null)
                field = sourceRepository.find(uuid)
            return field
        }
        set(s) {
            field = s
            sourceUuid = s?.uuid
        }

    override val amount: Int
        get() = income

    init {
        App.getAppComponent().inject(this)
    }

    @Ignore
    override fun credited(): Boolean {
        return credited
    }

    override fun debited(): Boolean {
        return true
    }

    val accountFromCache: Account?
        get() = getAccount(false)

    private fun loadAccount(): Account? {
        return getAccount(true)
    }

    private fun getAccount(ignoreCache: Boolean): Account? {
        if (account == null || ignoreCache) {
            accountUuid?.let {
                account = accountDataSource.find(it)
            }
        }
        return account
    }

    fun setAccount(a: Account) {
        this.account = a
        accountUuid = a.uuid
    }

    internal var isShowInResume: Boolean
        get() = !ignoreInResume
        set(b) {
            ignoreInResume = !b
        }

    val incomeFormatted: String
        get() = income.toCurrencyFormatted()

    fun repeat(originalName: String, index: Int): Receipt {
        val receipt = Receipt()
        if (installments > 1)
            receipt.name = formatReceiptName(originalName, index)
        else
            receipt.name = originalName
        receipt.date = date!!.plusMonths(1)
        receipt.income = income
        receipt.sourceUuid = sourceUuid
        receipt.accountUuid = accountUuid
        receipt.ignoreInResume = ignoreInResume
        receipt.serverId = serverId
        receipt.repetition = repetition
        receipt.installments = installments
        receipt.account = account
        receipt.source = source

        return receipt
    }

    fun formatReceiptName(originalName: String, i: Int): String {
        return String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, i, installments)
    }

    override fun getData(): String {
        return "name=" + name +
                "\nuuid=" + uuid +
                "\nserverId=" + serverId +
                "\ndate=" + Transaction.Companion.SIMPLE_DATE_FORMAT.format(date!!.toDate()) +
                "\nincome=" + income
    }

    @WorkerThread
    fun credit() {
        val acc = loadAccount()!!
        acc.credit(income)
        accountDataSource.save(acc)
        credited = true
        receiptRepository.save(this)
    }

    fun delete() {
        removed = true
        sync = false
        receiptRepository.save(this)
    }

    fun isDatePresent() = date != null

    override fun getDate(): DateTime {
        return date!!
    }

    fun setDate(date: DateTime) {
        this.date = date
    }
}
