package br.com.jonathanzanella.myexpenses.validations

import java.util.*

class ValidationResult {
    private val errors = ArrayList<ValidationError>()

    val isValid: Boolean
        get() = errors.isEmpty()

    fun addError(error: ValidationError) {
        errors.add(error)
    }

    fun getErrors(): List<ValidationError> {
        return errors
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
