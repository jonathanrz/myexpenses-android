package br.com.jonathanzanella.myexpenses.chargeable

enum class ChargeableType {
    ACCOUNT,
    DEBIT_CARD,
    CREDIT_CARD;

    companion object {
        fun getType(type: String): ChargeableType {
            return ChargeableType.values().first { it.name == type }
        }
    }
}