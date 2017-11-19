package br.com.jonathanzanella.myexpenses.receipt

import android.content.Context
import android.support.annotation.StringRes
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.source.Source
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import org.joda.time.DateTime

interface ReceiptContract {
    interface View {
        val context: Context
        fun setTitle(@StringRes string: Int)
        fun setTitle(string: String)
        fun showReceipt(receipt: Receipt)
    }

    interface EditView : View {
        fun fillReceipt(receipt: Receipt): Receipt
        fun finishView()
        fun showError(error: ValidationError)
        fun onSourceSelected(source: Source)
        fun onAccountSelected(account: Account)
        val installment: Int
        val repetition: Int
        fun onDateChanged(balanceDate: DateTime)
        fun showConfirmDialog(receipt: Receipt)
    }
}
