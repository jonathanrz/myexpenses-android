package br.com.jonathanzanella.myexpenses.exceptions

import br.com.jonathanzanella.myexpenses.validations.ValidationResult

class ValidationException(validationResult: ValidationResult) : RuntimeException(validationResult.errorsAsString)
