package br.com.jonathanzanella.myexpenses.bill;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jzanella on 8/27/16.
 */

class BillAdapterPresenter {
	private BillRepository repository;
	private BillAdapter adapter;

	private List<Bill> bills;
	private List<Bill> billsFiltered;

	BillAdapterPresenter(BillAdapter adapter, BillRepository repository) {
		this.repository = repository;
		this.adapter = adapter;
		loadBills();
	}

	private void loadBills() {
		bills = repository.userBills();
		billsFiltered = bills;
	}

	List<Bill> getBills(boolean invalidateCache) {
		if(invalidateCache)
			loadBills();
		return Collections.unmodifiableList(billsFiltered);
	}

	void addBill(Bill bill) {
		bills.add(bill);
		if(bills != billsFiltered)
			billsFiltered.add(bill);
		adapter.notifyItemInserted(bills.size() - 1);
	}

	public void filter(String filter) {
		if(filter == null || filter.compareTo("") == 0) {
			billsFiltered = bills;
			return;
		}

		billsFiltered = new ArrayList<>();
		for (Bill bill : bills) {
			if(StringUtils.containsIgnoreCase(bill.getName(), filter))
				billsFiltered.add(bill);
		}
	}
}
