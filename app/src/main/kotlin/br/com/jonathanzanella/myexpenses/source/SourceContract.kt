package br.com.jonathanzanella.myexpenses.source

import android.content.Context
import android.support.annotation.StringRes

import br.com.jonathanzanella.myexpenses.validations.ValidationError

interface SourceContract {
    interface View {
        val context: Context
        fun setTitle(@StringRes string: Int)
        fun setTitle(string: String)
        fun showSource(source: Source)
    }

    interface EditView : View {
        fun fillSource(source: Source): Source
        fun finishView()
        fun showError(error: ValidationError)
    }
}
