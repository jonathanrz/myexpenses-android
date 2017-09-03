package br.com.jonathanzanella.myexpenses.validations

import java.util.*

class ValidationResult {
    val errors = ArrayList<ValidationError>()

    val isValid: Boolean
        get() = errors.isEmpty()

    fun addError(error: ValidationError) {
        errors.add(error)
    }

    val errorsAsString: String
        get() {
            val builder = StringBuilder()
            for (error in errors) {
                if (builder.isNotEmpty())
                    builder.append("\n")
                builder.append(error.toString())
            }
            return builder.toString()
        }
}
