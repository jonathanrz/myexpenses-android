package br.com.jonathanzanella.myexpenses.receipt

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
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountRepository
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.source.Source
import br.com.jonathanzanella.myexpenses.source.SourceRepository
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.joda.time.DateTime

class ReceiptPresenter(private val repository: ReceiptRepository, private val sourceRepository: SourceRepository, private val accountRepository: AccountRepository) {
    private var view: ReceiptContract.View? = null
    private var editView: ReceiptContract.EditView? = null
    private var receipt: Receipt? = null
    private var source: Source? = null
    private var account: Account? = null
    private var date: DateTime? = null

    private fun resetCache() {
        date = null
        source = null
        account = null
        receipt = null
    }

    fun attachView(view: ReceiptContract.View) {
        this.view = view
    }

    fun attachView(view: ReceiptContract.EditView) {
        this.view = view
        this.editView = view
    }

    fun detachView() {
        this.view = null
        this.editView = null
    }

    @UiThread
    fun viewUpdated(invalidateCache: Boolean) {
        if (receipt != null || invalidateCache) {
            object : AsyncTask<Void, Void, Void>() {

                override fun doInBackground(vararg voids: Void): Void? {
                    loadReceipt(receipt!!.uuid!!)
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
    }

    fun updateView() {
        if (receipt != null) {
            if (editView != null) {
                editView!!.setTitle(R.string.edit_receipt_title)
            } else {
                val title = view!!.context.getString(R.string.receipt)
                view!!.setTitle(title + " " + receipt!!.name)
            }
            view!!.showReceipt(receipt!!)

            if (source != null)
                onSourceSelected(source!!.uuid!!)
            if (account != null)
                onAccountSelected(account!!.uuid!!)
            if (date == null && receipt != null)
                date = receipt!!.getDate()

        } else {
            if (editView != null)
                editView!!.setTitle(R.string.new_receipt_title)

            if (date == null)
                date = DateTime.now()
        }

        if (editView != null && date != null)
            editView!!.onDateChanged(date!!)
    }

    @UiThread
    fun refreshReceipt() {
        object : AsyncTask<Void, Void, Receipt>() {

            override fun doInBackground(vararg voids: Void): Receipt? {
                val uuid = receipt!!.uuid
                receipt = repository.find(uuid!!)
                if (receipt == null)
                    throw ReceiptNotFoundException(uuid)
                return receipt
            }

            override fun onPostExecute(receipt: Receipt?) {
                super.onPostExecute(receipt)
                updateView()
            }
        }.execute()
    }

    @WorkerThread
    fun loadReceipt(uuid: String) {
        resetCache()
        receipt = repository.find(uuid)
        if (receipt == null)
            throw ReceiptNotFoundException(uuid)
        source = receipt!!.source
        account = receipt!!.accountFromCache
        date = receipt!!.getDate()
    }

    private fun checkEditViewSet() {
        if (editView == null)
            throw InvalidMethodCallException("save", javaClass.toString(), "View should be a Edit View")
    }

    @UiThread
    fun save() {
        checkEditViewSet()
        if (receipt == null)
            receipt = Receipt()
        receipt = editView!!.fillReceipt(receipt!!)
        if (date != null)
            receipt!!.setDate(date!!)
        if (source != null)
            receipt!!.source = source
        if (account != null)
            receipt!!.setAccount(account!!)

        val originalName = receipt!!.name
        if (receipt!!.installments != 1) {
            receipt!!.name = receipt!!.formatReceiptName(receipt!!.name!!, 1)
            receipt!!.income = receipt!!.income / receipt!!.installments
        }

        object : AsyncTask<Void, Void, ValidationResult>() {

            override fun doInBackground(vararg voids: Void): ValidationResult {
                val result = repository.save(receipt!!)
                if (result.isValid)
                    generateReceiptsRepetition()

                return result
            }

            private fun generateReceiptsRepetition() {
                for (i in 1..receipt!!.repetition - 1) {
                    receipt = receipt!!.repeat(originalName!!, i + 1)
                    val repetitionResult = repository.save(receipt!!)
                    if (!repetitionResult.isValid)
                        Log.error("ExpensePresenter", "Error saving repetition of receipt " + receipt!!.getData() +
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
    fun delete(act: Activity) {
        AlertDialog.Builder(act)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage(R.string.message_confirm_deletion)
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    dialog.dismiss()

                    object : AsyncTask<Void, Void, Void>() {

                        //TODO: add loading

                        override fun doInBackground(vararg voids: Void): Void? {
                            val acc = receipt!!.accountFromCache
                            acc!!.credit(receipt!!.income * -1)
                            accountRepository.save(acc)

                            receipt!!.delete()
                            return null
                        }

                        override fun onPostExecute(aVoid: Void) {
                            super.onPostExecute(aVoid)
                            val i = Intent()
                            act.setResult(RESULT_OK, i)
                            act.finish()
                        }
                    }.execute()
                }
                .setNegativeButton(android.R.string.no) { dialog, which -> dialog.dismiss() }
                .show()
    }

    val uuid: String?
        get() = if (receipt != null) receipt!!.uuid else null

    @UiThread
    fun storeBundle(extras: Bundle) {
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                if (extras.containsKey(KEY_RECEIPT_UUID))
                    loadReceipt(extras.getString(KEY_RECEIPT_UUID))

                if (extras.containsKey(KEY_SOURCE_UUID))
                    source = sourceRepository.find(extras.getString(KEY_SOURCE_UUID))

                if (extras.containsKey(KEY_ACCOUNT_UUID))
                    account = accountRepository.find(extras.getString(KEY_ACCOUNT_UUID)!!)

                if (extras.containsKey(KEY_DATE))
                    date = DateTime(extras.getLong(KEY_DATE))
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                updateView()
            }
        }.execute()
    }

    fun onSaveInstanceState(outState: Bundle) {
        if (receipt != null)
            outState.putString(KEY_RECEIPT_UUID, receipt!!.uuid)
        if (source != null)
            outState.putString(KEY_SOURCE_UUID, source!!.uuid)
        if (account != null)
            outState.putString(KEY_ACCOUNT_UUID, account!!.uuid)
        if (date != null)
            outState.putLong(KEY_DATE, date!!.millis)
    }

    fun onSourceSelected(sourceUuid: String) {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                source = sourceRepository.find(sourceUuid)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                if (editView != null)
                    editView!!.onSourceSelected(source!!)
            }
        }.execute()
    }

    @UiThread
    fun onAccountSelected(accountUuid: String) {
        object : AsyncTask<Void, Void, Account>() {

            override fun doInBackground(vararg voids: Void): Account {
                account = accountRepository.find(accountUuid)
                return account!!
            }

            override fun onPostExecute(account: Account) {
                super.onPostExecute(account)
                editView?.onAccountSelected(account)
            }
        }.execute()
    }

    fun hasReceipt(): Boolean {
        return receipt != null
    }

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
        val KEY_RECEIPT_UUID = "KeyReceiptUuid"
        private val KEY_SOURCE_UUID = "KeySourceUuid"
        private val KEY_ACCOUNT_UUID = "KeyAccountUuid"
        private val KEY_DATE = "KeyDate"
    }
}