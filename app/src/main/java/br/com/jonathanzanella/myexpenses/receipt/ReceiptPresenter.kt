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
        val r = receipt
        if (r != null) {
            val v = editView
            if (v != null) {
                v.setTitle(R.string.edit_receipt_title)
            } else {
                view!!.let {
                    val title = it.context.getString(R.string.receipt)
                    it.setTitle(title + " " + r.name)
                }
            }
            view!!.showReceipt(r)

            source?.let { onSourceSelected(it.uuid!!) }
            account?.let { onAccountSelected(it.uuid!!) }

            if (date == null)
                date = r.getDate()
        } else {
            editView?.setTitle(R.string.new_receipt_title)

            if (date == null)
                date = DateTime.now()
        }

        editView?.onDateChanged(date!!)
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
        val r = repository.find(uuid) ?: throw ReceiptNotFoundException(uuid)
        receipt = r
        r.let {
            source = it.source
            account = it.accountFromCache
            date = it.getDate()
        }
    }

    private fun checkEditViewSet() {
        if (editView == null)
            throw InvalidMethodCallException("save", javaClass.toString(), "View should be a Edit View")
    }

    @UiThread
    fun save() {
        checkEditViewSet()
        receipt = editView!!.fillReceipt(receipt ?: Receipt())
        var r = receipt!!
        date?.let { r.setDate(it) }
        account?.let { r.setAccount(it) }

        if (source != null)
            r.source = source

        val originalName = r.name
        if (r.installments != 1) {
            r.name = r.formatReceiptName(r.name!!, 1)
            r.income = r.income / r.installments
        }

        object : AsyncTask<Void, Void, ValidationResult>() {

            override fun doInBackground(vararg voids: Void): ValidationResult {
                val result = repository.save(receipt!!)
                if (result.isValid)
                    generateReceiptsRepetition()

                return result
            }

            private fun generateReceiptsRepetition() {
                for (i in 1..r.repetition - 1) {
                    r = r.repeat(originalName!!, i + 1)
                    receipt = r
                    val repetitionResult = repository.save(r)
                    if (!repetitionResult.isValid)
                        Log.error("ExpensePresenter", "Error saving repetition of receipt " + r.getData() +
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
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    dialog.dismiss()

                    object : AsyncTask<Void, Void, Void>() {

                        //TODO: add loading

                        override fun doInBackground(vararg voids: Void): Void? {
                            receipt!!.let {
                                val acc = it.accountFromCache
                                acc!!.credit(it.income * -1)
                                accountRepository.save(acc)

                                it.delete()
                            }

                            return null
                        }

                        override fun onPostExecute(aVoid: Void?) {
                            super.onPostExecute(aVoid)
                            val i = Intent()
                            act.setResult(RESULT_OK, i)
                            act.finish()
                        }
                    }.execute()
                }
                .setNegativeButton(android.R.string.no) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    val uuid: String?
        get() = receipt?.uuid

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
        receipt?.let { outState.putString(KEY_RECEIPT_UUID, it.uuid) }
        source?.let { outState.putString(KEY_SOURCE_UUID, it.uuid) }
        account?.let { outState.putString(KEY_ACCOUNT_UUID, it.uuid) }
        date?.let { outState.putLong(KEY_DATE, it.millis) }
    }

    fun onSourceSelected(sourceUuid: String) {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                source = sourceRepository.find(sourceUuid)
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                editView?.onSourceSelected(source!!)
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
        DatePickerDialog(ctx, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
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