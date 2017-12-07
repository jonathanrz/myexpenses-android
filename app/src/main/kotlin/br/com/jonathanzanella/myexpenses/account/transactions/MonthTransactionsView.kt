package br.com.jonathanzanella.myexpenses.account.transactions

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.extensions.fromIoToMainThread
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.transaction.TransactionAdapter
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_account_month_transactions.view.*
import org.joda.time.DateTime
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MonthTransactionsView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), MonthTransactionsContractView {

    private val adapter = TransactionAdapter()
    private val simpleDateFormat = SimpleDateFormat("MMMM/yy", Locale.getDefault())
    private var singleRowHeight = context.resources.getDimensionPixelSize(R.dimen.single_row_height)
    private val monthTransactionsTemplate = resources.getString(R.string.month_transactions)
    private var showBalanceDisposable: Disposable? = null

    @Inject
    lateinit var presenter: MonthTransactionsPresenter

    init {
        App.getAppComponent().inject(this)
    }

    init {
        View.inflate(context, R.layout.view_account_month_transactions, this)

        list.adapter = adapter
        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(context)
        list.isNestedScrollingEnabled = false
    }

    internal fun showBalance(account: Account, month: DateTime) {
        showBalanceDisposable?.dispose()

        val date = simpleDateFormat.format(month.toDate())
        header.text = "$monthTransactionsTemplate $date"

        showBalanceDisposable = presenter.getAccountTransactions(account, month)
                .doOnNext { adapter.setTransactions(it) }
                .flatMap { presenter.calculateAccountBalance(account, it) }
                .fromIoToMainThread()
                .subscribe({
                    adapter.notifyDataSetChanged()
                    onBalanceUpdated(it)
                }, { e ->
                    Timber.tag(javaClass.canonicalName).e("Error showing transactions, message ${e.message}")
                })
    }

    override fun onDetachedFromWindow() {
        showBalanceDisposable?.dispose()
        super.onDetachedFromWindow()
    }

    override fun onBalanceUpdated(balance: Int) {
        this.balance.text = balance.toCurrencyFormatted()
        this.balance.setTextColor(ResourcesCompat.getColor(resources, (if (balance >= 0) R.color.value_unreceived else R.color.value_unpaid), null))

        list.minimumHeight = list.adapter.itemCount * singleRowHeight
    }
}
