package br.com.jonathanzanella.myexpenses.bill;

import java.util.Collections;
import java.util.List;

/**
 * Created by jzanella on 8/27/16.
 */

class BillAdapterPresenter {
	private BillRepository repository;
	private BillAdapter adapter;

	private List<Bill> bills;

	BillAdapterPresenter(BillAdapter adapter, BillRepository repository) {
		this.repository = repository;
		this.adapter = adapter;
		loadBills();
	}

	private void loadBills() {
		bills = repository.userBills();
	}

	List<Bill> getBills(boolean invalidateCache) {
		if(invalidateCache)
			loadBills();
		return Collections.unmodifiableList(bills);
	}

	void addBill(Bill bill) {
		bills.add(bill);
		adapter.notifyItemInserted(bills.size() - 1);
	}
}
