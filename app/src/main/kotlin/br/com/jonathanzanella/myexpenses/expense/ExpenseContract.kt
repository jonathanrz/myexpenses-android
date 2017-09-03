package br.com.jonathanzanella.myexpenses.expense

import android.content.Context
import android.support.annotation.StringRes
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import org.joda.time.DateTime

interface ExpenseContract {
    interface View {
        val context: Context
        fun setTitle(@StringRes string: Int)
        fun setTitle(string: String)
        fun showExpense(expense: Expense)
    }

    interface EditView : View {
        fun fillExpense(expense: Expense): Expense
        fun finishView()
        fun showError(error: ValidationError)
        fun onDateChanged(date: DateTime)
        fun onBillSelected(bill: Bill?)
        fun onChargeableSelected(chargeable: Chargeable)
    }
}
