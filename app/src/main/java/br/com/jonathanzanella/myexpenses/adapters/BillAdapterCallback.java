package br.com.jonathanzanella.myexpenses.adapters;

import br.com.jonathanzanella.myexpenses.models.Bill;

/**
 * Created by jzanella on 2/1/16.
 */
public interface BillAdapterCallback {
	void onBillSelected(Bill bill);
}