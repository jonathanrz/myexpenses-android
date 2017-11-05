package br.com.jonathanzanella.myexpenses.resume

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.UiThread
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.AccountAdapter
import br.com.jonathanzanella.myexpenses.bill.BillMonthlyResumeAdapter
import br.com.jonathanzanella.myexpenses.expense.ExpenseDataSource
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.receipt.ReceiptDataSource
import br.com.jonathanzanella.myexpenses.views.FilterableView
import br.com.jonathanzanella.myexpenses.views.RefreshableView
import kotlinx.android.synthetic.main.view_monthly_resume.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import javax.inject.Inject

@SuppressLint("ViewConstructor")
class ResumeMonthlyView(context: Context, private val month: DateTime) : FrameLayout(context), RefreshableView, FilterableView {
    override var filter = ""
    private var singleRowHeight: Int = 0

    @Inject
    lateinit var receiptDataSource: ReceiptDataSource
    @Inject
    lateinit var expenseDataSource: ExpenseDataSource
    @Inject
    lateinit var accountAdapter: AccountAdapter

    private val receiptAdapter: ReceiptMonthlyResumeAdapter
    private var expensesAdapter = ExpenseMonthlyResumeAdapter()
    private var billsAdapter = BillMonthlyResumeAdapter()

    init {
        App.getAppComponent().inject(this)
        receiptAdapter = ReceiptMonthlyResumeAdapter(receiptDataSource)
        singleRowHeight = resources.getDimensionPixelSize(R.dimen.single_row_height)

        View.inflate(context, R.layout.view_monthly_resume, this)

        initAccount()
        initReceipts()
        initExpenses()
        initBills()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        accountAdapter.setFormat(AccountAdapter.Format.RESUME)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        accountAdapter.onDestroy()
        billsAdapter.onDestroy()
    }

    private fun initAccount() {
        accounts.adapter = accountAdapter
        accounts.setHasFixedSize(true)
        accounts.layoutManager = GridLayoutManager(context, 1)
        receipts.isNestedScrollingEnabled = false
    }

    private fun initReceipts() {
        receipts.adapter = receiptAdapter
        receipts.setHasFixedSize(true)
        receipts.layoutManager = LinearLayoutManager(context)
        receipts.isNestedScrollingEnabled = false
    }

    private fun initExpenses() {
        expenses.adapter = expensesAdapter
        expenses.setHasFixedSize(true)
        expenses.layoutManager = LinearLayoutManager(context)
        expenses.isNestedScrollingEnabled = false
    }

    private fun initBills() {
        bills.adapter = billsAdapter
        bills.setHasFixedSize(true)
        bills.layoutManager = GridLayoutManager(context, 1)
        bills.isNestedScrollingEnabled = false
    }

    @UiThread
    override fun refreshData() {
        loadReceipts()
        loadExpenses()
        loadBills()
    }

    @UiThread
    private fun loadBills() {
        doAsync {
            billsAdapter.loadData(month)

            uiThread {
                billsAdapter.notifyDataSetChanged()
                bills.layoutParams.height = singleRowHeight * billsAdapter.itemCount

                updateTotalExpenses()
            }
        }
    }

    @UiThread
    private fun loadExpenses() {
        doAsync {
            val expensesList = expenseDataSource.expensesForResumeScreen(month)

            uiThread {
                expensesAdapter.setExpenses(expensesList)
                expensesAdapter.notifyDataSetChanged()
                expenses.layoutParams.height = singleRowHeight * expensesAdapter.itemCount

                updateTotalExpenses()
            }
        }
    }

    @UiThread
    private fun updateTotalExpenses() {
        var totalExpensesValue = expensesAdapter.totalValue
        totalExpensesValue += billsAdapter.totalValue
        totalExpenses.text = totalExpensesValue.toCurrencyFormatted()

        updateBalance()
    }

    @UiThread
    private fun loadReceipts() {
        receiptAdapter.loadDataAsync(month, Runnable {
            receipts.layoutParams.height = singleRowHeight * receiptAdapter.itemCount
            val totalReceiptsValue = receiptAdapter.totalValue
            totalReceipts.text = totalReceiptsValue.toCurrencyFormatted()

            updateBalance()
        })
    }

    @UiThread
    private fun updateBalance() {
        var totalExpensesValue = expensesAdapter.totalValue
        totalExpensesValue += billsAdapter.totalValue

        val balanceValue = receiptAdapter.totalValue - totalExpensesValue
        balance.text = balanceValue.toCurrencyFormatted()
        if (balanceValue >= 0) {
            balance.setTextColor(ResourcesCompat.getColor(resources, R.color.value_unreceived, null))
        } else {
            balance.setTextColor(ResourcesCompat.getColor(resources, R.color.value_unpaid, null))
        }
    }
}
