package br.com.jonathanzanella.myexpenses.resume

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.support.annotation.UiThread
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.AccountAdapter
import br.com.jonathanzanella.myexpenses.bill.BillMonthlyResumeAdapter
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository
import br.com.jonathanzanella.myexpenses.views.BaseView
import kotlinx.android.synthetic.main.view_monthly_resume.view.*
import org.joda.time.DateTime

@SuppressLint("ViewConstructor")
internal class ResumeMonthlyView(context: Context, private val month: DateTime) : BaseView(context) {
    var singleRowHeight: Int = 0

    private var accountAdapter: AccountAdapter? = null
    private var receiptAdapter: ReceiptMonthlyResumeAdapter? = null
    private var expensesAdapter: ExpenseMonthlyResumeAdapter? = null
    private var billsAdapter: BillMonthlyResumeAdapter? = null
    private var receiptRepository: ReceiptRepository? = null
    private var expenseRepository: ExpenseRepository? = null

    init {
        singleRowHeight = resources.getDimensionPixelSize(R.dimen.single_row_height)
    }

    @UiThread
    override fun init() {
        View.inflate(context, R.layout.view_monthly_resume, this)
        expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(context))
        receiptRepository = ReceiptRepository(RepositoryImpl<Receipt>(context))

        initAccount()
        initReceipts()
        initExpenses()
        initBills()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshData()
    }

    private fun initAccount() {
        accountAdapter = AccountAdapter(month)
        accountAdapter!!.setFormat(AccountAdapter.Format.RESUME)

        accounts.adapter = accountAdapter
        accounts.setHasFixedSize(true)
        accounts.layoutManager = GridLayoutManager(context, 1)
        receipts.isNestedScrollingEnabled = false
    }

    private fun initReceipts() {
        receiptAdapter = ReceiptMonthlyResumeAdapter(receiptRepository!!)

        receipts.adapter = receiptAdapter
        receipts.setHasFixedSize(true)
        receipts.layoutManager = LinearLayoutManager(context)
        receipts.isNestedScrollingEnabled = false
    }

    private fun initExpenses() {
        expensesAdapter = ExpenseMonthlyResumeAdapter()

        expenses.adapter = expensesAdapter
        expenses.setHasFixedSize(true)
        expenses.layoutManager = LinearLayoutManager(context)
        expenses.isNestedScrollingEnabled = false
    }

    private fun initBills() {
        billsAdapter = BillMonthlyResumeAdapter()

        bills!!.adapter = billsAdapter
        bills!!.setHasFixedSize(true)
        bills!!.layoutManager = GridLayoutManager(context, 1)
        bills!!.isNestedScrollingEnabled = false
    }

    @UiThread
    override fun refreshData() {
        super.refreshData()
        val adapter = accountAdapter
        adapter?.let {
            adapter.refreshData()
            adapter.notifyDataSetChanged()
        }

        loadReceipts()
        loadExpenses()
        loadBills()
    }

    @UiThread
    private fun loadBills() {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                billsAdapter!!.loadData(month)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                billsAdapter!!.notifyDataSetChanged()
                bills!!.layoutParams.height = singleRowHeight * billsAdapter!!.itemCount

                updateTotalExpenses()
            }
        }.execute()
    }

    @UiThread
    private fun loadExpenses() {
        object : AsyncTask<Void, Void, List<Expense>>() {

            override fun doInBackground(vararg voids: Void): List<Expense> {
                return expenseRepository!!.expensesForResumeScreen(month)
            }

            override fun onPostExecute(expensesList: List<Expense>) {
                super.onPostExecute(expensesList)
                val adapter = expensesAdapter
                adapter?.let {
                    adapter.setExpenses(expensesList)
                    adapter.notifyDataSetChanged()
                }
                expenses!!.layoutParams.height = singleRowHeight * expensesAdapter!!.itemCount

                updateTotalExpenses()
            }
        }.execute()
    }

    @UiThread
    private fun updateTotalExpenses() {
        var totalExpensesValue = expensesAdapter!!.totalValue
        totalExpensesValue += billsAdapter!!.totalValue
        totalExpenses.text = CurrencyHelper.format(totalExpensesValue)

        updateBalance()
    }

    @UiThread
    private fun loadReceipts() {
        val adapter = receiptAdapter!!
        adapter.loadDataAsync(month, Runnable {
            receipts.layoutParams.height = singleRowHeight * adapter.itemCount
            val totalReceiptsValue = receiptAdapter!!.totalValue
            totalReceipts.text = CurrencyHelper.format(totalReceiptsValue)

            updateBalance()
        })
    }

    @UiThread
    private fun updateBalance() {
        var totalExpensesValue = expensesAdapter!!.totalValue
        totalExpensesValue += billsAdapter!!.totalValue

        val balanceValue = receiptAdapter!!.totalValue - totalExpensesValue
        balance.text = CurrencyHelper.format(balanceValue)
        if (balanceValue >= 0) {
            balance.setTextColor(ResourcesCompat.getColor(resources, R.color.value_unreceived, null))
        } else {
            balance.setTextColor(ResourcesCompat.getColor(resources, R.color.value_unpaid, null))
        }
    }
}
