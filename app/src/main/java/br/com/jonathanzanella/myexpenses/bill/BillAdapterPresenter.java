package br.com.jonathanzanella.myexpenses.bill;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BillAdapterPresenter {
	private BillRepository repository;

	private List<Bill> bills;
	private List<Bill> billsFiltered;

	BillAdapterPresenter(BillRepository repository) {
		this.repository = repository;
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
