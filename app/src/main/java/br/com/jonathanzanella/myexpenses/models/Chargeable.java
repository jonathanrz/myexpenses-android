package br.com.jonathanzanella.myexpenses.models;

/**
 * Created by jzanella on 2/2/16.
 */
public interface Chargeable {
	long getId();
	String getUuid();
	ChargeableType getChargeableType();
	String getName();
	boolean canBePaidNextMonth();
	void debit(int value);
	void credit(int value);
	void save();
}
