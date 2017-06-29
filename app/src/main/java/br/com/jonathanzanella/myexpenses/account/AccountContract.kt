package br.com.jonathanzanella.myexpenses.account

import android.content.Context
import android.support.annotation.StringRes

import br.com.jonathanzanella.myexpenses.validations.ValidationError

internal interface AccountContract {
    interface View {
        val context: Context
        fun setTitle(@StringRes string: Int)
        fun setTitle(string: String)
        fun showAccount(account: Account)
    }

    interface EditView : View {
        fun fillAccount(account: Account): Account
        fun finishView()
        fun showError(error: ValidationError)
    }
}
