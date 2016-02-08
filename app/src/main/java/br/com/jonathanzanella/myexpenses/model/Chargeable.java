package br.com.jonathanzanella.myexpenses.model;

/**
 * Created by jzanella on 2/2/16.
 */
public interface Chargeable {
	long getId();
	ChargeableType getChargeableType();
	String getName();
	boolean canBePaidNextMonth();
	void debit(int value);
	void credit(int value);
	void save();
}
