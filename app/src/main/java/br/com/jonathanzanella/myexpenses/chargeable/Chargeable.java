package br.com.jonathanzanella.myexpenses.chargeable;

public interface Chargeable {
	String getUuid();
	ChargeableType getChargeableType();
	String getName();
	boolean canBePaidNextMonth();
	void debit(int value);
	void credit(int value);
}
