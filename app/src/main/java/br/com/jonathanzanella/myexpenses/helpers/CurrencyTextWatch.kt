package br.com.jonathanzanella.myexpenses.helpers

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class CurrencyTextWatch(private val edit: EditText) : TextWatcher {
    private var current: String? = null

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.toString() != current) {
            edit.removeTextChangedListener(this)

            val cleanString = s.toString().replace("[^\\d]".toRegex(), "")

            var formatted = ""
            if (!cleanString.isEmpty()) {
                val parsed = java.lang.Double.parseDouble(cleanString)
                formatted = parsed.toInt().toCurrencyFormatted()
            }

            current = formatted
            edit.setText(formatted)
            edit.setSelection(formatted.length)

            edit.addTextChangedListener(this)
        }
    }

    override fun afterTextChanged(s: Editable) {}
}
