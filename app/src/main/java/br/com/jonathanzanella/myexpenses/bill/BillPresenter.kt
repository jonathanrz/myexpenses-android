package br.com.jonathanzanella.myexpenses.bill

import android.app.DatePickerDialog
import android.content.Context
import android.os.AsyncTask
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException
import org.joda.time.DateTime

internal class BillPresenter(private val repository: BillRepository) {
    private var view: BillContract.View? = null
    private var editView: BillContract.EditView? = null
    private var bill: Bill? = null
    private var initDate: DateTime? = null
    private var endDate: DateTime? = null

    fun attachView(view: BillContract.View) {
        this.view = view
    }

    fun attachView(view: BillContract.EditView) {
        this.view = view
        this.editView = view
    }

    fun detachView() {
        this.view = null
        this.editView = null
    }

    @UiThread
    fun onViewUpdated(invalidateCache: Boolean) {
        if (bill != null) {
            if (invalidateCache) {
                object : AsyncTask<Void, Void, Void>() {

                    override fun doInBackground(vararg voids: Void): Void? {
                        loadBill(bill!!.uuid!!)
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

    fun updateView() {
        if (bill != null) {
            if (editView != null) {
                editView!!.setTitle(R.string.edit_bill_title)
            } else {
                val title = view!!.context.getString(R.string.bill)
                view!!.setTitle(title + " " + bill!!.name)
            }
            view!!.showBill(bill!!)
            initDate = bill!!.initDate
            if (editView != null)
                editView!!.onInitDateChanged(initDate!!)
            endDate = bill!!.endDate
            if (editView != null)
                editView!!.onEndDateChanged(endDate!!)
        } else {
            if (editView != null)
                editView!!.setTitle(R.string.new_bill_title)
        }
    }

    fun onInitDate(ctx: Context) {
        checkEditViewSet()
        var time = initDate
        if (time == null)
            time = DateTime.now()
        DatePickerDialog(ctx, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            if (initDate == null)
                initDate = DateTime.now()
            initDate = initDate!!.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
            editView!!.onInitDateChanged(initDate!!)
        }, time!!.year, time.monthOfYear - 1, time.dayOfMonth).show()
    }

    fun onEndDate(ctx: Context) {
        checkEditViewSet()
        var time = initDate
        if (time == null)
            time = DateTime.now()
        DatePickerDialog(ctx, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            if (endDate == null)
                endDate = DateTime.now()
            endDate = endDate!!.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
            editView!!.onEndDateChanged(endDate!!)
        }, time!!.year, time.monthOfYear - 1, time.dayOfMonth).show()
    }

    @WorkerThread
    fun loadBill(uuid: String) {
        bill = repository.find(uuid)
    }

    private fun checkEditViewSet() {
        if (editView == null)
            throw InvalidMethodCallException("save", javaClass.toString(), "View should be a Edit View")
    }

    fun save() {
        checkEditViewSet()
        val b = when(bill) {
            null -> editView!!.fillBill(Bill())
            else -> editView!!.fillBill(bill!!)
        }
        b.initDate = initDate
        b.endDate = endDate
        val result = repository.save(b)

        if (result.isValid) {
            editView!!.finishView()
        } else {
            for (validationError in result.errors)
                editView!!.showError(validationError)
        }
    }

    val uuid: String?
        get() = if (bill != null) bill!!.uuid else null
}
