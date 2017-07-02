package br.com.jonathanzanella.myexpenses.database

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.validations.ValidationResult

interface ModelRepository<in T : UnsyncModel> {
    fun syncAndSave(unsyncAccount: T): ValidationResult
}