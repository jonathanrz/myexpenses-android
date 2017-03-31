package br.com.jonathanzanella.myexpenses.expense;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ExpenseAdapterPresenter {
	private final ExpenseRepository repository;
	private final ExpenseAdapter adapter;

	private List<Expense> receipts;
	private List<Expense> receiptsFiltered;

	ExpenseAdapterPresenter(ExpenseAdapter adapter, ExpenseRepository repository) {
		this.repository = repository;
		this.adapter = adapter;
	}

	private void loadExpenses(DateTime date) {
		receipts = repository.monthly(date);
		receiptsFiltered = receipts;
	}

	List<Expense> getExpenses(boolean invalidateCache, DateTime date) {
		if(invalidateCache)
			loadExpenses(date);
		if(receiptsFiltered == null)
			return null;
		return Collections.unmodifiableList(receiptsFiltered);
	}

	public void filter(String filter) {
		if(filter == null || filter.compareTo("") == 0) {
			receiptsFiltered = receipts;
			return;
		}

		receiptsFiltered = new ArrayList<>();
		for (Expense bill : receipts) {
			if(StringUtils.containsIgnoreCase(bill.getName(), filter))
				receiptsFiltered.add(bill);
		}
	}
}
