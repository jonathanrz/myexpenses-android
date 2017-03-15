package br.com.jonathanzanella.myexpenses.receipt;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ReceiptAdapterPresenter {
	private ReceiptRepository repository;

	private List<Receipt> receipts;
	private List<Receipt> receiptsFiltered;

	ReceiptAdapterPresenter(ReceiptRepository repository) {
		this.repository = repository;
	}

	private void loadReceipts(DateTime date) {
		receipts = repository.monthly(date);
		receiptsFiltered = receipts;
	}

	List<Receipt> getReceipts(boolean invalidateCache, DateTime date) {
		if(invalidateCache)
			loadReceipts(date);
		return Collections.unmodifiableList(receiptsFiltered);
	}

	public void filter(String filter) {
		if(filter == null || filter.compareTo("") == 0) {
			receiptsFiltered = receipts;
			return;
		}

		receiptsFiltered = new ArrayList<>();
		for (Receipt bill : receipts) {
			if(StringUtils.containsIgnoreCase(bill.getName(), filter))
				receiptsFiltered.add(bill);
		}
	}
}
