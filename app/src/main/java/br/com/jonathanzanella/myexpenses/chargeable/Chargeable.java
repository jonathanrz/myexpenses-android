package br.com.jonathanzanella.myexpenses.chargeable;

/**
 * Created by jzanella on 2/2/16.
 */
public interface Chargeable {
	String getUuid();
	ChargeableType getChargeableType();
	String getName();
	boolean canBePaidNextMonth();
	void debit(int value);
	void credit(int value);
	void save();
}
