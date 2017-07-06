package br.com.jonathanzanella.myexpenses.account.transactions

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.views.BaseView
import kotlinx.android.synthetic.main.view_account_month_transactions.view.*
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

class MonthTransactionsView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseView(context, attrs, defStyleAttr), MonthTransactionsContractView {

    val simpleDateFormat = SimpleDateFormat("MMMM/yy", Locale.getDefault())
    private var singleRowHeight = context.resources.getDimensionPixelSize(R.dimen.single_row_height)
    private val monthTransactionsTemplate = resources.getString(R.string.month_transactions)
    private val presenter = MonthTransactionsPresenter(context, this)
    private var loadTransactionsCallback: LoadTransactionsCallback? = null

    private var account : Account? = null
    private var month : DateTime? = null
    private var balanceValue : Int? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        View.inflate(context, R.layout.view_account_month_transactions, this)

        list.adapter = presenter.adapter
        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(context)
        list.isNestedScrollingEnabled = false

        account?.let {
            showBalance(it, month!!, balanceValue!!)
            account = null
            month = null
            balanceValue = null
        }
    }

    internal fun showBalance(account: Account, month: DateTime, balance: Int) {
        if(header != null) {
            header.text = "$monthTransactionsTemplate $simpleDateFormat.format(month.toDate())"
            presenter.showBalance(account, month, balance)
        } else {
            this.account = account
            this.month = month
            this.balanceValue = balance
        }
    }

    override fun onBalanceUpdated(balance: Int) {
        this.balance.text = CurrencyHelper.format(balance)
        this.balance.setTextColor(ResourcesCompat.getColor(resources, (if (balance >= 0) R.color.value_unreceived else R.color.value_unpaid), null))

        loadTransactionsCallback?.onTransactionsLoaded(balance)

        list.minimumHeight = list.adapter.itemCount * singleRowHeight
    }

    fun setLoadTransactionsCallback(loadTransactionsCallback: LoadTransactionsCallback) {
        this.loadTransactionsCallback = loadTransactionsCallback
    }
}
