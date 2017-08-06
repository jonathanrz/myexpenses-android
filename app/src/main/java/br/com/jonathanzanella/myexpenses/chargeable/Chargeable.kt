package br.com.jonathanzanella.myexpenses.chargeable

interface Chargeable {
    val uuid: String?
    val chargeableType: ChargeableType
    var name: String?

    fun canBePaidNextMonth(): Boolean
    fun debit(value: Int)
    fun credit(value: Int)
}
