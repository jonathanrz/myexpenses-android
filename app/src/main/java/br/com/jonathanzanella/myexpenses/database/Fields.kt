package br.com.jonathanzanella.myexpenses.database

enum class Fields constructor(private val fieldName: String) {
    ID("id"),
    NAME("name"),
    TITLE("title"),
    DESCRIPTION("description"),
    UUID("uuid"),
    USER_UUID("userUuid"),
    SERVER_ID("serverId"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),
    AMOUNT("amount"),
    BALANCE("balance"),
    CREDITED("credited"),
    CHARGED("charged"),
    CHARGE_NEXT_MONTH("chargedNextMonth"),
    IGNORE_IN_OVERVIEW("ignoreInOverview"),
    IGNORE_IN_RESUME("ignoreInResume"),
    DATE("date"),
    DUE_DATE("due_date"),
    INIT_DATE("init_date"),
    END_DATE("end_date"),
    INCOME("income"),
    VALUE("value"),
    VALUE_TO_SHOW_IN_OVERVIEW("valueToShowInOverview"),
    ACCOUNT_TO_PAY_CREDIT_CARD("accountToPayCreditCard"),
    ACCOUNT_TO_PAY_BILLS("accountToPayBills"),
    ACCOUNT_UUID("accountUuid"),
    BILL_UUID("billUuid"),
    CHARGEABLE_UUID("chargeableUuid"),
    CHARGEABLE_TYPE("chargeableType"),
    SOURCE_UUID("sourceUuid"),
    TYPE("type"),
    REMOVED("removed"),
    SYNC("sync");

    override fun toString(): String {
        return fieldName
    }
}