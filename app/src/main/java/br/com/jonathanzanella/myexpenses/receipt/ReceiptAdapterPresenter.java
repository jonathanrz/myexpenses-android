package br.com.jonathanzanella.myexpenses.receipt;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jzanella on 8/27/16.
 */

class ReceiptAdapterPresenter {
	private ReceiptRepository repository;
	private ReceiptAdapter adapter;

	private List<Receipt> receipts;
	private List<Receipt> receiptsFiltered;

	ReceiptAdapterPresenter(ReceiptAdapter adapter, ReceiptRepository repository) {
		this.repository = repository;
		this.adapter = adapter;
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

	void addReceipt(Receipt bill) {
		receipts.add(bill);
		if(receipts != receiptsFiltered)
			receiptsFiltered.add(bill);
		adapter.notifyItemInserted(receipts.size() - 1);
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
