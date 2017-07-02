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
        if (expense != null) {
            if (invalidateCache) {
                object : AsyncTask<Void, Void, Void>() {

                    override fun doInBackground(vararg voids: Void): Void? {
                        expense = repository.find(expense!!.uuid!!)
                        return null
                    }

                    override fun onPostExecute(aVoid: Void) {
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
        if (expense != null) {
            if (editView != null) {
                editView!!.setTitle(R.string.edit_expense_title)
            } else {
                val title = view!!.context.getString(R.string.expense)
                view!!.setTitle(title + " " + expense!!.name)
            }
            view!!.showExpense(expense!!)

            loadBill()
            loadChargeable()

            if (date == null)
                date = expense!!.getDate()
            if (editView != null && date != null)
                editView!!.onDateChanged(date!!)
        } else {
            if (editView != null)
                editView!!.setTitle(R.string.new_expense_title)

            if (date == null)
                date = DateTime.now()
            if (editView != null && date != null)
                editView!!.onDateChanged(date!!)
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
                if (editView != null && chargeable != null)
                    editView!!.onChargeableSelected(chargeable)
            }
        }.execute()
    }

    private fun loadBill() {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                bill = expense!!.bill
                return null
            }

            override fun onPostExecute(aVoid: Void) {
                super.onPostExecute(aVoid)
                if (editView != null && bill != null)
                    editView!!.onBillSelected(bill!!)
            }
        }.execute()
    }

    @UiThread
    fun refreshExpense() {
        if (expense != null) {
            object : AsyncTask<Void, Void, Void>() {

                override fun doInBackground(vararg voids: Void): Void? {
                    loadExpense(expense!!.uuid!!)
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

    private fun checkEditViewSet() {
        if (editView == null)
            throw InvalidMethodCallException("save", javaClass.toString(), "View should be a Edit View")
    }

    @UiThread
    fun save() {
        checkEditViewSet()
        if (expense == null)
            expense = Expense()
        expense = editView!!.fillExpense(expense!!)
        if (date != null)
            expense!!.setDate(date!!)
        if (bill != null)
            expense!!.bill = bill
        if (chargeable != null)
            expense!!.setChargeable(chargeable!!)
        val originalName = expense!!.name
        if (expense!!.installments != 1) {
            expense!!.name = expense!!.formatExpenseName(expense!!.name!!, 1)
            expense!!.value = expense!!.value / expense!!.installments
            expense!!.valueToShowInOverview = expense!!.valueToShowInOverview / expense!!.installments
        }

        object : AsyncTask<Void, Void, ValidationResult>() {
            override fun doInBackground(vararg voids: Void): ValidationResult {
                val result = repository.save(expense!!)
                if (result.isValid)
                    generateExpensesRepetition()

                return result
            }

            private fun generateExpensesRepetition() {
                for (i in 1..expense!!.repetition - 1) {
                    expense = expense!!.repeat(originalName!!, i + 1)
                    val repetitionResult = repository.save(expense!!)
                    if (!repetitionResult.isValid)
                        Log.error("ExpensePresenter", "Error saving repetition of expense " + expense!!.getData() +
                                " error=" + repetitionResult.errors.toString())
                }
            }

            override fun onPostExecute(result: ValidationResult) {
                super.onPostExecute(result)
                if (result.isValid) {
                    editView!!.finishView()
                } else {
                    for (validationError in result.errors)
                        editView!!.showError(validationError)
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
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    dialog.dismiss()

                    expense!!.uncharge()
                    expense!!.delete()
                    val i = Intent()
                    act.setResult(RESULT_OK, i)
                    act.finish()
                }
                .setNegativeButton(android.R.string.no) { dialog, which -> dialog.dismiss() }
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

            override fun onPostExecute(aVoid: Void) {
                super.onPostExecute(aVoid)
                if (bill != null)
                    editView!!.onBillSelected(bill!!)
            }
        }.execute()
    }

    val uuid: String?
        get() = if (expense != null) expense!!.uuid else null

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

            override fun onPostExecute(aVoid: Void) {
                super.onPostExecute(aVoid)
                updateView()
            }
        }.execute()
    }

    fun onSaveInstanceState(outState: Bundle) {
        if (expense != null)
            outState.putString(KEY_EXPENSE_UUID, expense!!.uuid)
        if (bill != null)
            outState.putString(KEY_BILL_UUID, bill!!.uuid)
        if (date != null)
            outState.putLong(KEY_DATE, date!!.millis)
        if (chargeable != null) {
            outState.putString(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_UUID, chargeable!!.uuid)
            outState.putSerializable(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE, chargeable!!.chargeableType)
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

    private fun hasExpense(): Boolean {
        return expense != null
    }

    @WorkerThread
    fun hasChargeable(): Boolean {
        return hasExpense() && expense!!.chargeableFromCache != null
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