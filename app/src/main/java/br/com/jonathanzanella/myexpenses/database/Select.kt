package br.com.jonathanzanella.myexpenses.database

import java.util.*

/**
 * Created by jzanella on 11/12/16.
 */

data class Select internal constructor(internal var where: String, internal var parameters: Array<String>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        if (other is Select && !Arrays.equals(parameters, other.parameters)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(parameters)
    }
}