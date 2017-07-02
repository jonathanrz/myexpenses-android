package br.com.jonathanzanella.myexpenses.expense

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.v7.app.AlertDialog
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.bill.BillRepository
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import br.com.jonathanzanella.myexpenses.chargeable.ListChargeableActivity
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.joda.time.DateTime

class ExpensePresenter(private val repository: ExpenseRepository, private val billRepository: BillRepository) {
    private var view: ExpenseContract.View? = null
    private var editView: ExpenseContract.EditView? = null
    private var expense: Expense? = null
    private var date: DateTime? = null
    private var bill: Bill? = null
    private var chargeable: Chargeable? = null

    private fun resetCache() {
        date = null
        bill = null
        chargeable = null
        expense = null
    }

    fun attachView(view: ExpenseContract.View) {
        this.view = view
    }

    fun attachView(view: ExpenseContract.EditView) {
        this.view = view
        this.editView = view
    }

    fun detachView() {
        this.view = null
        this.editView = null
    }

    @UiThread
    fun onViewUpdated(invalidateCache: Boolean) {
        val e = expense
        if (e != null) {
            if (invalidateCache) {
                object : AsyncTask<Void, Void, Void>() {

                    override fun doInBackground(vararg voids: Void): Void? {
                        expense = repository.find(e.uuid!!)
                        return null
                    }

                    override fun onPostExecute(aVoid: Void?) {
                        super.onPostExecute(aVoid)
                        updateView()
                    }
                }.execute()
            } else {
                updateView()
            }
        } else {
            updateView()
        }
    }

    private fun updateView() {
        val v = editView
        val e = expense
        if (e != null) {
            if (v != null) {
                v.setTitle(R.string.edit_expense_title)
            } else {
                view!!.let {
                    val title = it.context.getString(R.string.expense)
                    it.setTitle(title + " " + e.name)
                }
            }
            view!!.showExpense(e)

            loadBill()
            loadChargeable()

            date = date ?: e.getDate()
            val d = date
            if (d != null)
                v?.onDateChanged(d)
        } else {
            v?.setTitle(R.string.new_expense_title)

            date = date ?: DateTime.now()
            v?.onDateChanged(date!!)
        }
    }

    private fun loadChargeable() {
        object : AsyncTask<Void, Void, Chargeable>() {

            override fun doInBackground(vararg voids: Void): Chargeable? {
                chargeable = expense!!.chargeableFromCache
                return chargeable
            }

            override fun onPostExecute(chargeable: Chargeable?) {
                super.onPostExecute(chargeable)
                if (chargeable != null)
                    editView?.onChargeableSelected(chargeable)
            }
        }.execute()
    }

    private fun loadBill() {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                bill = expense!!.bill
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                val b = bill
                if (b != null)
                    editView?.onBillSelected(b)
            }
        }.execute()
    }

    @UiThread
    fun refreshExpense() {
        val e = expense
        if (e != null) {
            object : AsyncTask<Void, Void, Void>() {

                override fun doInBackground(vararg voids: Void): Void? {
                    loadExpense(e.uuid!!)
                    return null
                }

                override fun onPostExecute(aVoid: Void?) {
                    super.onPostExecute(aVoid)
                    updateView()
                }
            }.execute()
        }
    }

    @WorkerThread
    fun loadExpense(uuid: String): Expense {
        resetCache()
        expense = repository.find(uuid)
        val e = expense ?: throw ExpenseNotFoundException(uuid)
        date = e.getDate()
        return e
    }

    private fun checkEditViewSet() : ExpenseContract.EditView {
        return editView ?: throw InvalidMethodCallException("save", javaClass.toString(), "View should be a Edit View")
    }

    @UiThread
    fun save() {
        val v = checkEditViewSet()
        expense = v.fillExpense(expense ?: Expense())
        var e = expense!!
        date?.let { e.setDate(it) }
        chargeable?.let { e.setChargeable(it) }
        bill?.let { e.bill = it }

        val originalName = e.name
        if (e.installments != 1) {
            e.name = e.formatExpenseName(e.name!!, 1)
            e.value = e.value / e.installments
            e.valueToShowInOverview = e.valueToShowInOverview / e.installments
        }

        object : AsyncTask<Void, Void, ValidationResult>() {
            override fun doInBackground(vararg voids: Void): ValidationResult {
                val result = repository.save(e)
                if (result.isValid)
                    generateExpensesRepetition()

                return result
            }

            private fun generateExpensesRepetition() {
                for (i in 1..e.repetition - 1) {
                    e = e.repeat(originalName!!, i + 1)
                    val repetitionResult = repository.save(e)
                    if (!repetitionResult.isValid)
                        Log.error("ExpensePresenter", "Error saving repetition of expense " + e.getData() +
                                " error=" + repetitionResult.errors.toString())
                }
            }

            override fun onPostExecute(result: ValidationResult) {
                super.onPostExecute(result)
                if (result.isValid) {
                    v.finishView()
                } else {
                    for (validationError in result.errors)
                        v.showError(validationError)
                }
            }
        }.execute()
    }

    @UiThread
    fun edit(act: Activity) {
        val i = Intent(act, EditExpenseActivity::class.java)
        i.putExtra(EditExpenseActivity.KEY_EXPENSE_UUID, uuid)
        act.startActivityForResult(i, REQUEST_EDIT_EXPENSE)
    }

    @UiThread
    fun delete(act: Activity) {
        AlertDialog.Builder(act)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage(R.string.message_confirm_deletion)
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    dialog.dismiss()

                    expense!!.apply {
                        uncharge()
                        delete()
                    }

                    val i = Intent()
                    act.setResult(RESULT_OK, i)
                    act.finish()
                }
                .setNegativeButton(android.R.string.no) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    @UiThread
    fun onChargeableSelected(type: ChargeableType, uuid: String) {
        object : AsyncTask<Void, Void, Chargeable>() {

            override fun doInBackground(vararg voids: Void): Chargeable? {
                chargeable = Expense.findChargeable(type, uuid)
                return chargeable
            }

            override fun onPostExecute(chargeable: Chargeable?) {
                super.onPostExecute(chargeable)
                if (chargeable != null)
                    editView!!.onChargeableSelected(chargeable)
            }
        }.execute()
    }

    @UiThread
    fun onBillSelected(uuid: String) {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                bill = BillRepository(RepositoryImpl<Bill>(MyApplication.getContext()), repository).find(uuid)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                val b = bill
                if (b != null)
                    editView!!.onBillSelected(b)
            }
        }.execute()
    }

    val uuid: String?
        get() = expense?.uuid

    @UiThread
    fun storeBundle(extras: Bundle) {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                if (extras.containsKey(KEY_EXPENSE_UUID))
                    loadExpense(extras.getString(KEY_EXPENSE_UUID))
                if (extras.containsKey(KEY_BILL_UUID))
                    bill = billRepository.find(extras.getString(KEY_BILL_UUID)!!)
                if (extras.containsKey(KEY_DATE))
                    date = DateTime(extras.getLong(KEY_DATE))
                val key = ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE
                if (extras.containsKey(key)) {
                    val selectedUuid = extras.getString(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_UUID)
                    chargeable = Expense.findChargeable(extras.getSerializable(key) as ChargeableType, selectedUuid)
                }
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                updateView()
            }
        }.execute()
    }

    fun onSaveInstanceState(outState: Bundle) {
        expense?.let { outState.putString(KEY_EXPENSE_UUID, it.uuid) }
        bill?.let { outState.putString(KEY_BILL_UUID, it.uuid) }
        date?.let { outState.putLong(KEY_DATE, it.millis) }
        chargeable?.let {
            outState.putString(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_UUID, it.uuid)
            outState.putSerializable(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE, it.chargeableType)
        }
    }

    @UiThread
    fun onActivityResult(requestCode: Int, resultCode: Int) {
        when (requestCode) {
            REQUEST_EDIT_EXPENSE -> {
                if (resultCode == Activity.RESULT_OK)
                    refreshExpense()
            }
        }
    }

    @WorkerThread
    fun hasChargeable(): Boolean {
        return expense?.chargeableFromCache != null
    }

    @UiThread
    fun onDate(ctx: Context) {
        checkEditViewSet()
        var time = date
        if (time == null)
            time = DateTime.now()
        DatePickerDialog(ctx, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            if (date == null)
                date = DateTime.now()
            date = date!!.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
            editView!!.onDateChanged(date!!)
        }, time!!.year, time.monthOfYear - 1, time.dayOfMonth).show()
    }

    companion object {
        val KEY_EXPENSE_UUID = "KeyExpenseUuid"
        private val REQUEST_EDIT_EXPENSE = 1
        private val KEY_BILL_UUID = "KeyBillUuid"
        private val KEY_DATE = "KeyDate"
    }
}