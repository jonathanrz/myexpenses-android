package br.com.jonathanzanella.myexpenses.card

import android.content.Context
import android.support.annotation.StringRes

import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.validations.ValidationError

internal interface CardContract {
    interface View {
        val context: Context
        fun setTitle(@StringRes string: Int)
        fun setTitle(string: String)
        fun showCard(card: Card)
    }

    interface EditView : View {
        fun fillCard(card: Card): Card
        fun finishView()
        fun showError(error: ValidationError)
        fun onAccountSelected(account: Account)
    }
}
