package br.com.jonathanzanella.myexpenses.account.transactions

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import kotlinx.android.synthetic.main.view_account_month_transactions.view.*
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

class MonthTransactionsView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), MonthTransactionsContractView {

    private val simpleDateFormat = SimpleDateFormat("MMMM/yy", Locale.getDefault())
    private var singleRowHeight = context.resources.getDimensionPixelSize(R.dimen.single_row_height)
    private val monthTransactionsTemplate = resources.getString(R.string.month_transactions)
    private val presenter = MonthTransactionsPresenter(this)
    private var loadTransactionsCallback: LoadTransactionsCallback? = null

    init {
        View.inflate(context, R.layout.view_account_month_transactions, this)

        list.adapter = presenter.adapter
        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(context)
        list.isNestedScrollingEnabled = false
    }

    internal fun showBalance(account: Account, month: DateTime, balance: Int) {
        val date = simpleDateFormat.format(month.toDate())
        header.text = "$monthTransactionsTemplate $date"
        presenter.showBalance(account, month, balance)
    }

    override fun onBalanceUpdated(balance: Int) {
        this.balance.text = balance.toCurrencyFormatted()
        this.balance.setTextColor(ResourcesCompat.getColor(resources, (if (balance >= 0) R.color.value_unreceived else R.color.value_unpaid), null))

        loadTransactionsCallback?.onTransactionsLoaded(balance)

        list.minimumHeight = list.adapter.itemCount * singleRowHeight
    }

    fun setLoadTransactionsCallback(loadTransactionsCallback: LoadTransactionsCallback) {
        this.loadTransactionsCallback = loadTransactionsCallback
    }
}