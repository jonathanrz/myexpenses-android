package br.com.jonathanzanella.myexpenses.chargeable;

/**
 * Created by Jonathan Zanella on 04/02/16.
 */
public enum ChargeableType {
	ACCOUNT,
	DEBIT_CARD,
	CREDIT_CARD;

	public static ChargeableType getType(String type) {
		for (ChargeableType chargeableType : ChargeableType.values()) {
			if(chargeableType.name().equals(type))
				return chargeableType;
		}
		return null;
	}
}
