package br.com.jonathanzanella.myexpenses.bill

import android.app.DatePickerDialog
import android.content.Context
import android.support.annotation.UiThread
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import javax.inject.Inject

class BillPresenter @Inject constructor(private val dataSource: BillDataSource) {
    private val disposable = CompositeDisposable()
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
        disposable.dispose()
        this.view = null
        this.editView = null
    }

    @UiThread
    fun onViewUpdated(invalidateCache: Boolean) {
        if (bill != null) {
            if (invalidateCache) {
                loadBill(bill!!.uuid!!)
            } else {
                updateView()
            }
        } else {
            updateView()
        }
    }

    fun updateView() {
        val v = editView
        val b = bill
        if (b != null) {
            if (v != null) {
                v.setTitle(R.string.edit_bill_title)
            } else {
                view!!.let {
                    val title = it.context.getString(R.string.bill)
                    it.setTitle(title + " " + b.name)
                }
            }
            view!!.showBill(b)
            initDate = b.initDate
            v?.onInitDateChanged(initDate!!)
            endDate = b.endDate
            v?.onEndDateChanged(endDate!!)
        } else {
            v?.setTitle(R.string.new_bill_title)
        }
    }

    fun onInitDate(ctx: Context) {
        checkEditViewSet()
        var time = initDate
        if (time == null)
            time = DateTime.now()
        DatePickerDialog(ctx, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
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
        DatePickerDialog(ctx, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            if (endDate == null)
                endDate = DateTime.now()
            endDate = endDate!!.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
            editView!!.onEndDateChanged(endDate!!)
        }, time!!.year, time.monthOfYear - 1, time.dayOfMonth).show()
    }

    @UiThread
    fun loadBill(uuid: String) {
        disposable.addAll(
            dataSource.find(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    bill = it
                    updateView()
                })
    }

    private fun checkEditViewSet() {
        if (editView == null)
            throw InvalidMethodCallException("save", javaClass.toString(), "View should be a Edit View")
    }

    @UiThread
    fun save() {
        checkEditViewSet()

        val v = editView!!
        val b = when(bill) {
            null -> v.fillBill(Bill())
            else -> v.fillBill(bill!!)
        }
        b.initDate = initDate
        b.endDate = endDate

        doAsync {
            val result = dataSource.save(b)

            uiThread {
                if (result.isValid) {
                    v.finishView()
                } else {
                    for (validationError in result.errors)
                        v.showError(validationError)
                }
            }
        }
    }

    val uuid: String?
        get() = bill?.uuid
}
