package br.com.jonathanzanella.myexpenses.bill

import android.content.Context
import android.support.annotation.StringRes
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import org.joda.time.DateTime

internal interface BillContract {
    interface View {
        val context: Context
        fun setTitle(@StringRes string: Int)
        fun setTitle(string: String)
        fun showBill(bill: Bill)
    }

    interface EditView : View {
        fun fillBill(bill: Bill): Bill
        fun finishView()
        fun showError(error: ValidationError)
        fun onInitDateChanged(date: DateTime)
        fun onEndDateChanged(date: DateTime)
    }
}
